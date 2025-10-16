package com.inklop.inklop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inklop.inklop.entities.Wallet;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet,Long>{
    Optional<Wallet> findByUserId(Long userId);
}
