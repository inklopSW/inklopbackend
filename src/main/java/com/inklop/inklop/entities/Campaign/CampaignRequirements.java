package com.inklop.inklop.entities.Campaign;


import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "campaign_requirements")
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class CampaignRequirements {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_campaign", referencedColumnName = "id", nullable = false)
    private Campaign campaign;

    @Column( length = 4000)
    private String requirement;
    
}
