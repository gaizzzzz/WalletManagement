package com.example.managewallet.repository;

import com.example.managewallet.domain.WalletTransaction;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {

    List<WalletTransaction> findByOccurredAtGreaterThanEqualAndOccurredAtLessThanOrderByOccurredAtDescIdDesc(
            LocalDateTime start,
            LocalDateTime end
    );

    List<WalletTransaction> findTop10ByOrderByOccurredAtDescIdDesc();
}
