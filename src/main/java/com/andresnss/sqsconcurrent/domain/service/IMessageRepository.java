package com.andresnss.sqsconcurrent.domain.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.andresnss.sqsconcurrent.domain.model.Message;

public interface IMessageRepository {
    CompletableFuture<List<Message>> fetchMessages();
    void deleteMessage(String receiptHandle);
    int getVisibleMessageCount();
    void handleEmptyResponse();
    void resetBackoffTime();
}
