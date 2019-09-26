package com.study.botssm.botssmstudy.database.repository;

import org.springframework.statemachine.data.mongodb.MongoDbStateMachineRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SMRepository extends MongoDbStateMachineRepository {

}
