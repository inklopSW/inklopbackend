package com.inklop.inklop.entities.Campaign;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "campaign_country")
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class CampaignCountry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_campaign", referencedColumnName = "id", nullable = false)
    private Campaign campaign;

    private String country;

    private String department;
}
