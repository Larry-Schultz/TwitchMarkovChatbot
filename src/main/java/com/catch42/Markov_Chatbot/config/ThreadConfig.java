package com.catch42.Markov_Chatbot.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.catch42.Markov_Chatbot.model.Channel;
import com.catch42.Markov_Chatbot.model.ChatMessage;
import com.catch42.Markov_Chatbot.repository.ChannelRepository;
import com.catch42.Markov_Chatbot.repository.MarkovChainRepository;
import com.catch42.Markov_Chatbot.service.ChannelTextRepositoryThread;
import com.catch42.Markov_Chatbot.service.irc.IrcChatbotThread;
import com.catch42.Markov_Chatbot.service.sentence.SentenceGeneratorService;
import com.catch42.Markov_Chatbot.service.sentence.SentenceGeneratorThread;
import com.catch42.Markov_Chatbot.util.Router;

@Configuration
public class ThreadConfig {

    @Value("${irc.username}")
    private String username;

    @Value("${irc.password}")
    private String password;

    /*
     * @Bean public List<String> ircChannels() { List<String> channels = new ArrayList<String>();
     * channels.add("dansgaming"); channels.add("dolphinchemist"); channels.add("streamfourstar");
     * channels.add("ragtagg");
     * 
     * return channels; }
     */

    @Bean
    public Map<String, IrcChatbotThread> ircBotMap(Router<ChatMessage> chatMessageRouter,
            ChannelRepository channelRepository) {
        List<Object[]> allChannelResults = channelRepository.getAllChannels();
        List<Channel> channelList = new ArrayList<>();
        for (Object[] result : allChannelResults) {
            channelList.add(new Channel(result));
        }

        Map<String, IrcChatbotThread> ircBots = new HashMap<String, IrcChatbotThread>();
        for (Channel ircChannel : channelList) {
            IrcChatbotThread thread = new IrcChatbotThread(this.username, this.password, ircChannel.getChannelName(),
                    chatMessageRouter);
            thread.start();
            ircBots.put(ircChannel.getChannelName(), thread);
        }
        return ircBots;

    }

    @Bean
    public ChannelTextRepositoryThread channelTextRepositoryThread(
            BlockingQueue<ChatMessage> repositoryBoundChatMessagesQueue, MarkovChainRepository markovChainRepository) {
        ChannelTextRepositoryThread thread = new ChannelTextRepositoryThread(repositoryBoundChatMessagesQueue,
                markovChainRepository);
        thread.start();
        return thread;
    }

    @Bean
    public SentenceGeneratorThread sentenceGeneratorThread(Router<String> generatedSentenceRouter,
            SentenceGeneratorService sentenceGeneratorService) {
        SentenceGeneratorThread thread = new SentenceGeneratorThread(generatedSentenceRouter, sentenceGeneratorService);
        thread.start();
        return thread;
    }

}
