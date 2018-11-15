package com.catch42.Markov_Chatbot.service;

import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import com.catch42.Markov_Chatbot.model.ChatMessage;
import com.catch42.Markov_Chatbot.model.MarkovChain;
import com.catch42.Markov_Chatbot.repository.MarkovChainRepository;

public class ChannelTextRepositoryThread extends Thread {
	private Logger log = LoggerFactory.getLogger(ChannelTextRepositoryThread.class);
	
	@Autowired
	private MarkovGeneratorService markovGeneratorService;
	
	private BlockingQueue<ChatMessage> repositoryBoundChatMessagesQueue;
	private MarkovChainRepository repository;
	
	public ChannelTextRepositoryThread(BlockingQueue<ChatMessage> repositoryBoundChatMessagesQueue, MarkovChainRepository repository) {
		super(ChannelTextRepositoryThread.class.getName());
		this.repository = repository;
		this.repositoryBoundChatMessagesQueue = repositoryBoundChatMessagesQueue;
	}
	
	public void run() {
		log.info("Starting ChannelTextRepositoryThread");
		try {
			while(true) {
				ChatMessage chatMessage = this.repositoryBoundChatMessagesQueue.take();
				log.debug("Adding string to repo: {}", chatMessage.toString());
				this.addString(chatMessage.getMessage());
			}
		} catch(InterruptedException e) {
			log.info("Closing ChanntelTextRepositoryThread");
		}
	}
	
	/**
	 * convert strings to markov chains, and the add them to the repo.
	 * @param strings
	 */
	public void addString(Collection<String> strings) {
		for(String str: strings) {
			this.addString(str);
		}
	}
	
	/**
	 * convert single string to markov chain, and then adds them to the repo.
	 * We use database constraints to prevent duplicates from being read by the database.
	 * Since having a duplicate is expected, we catch the exceptions and continue forth.
	 * @param str
	 */
	public void addString(String str) {
		Collection<MarkovChain> textEntries = this.markovGeneratorService.generateMarkovChains(str);
		for(MarkovChain entry : textEntries) {
			try {
				if(!this.isTextEntryAlreadyInDatbase(entry)) {
					this.repository.save(entry);
				}
			} catch(ConstraintViolationException | DataIntegrityViolationException e) {
				log.trace("ignoring duplicate entry");
			}
		}
	}
	
	public boolean isTextEntryAlreadyInDatbase(MarkovChain entry) {
		List<Object[]> idResult = this.repository.getIdByKeyAndNextKey(entry.getKey(), entry.getNextKey());
		return idResult != null && idResult.size() != 0;
	}

}
