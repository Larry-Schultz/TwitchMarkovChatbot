package com.catch42.Markov_Chatbot.service.irc;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.catch42.Markov_Chatbot.model.ChatMessage;
import com.catch42.Markov_Chatbot.model.generator.SentenceGenerator;
import com.catch42.Markov_Chatbot.util.Router;

public class TwitchChatListenerAdapter extends ListenerAdapter {
	Logger log = LoggerFactory.getLogger(TwitchChatListenerAdapter.class);
	
	private Router<ChatMessage> chatMessageRouter;
	
	public TwitchChatListenerAdapter() {
		super();
	}
	
	public TwitchChatListenerAdapter(Router<ChatMessage> chatMessageRouter) {
		super();
		this.chatMessageRouter = chatMessageRouter;
	}
	
	@Override
    public void onGenericMessage(GenericMessageEvent event) {
		//this.sentenceGenerator.addString(event.getMessage());
		//log.info("message: " + event.getMessage());
		this.chatMessageRouter.sendDataToQueues(new ChatMessage(event.getUser().getNick(), event.getMessage()));
    }

}
