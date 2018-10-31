package com.catch42.Markov_Chatbot.config;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.catch42.Markov_Chatbot.model.ChatMessage;
import com.catch42.Markov_Chatbot.util.Router;

@Configuration
public class QueueRouterConfig {

	private static final int maxQueueCapacity = 100;
	
	@Bean
	public BlockingQueue<ChatMessage> repositoryBoundChatMessagesQueue() {
		return new LinkedBlockingQueue<ChatMessage>(maxQueueCapacity);
	}
	
	@Bean 
	public BlockingQueue<ChatMessage> websocketBoundChatMessagesQueue() {
		return new LinkedBlockingQueue<ChatMessage>(maxQueueCapacity);
	}
	
	@Bean
	public Router<ChatMessage> chatMessageRouter(Queue<ChatMessage> repositoryBoundChatMessagesQueue, Queue<ChatMessage> websocketBoundChatMessagesQueue) {
		return new Router<ChatMessage>(repositoryBoundChatMessagesQueue, websocketBoundChatMessagesQueue);
	}
	
	@Bean
	public BlockingQueue<String> commandLineExampleSentenceQueue() {
		return new LinkedBlockingQueue<String>(maxQueueCapacity);
	}
	
	@Bean
	public BlockingQueue<String> websocketBoundSentenceQueue() {
		return new LinkedBlockingQueue<String>(maxQueueCapacity);
	}
	
	@Bean
	public Router<String> generatedSentenceRouter(Queue<String> commandLineExampleSentenceQueue, Queue<String> websocketBoundSentenceQueue) {
		return new Router<String>(commandLineExampleSentenceQueue, websocketBoundSentenceQueue);
	}
}
