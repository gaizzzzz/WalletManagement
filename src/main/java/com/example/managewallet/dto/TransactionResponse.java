package com.example.managewallet.dto;

import com.example.managewallet.domain.FlowType;
import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionResponse(
        String id,
        LocalDate date,
        FlowType type,
        String category,
        BigDecimal amount,
        String note
) {
}
