package com.example.managewallet.service;

import com.example.managewallet.domain.FlowType;
import com.example.managewallet.domain.WalletTransaction;
import com.example.managewallet.dto.DashboardSummaryResponse;
import com.example.managewallet.dto.HeatmapPointResponse;
import com.example.managewallet.dto.TopCategoryResponse;
import com.example.managewallet.repository.WalletTransactionRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardService {

    private final WalletTransactionRepository transactionRepository;

    public DashboardService(WalletTransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Transactional(readOnly = true)
    public DashboardSummaryResponse getSummary(String month) {
        List<WalletTransaction> monthTransactions = getTransactionsForMonth(month);

        BigDecimal incomeTotal = monthTransactions.stream()
                .filter(tx -> tx.getFlow() == FlowType.INCOME)
                .map(WalletTransaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal expenseTotal = monthTransactions.stream()
                .filter(tx -> tx.getFlow() == FlowType.EXPENSE)
                .map(WalletTransaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal net = incomeTotal.subtract(expenseTotal);
        return new DashboardSummaryResponse(incomeTotal, expenseTotal, net, monthTransactions.size());
    }

    @Transactional(readOnly = true)
    public List<HeatmapPointResponse> getHeatmap(String month) {
        List<WalletTransaction> monthTransactions = getTransactionsForMonth(month);
        Map<LocalDate, List<WalletTransaction>> grouped = new LinkedHashMap<>();

        monthTransactions.stream()
                .filter(tx -> tx.getFlow() == FlowType.EXPENSE)
                .sorted(Comparator.comparing(WalletTransaction::getOccurredAt))
                .forEach(tx -> grouped.computeIfAbsent(tx.getOccurredAt().toLocalDate(), ignored -> new ArrayList<>()).add(tx));

        return grouped.entrySet().stream()
                .map(entry -> {
                    BigDecimal total = entry.getValue().stream()
                            .map(WalletTransaction::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    return new HeatmapPointResponse(
                            entry.getKey(),
                            total
                    );
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TopCategoryResponse> getTopExpenseCategories(String month) {
        List<WalletTransaction> monthTransactions = getTransactionsForMonth(month);

        Map<String, BigDecimal> totalsByCategory = monthTransactions.stream()
                .filter(tx -> tx.getFlow() == FlowType.EXPENSE)
                .collect(Collectors.groupingBy(
                        tx -> normalizeCategory(tx.getCategory()),
                        Collectors.mapping(
                                WalletTransaction::getAmount,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                        )
                ));

        BigDecimal totalExpense = totalsByCategory.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalExpense.compareTo(BigDecimal.ZERO) == 0) {
            return List.of();
        }

        return totalsByCategory.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .limit(5)
                .map(entry -> new TopCategoryResponse(
                        entry.getKey(),
                        entry.getValue(),
                        entry.getValue()
                                .multiply(BigDecimal.valueOf(100))
                                .divide(totalExpense, 2, java.math.RoundingMode.HALF_UP)
                ))
                .toList();
    }

    private List<WalletTransaction> getTransactionsForMonth(String month) {
        MonthWindow monthWindow = MonthRangeParser.parse(month);
        return transactionRepository.findByOccurredAtGreaterThanEqualAndOccurredAtLessThanOrderByOccurredAtDescIdDesc(
                monthWindow.start(),
                monthWindow.end()
        );
    }

    private String normalizeCategory(String category) {
        if (category == null) {
            return "Other";
        }
        String normalized = category.trim();
        return normalized.isEmpty() ? "Other" : normalized;
    }
}

