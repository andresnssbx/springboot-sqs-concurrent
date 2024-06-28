package com.andresnss.sqsconcurrent.domain.service;

import org.springframework.stereotype.Service;

import com.andresnss.sqsconcurrent.domain.model.Message;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MessageProcessingService {

    private final IMessageRepository messageRepository;

    public MessageProcessingService(IMessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }


    public void processMessage(Message message) {
        log.debug("Processing message: {}", message.getBody());
        messageRepository.deleteMessage(message.getReceiptHandle());
    }
}

