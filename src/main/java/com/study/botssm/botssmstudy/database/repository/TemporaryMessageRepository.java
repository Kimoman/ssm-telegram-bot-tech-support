package com.study.botssm.botssmstudy.database.repository;

import com.study.botssm.botssmstudy.database.model.TemporaryMessage;
import com.study.botssm.botssmstudy.database.model.TemporaryMessageStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemporaryMessageRepository extends MongoRepository<TemporaryMessage, Long> {
    @Override
    List<TemporaryMessage> findAll();

    List<TemporaryMessage> findByIdAndStatus(Long id, TemporaryMessageStatus status);

    List<TemporaryMessage> findByChatIdAndStatus(Long chatId, TemporaryMessageStatus status);

    void deleteAllByChatIdAndStatus(Long chatId, TemporaryMessageStatus status);
}
