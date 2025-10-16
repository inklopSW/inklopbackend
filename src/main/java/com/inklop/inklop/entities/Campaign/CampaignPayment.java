package com.inklop.inklop.entities.Campaign;

import com.inklop.inklop.entities.valueObject.campaign.PaymentStatus;
import com.inklop.inklop.entities.valueObject.campaign.PaymentType;
import com.inklop.inklop.entities.valueObject.campaign.Currency;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "campaign_payment")
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class CampaignPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="id_campaign",referencedColumnName = "id",nullable = false)
    private Campaign campaign;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus; // default

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false)
    private Currency currency;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false)
    private PaymentType paymentType;

    //dos dias de gracia para que el pago se refleje
    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate; // default

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt; //default

    private String RUC;
    
    @Column(name = "business_name", nullable = false)
    private String businessName;

    @PrePersist
    public void prePersist() {
        this.paymentDate = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.paymentStatus = PaymentStatus.PENDING;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
