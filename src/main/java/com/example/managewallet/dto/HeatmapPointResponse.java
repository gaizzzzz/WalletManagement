package com.example.managewallet.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record HeatmapPointResponse(
        LocalDate date,
        BigDecimal intensity
) {
}
