package com.andresnss.sqsconcurrent.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Message {
    private String body;
    private String receiptHandle;
}

