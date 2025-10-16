package com.inklop.inklop.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.inklop.inklop.entities.valueObject.campaign.PaymentStatus;

@Entity
@Table(name = "bank_withdrawal")
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class BankWithdrawal{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "bank_account_id", referencedColumnName = "id")
    public BankAccount bankAccount;

    public BigDecimal mount;

    @Enumerated(EnumType.STRING)
    public PaymentStatus paymentStatus;

    @CreatedDate
    public LocalDateTime createdAt;

    public String description;

    @LastModifiedDate
    public LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.paymentStatus=PaymentStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    
    
    }
}