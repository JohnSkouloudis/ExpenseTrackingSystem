package com.example.expensetrackingsystem.repositories;

import com.example.expensetrackingsystem.entities.Account;
import com.example.expensetrackingsystem.entities.Category;
import com.example.expensetrackingsystem.entities.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    Page<Transaction> findByAccount(Account account, Pageable pageable);
    List<Transaction> findByAccount(Account account);

    List<Transaction> findByCategory(Category category);

    List<Transaction> findByAccountId(int accountId);
    Page<Transaction> findByAccountId(int accountId, Pageable pageable);

    List<Transaction> findByAccountIdAndDateBetween(int accountId, LocalDate from, LocalDate to);


}
