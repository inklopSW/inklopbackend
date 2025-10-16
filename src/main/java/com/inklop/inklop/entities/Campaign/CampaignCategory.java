package com.inklop.inklop.entities.Campaign;

import com.inklop.inklop.entities.valueObject.campaign.CampaignCategoryType;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "campaign_category")
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class CampaignCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private CampaignCategoryType category;

    @ManyToOne
    @JoinColumn(name="id_campaign",referencedColumnName = "id",nullable = false)
    private Campaign campaign;
}
