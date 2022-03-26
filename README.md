# Secure-Pseudo-Queue
Implemented a Pseudo Queue that acts as a fake queue.

## Tech Stack
1. Spring Boot
2. H2 In memory Database
3. JPA

## Folder Structure
### 1. Server/  
    -> POST API URL = http://127.0.0.1:8080/api/transaction/data  (accepts = transaction object in json format) <br />
    -> Created an API for accepting transaction object from the user. <br />
    -> Using AES encryption algorithm for encrypting the fields. <br />
    -> Using RestTemplate class for making an api request to the fake queue service. <br />

### 2. receiever/ (Fake Queue Application) <br />
    -> POST API URL - http://127.0.0.1:8081/api/encrypted/data   (accepts = encrypted transaction object in json format) <br />
    -> Created an API for accepting the encrypted transaction object from server application. <br />
    -> Decrypting AES encrypted transaction object. <br />
    -> After decryption, saving the transaction object in H2 database using JPA. <br />
   
Extras: <br />
Exposed an api to get the list of all saved transactions. 
API URL = http://127.0.0.1:8081/api/get/all/transactions
