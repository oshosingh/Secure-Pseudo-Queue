# *** Note
  While accessing the api, if you get connection refused, please mail at oshosingh25@gmail.com.
  I will restart the server as soon as possible and will notify you. I have used elastic ip for 
  ec2 so nothing will change in the process.
  Thank you.

# Secure-Pseudo-Queue
Implemented a Pseudo Queue that acts as a fake queue.

## Tech Stack
1. Spring Boot
2. H2 In memory Database
3. JPA
4. AWS EC2 for deploying the application

## Folder Structure
### 1. Server/  
    -> POST API URL = http://43.204.8.156:8080/api/transaction/data  (accepts = transaction object in json format) 
    -> Created an API for accepting transaction object from the user. 
    -> Using AES encryption algorithm with Initialization verctor to increase security by making the pattern complex for encrypting the fields. 
    -> Using RestTemplate class for making an api request to the fake queue service. 

### 2. receiever/ (Fake Queue Application) <br />
    -> POST API URL - http://43.204.8.156:8081/api/encrypted/data   (accepts = encrypted transaction object in json format) 
    -> Created an API for accepting the encrypted transaction object from server application. 
    -> Decrypting AES encrypted transaction object. 
    -> After decryption, saving the transaction object in H2 database using JPA. 
   
# Extras: <br />
Exposed an api to get the list of all saved transactions. 
API URL = http://43.204.8.156:8081/api/get/all/transactions
This api can be used to check if the data was saved in the db or not.


### Build Steps
    -> install Java runtime version 11
    -> install mvn binary
    -> To build the application, mvn clean install
    -> After build do below steps to run the application:
        For server application -> java -jar target/server-0.0.1-SNAPSHOT.jar
        For receiver application (fake queue app) -> java -jar target/receiver-0.0.1-SNAPSHOT.jar
        
