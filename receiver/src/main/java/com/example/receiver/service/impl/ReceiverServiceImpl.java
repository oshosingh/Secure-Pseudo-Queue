package com.example.receiver.service.impl;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.receiver.model.TransactionDto;
import com.example.receiver.model.TransactionModel;
import com.example.receiver.repository.TransactionRepository;
import com.example.receiver.service.ReceiverService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReceiverServiceImpl implements ReceiverService {

	@Value("${encryptuon.password}")
	private String encryptionPassword;

	@Value("${encryption.salt}")
	private String encryptionSalt;
	
	@Autowired
	private TransactionRepository transactionRepository;

	@Override
	public ResponseEntity<Object> saveDataInDb(TransactionDto transactionDto) {
		try {
			String ivParam = transactionDto.getIvParam();
			byte[] iv = Base64.decodeBase64(ivParam);
			
			IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

			decryptTransactionData(transactionDto, ivParameterSpec);
			
			TransactionModel model = decryptTransactionData(transactionDto, ivParameterSpec);
			
			// Save decrypted data in H2 in memory Database
			transactionRepository.saveAndFlush(model);
			
			log.info("Transaction data saved in db");
			
			return ResponseEntity.ok().body(model);

		} catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | NoSuchPaddingException
				| InvalidAlgorithmParameterException | BadPaddingException | IllegalBlockSizeException e) {
			log.error("Exception occured : {}", e.getMessage());
			return null;
		}

	}

	private TransactionModel decryptTransactionData(TransactionDto transactionDto, IvParameterSpec ivParameterSpec)
			throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, NoSuchPaddingException,
			InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException {

		SecretKey key = getKeyFromPassword();
		String algorithm = "AES/CBC/PKCS5Padding";
		
		TransactionModel transactionModel = new TransactionModel();
		transactionModel.setAccountFrom(decrypt(algorithm, transactionDto.getAccountFrom(), key, ivParameterSpec));
		transactionModel.setAccountNumber(decrypt(algorithm, transactionDto.getAccountNumber(), key, ivParameterSpec));
		transactionModel.setAmount(decrypt(algorithm, transactionDto.getAmount(), key, ivParameterSpec));
		transactionModel.setCurrency(decrypt(algorithm, transactionDto.getCurrency(), key, ivParameterSpec));
		transactionModel.setType(decrypt(algorithm, transactionDto.getType(), key, ivParameterSpec));
		
		return transactionModel;
	}

	private SecretKey getKeyFromPassword() throws NoSuchAlgorithmException, InvalidKeySpecException {
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		KeySpec spec = new PBEKeySpec(encryptionPassword.toCharArray(), encryptionSalt.getBytes(), 65536, 256);
		SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
		return secret;
	}
	
	private String decrypt(String algorithm, String cipherText, SecretKey key, IvParameterSpec iv)
			throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
			InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

		Cipher cipher = Cipher.getInstance(algorithm);
		cipher.init(Cipher.DECRYPT_MODE, key, iv);
//		    byte[] plainText = cipher.doFinal(Base64.getDecoder()
//		        .decode(cipherText));
		byte[] plainText = cipher.doFinal(Base64.decodeBase64(cipherText));
		return new String(plainText);
	}

	@Override
	public ResponseEntity<Object> getAllTransactions() {
		List<TransactionModel> allTransactions = transactionRepository.findAll();
		return ResponseEntity.ok().body(allTransactions);
	}

}
