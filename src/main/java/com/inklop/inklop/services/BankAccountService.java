package com.inklop.inklop.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.inklop.inklop.controllers.bankAccount.request.BankAccountRequest;
import com.inklop.inklop.controllers.bankAccount.request.BankWithdrawalRequest;
import com.inklop.inklop.controllers.bankAccount.response.BankWithdrawalResponseTicket;
import com.inklop.inklop.entities.BankAccount;
import com.inklop.inklop.entities.BankWithdrawal;
import com.inklop.inklop.entities.Wallet;
import com.inklop.inklop.entities.valueObject.DateRangeType;
import com.inklop.inklop.entities.valueObject.campaign.Currency;
import com.inklop.inklop.entities.valueObject.campaign.PaymentStatus;
import com.inklop.inklop.repositories.BankAccountRepository;
import com.inklop.inklop.repositories.BankWithdrawalRepository;
import com.inklop.inklop.repositories.UserRepository;
import com.inklop.inklop.repositories.WalletRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BankAccountService {
    private final BankAccountRepository bankAccountRepository;
    private final BankWithdrawalRepository bankWithdrawalRepository;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;


    public BankAccount createBankAccount(BankAccountRequest bankAccountRequest){
        BankAccount bankAccount = new BankAccount();
        bankAccount.setBankName(bankAccountRequest.bankName());
        bankAccount.setAccountNumber(bankAccountRequest.accountNumber());
        bankAccount.setInterbankNumber(bankAccountRequest.interbankNumber());
        bankAccount.setAccountType(bankAccountRequest.accountType());
        bankAccount.setCurrency(bankAccountRequest.currency());
        bankAccount.setAccountHolderName(bankAccountRequest.accountHolderName());
        bankAccount.setDni(bankAccountRequest.dni());
        bankAccount.setUser(userRepository.findById(bankAccountRequest.userId()).get());
        bankAccountRepository.save(bankAccount);
        return bankAccount;
    }

    public List<BankAccount> getAllBankAccountsbyUserId(Long userId){
        return bankAccountRepository.findByUserId(userId);
    }

    public BankWithdrawalResponseTicket createBankWithdrawal(BankWithdrawalRequest bankWithdrawalRequest){
        BankWithdrawal bankWithdrawal= new BankWithdrawal();
        BankAccount bankAccount = bankAccountRepository.findById(bankWithdrawalRequest.bankAccountId())
            .orElseThrow(() -> new IllegalArgumentException("Bank account not found with id: " + bankWithdrawalRequest.bankAccountId()));
        Wallet wallet = bankAccount.getUser().getWallet();

        if(bankAccount.getCurrency().equals(Currency.USD)){
            if(wallet.getUSD().compareTo(bankWithdrawalRequest.mount()) < 0){
                throw new IllegalArgumentException("Insufficient USD balance in wallet");
            }
            wallet.setUSD(wallet.getUSD().subtract(bankWithdrawalRequest.mount()));
        } else if (bankAccount.getCurrency().equals(Currency.PEN)){
            if(wallet.getPEN().compareTo(bankWithdrawalRequest.mount()) < 0){
                throw new IllegalArgumentException("Insufficient PEN balance in wallet");
            }
            wallet.setPEN(wallet.getPEN().subtract(bankWithdrawalRequest.mount()));
        } else {
            throw new IllegalArgumentException("Unsupported currency: " + bankAccount.getCurrency());
        }

        walletRepository.save(wallet);
        bankWithdrawal.setBankAccount(bankAccount);
        bankWithdrawal.setMount(bankWithdrawalRequest.mount());
        bankWithdrawalRepository.save(bankWithdrawal);
        return new BankWithdrawalResponseTicket(
            bankWithdrawal.getId(),
            bankWithdrawal.getMount(),
            bankWithdrawal.getCreatedAt(),
            "INK - 22132101"
        );
    }


    public BankWithdrawal setPaymentStatus(Long id, PaymentStatus newStatus) {
        BankWithdrawal bankWithdrawal = bankWithdrawalRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Bank withdrawal not found with id: " + id));
            
        PaymentStatus currentStatus = bankWithdrawal.getPaymentStatus();
        // Evitar cambios desde estados finales
        if (currentStatus.equals(PaymentStatus.REJECTED) || currentStatus.equals(PaymentStatus.DONE)) {
            throw new IllegalArgumentException("Withdrawals in " + currentStatus + " state cannot be modified.");
        }

        // === PENDING → APPROVED ===
        if (currentStatus.equals(PaymentStatus.PENDING) && newStatus.equals(PaymentStatus.APPROVED)) {
            bankWithdrawal.setDescription("Withdrawal approved");
            bankWithdrawal.setPaymentStatus(PaymentStatus.APPROVED);
        }

        // === PENDING → REJECTED ===
        else if (currentStatus.equals(PaymentStatus.PENDING) && newStatus.equals(PaymentStatus.REJECTED)) {
            refundWithdrawal(bankWithdrawal);
            bankWithdrawal.setDescription("Withdrawal rejected");
            bankWithdrawal.setPaymentStatus(PaymentStatus.REJECTED);
        }

        // === APPROVED → DONE ===
        else if (currentStatus.equals(PaymentStatus.APPROVED) && newStatus.equals(PaymentStatus.DONE)) {
            bankWithdrawal.setDescription("Withdrawal completed");
            bankWithdrawal.setPaymentStatus(PaymentStatus.DONE);
        }

        // === APPROVED → REJECTED ===
        else if (currentStatus.equals(PaymentStatus.APPROVED) && newStatus.equals(PaymentStatus.REJECTED)) {
            refundWithdrawal(bankWithdrawal);
            bankWithdrawal.setDescription("Withdrawal rejected after approval");
            bankWithdrawal.setPaymentStatus(PaymentStatus.REJECTED);
        }

        else {
            throw new IllegalArgumentException(
                "Invalid transition: cannot move from " + currentStatus + " to " + newStatus
                );
            }
            
        return bankWithdrawalRepository.save(bankWithdrawal);
    }

    
    private void refundWithdrawal(BankWithdrawal bankWithdrawal) {
        Wallet wallet = bankWithdrawal.getBankAccount().getUser().getWallet();
        BigDecimal amount = bankWithdrawal.getMount();
        Currency currency = bankWithdrawal.getBankAccount().getCurrency();
        if (currency.equals(Currency.USD)) {
            wallet.setUSD(wallet.getUSD().add(amount));
        } else if (currency.equals(Currency.PEN)) {
            wallet.setPEN(wallet.getPEN().add(amount));
        }
        walletRepository.save(wallet);
    }


    public List<BankWithdrawal> getAllWithdrawalsToday(Long userId){
        return bankWithdrawalRepository.findByBankAccountUserIdAndCreatedAtBetween(
            userId,
            java.time.LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0),
            java.time.LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999)
        );
    }

    public List<BankWithdrawal> getWithdrawalsByRange(Long userId, DateRangeType rangeType) {
        LocalDateTime startDate;
        LocalDateTime endDate;

        switch (rangeType) {
            case DAY:
                startDate = LocalDate.now().atStartOfDay();
                endDate = startDate.plusDays(1).minusNanos(1);
                break;

            case WEEK:
                startDate = LocalDate.now()
                    .with(java.time.DayOfWeek.MONDAY) // inicio de semana
                    .atStartOfDay();
                endDate = startDate.plusWeeks(1).minusNanos(1);
                break;

            case MONTH:
                startDate = LocalDate.now()
                    .withDayOfMonth(1)
                    .atStartOfDay();
                endDate = startDate.plusMonths(1).minusNanos(1);
                break;

            case YEAR:
                startDate = LocalDate.now()
                    .withDayOfYear(1)
                    .atStartOfDay();
                endDate = startDate.plusYears(1).minusNanos(1);
                break;

            default:
                throw new IllegalArgumentException("Invalid range type: " + rangeType);
            }

        return bankWithdrawalRepository.findByBankAccountUserIdAndCreatedAtBetween(userId, startDate, endDate);
    }


}
