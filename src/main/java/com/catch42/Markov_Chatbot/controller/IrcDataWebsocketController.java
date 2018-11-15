package com.catch42.Markov_Chatbot.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import com.catch42.Markov_Chatbot.model.ApplicationStatistics;
import com.catch42.Markov_Chatbot.model.ChatMessage;
import com.catch42.Markov_Chatbot.service.StatisticsService;
import com.catch42.Markov_Chatbot.service.irc.IrcChatbotService;

@EnableScheduling
@Controller
public class IrcDataWebsocketController {

    private static final Logger log = LoggerFactory.getLogger(IrcDataWebsocketController.class);

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private BlockingQueue<ChatMessage> websocketBoundChatMessagesQueue;

    @Autowired
    private BlockingQueue<String> websocketBoundSentenceQueue;

    @Autowired
    private IrcChatbotService ircChatbotService;

    @Autowired
    private StatisticsService statisticsService;

    @Scheduled(fixedRate = 5000)
    public void chatMessage() throws Exception {
        List<ChatMessage> messagesList = new ArrayList<ChatMessage>();
        while (this.websocketBoundChatMessagesQueue.peek() != null) {
            try {
                messagesList.add(this.websocketBoundChatMessagesQueue.remove());
            } catch (NoSuchElementException e) {
                // do nothing because all we care about is getting what is currently in the queue
            }
        }
        this.template.convertAndSend("/chain/chatMessages", messagesList);

        return;
    }

    @Scheduled(fixedRate = 5000)
    public void generatedSentence() throws Exception {
        List<String> generatedSentenceList = new ArrayList<String>();
        while (this.websocketBoundSentenceQueue.peek() != null) {
            try {
                generatedSentenceList.add(this.websocketBoundSentenceQueue.remove());
            } catch (NoSuchElementException e) {
                // do nothing because all we care about is getting what is currently in the queue
            }
        }
        this.template.convertAndSend("/chain/generatedSentences", generatedSentenceList);
    }

    @Scheduled(fixedRate = 5000)
    public void channelList() throws Exception {
        List<String> channels = this.ircChatbotService.channels();
        this.template.convertAndSend("/chain/channels", channels);
    }

    @Scheduled(fixedRate = 5000)
    public void sendApplicationStatistics() throws Exception {
        ApplicationStatistics applicationStatistics = this.statisticsService.getApplicationStatistics();
        this.template.convertAndSend("/chain/stats", applicationStatistics);
    }
}
