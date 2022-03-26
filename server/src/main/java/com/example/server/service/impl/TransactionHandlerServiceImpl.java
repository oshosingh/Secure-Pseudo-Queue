package com.example.server.service.impl;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.server.model.TransactionDto;
import com.example.server.model.TransactionModel;
import com.example.server.service.TransactionHandlerService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TransactionHandlerServiceImpl implements TransactionHandlerService {

	@Value("${encryptuon.password}")
	private String encryptionPassword;

	@Value("${encryption.salt}")
	private String encryptionSalt;

	@Value("${receiver.endpoint.url}")
	private String queueApiEndpointUrl;

	@Autowired
	private RestTemplate restTemplate;

	@Override
	public ResponseEntity<Object> saveTransactionDataInDb(TransactionModel transactionData) {
		try {

			byte[] iv = new byte[16];
			new SecureRandom().nextBytes(iv);
			
			IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
			encryptTransactionData(transactionData, ivParameterSpec);
			
			String response = sendDataToQueueServer(transactionData, iv);

			return ResponseEntity.ok().body("Data Saved Successfully");
		} catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeySpecException
				| InvalidAlgorithmParameterException | InvalidKeyException | IllegalBlockSizeException
				| BadPaddingException e) {
			log.error("Exception occured with message : {}", e.getMessage());
			e.printStackTrace();
			return ResponseEntity.status(500).body(e.getMessage());
		}
	}

	private void encryptTransactionData(TransactionModel transactionData, IvParameterSpec ivParameterSpec)
			throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, NoSuchPaddingException,
			InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException {
		SecretKey key = getKeyFromPassword();
		String algorithm = "AES/CBC/PKCS5Padding";

		transactionData.setAccountFrom(encrypt(algorithm, transactionData.getAccountFrom(), key, ivParameterSpec));
		transactionData.setAccountNumber(encrypt(algorithm, transactionData.getAccountNumber(), key, ivParameterSpec));
		transactionData.setAmount(encrypt(algorithm, transactionData.getAmount(), key, ivParameterSpec));
		transactionData.setCurrency(encrypt(algorithm, transactionData.getCurrency(), key, ivParameterSpec));
		transactionData.setType(encrypt(algorithm, transactionData.getType(), key, ivParameterSpec));

	}

	private SecretKey getKeyFromPassword() throws NoSuchAlgorithmException, InvalidKeySpecException {
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		KeySpec spec = new PBEKeySpec(encryptionPassword.toCharArray(), encryptionSalt.getBytes(), 65536, 256);
		SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
		return secret;
	}
	
	private static String encrypt(String algorithm, String input, SecretKey key, IvParameterSpec iv)
			throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
			InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

		Cipher cipher = Cipher.getInstance(algorithm);
		cipher.init(Cipher.ENCRYPT_MODE, key, iv);
		byte[] cipherText = cipher.doFinal(input.getBytes());
		return new String(Base64.encodeBase64(cipherText));
	}

	private String sendDataToQueueServer(TransactionModel transactionData, byte[] iv) {

		String ivParam = Base64.encodeBase64String(iv);
		
		TransactionDto transactionDto = new TransactionDto();
		transactionDto.setAccountFrom(transactionData.getAccountFrom());
		transactionDto.setAccountNumber(transactionData.getAccountNumber());
		transactionDto.setAmount(transactionData.getAmount());
		transactionDto.setCurrency(transactionData.getCurrency());
		transactionDto.setIvParam(ivParam);
		transactionDto.setType(transactionData.getType());

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		// TransactionDto is payload for queue server

		HttpEntity<TransactionDto> entity = new HttpEntity<>(transactionDto, headers);

		try {
			ResponseEntity<String> responseFromQueueServer = restTemplate.exchange(queueApiEndpointUrl, HttpMethod.POST,
					entity, String.class);
			return responseFromQueueServer.getBody();
		} catch (Exception e) {
			log.error("Exception occured in sendDataToQueueServer method with message : {}", e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

}
