package com.study.botssm.botssmstudy.database.service;

import com.study.botssm.botssmstudy.database.model.TemporaryMessage;
import com.study.botssm.botssmstudy.database.model.TemporaryMessageStatus;
import com.study.botssm.botssmstudy.database.repository.TemporaryMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.study.botssm.botssmstudy.database.model.TemporaryMessageStatus.ANSWER;
import static com.study.botssm.botssmstudy.database.model.TemporaryMessageStatus.APPEAL;

@Service
public class TemporaryMessageService {

    @Autowired
    private TemporaryMessageRepository repository;

    @Autowired
    private GeneratorService generatorService;

    public void saveCommonMessage(Long chatId, String event) {
        TemporaryMessage temporaryMessage = new TemporaryMessage();
        temporaryMessage
                .setId(generatorService.generateSequence(TemporaryMessage.SEQUENCE_NAME))
                .setChatId(chatId)
                .setText(event)
                .setStatus(APPEAL);
        repository.save(temporaryMessage);
    }

    public void saveAnswerMessage(Long chatId, String event) {
        TemporaryMessage temporaryMessage = new TemporaryMessage();
        temporaryMessage
                .setId(generatorService.generateSequence(TemporaryMessage.SEQUENCE_NAME))
                .setChatId(chatId)
                .setText(event)
                .setStatus(ANSWER);
        repository.save(temporaryMessage);
    }

    public List<TemporaryMessage> findMessages(Long chatId, TemporaryMessageStatus status) {
        return repository.findByChatIdAndStatus(chatId, status);
    }

    public Optional<String> textOfMessages(Long chatId, TemporaryMessageStatus status) {
        String string = repository.findByChatIdAndStatus(chatId, status)
                .stream().map(item -> item.getText()).collect(Collectors.joining(" "));

        return !string.isEmpty() ? Optional.of(string) : Optional.empty();
    }

    public void deleteMessage(Long chatId, TemporaryMessageStatus status) {
        repository.deleteAllByChatIdAndStatus(chatId, status);
    }
}
