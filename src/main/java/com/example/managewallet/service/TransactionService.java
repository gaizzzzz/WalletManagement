package com.example.managewallet.service;

import com.example.managewallet.domain.FlowType;
import com.example.managewallet.domain.PaymentType;
import com.example.managewallet.domain.WalletTransaction;
import com.example.managewallet.dto.CreateTransactionRequest;
import com.example.managewallet.dto.TransactionResponse;
import com.example.managewallet.exception.NotFoundException;
import com.example.managewallet.repository.WalletTransactionRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {

    private final WalletTransactionRepository transactionRepository;

    public TransactionService(WalletTransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public TransactionResponse createTransaction(CreateTransactionRequest request) {
        FlowType flow = request.getType() != null ? request.getType() : request.getFlow();
        if (flow == null) {
            throw new IllegalArgumentException("type is required");
        }

        if (request.getDate() == null && request.getOccurredAt() == null) {
            throw new IllegalArgumentException("date is required");
        }

        WalletTransaction tx = new WalletTransaction();
        tx.setAmount(request.getAmount());
        tx.setCategory(request.getCategory().trim());
        tx.setDescription(resolveNote(request));
        tx.setOccurredAt(request.getOccurredAt() != null ? request.getOccurredAt() : request.getDate().atStartOfDay());
        tx.setFlow(flow);
        tx.setPayment(request.getPayment() != null ? request.getPayment() : PaymentType.CASH);

        WalletTransaction saved = transactionRepository.save(tx);
        return toResponse(saved);
    }

    @Transactional
    public void deleteTransaction(Long id) {
        WalletTransaction tx = transactionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Transaction with id " + id + " not found"));
        transactionRepository.delete(tx);
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> listByMonth(String month) {
        MonthWindow monthWindow = MonthRangeParser.parse(month);
        return transactionRepository
                .findByOccurredAtGreaterThanEqualAndOccurredAtLessThanOrderByOccurredAtDescIdDesc(
                        monthWindow.start(),
                        monthWindow.end()
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> recentTransactions() {
        return transactionRepository.findTop10ByOrderByOccurredAtDescIdDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private TransactionResponse toResponse(WalletTransaction tx) {
        return new TransactionResponse(
                String.valueOf(tx.getId()),
                tx.getOccurredAt().toLocalDate(),
                tx.getFlow(),
                tx.getCategory(),
                tx.getAmount(),
                tx.getDescription()
        );
    }

    private String resolveNote(CreateTransactionRequest request) {
        if (request.getNote() != null) {
            return request.getNote();
        }
        return request.getDescription();
    }
}
