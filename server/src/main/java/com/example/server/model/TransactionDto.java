package com.example.server.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TransactionDto {
	private String accountNumber;
	private String type;
	private String amount;
	private String currency;
	private String accountFrom;
	private String ivParam;
}
