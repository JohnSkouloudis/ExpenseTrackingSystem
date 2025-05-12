package com.example.expensetrackingsystem.services;

import com.example.expensetrackingsystem.entities.Account;
import com.example.expensetrackingsystem.entities.User;
import com.example.expensetrackingsystem.repositories.AccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public void saveAccount(Account account) {
        accountRepository.save(account);
    }

    @Transactional
    public Page<Account> getAccountsByUser(User user, Pageable pageable) {
        return accountRepository.findByUser(user, pageable);
    }

    @Transactional
    public List<Account> getAccountsByUser(User user) {
        return accountRepository.findByUser(user);
    }

    @Transactional
    public void deleteAccount(Account account) {
        accountRepository.delete(account);
    }






}
