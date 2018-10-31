package com.catch42.Markov_Chatbot.repository;

import org.springframework.data.repository.CrudRepository;

import com.catch42.Markov_Chatbot.model.ChatMessage;

public interface ChatMessageRepository extends CrudRepository<ChatMessage, Long> { 


}
