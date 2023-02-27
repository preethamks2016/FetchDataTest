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
- pip install awscli 
- Psql - install https://www.postgresql.org/download/

Docker images have been given as part of the project
Pull the images by running the following commands:
```agsl
docker pull fetchdocker/data-takehome-postgres
docker pull fetchdocker/data-takehome-localstack
```

## Steps to Run:

### Command Instructions
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
### Main program
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
- can use better masking algorithms -like SHA hashing etc
- inserting records to postgres db can be done in-parallel asynchronously by using ThreadPool Executor.
- Values such as postgres url, aws url etc can be provided as arguments or reading from a config file.
- Can add a validator to validate the data such as checking the character limits, null values, datatypes etc, before inserting to table.
- Mapping from json to object can be done better by naming the fields using @JsonProperty annotations
- better packaging of the service with modules for service, postgres client, data transfer objects, mapper etc

## Questions
How would you deploy this application in production?
- We would need to setup a build and deploy pipeline. For example Jenkins can be used to build, compile and push the executable/jar
to some repository. Deployment can be done using scripting tools such as ansible, chef, puppet to copy the executables to the VMs and run them.
Additionally the VMs would need to have the prerequisites installed which can again be done using ansible scripts. We could also dockerise the application and deploy it on a Kubernetes cluster.
- A better suited approach would to write a spark application and deploy our application on a Spark cluster. This would require setting up a spark cluster 
and submitting your application using spark-submit. We'll need to provide the path to the JAR file and any additional configuration options the application requires.

What other components would you want to add to make this production ready?
- We can dockerise the application. We would need write a DockerFile using Java / Maven as base image with steps to compile and execute program.
- We would need to write a helm chart to deploy it on Kubernetes. And write config files for connecting to upstream and downstream services such as 
AWS SQS, postgres database.
- Separate modules for service, postgres client, data transfer objects
- Incorporate logging, version control, running the application under different load scenarios to ensure that it can handle high traffic.

How can this application scale with a growing dataset.
- This application is best suited for spark since we are basically doing a stateless data transformation.
- By deploying it on a spark cluster we should be able to scale easily by adding new executor nodes to the cluster.
- If we use Kubernetes, auto-scaling policies can be setup easily in Kubernetes via helm charts.
- We are assuming that the input SQS queue is well partitioned and we are able to have a good read throughput.
- Additionally, we can parallelly insert records using multi-threading.

How can PII be recovered later on?
- We can use a Base64 decoder to decode the masked values: ***Base64.getDecoder().decode(encodedString)***
- We can write a script to read data from postgres and decode the masked values.

What are the assumptions you made?
- I am adhering to the existing **user_logins** table schema. That has integer field for the appversion. Since appversion is 
a string I am removing the dots (delimiter) and concatenating the values to make it an integer.