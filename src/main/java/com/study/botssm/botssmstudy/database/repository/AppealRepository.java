package com.study.botssm.botssmstudy.database.repository;

import com.study.botssm.botssmstudy.database.model.Appeal;
import com.study.botssm.botssmstudy.database.model.AppealStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppealRepository extends MongoRepository<Appeal, Long> {

    @Override
    List<Appeal> findAll();

    Optional<Appeal> findById(Long id);

    List<Appeal> findByChatId(Long chatId);

    List<Appeal> findByStatus(AppealStatus status);
}
