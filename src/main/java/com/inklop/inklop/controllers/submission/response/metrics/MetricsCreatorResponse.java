package com.inklop.inklop.controllers.submission.response.metrics;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.inklop.inklop.entities.valueObject.campaign.Currency;

import com.inklop.inklop.controllers.submission.response.ShowFullSubmission;

public record MetricsCreatorResponse(
    Long views,
    Integer quantity,
    Long viewsIg,
    Long viewsFb,
    Long viewsTk,
    List<ShowFullSubmission> submissions,
    BigDecimal walletUSD,
    BigDecimal walletPEN,
    BigDecimal totalBalance,
    List<IncomesResponse> incomes
) {
    public record IncomesResponse(
        Long submissionId,
        String image,
        String title,
        BigDecimal money,
        Currency currency,
        LocalDate date
    ){
    }
}
