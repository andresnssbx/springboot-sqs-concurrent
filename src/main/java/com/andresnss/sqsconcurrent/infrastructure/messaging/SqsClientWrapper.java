package com.andresnss.sqsconcurrent.infrastructure.messaging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.andresnss.sqsconcurrent.domain.model.Message;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.QueueAttributeName;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SqsClientWrapper {

    @Value("${cloud.aws.sqs.queue-url}")
    private String queueUrl;

    @Value("${cloud.aws.sqs.wait-time-seconds}")
    private int waitTimeSeconds; 

    private SqsClient sqsClient;

    public SqsClientWrapper(SqsClient sqsClient){
        this.sqsClient = sqsClient;
    }

    public List<Message> receiveMessages(int maxMessages) {
        ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                .queueUrl(this.queueUrl)
                .maxNumberOfMessages(maxMessages)
                .waitTimeSeconds(this.waitTimeSeconds)
                .build();

        ReceiveMessageResponse response = sqsClient.receiveMessage(request);
        log.info("size-polling-message: {}", response.messages().size());
        return response.messages().stream()
                .map(m -> {
                    Message message = new Message();
                    message.setBody(m.body());
                    message.setReceiptHandle(m.receiptHandle());
                    return message;
                })
                .collect(Collectors.toList());
    }

    public void deleteMessage(String receiptHandle) {
        sqsClient.deleteMessage(b -> b.queueUrl(this.queueUrl).receiptHandle(receiptHandle));
    }

    public int getApproximateNumberOfMessages() {
        return Integer.parseInt(
            sqsClient.getQueueAttributes(b -> b.queueUrl(this.queueUrl).attributeNames(QueueAttributeName.APPROXIMATE_NUMBER_OF_MESSAGES))
                    .attributes().get(QueueAttributeName.APPROXIMATE_NUMBER_OF_MESSAGES)
        );
    }
}

