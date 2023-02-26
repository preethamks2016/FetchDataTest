# Fetch Rewards Coding Exercise
Take home assessment as part of Fetch Rewards Summer 2023 internship
Exercise at: https://fetch-hiring.s3.amazonaws.com/data-engineer/pii-masking.pdf

## Installation Instructions:
The program is written in **Java** language with **Maven** as the build tool. You will need to setup Java and Maven as mentioned below in order to compile and run the program.
### Prerequisites
Before you install Maven, you should ensure Java Development Kit (JDK) is installed in your computer. Maven requires a JDK to be installed on your machine to function. You can download the latest JDK from the official Oracle website at https://www.oracle.com/java/technologies/javase-downloads.html. Maven 3.9+ requires JDK 8 or above to execute.

Once you have ensured that you have a JDK installed, and have set the **JAVA_HOME** environment variable pointing to your JDK installation, you can proceed with the steps to install Maven.

### Install Maven
- You can download the latest version of Maven from here: https://maven.apache.org/download.cgi.
- Steps to install Maven can be found here: https://maven.apache.org/install.html

After following these steps verify if Maven has been installed properly by running the below command in a new shell:
```
mvn -v
```
The result should look similar to:
```
Apache Maven 3.9.0 (9b58d2bad23a66be161c4664ef21ce219c2c8584)
Maven home: /opt/apache-maven-3.9.0
Java version: 1.8.0_45, vendor: Oracle Corporation
Java home: /Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/jre
Default locale: en_US, platform encoding: UTF-8
OS name: "mac os x", version: "10.8.5", arch: "x86_64", family: "mac"
```

You will also need the following installed on your local machine
- docker -- https://docs.docker.com/get-docker/
- docker-compose -- https://docs.docker.com/compose/gettingstarted/
- pip install awscli-local
- pip install aws-cli 
- Psql - install https://www.postgresql.org/download/

Docker images have been given as part of the project
Pull the images by running the following commands:
```agsl
docker pull fetchdocker/data-takehome-postgres
docker pull fetchdocker/data-takehome-localstack
```

## Steps to Run:

### Command Instructions
Clone this repository and 'cd' into the project working directory - **FetchDataTest**

Configure AWS properties by running below script:
```agsl
./aws_configure.sh
```
In the same directory ***docker-compose.yml*** is present. Run:
```agsl
docker compose up
```
This will bring up the localstack and postgres services.

Verify is localstack is working properly by reading a message from the queue:
```agsl
awslocal sqs receive-message --queue-url http://localhost:4566/000000000000/login-queue
```
Connect to postgres and check:
```agsl
 psql -d postgres -U postgres -p 5432 -h localhost -W
//password: postgres
 postgres=# \dt //list the tables
```
Run the below command to compile and execute our Java program:
```
mvn compile exec:java -Dexec.mainClass="org.fetch.FetchTest" 
```
FetchTest.java is the main class of our program.

You should an output saying ***"A new row has been inserted."*** above the ***BUILD SUCCESS*** message as shown here:
```agsl
[INFO] --- exec:3.1.0:java (default-cli) @ FetchDataTest ---
A new row has been inserted.
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  3.505 s
[INFO] Finished at: 2023-02-26T11:00:22-06:00
[INFO] ------------------------------------------------------------------------
```
## Next Steps
- handle nullable values
- can add a validator to check the character limits, datatypes before inserting to table
- integer concat - change to float
- can use better masking such as SHA
- writing can be done in parallel using a threadpool
- mapping from json to object can be done better with Named annotations
- better packaging
- modules for service, client, data , mapper etc
  arguments / config file for AWS, postgres etc


## Questions
How would you deploy this application in production?
- docker kubernetes 

What other components would you want to add to make this production ready?
- docker kubernetes

How can this application scale with a growing dataset.
- spark - transformations

How can PII be recovered later on?
- base64 decoding

What are the assumptions you made?
- sticking to existing user_logins table schema