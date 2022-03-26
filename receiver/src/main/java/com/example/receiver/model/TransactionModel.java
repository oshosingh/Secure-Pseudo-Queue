package com.example.receiver.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "transaction")
@NoArgsConstructor
@Getter
@Setter
public class TransactionModel {
	@Id
	private String accountNumber;
	private String type;
	private String amount;
	private String currency;
	private String accountFrom;
}
