package com.example.server.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@Getter
@Setter
public class TransactionModel {
	private String accountNumber;
	private String type;
	private String amount;
	private String currency;
	private String accountFrom;
}
