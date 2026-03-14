package com.example.managewallet.controller;

import com.example.managewallet.dto.CreateTransactionRequest;
import com.example.managewallet.dto.TransactionResponse;
import com.example.managewallet.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Transactions", description = "Transaction CRUD APIs")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a transaction")
    public TransactionResponse createTransaction(@Valid @RequestBody CreateTransactionRequest request) {
        return transactionService.createTransaction(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a transaction by id")
    public void deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
    }

    @GetMapping
    @Operation(summary = "List transactions by month (yyyy-MM) or date range, limited to 10 most recent")
    public List<TransactionResponse> listTransactions(
            @RequestParam(required = false) String month,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        if (month != null) {
            return transactionService.listByMonth(month);
        } else if (startDate != null && endDate != null) {
            return transactionService.listByDateRange(startDate, endDate);
        } else {
            return transactionService.recentTransactions();
        }
    }

    @GetMapping("/recent")
    @Operation(summary = "Get recent 10 transactions")
    public List<TransactionResponse> recentTransactions() {
        return transactionService.recentTransactions();
    }
}
