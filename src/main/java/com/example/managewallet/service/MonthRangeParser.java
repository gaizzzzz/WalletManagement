package com.example.managewallet.service;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;

public final class MonthRangeParser {

    private MonthRangeParser() {
    }

    public static MonthWindow parse(String month) {
        if (month == null || month.isBlank()) {
            throw new IllegalArgumentException("month is required in yyyy-MM format");
        }
        try {
            YearMonth ym = YearMonth.parse(month);
            return new MonthWindow(ym.atDay(1).atStartOfDay(), ym.plusMonths(1).atDay(1).atStartOfDay());
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("month must be in yyyy-MM format");
        }
    }
}
