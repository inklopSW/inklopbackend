package com.inklop.inklop.controllers.campaign.request;

public record FullBankPayment(
    Long campaignId,
    String businessName,
    String RUC,
    String bankName,
    String operationNumber,
    String accountNumber
) {
    
}
