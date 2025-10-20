package com.inklop.inklop.entities;

import com.inklop.inklop.entities.valueObject.user.BusinessType;
import com.inklop.inklop.entities.valueObject.user.SectorBusiness;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "business")
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Business {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    @Column(name = "business_name",nullable = false)
    private String businessName;

    @Column(name = "ruc", nullable = false)
    private String RUC;

    @Enumerated(EnumType.STRING)
    @Column(name = "sector", nullable = false)
    private SectorBusiness sector;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "avatar_business")
    private String avatarBusiness;

    @Enumerated(EnumType.STRING)
    @Column(name = "business_type", nullable = false)
    private BusinessType businessType;

}
