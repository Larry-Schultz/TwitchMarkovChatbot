package com.catch42.Markov_Chatbot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.catch42.Markov_Chatbot.service.irc.IrcChatbotService;

@Controller
public class ChannelController {

	@Autowired
	private IrcChatbotService ircChatbotService;
	
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
