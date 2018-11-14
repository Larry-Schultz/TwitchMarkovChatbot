package com.catch42.Markov_Chatbot.service.irc;

import java.io.IOException;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.cap.EnableCapHandler;
import org.pircbotx.exception.IrcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.catch42.Markov_Chatbot.model.ChatMessage;
import com.catch42.Markov_Chatbot.util.Router;

public class IrcChatbotThread extends Thread {
	private static final Logger log = LoggerFactory.getLogger(IrcChatbotThread.class);
	
	private PircBotX bot;
	private String channel;
	
	public IrcChatbotThread(String username, String password, String channel, Router<ChatMessage> chatMessageRouter) {
		super(IrcChatbotThread.class.getName());
		this.channel = channel;
    	//Configure what we want our bot to do
        Configuration configuration = new Configuration.Builder()
        		.setAutoNickChange(false) //Twitch doesn't support multiple users
        		.setOnJoinWhoEnabled(false) //Twitch doesn't support WHO command
        		.setCapEnabled(true)
        		.addCapHandler(new EnableCapHandler("twitch.tv/membership")) //Twitch by default doesn't send JOIN, PART, and NAMES unless you request it, see https://github.com/justintv/Twitch-API/blob/master/IRC.md#membership
        		.addServer("irc.twitch.tv")
        		.setName(username) //Your twitch.tv username
        		.setServerPassword(password) //Your oauth password from http://twitchapps.com/tmi
        		.addAutoJoinChannel("#" + channel) //Some twitch channel
                .addListener(new TwitchChatListenerAdapter(chatMessageRouter, channel)) //Add our listener that will be called on Events
                .buildConfiguration();

        //Create our bot with the configuration
        this.bot = new PircBotX(configuration);
	}
	
	public void run() {
		log.info("Starting IrcChatbotThread");
		log.info("Joining channel: " + "#" + this.channel);
        try {
			bot.startBot();
		} catch (IOException | IrcException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void killBot() {
		this.bot.close();
	}

}
