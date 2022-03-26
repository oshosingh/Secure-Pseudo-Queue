package com.example.receiver.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.receiver.model.TransactionModel;

public interface TransactionRepository extends JpaRepository<TransactionModel, String>{

}
