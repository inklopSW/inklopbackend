package com.inklop.inklop.controllers.user.request;

import java.util.List;
import com.inklop.inklop.entities.valueObject.user.BusinessType;
import com.inklop.inklop.entities.valueObject.user.SectorBusiness;

public record BusinessRequest(
    //user
    UserRegisterRequest user,
    //business
    String businessName,
    String description,
    String businessImage,
    String ruc,
    BusinessType businessType,
    SectorBusiness sector, // PUNTO DE MEJORA URGENTE: HACER ENUM
    //socialMedia
    List<SocialMediaRequest> socialMedias
) {

}

