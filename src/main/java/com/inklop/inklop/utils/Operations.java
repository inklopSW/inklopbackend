package com.inklop.inklop.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Operations {
    public static boolean equalsWithTolerance(BigDecimal a, BigDecimal b) {
        if (a == null || b == null) return false;

        // Diferencia absoluta |a - b|
        BigDecimal diff = a.subtract(b).abs();

        // Tomamos el mayor valor absoluto como base
        BigDecimal base = a.abs().max(b.abs());

        // Si ambos son cero
        if (base.compareTo(BigDecimal.ZERO) == 0) {
            return diff.compareTo(BigDecimal.ZERO) == 0;
        }

        // Calculamos el % de diferencia: (|a-b| / base) * 100
        BigDecimal percentageDiff = diff
                .divide(base, 6, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        // Tolerancia de 0.5%
        BigDecimal tolerance = BigDecimal.valueOf(0.5);

        return percentageDiff.compareTo(tolerance) <= 0;
    }
}
