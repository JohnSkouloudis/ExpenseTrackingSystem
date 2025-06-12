package com.example.expensetrackingsystem.components;

import com.example.expensetrackingsystem.entities.Transaction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TransactionCreationEvent extends ApplicationEvent {

    private final Transaction transaction;
    private final int userId;


    public TransactionCreationEvent(Object source, Transaction transaction, int userId) {
        super(source);
        this.transaction = transaction;
        this.userId = userId;
    }
}
