package org.fetch;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

public class FetchTest {

    static String AMAZON_SQS_URL = "http://localhost:4566";
    static String AMAZON_REGION = "us-east-1";
    static String AMAZON_SQS_QUEUE = "http://localhost:4566/000000000000/login-queue";
    static String POSTGRES_URL = "jdbc:postgresql://localhost:5432/postgres";
    static String POSTGRES_USER = "postgres";
    static String POSTGRES_PWD = "postgres";

    public static void main(String[] args) {

        // Initialise Postgres client
        PostgresClient postgresClient = new PostgresClient(POSTGRES_URL, POSTGRES_USER, POSTGRES_PWD);

        // AmazonSQS
        AmazonSQS sqs = AmazonSQSClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(AMAZON_SQS_URL, AMAZON_REGION))
                .build();
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(AMAZON_SQS_QUEUE);

        List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
        for (Message message : messages) {
            String msgJson = message.getBody();
            ObjectMapper om = new ObjectMapper();
            UserRecord record;
            try {
                // map Json to Object
                record = om.readValue(msgJson, UserRecord.class);
                // insert row to
                postgresClient.insertRecord(record);
            } catch (JsonProcessingException e) {
                System.out.println("An error while converting Json: " + msgJson);
                e.printStackTrace();
            } catch (SQLException ex) {
                System.out.println("An error occurred while inserting the row: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
}