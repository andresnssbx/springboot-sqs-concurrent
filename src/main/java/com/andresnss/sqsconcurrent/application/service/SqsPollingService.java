package com.andresnss.sqsconcurrent.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.andresnss.sqsconcurrent.domain.service.IMessageRepository;
import com.andresnss.sqsconcurrent.domain.service.MessageProcessingService;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
public class SqsPollingService {


    @Value("${app.thread.concurrent.max}")
    private int maxParallelRequests;

    @Value("${app.thread.concurrent.min}")
    private int minParallelRequests;

    private AtomicInteger currentParallelRequests;

    private IMessageRepository messageRepository;

    private MessageProcessingService messageProcessingService;

    public SqsPollingService(IMessageRepository messageRepository, MessageProcessingService messageProcessingService){
        this.messageRepository = messageRepository;
        this.messageProcessingService = messageProcessingService;
        currentParallelRequests = new AtomicInteger(minParallelRequests);
    }

    

    @Scheduled(fixedRateString = "${app.schedule.fixed-rate}")
    public void pollMessages() {
        log.info("----init-process----");
        adjustParallelRequests();

        var futures = IntStream.range(0, currentParallelRequests.get())
                .mapToObj(i -> fetchMessages())
                .collect(Collectors.toList());

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        log.info("----finish-process----\n");
    }

    private CompletableFuture<Void> fetchMessages() {
        return messageRepository.fetchMessages()
                .thenAccept(messages -> {
                    if (messages.isEmpty()) {
                        log.info("No messages received");
                    } else {
                        messages.forEach(messageProcessingService::processMessage);
                    }
                })
                .exceptionally(ex -> {
                    if (ex.getCause() instanceof InterruptedException) {
                        log.error("Message fetching was interrupted", ex);
                        Thread.currentThread().interrupt();
                    } else {
                        log.error("An error occurred while fetching messages", ex);
                    }
                    return null;
                });
    }

    private void adjustParallelRequests() {
        int visibleMessageCount = messageRepository.getVisibleMessageCount();
        log.info("visible-messages: {}", visibleMessageCount);
        int newParallelRequests = Math.min(visibleMessageCount / 10, maxParallelRequests);
        currentParallelRequests.set(Math.max(newParallelRequests, 1));
    }
}
