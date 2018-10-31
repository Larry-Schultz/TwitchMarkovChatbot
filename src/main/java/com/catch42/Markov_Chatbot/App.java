package com.catch42.Markov_Chatbot;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.cap.EnableCapHandler;
import org.pircbotx.exception.IrcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.catch42.Markov_Chatbot.model.ChatMessage;
import com.catch42.Markov_Chatbot.model.TextEntry;
import com.catch42.Markov_Chatbot.model.generator.SentenceGenerator;
import com.catch42.Markov_Chatbot.model.generator.SentenceGeneratorManager;
import com.catch42.Markov_Chatbot.model.generator.SentenceGeneratorThread;
import com.catch42.Markov_Chatbot.model.TextEntryFactory;
import com.catch42.Markov_Chatbot.repository.ChannelTextRepository;
import com.catch42.Markov_Chatbot.repository.ChannelTextRepositoryThread;
import com.catch42.Markov_Chatbot.service.irc.IrcChatbotThread;
import com.catch42.Markov_Chatbot.service.irc.TwitchChatListenerAdapter;
import com.catch42.Markov_Chatbot.util.Router;

@SpringBootApplication
public class App {

	private static final Logger log = LoggerFactory.getLogger(App.class);
	
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
    
    @Value("${irc.username}")
    private String username;
    
    @Value("${irc.password}")
    private String password;
    
    
    @Bean
    public SentenceGenerator sentenceGenerator(ChannelTextRepository repository) {
    	return new SentenceGenerator(repository, 20, 3);
    }
    
    @Bean
    public IrcChatbotThread ircBot(Router<ChatMessage> chatMessageRouter) {
    	IrcChatbotThread thread = new IrcChatbotThread(this.username, this.password, chatMessageRouter);
    	thread.start();
    	return thread;
    	
    }
    
    @Bean
    public ChannelTextRepositoryThread channelTextRepositoryThread(BlockingQueue<ChatMessage> repositoryBoundChatMessagesQueue, ChannelTextRepository repository) {
    	ChannelTextRepositoryThread thread = new ChannelTextRepositoryThread(repositoryBoundChatMessagesQueue, repository);
    	thread.start();
    	return thread;
    }
    
    @Bean
    public SentenceGeneratorThread sentenceGeneratorThread(Router<String> generatedSentenceRouter, SentenceGenerator sentenceGenerator) {
    	SentenceGeneratorThread thread = new SentenceGeneratorThread(generatedSentenceRouter, sentenceGenerator);
    	thread.start();
    	return thread;
    }
    
    @Bean
	public CommandLineRunner demo(SentenceGenerator sentenceGenerator, ChannelTextRepositoryThread channelTextRepositoryThread,
			BlockingQueue<String> commandLineExampleSentenceQueue) {
    	return (args) -> {
			while(true) {
				try {
					String sentence = commandLineExampleSentenceQueue.take();
					log.info("example sentence: " + sentence);
				} catch(InterruptedException e) {
					break; //exit loop
				}
			}
    	};
    }
    
}
