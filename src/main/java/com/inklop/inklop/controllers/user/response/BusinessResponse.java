package com.inklop.inklop.controllers.user.response;

import com.inklop.inklop.entities.valueObject.user.BusinessType;
import com.inklop.inklop.entities.valueObject.user.SectorBusiness;

public record BusinessResponse(
    String bussinessName,
    String description,
    String bussinessImage,
    String ruc,
    SectorBusiness sector,
    BusinessType businessType
 ) {
    
}
