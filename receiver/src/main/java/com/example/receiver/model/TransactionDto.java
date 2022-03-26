package com.example.receiver.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionDto {
	private String accountNumber;
	private String type;
	private String amount;
	private String currency;
	private String accountFrom;
	private String ivParam;
}
