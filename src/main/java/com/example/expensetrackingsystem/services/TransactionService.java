package com.example.expensetrackingsystem.services;

import com.example.expensetrackingsystem.dto.TransactionDTO;
import com.example.expensetrackingsystem.entities.Account;
import com.example.expensetrackingsystem.entities.Category;
import com.example.expensetrackingsystem.entities.Transaction;
import com.example.expensetrackingsystem.repositories.AccountRepository;
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

    private final AccountRepository accountRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, CategoryRepository categoryRepository,
                              AccountRepository accountRepository ) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
        this.accountRepository = accountRepository;
    }

    // Save a transaction
    @Transactional
    public void saveTransaction(TransactionDTO transactiondto) {

        Category category = categoryRepository.findByCategoryName(transactiondto.categoryName().toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        Account account = accountRepository.findById(transactiondto.accountId()).orElseThrow(() -> new IllegalArgumentException("Account not found"));

        Transaction transaction = new Transaction();
         transaction.setCategory(category);
         transaction.setAccount(account);
         transaction.setAmount(transactiondto.amount());
         transaction.setDescription(transactiondto.description());
         transaction.setDate(transactiondto.date());

        transactionRepository.save(transaction);
    }

    // Get a page of transactions for a specific account
    @Transactional
    public Page<TransactionDTO> getAccountTransactions(int accountId, Pageable pageable) {

          Page<Transaction> transactions= transactionRepository.findByAccountId(accountId, pageable);

           return  transactions.map(this::toDto);
    }

    // Get a list of transactions for a specific account
    @Transactional
    public List<TransactionDTO> getAccountTransactions(int accountId) {

        List<Transaction> transactions = transactionRepository.findByAccountId(accountId);

        return transactions.stream()
                .map(this::toDto)
                .toList();
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


    // turn Transaction to TransactionDTO
    public TransactionDTO toDto(Transaction transaction) {
        return new TransactionDTO(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getDate(),
                transaction.getAccount().getId(),
                transaction.getCategory().getCategoryName()
        );


    }

}
