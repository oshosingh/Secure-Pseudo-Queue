package com.example.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.server.model.TransactionModel;
import com.example.server.service.impl.TransactionHandlerServiceImpl;

@RestController
public class UserInputController {
	
	@Autowired
	private TransactionHandlerServiceImpl transactionHandlerService;
	
	@PostMapping("/api/transaction/data")
	ResponseEntity<Object> processTransactionDataObject(@RequestBody TransactionModel transactionData){
		return transactionHandlerService.saveTransactionDataInDb(transactionData);
	}

}
