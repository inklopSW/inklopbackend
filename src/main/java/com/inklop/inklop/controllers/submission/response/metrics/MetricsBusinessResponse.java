package com.inklop.inklop.controllers.submission.response.metrics;

import java.math.BigDecimal;


public record MetricsBusinessResponse (
    Long userId,
    BigDecimal totalBudget,
    Long views,
    Integer quantity,
    BigDecimal engagement
){
    
}
