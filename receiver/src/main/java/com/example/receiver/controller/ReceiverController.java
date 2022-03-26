package com.example.receiver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.receiver.model.TransactionDto;
import com.example.receiver.service.impl.ReceiverServiceImpl;

@RestController
public class ReceiverController {
	
	@Autowired
	private ReceiverServiceImpl receiverServiceImpl;
	
	@PostMapping("/api/encrypted/data")
	ResponseEntity<Object> saveData(@RequestBody TransactionDto transactionDto){
		return receiverServiceImpl.saveDataInDb(transactionDto);
	}
	
	@GetMapping("/api/get/all/transactions")
	ResponseEntity<Object> allTransactions(){
		return receiverServiceImpl.getAllTransactions();
	}
}
