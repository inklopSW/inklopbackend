package com.inklop.inklop.controllers.user.response;

import com.inklop.inklop.entities.valueObject.user.BusinessType;

public record BusinessResponse(
    String bussinessName,
    String description,
    String bussinessImage,
    String sector,
    BusinessType businessType
 ) {
    
}
