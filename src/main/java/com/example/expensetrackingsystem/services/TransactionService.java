package com.example.expensetrackingsystem.services;

import com.example.expensetrackingsystem.entities.Account;
import com.example.expensetrackingsystem.entities.Category;
import com.example.expensetrackingsystem.entities.Transaction;
import com.example.expensetrackingsystem.repositories.CategoryRepository;
import com.example.expensetrackingsystem.repositories.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    private final CategoryRepository categoryRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, CategoryRepository categoryRepository) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
    }

    // Save a transaction
    @Transactional
    public void saveTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
    }

    // Get a page of transactions for a specific account
    @Transactional
    public Page<Transaction> getAccountTransactions(int accountId, Pageable pageable) {

        return transactionRepository.findByAccountId(accountId, pageable);
    }

    // Get a list of transactions for a specific account
    @Transactional
    public List<Transaction> getAccountTransactions(int accountId) {

        return transactionRepository.findByAccountId(accountId);
    }

    //Get a list  of transactions for a specific category
    @Transactional
    public List<Transaction> getTransactionsByCategory(String categoryName) {

        Optional<Category> category = categoryRepository.findByCategoryName(categoryName.toUpperCase());

        if (category.isPresent()) {
            return transactionRepository.findByCategory(category.get());
        }else{
            return null;
        }


    }

 // delete a transaction
    @Transactional
    public void  deleteTransaction(Transaction transaction) {
        transactionRepository.delete(transaction);
    }

}
