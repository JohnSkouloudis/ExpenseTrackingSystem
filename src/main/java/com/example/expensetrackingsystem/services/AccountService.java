package com.example.expensetrackingsystem.services;

import com.example.expensetrackingsystem.dto.AccountDTO;
import com.example.expensetrackingsystem.dto.TransactionDTO;
import com.example.expensetrackingsystem.entities.Account;
import com.example.expensetrackingsystem.entities.Transaction;
import com.example.expensetrackingsystem.entities.User;
import com.example.expensetrackingsystem.repositories.AccountRepository;
import com.example.expensetrackingsystem.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    private final UserRepository userRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void saveAccount(AccountDTO accountdto) {


        User user = userRepository.findById(accountdto.userId()).orElseThrow(() -> new IllegalArgumentException("User not found"));

        Account account = new Account();
        account.setUser(user);
        account.setAccountName(accountdto.accountName());
        account.setBalance(accountdto.balance());

        accountRepository.save(account);
    }

    @Transactional
    public Page<AccountDTO> getAccountsByUser(int userId, Pageable pageable) {

        Page<Account> accounts= accountRepository.findByUserId(userId, pageable);

        return  accounts.map(this::toDto);
    }

    @Transactional
    public List<AccountDTO> getAccountsByUser(int userId) {

        List<Account> accounts= accountRepository.findByUserId(userId);

        return  accounts.stream()
                .map(this::toDto)
                .toList();
    }

    // TODO: implement this method
    @Transactional
    public void deleteAccount(Account account) {
        accountRepository.delete(account);
    }

    // TODO: implement this method
    @Transactional
    public void updateAccount(int accountId, AccountDTO accountdto) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new IllegalArgumentException("Account not found"));

        account.setAccountName(accountdto.accountName());
        account.setBalance(accountdto.balance());

        accountRepository.save(account);
    }

    public AccountDTO toDto(Account account) {
        return new AccountDTO(
                account.getId(),
                account.getBalance(),
                account.getAccountName(),
                account.getUser().getId()

        );


    }






}
