package com.inklop.inklop.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inklop.inklop.entities.BankAccount;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long>{
    List<BankAccount> findByUserId(Long userId);
}
