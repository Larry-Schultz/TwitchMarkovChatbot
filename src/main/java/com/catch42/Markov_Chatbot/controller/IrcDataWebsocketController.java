package com.catch42.Markov_Chatbot.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.catch42.Markov_Chatbot.model.ChatMessage;
import com.catch42.Markov_Chatbot.service.irc.IrcChatbotService;
import com.catch42.Markov_Chatbot.service.irc.IrcChatbotThread;

@EnableScheduling
@Controller
public class IrcDataWebsocketController {

	@Autowired
    private SimpMessagingTemplate template;
	
	@Autowired
	private BlockingQueue<ChatMessage> websocketBoundChatMessagesQueue;
	
	@Autowired
	private BlockingQueue<String> websocketBoundSentenceQueue;
	
	@Autowired
	private IrcChatbotService ircChatbotService;
	
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
	
	@Scheduled(fixedRate = 5000)
	public void channelList() throws Exception {
		List<String> channels = this.ircChatbotService.channels();
		this.template.convertAndSend("/chain/channels", channels);
	}
	
	@RequestMapping(value = "/channel/add/{channelName}", method = RequestMethod.GET)
	public @ResponseBody String addChannel(@PathVariable("channelName") String channelName) {
		this.ircChatbotService.addChannel(channelName.replace("#", ""));
		return "";
	}
	
	@RequestMapping(value = "/channel/remove/{channelName}", method = RequestMethod.GET)
	public @ResponseBody String removeChannel(@PathVariable("channelName") String channelName) {
		this.ircChatbotService.removeChannel(channelName.replace("#", ""));
		return "";
	}
}
