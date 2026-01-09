package com.aifa.finance.repository;

import com.aifa.finance.domain.Transaction;
import com.aifa.finance.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserOrderByTransactionDateDesc(User user);
    List<Transaction> findByUserAndTransactionDateBetween(User user, LocalDate start, LocalDate end);
    List<Transaction> findByUserAndCategory(User user, String category);
}
