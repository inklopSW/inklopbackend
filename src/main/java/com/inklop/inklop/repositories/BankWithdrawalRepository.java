package com.inklop.inklop.repositories;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inklop.inklop.entities.BankWithdrawal;

import java.util.List;

@Repository
public interface BankWithdrawalRepository extends JpaRepository<BankWithdrawal, Long>{
    List<BankWithdrawal> findByBankAccountUserIdAndCreatedAtBetween(
            Long userId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );
}
