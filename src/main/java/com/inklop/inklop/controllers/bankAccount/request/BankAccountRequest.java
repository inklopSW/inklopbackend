package com.inklop.inklop.controllers.bankAccount.request;

import com.inklop.inklop.entities.valueObject.campaign.Currency;

public record BankAccountRequest(
    String bankName,
    String accountHolderName,
    String dni,
    String accountNumber,
    String interbankNumber,
    String accountType,
    Currency currency,
    Long userId
) {
    
}
