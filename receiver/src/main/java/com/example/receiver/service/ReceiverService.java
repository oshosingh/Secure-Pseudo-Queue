package com.example.receiver.service;

import org.springframework.http.ResponseEntity;

import com.example.receiver.model.TransactionDto;

public interface ReceiverService {
	ResponseEntity<Object> saveDataInDb(TransactionDto transactionDto);
	ResponseEntity<Object> getAllTransactions();
}
