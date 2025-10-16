package com.inklop.inklop.entities.Campaign;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.inklop.inklop.entities.valueObject.campaign.Currency;
import com.inklop.inklop.entities.valueObject.campaign.TypeText;
import com.inklop.inklop.entities.valueObject.campaign.PaymentStatus;
import com.inklop.inklop.entities.valueObject.user.CreatorType;
import com.inklop.inklop.utils.Operations;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.inklop.inklop.entities.Business;
import com.inklop.inklop.entities.Room;
import com.inklop.inklop.entities.Submission;
import com.inklop.inklop.entities.valueObject.campaign.CampaignStatus;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "campaign")
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Campaign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="id_business",referencedColumnName = "id",nullable = false)
    private Business business;

    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private CreatorType type;

    @Column(name="logo")
    private String logo;

    private String description;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name="type_text")
    private TypeText typeText;

    @Column(name = "text_influencer")
    private String textInfluencer; // link de drive

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false)
    private Currency currency;

    @Column(name= "cpm", nullable = false)
    private BigDecimal cpm;

    @Column(name = "total_budget", nullable = false)
    private BigDecimal totalBudget;

    @Column(name="consumed_budget", nullable = false)
    private BigDecimal consumedBudget;

    @Column(name = "maximun_payment", nullable = false)
    private BigDecimal maximunPayment;

    @Column(name = "minimum_payment", nullable = false)
    private BigDecimal minimumPayment;

    @Enumerated(EnumType.STRING)
    @Column(name="campaign_status")
    private CampaignStatus campaignStatus;

    @Enumerated(EnumType.STRING)
    @Column(name="payment_status")
    private PaymentStatus paymentStatus;

    // Social Media Presence, filter
    private Boolean hasTiktok;

    private Boolean hasInstagram;

    private Boolean hasFacebook;

    private Boolean isPrivate;

    //relations
    @JsonIgnore
    @OneToMany(mappedBy = "campaign")
    private List<CampaignCategory> campaignCategories;

    @JsonIgnore
    @OneToMany(mappedBy = "campaign")
    private List<CampaignCountry> campaignCountries;

    @JsonIgnore
    @OneToMany(mappedBy = "campaign")
    private List<CampaignRequirements> campaignRequirements;

    @JsonIgnore
    @OneToMany(mappedBy = "campaign")
    private List<CampaignPayment> campaignPayments;

    @JsonIgnore
    @OneToMany(mappedBy = "campaign")
    private List<Submission> submissions;

    @JsonIgnore
    @OneToMany(mappedBy = "campaign")
    private List<Room> rooms;

    @Column(name="created_at", nullable=false)
    private LocalDateTime createdAt;

    @JsonIgnore
    @Column(name="updated_at",nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist(){
        this.createdAt = LocalDateTime.now();
        this.updatedAt= LocalDateTime.now();
        this.consumedBudget = BigDecimal.ZERO;
        this.campaignStatus = CampaignStatus.PENDING;
        this.paymentStatus = PaymentStatus.PENDING;
        this.isPrivate = false;
    }

    @PreUpdate
    public void preUpdate(){
        this.updatedAt= LocalDateTime.now();
        if (Operations.equalsWithTolerance(totalBudget, consumedBudget)){
            this.campaignStatus = CampaignStatus.COMPLETED;
        }
        if (!endDate.isAfter(LocalDate.now()) && paymentStatus.equals(PaymentStatus.APPROVED)){
            this.campaignStatus = CampaignStatus.COMPLETED;
        }
    }

    public Integer actualDays(){
        if (LocalDate.now().isBefore(this.getStartDate())){
            return 0;
        } else if (LocalDate.now().isAfter(this.getEndDate())){
            return this.totalDays();
        
        }
        return (int) ChronoUnit.DAYS.between(this.getStartDate(),LocalDate.now());

    }

    public Integer totalDays(){
        return (int) ChronoUnit.DAYS.between(this.getStartDate(),this.getEndDate());
    }

}
