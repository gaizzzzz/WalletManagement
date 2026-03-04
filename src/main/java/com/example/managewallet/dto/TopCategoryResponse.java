package com.example.managewallet.dto;

import java.math.BigDecimal;

public record TopCategoryResponse(
        String type,
        BigDecimal amount,
        BigDecimal percent
) {
}
