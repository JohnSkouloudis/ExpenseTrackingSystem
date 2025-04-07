package com.example.expensetrackingsystem.repositories;

import com.example.expensetrackingsystem.entities.Account;
import com.example.expensetrackingsystem.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

    Page<Account> findByUser(User user, Pageable pageable);
}
