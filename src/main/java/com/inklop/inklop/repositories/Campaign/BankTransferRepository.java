package com.inklop.inklop.repositories.Campaign;

import com.inklop.inklop.entities.Campaign.BankTransfer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankTransferRepository extends JpaRepository<BankTransfer, Long> {
}
