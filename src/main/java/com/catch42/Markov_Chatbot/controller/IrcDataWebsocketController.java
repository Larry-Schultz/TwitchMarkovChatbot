package com.catch42.Markov_Chatbot.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import com.catch42.Markov_Chatbot.model.ChatMessage;

@EnableScheduling
@Controller
public class IrcDataWebsocketController {

	@Autowired
    private SimpMessagingTemplate template;
	
	@Autowired
	private BlockingQueue<ChatMessage> websocketBoundChatMessagesQueue;
	
	@Autowired
	private BlockingQueue<String> websocketBoundSentenceQueue;
	
	@Scheduled(fixedRate = 5000)
    public void chatMessage() throws Exception {
        List<ChatMessage> messagesList = new ArrayList<ChatMessage>();
        while(this.websocketBoundChatMessagesQueue.peek() != null) {
        	try {
        		messagesList.add(this.websocketBoundChatMessagesQueue.remove());
        	} catch(NoSuchElementException e) {
        		//do nothing because all we care about is getting what is currently in the queue
        	}
        }
        this.template.convertAndSend("/chain/chatMessages", messagesList);
        
        return;
    }
	
	@Scheduled(fixedRate = 5000)
	public void generatedSentence() throws Exception {
		List<String> generatedSentenceList = new ArrayList<String>();
		while(this.websocketBoundSentenceQueue.peek() != null) {
			try {
				generatedSentenceList.add(this.websocketBoundSentenceQueue.remove());
			} catch(NoSuchElementException e) {
        		//do nothing because all we care about is getting what is currently in the queue
        	}
		}
		this.template.convertAndSend("/chain/generatedSentences", generatedSentenceList);
	}
}
