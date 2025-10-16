package com.inklop.inklop.controllers.submission.response.metrics;

import com.inklop.inklop.controllers.submission.response.ShowFullSubmission;

import java.math.BigDecimal;
import java.util.List;

public record MetricsCampaignResponse(
    Long campaignId,
    String title,
    String photo,
    BigDecimal totalBudget,
    BigDecimal consumedBudget,
    Integer daysActual,
    Integer daysTotal,
    Integer quantity,
    Long views,
    Long likes,
    Long comments,
    Long shareCount,
    Long viewsFb,
    Long viewsTk,
    Long viewsIg, 
    BigDecimal engagement,
    List<ShowFullSubmission> submissions
) {
    
}
