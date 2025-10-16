package com.inklop.inklop.entities.Campaign;


import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bank_transfer")
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class BankTransfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name="id_campaign_payment",referencedColumnName = "id",nullable = false)
    private CampaignPayment campaignPayment;

    @Column(name = "bank_name", nullable = false)
    private String bankName;

    @Column(name="operation_number",nullable = false)
    private String operationNumber;

    @Column(name = "ammount", nullable = false)
    private BigDecimal ammount;

    @Column(name = "transfer_date", nullable = false)
    private LocalDateTime transferDate;

    @PrePersist
    public void prePersist(){
        this.transferDate=LocalDateTime.now();
    }

}
