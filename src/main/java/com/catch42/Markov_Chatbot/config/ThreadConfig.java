package com.catch42.Markov_Chatbot.config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.catch42.Markov_Chatbot.model.ChatMessage;
import com.catch42.Markov_Chatbot.model.generator.SentenceGenerator;
import com.catch42.Markov_Chatbot.model.generator.SentenceGeneratorThread;
import com.catch42.Markov_Chatbot.repository.ChannelTextRepository;
import com.catch42.Markov_Chatbot.repository.ChannelTextRepositoryThread;
import com.catch42.Markov_Chatbot.service.irc.IrcChatbotThread;
import com.catch42.Markov_Chatbot.util.Router;

@Configuration
public class ThreadConfig {

    @Value("${irc.username}")
    private String username;
    
    @Value("${irc.password}")
    private String password;
    
    @Bean
    public List<String> ircChannels() {
    	List<String> channels = new ArrayList<String>();
    	channels.add("dansgaming");
    	channels.add("dolphinchemist");
    	channels.add("streamfourstar");
    	channels.add("ragtagg");
    	
    	return channels;
    }
    
    @Bean
    public List<IrcChatbotThread> ircBot(Router<ChatMessage> chatMessageRouter, List<String> ircChannels) {
    	List<IrcChatbotThread> threads = new ArrayList<IrcChatbotThread>();
    	for(String ircChannel: ircChannels) {
	    	IrcChatbotThread thread = new IrcChatbotThread(this.username, this.password, ircChannel, chatMessageRouter);
	    	thread.start();
	    	threads.add(thread);
    	}
    	return threads;
    	
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
	
}
