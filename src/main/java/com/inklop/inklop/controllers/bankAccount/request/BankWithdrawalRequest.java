package com.inklop.inklop.controllers.bankAccount.request;

import java.math.BigDecimal;

public record BankWithdrawalRequest(
    Long bankAccountId,
    BigDecimal mount
) {
}  