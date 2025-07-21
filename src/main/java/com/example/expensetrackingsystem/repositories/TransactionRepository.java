package com.example.expensetrackingsystem.repositories;

import com.example.expensetrackingsystem.entities.Account;
import com.example.expensetrackingsystem.entities.Category;
import com.example.expensetrackingsystem.entities.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    Page<Transaction> findByAccount(Account account, Pageable pageable);
    List<Transaction> findByAccount(Account account);

    List<Transaction> findByCategory(Category category);

    List<Transaction> findByAccountId(int accountId);
    Page<Transaction> findByAccountId(int accountId, Pageable pageable);

    List<Transaction> findByAccountIdAndDateBetween(int accountId, LocalDate from, LocalDate to);

    long countByCategoryAndAccountIn(Category category, List<Account> accounts);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.category = :category AND t.account IN :accounts")
    Optional<Long> sumAmountByCategoryAndAccountIn(@Param("category") Category category, @Param("accounts") List<Account> accounts);



}
