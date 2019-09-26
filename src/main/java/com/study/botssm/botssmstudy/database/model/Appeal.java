package com.study.botssm.botssmstudy.database.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "appeals")
public class Appeal {

    @Transient
    public static final String SEQUENCE_NAME = "appeals_sequence";

    @Id
    private Long id;

    private Long chatId;

    private String text;

    private String answer;

    private AppealStatus status;

    public Long getChatId() {
        return chatId;
    }

    public Appeal setChatId(Long chatId) {
        this.chatId = chatId;
        return this;
    }

    public String getText() {
        return text;
    }

    public Appeal setText(String text) {
        this.text = text;
        return this;
    }

    public Long getId() {
        return id;
    }

    public Appeal setId(Long id) {
        this.id = id;
        return this;
    }

    public String getAnswer() {
        return answer;
    }

    public Appeal setAnswer(String answer) {
        this.answer = answer;
        return this;
    }

    public AppealStatus getStatus() {
        return status;
    }

    public Appeal setStatus(AppealStatus status) {
        this.status = status;
        return this;
    }
}
