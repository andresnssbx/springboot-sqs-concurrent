package com.andresnss.sqsconcurrent.infrastructure.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.andresnss.sqsconcurrent.domain.model.Message;
import com.andresnss.sqsconcurrent.domain.service.IMessageRepository;
import com.andresnss.sqsconcurrent.infrastructure.messaging.SqsClientWrapper;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
@Slf4j
public class SqsMessageRepository implements IMessageRepository{

    
    private AtomicInteger backoffTime = new AtomicInteger(1); // En segundos
    private static final int MAX_BACKOFF_TIME = 32; // En segundos

     @Value("${cloud.aws.sqs.messages-per-request}")
     private int messagesPerRequest;

    private SqsClientWrapper sqsClientWrapper;

    public SqsMessageRepository(SqsClientWrapper sqsClientWrapper){
        this.sqsClientWrapper = sqsClientWrapper;
    }

    @Override
    public CompletableFuture<List<Message>> fetchMessages() {
        return CompletableFuture.supplyAsync(() -> sqsClientWrapper.receiveMessages(messagesPerRequest));
    }

    @Override
    public void handleEmptyResponse() {
        try {
            int currentBackoffTime = backoffTime.get();
            log.info("No messages received, backing off for {} seconds.", currentBackoffTime);
            TimeUnit.SECONDS.sleep(currentBackoffTime);
            if (currentBackoffTime < MAX_BACKOFF_TIME) {
                backoffTime.set(Math.min(currentBackoffTime * 2, MAX_BACKOFF_TIME));
            }
        } catch (InterruptedException e) {
            log.error("Thread was interrupted during backoff", e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("An unexpected error occurred during backoff", e);
        }
    }

    @Override
    public void resetBackoffTime() {
        backoffTime.set(1);
    }

    @Override
    public int getVisibleMessageCount() {
        return sqsClientWrapper.getApproximateNumberOfMessages();
    }

    @Override
    public void deleteMessage(String receiptHandle) {
        sqsClientWrapper.deleteMessage(receiptHandle);
    }


}
