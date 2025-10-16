package com.inklop.inklop.controllers.campaign.request;

import java.math.BigDecimal;

public record AddCampaignRequest (
    BigDecimal addOn,
    Integer days
){    
}
