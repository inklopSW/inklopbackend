package com.inklop.inklop.controllers.user.response;

import java.math.BigDecimal;

public record WalletResponse(
    BigDecimal balancePEN,
    BigDecimal balanceUSD
) {
}