package com.university.admissions.repository;

import com.university.admissions.model.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends MongoRepository<ChatMessage, String> {

    // Most recent conversations first
    List<ChatMessage> findByUserEmailOrderByTimestampDesc(String userEmail);
}
