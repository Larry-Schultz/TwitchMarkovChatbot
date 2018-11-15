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
import com.catch42.Markov_Chatbot.model.MarkovChain;
import com.catch42.Markov_Chatbot.repository.MarkovChainRepository;
import com.catch42.Markov_Chatbot.service.ChannelTextRepositoryThread;
import com.catch42.Markov_Chatbot.service.MarkovGeneratorServiceImpl;
import com.catch42.Markov_Chatbot.service.irc.IrcChatbotThread;
import com.catch42.Markov_Chatbot.service.irc.TwitchChatListenerAdapter;
import com.catch42.Markov_Chatbot.service.sentence.SentenceGeneratorManager;
import com.catch42.Markov_Chatbot.service.sentence.SentenceGeneratorService;
import com.catch42.Markov_Chatbot.service.sentence.SentenceGeneratorThread;
import com.catch42.Markov_Chatbot.util.Router;

@SpringBootApplication
public class App {

	private static final Logger log = LoggerFactory.getLogger(App.class);
	
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
    
    @Bean
	public CommandLineRunner demo(SentenceGeneratorService sentenceGeneratorService, ChannelTextRepositoryThread channelTextRepositoryThread,
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
