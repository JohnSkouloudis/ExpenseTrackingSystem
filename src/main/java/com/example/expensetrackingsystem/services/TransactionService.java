package com.example.expensetrackingsystem.services;

import com.example.expensetrackingsystem.dto.AccountDTO;
import com.example.expensetrackingsystem.dto.TransactionDTO;
import com.example.expensetrackingsystem.entities.Account;
import com.example.expensetrackingsystem.entities.Category;
import com.example.expensetrackingsystem.entities.Transaction;
import com.example.expensetrackingsystem.repositories.AccountRepository;
import com.example.expensetrackingsystem.repositories.CategoryRepository;
import com.example.expensetrackingsystem.repositories.TransactionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
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
    public Transaction saveTransaction(TransactionDTO transactiondto) {

        Category category = categoryRepository.findByCategoryName(transactiondto.categoryName().toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        Account account = accountRepository.findById(transactiondto.accountId()).orElseThrow(() -> new IllegalArgumentException("Account not found"));

        Transaction transaction = new Transaction();
         transaction.setCategory(category);
         transaction.setAccount(account);
         transaction.setAmount(transactiondto.amount());
         transaction.setDescription(transactiondto.description());
         transaction.setDate(transactiondto.date());

        Transaction save = transactionRepository.save(transaction);

        return save;
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
    // TODO: implement this unfinished method
    @Transactional
    public List<Transaction> getTransactionsByCategory(String categoryName, int accountId) {

        // Find the account by ID
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        Optional<Category> category = categoryRepository.findByCategoryName(categoryName.toUpperCase());

        if (category.isPresent()) {

            return transactionRepository.findByCategory(category.get());
        }else{
            return null;
        }


    }

    public TransactionDTO getTransactionDetails(int transactionId){

            Transaction transaction = transactionRepository.findById(transactionId).orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

            return toDto(transaction);
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

    public List<TransactionDTO> findByDateBetween(LocalDate from, LocalDate to, List<AccountDTO> accounts){

        List<TransactionDTO> results = new ArrayList<>();

        for(AccountDTO account : accounts){
            List<Transaction> transactions = transactionRepository.findByAccountIdAndDateBetween(account.getId(),from, to );

            for(Transaction transaction : transactions){
                results.add(toDto(transaction));
            }

        }
        return results;

    }

    public TransactionDTO convertToTransactionDto(String transactiDtoString) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper.readValue(transactiDtoString, TransactionDTO.class);
    }

}
