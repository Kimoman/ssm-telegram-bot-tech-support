package com.study.botssm.botssmstudy.bot;

import com.study.botssm.botssmstudy.ssmconfig.States;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.PostConstruct;

@Component
public class TechBot extends TelegramLongPollingBot {

    private static final Logger logger
            = LoggerFactory.getLogger(TechBot.class);

    @Autowired
    protected MongoTemplate mongoTemplate;
    @Autowired
    private StateMachineService<States, String> stateMachineService;

    private StateMachine<States, String> stateMachine;

    @Autowired
    private BotConfig botConfig;

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {

            Long chatId = update.getMessage().getChatId();
            String event = update.getMessage().getText();

            try {
                stateMachine = getStateMachine(String.valueOf(chatId));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            Message<String> message = MessageBuilder
                    .withPayload(event)
                    .setHeader("text", event)
                    .build();
            stateMachine.sendEvent(message);
            logger.info("message : {}", message);
        } else if (update.hasCallbackQuery()) {

            String call_data = update.getCallbackQuery().getData();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();

            try {
                stateMachine = getStateMachine(String.valueOf(chatId));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            Message<String> message = MessageBuilder
                    .withPayload(call_data)
                    .setHeader("text", call_data)
                    .build();
            stateMachine.sendEvent(message);
            logger.info("message : {}", message);
        }
    }


    @Override
    public String getBotUsername() {
        return botConfig.getUsername();
    }

    private synchronized StateMachine<States, String> getStateMachine(String machineId) throws Exception {
        if (stateMachine == null) {
            stateMachine = stateMachineService.acquireStateMachine(machineId);
            stateMachine.start();
        } else if (!ObjectUtils.nullSafeEquals(stateMachine.getId(), machineId)) {
            stateMachineService.releaseStateMachine(stateMachine.getId());
            stateMachine.stop();
            stateMachine = stateMachineService.acquireStateMachine(machineId);
            stateMachine.start();
        }
        return stateMachine;
    }

    @PostConstruct
    public void init() {
        logger.info("username: {}, token: {}", botConfig.getUsername(), botConfig.getToken());
    }
}