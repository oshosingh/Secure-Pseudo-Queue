package com.example.server.service;

import org.springframework.http.ResponseEntity;

import com.example.server.model.TransactionModel;

public interface TransactionHandlerService {

	ResponseEntity<Object> saveTransactionDataInDb(TransactionModel transactionData);
}
