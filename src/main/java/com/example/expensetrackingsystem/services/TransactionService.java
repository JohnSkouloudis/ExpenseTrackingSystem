package com.example.expensetrackingsystem.services;

import com.example.expensetrackingsystem.components.TransactionCreationEvent;
import com.example.expensetrackingsystem.dto.AccountDTO;
import com.example.expensetrackingsystem.dto.TransactionDTO;
import com.example.expensetrackingsystem.entities.*;
import com.example.expensetrackingsystem.repositories.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
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

    private final ScheduledTransactionRepository scheduledTransactionRepository;

    private final ApplicationEventPublisher eventPublisher;

    private final UserRepository userRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, CategoryRepository categoryRepository,
                              AccountRepository accountRepository, ScheduledTransactionRepository scheduledTransactionRepository,
                              ApplicationEventPublisher eventPublisher,UserRepository userRepository ) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
        this.accountRepository = accountRepository;
        this.scheduledTransactionRepository = scheduledTransactionRepository;
        this.eventPublisher = eventPublisher;
        this.userRepository = userRepository;
    }

    @Transactional
    public List<ScheduledTransaction> getScheduledTransactions(int userId){

       Optional <List<ScheduledTransaction>> scheduledTransactions = scheduledTransactionRepository.findByUserId(userId);

        if (scheduledTransactions.isPresent()) {
            return scheduledTransactions.get();
        }else {
            return new ArrayList<>();
        }

    }

    @Transactional
    public void saveScheduledTransaction(ScheduledTransaction scheduledTransaction,int userId) {

        if (scheduledTransaction == null) {
            throw new IllegalArgumentException("Scheduled transaction cannot be null");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        scheduledTransaction.setUser(user);

        Account account = accountRepository.findById(scheduledTransaction.getAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        Category category = categoryRepository.findByCategoryName(scheduledTransaction.getCategoryName().toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        scheduledTransactionRepository.save(scheduledTransaction);

    }
    public void deleteScheduledTransaction(int scheduledTransactionId) {

        ScheduledTransaction scheduledTransaction = scheduledTransactionRepository.findById(scheduledTransactionId)
                .orElseThrow(() -> new IllegalArgumentException("Scheduled transaction not found"));

        scheduledTransactionRepository.delete(scheduledTransaction);
    }

    public ScheduledTransaction getScheduledTransactionByIdAndUserId(int scheduledTransactionId,int userId) {

        return  scheduledTransactionRepository.findByIdAndUserId(scheduledTransactionId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Scheduled transaction not found"));


    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void addScheduledTransactions(){
        LocalDate now = LocalDate.now();
        System.out.println("adding scheduled transactions for " + now);

        List<ScheduledTransaction> scheduledTransactions = scheduledTransactionRepository.findByExecutionDate(now);

        if (scheduledTransactions.isEmpty()) {
            System.out.println("No scheduled transactions found for " + now);
            return;
        }

        for( ScheduledTransaction scheduledTransaction : scheduledTransactions) {

            TransactionDTO transaction = new TransactionDTO(
                    0,
                    scheduledTransaction.getAmount(),
                    scheduledTransaction.getDescription(),
                    now,
                    scheduledTransaction.getAccountId(),
                    scheduledTransaction.getCategoryName());

            saveTransaction(transaction);

            scheduledTransaction.setExecutionDate( scheduledTransaction.getExecutionDate().plusDays(scheduledTransaction.getFrequencyInDays()));
            scheduledTransactionRepository.save(scheduledTransaction);

        }
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

        eventPublisher.publishEvent(new TransactionCreationEvent(this,save,account.getUser().getId()) );

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
