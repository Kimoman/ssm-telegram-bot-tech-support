package com.study.botssm.botssmstudy.database.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "temporary_messages")
public class TemporaryMessage {

    @Transient
    public static final String SEQUENCE_NAME = "temporary_messages_sequence";

    @Id
    private Long id;

    private Long chatId;

    private String text;

    private TemporaryMessageStatus status;

    public TemporaryMessage setStatus(TemporaryMessageStatus status) {
        this.status = status;
        return this;
    }

    public Long getId() {
        return id;
    }

    public TemporaryMessage setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getChatId() {
        return chatId;
    }

    public TemporaryMessage setChatId(Long chatId) {
        this.chatId = chatId;
        return this;
    }

    public String getText() {
        return text;
    }

    public TemporaryMessage setText(String text) {
        this.text = text;
        return this;
    }
}
