package com.example.expensetrackingsystem.services;

import com.example.expensetrackingsystem.entities.Account;
import com.example.expensetrackingsystem.entities.Transaction;
import com.example.expensetrackingsystem.repositories.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public void saveTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
    }

    @Transactional
    public Page<Transaction> getAccountTransactions(Account account, Pageable pageable) {

        return transactionRepository.findByAccount(account, pageable);

    }

    @Transactional
    public Page<Transaction> getTransactions(Pageable pageable) {
        return transactionRepository.findAll(pageable);
    }

    @Transactional
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

}
