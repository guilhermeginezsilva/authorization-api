# Authorization API

**Challenge:** Design and implement a RESTful API for online transactions authorization.

#### Prerequisites

This version of this project is designed to run locally and in a docker container. All the steps below are dedicated to run the application locally, but you can try out an deployed version of this application in AWS with docker container:

http://3.92.212.39:5000/ -> REST
http://3.92.212.39:5001/ -> TCP Socket

To run this project you need to have the following applications:

**important**: Below each requirement, I'm noting the version I've used during the development.
	
1. Java 8

	My version: java version "1.8.0_121"
	
	To check if you have installed just type in you console:
	java -version
	
2. Apache Maven

	My version: Apache Maven 3.5.4 (1edded0938998edf8bf061f1ceb3cfdeccf443fe; 2018-06-17T15:33:14-03:00)
	
	To check if you have installed just type in you console:
	mvn --version

#### Clone project

    ###To clone it just type in your console the command below on the desired directory:
    git clone https://github.com/guilhermeginezsilva/authorization-api.git

#### Tests
This project contains tests to validate all the three layers: 

Controllers, Services and Repositories

    ###On the root directory of the project, just type in console:
    mvn test
    
#### Running
There are 2 options to run this project:

1. Build and Run:

    ###Just navigate to the cloned directory run the command below in the console:
    mvn spring-boot:run

    
2. Run the executable:
        
    ###Just navigate to the releases directory inside the project and run the command below:
    java -jar transaction-auth-api-1.0.0.jar
    
### It's Running!! And now?
Well, running the tests you can check that the application is working correctly, but we know that there isn't nothing better than having some fun with these projects, so you can access it running on localhost or you can access the AWS deployed application:

Site: http://3.92.212.39:5000/

Server routes to check last 10 transactions and balance of a card:
* http://3.92.212.39:5000/v1/transactions/cards/1234567890123456 -> Card with an amount of 1000.00
* http://3.92.212.39:5000/v1/transactions/cards/1234567890123457 -> Card with an amount of 1000.00
* http://3.92.212.39:5000/v1/transactions/cards/1234567890123458 -> Card with an amount of 50000.00

Server TCP Socket server to send transactions:
http://3.92.212.39:5001

Each block of data must always be sent with this format:
L: 4 digits with the data content length (Length of the data after converted to Hexadecimal String)
D: Data content converted to hexadecimal string (utf8)

Example:
0010FFFFFFFFFF
LLLLDDDDDDDDDD -> L = Length / D = JSON DATA CONVERTED TO HEXADECIMAL STRING
