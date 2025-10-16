package com.inklop.inklop.controllers.bankAccount.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BankWithdrawalResponseTicket(
    Long withdrawalId,
    BigDecimal amount,
    LocalDateTime date,
    String nrRef
) {    
}
