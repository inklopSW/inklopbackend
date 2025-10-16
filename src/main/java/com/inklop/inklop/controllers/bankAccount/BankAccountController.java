package com.inklop.inklop.controllers.bankAccount;


import com.inklop.inklop.services.BankAccountService;
import com.inklop.inklop.entities.BankAccount;
import com.inklop.inklop.entities.BankWithdrawal;
import com.inklop.inklop.entities.valueObject.DateRangeType;
import com.inklop.inklop.entities.valueObject.campaign.PaymentStatus;
import com.inklop.inklop.controllers.bankAccount.request.BankAccountRequest;
import com.inklop.inklop.controllers.bankAccount.request.BankWithdrawalRequest;
import com.inklop.inklop.controllers.bankAccount.response.BankWithdrawalResponseTicket;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;





@RestController
@RequestMapping("/bank-account")
public class BankAccountController {
    private final BankAccountService bankAccountService;

    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @PostMapping
    public ResponseEntity<BankAccount> createBankAccount(@RequestBody BankAccountRequest request) {
        return ResponseEntity.ok(bankAccountService.createBankAccount(request));
    }

    
    @GetMapping("/user/{id}")
    public ResponseEntity<List<BankAccount>> getAllBankAccountsbyUserId(@PathVariable Long id) {
        return ResponseEntity.ok(bankAccountService.getAllBankAccountsbyUserId(id));
    }

    @PostMapping("/withdrawal")
    public ResponseEntity<BankWithdrawalResponseTicket> createBankWithDrawal(@RequestBody BankWithdrawalRequest request){
        return ResponseEntity.ok(bankAccountService.createBankWithdrawal(request));
    }
    

    @GetMapping("/user/{id}/date-range/{rangeType}")
    public ResponseEntity<List<BankWithdrawal>> getWithdrawalsByRange(@PathVariable Long id, @PathVariable DateRangeType rangeType) {
        return ResponseEntity.ok(bankAccountService.getWithdrawalsByRange(id, rangeType));
    }   
    
    @PutMapping("/withdrawal/{id}/paymentStatus/{paymentStatus}")
    public ResponseEntity<BankWithdrawal> setPaymentStatus(@PathVariable Long id, @PathVariable PaymentStatus paymentStatus) {
        return ResponseEntity.ok(bankAccountService.setPaymentStatus(id, paymentStatus));
    }   

}
