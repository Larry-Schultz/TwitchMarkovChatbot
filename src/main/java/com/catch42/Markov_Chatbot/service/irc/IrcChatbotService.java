package com.catch42.Markov_Chatbot.service.irc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.catch42.Markov_Chatbot.model.Channel;
import com.catch42.Markov_Chatbot.model.ChatMessage;
import com.catch42.Markov_Chatbot.repository.ChannelRepository;
import com.catch42.Markov_Chatbot.util.Router;

@Service
public class IrcChatbotService {

    private static final Logger log_ = LoggerFactory.getLogger(IrcChatbotService.class);

    @Autowired
    private Map<String, IrcChatbotThread> ircBotMap;

    @Value("${irc.username}")
    private String username;

    @Value("${irc.password}")
    private String password;

    @Autowired
    private Router<ChatMessage> chatMessageRouter;

    @Autowired
    private ChannelRepository channelRepository;

    public void addChannel(String ircChannel) {
        if (!this.ircBotMap.containsKey(ircChannel)) {
            IrcChatbotThread thread = new IrcChatbotThread(this.username, this.password, ircChannel,
                    this.chatMessageRouter);
            thread.start();
            this.ircBotMap.put(ircChannel, thread);
            this.addChannelToRepositoryIfNotPresent(ircChannel);
        }
    }

    public void addChannelToRepositoryIfNotPresent(String channelName) {
        Long channelId = this.channelRepository.getChannelByName(channelName);
        if (channelId == null || channelId < 0) {
            this.channelRepository.save(new Channel(channelName));
        } else {
            log_.warn("Tried to add duplicate channel: {}", channelName);
        }
    }

    public void removeChannel(String ircChannel) {
        if (this.ircBotMap.containsKey(ircChannel)) {
            IrcChatbotThread thread = this.ircBotMap.get(ircChannel);
            thread.killBot();
            this.ircBotMap.remove(ircChannel);
            this.removeChannelFromRepository(ircChannel);
        }
    }

    public void removeChannelFromRepository(String ircChannel) {
        Long channelId = this.channelRepository.getChannelByName(ircChannel);
        this.channelRepository.deleteById(channelId);
    }

    public List<String> channels() {
        return new ArrayList<String>(this.ircBotMap.keySet());
    }

}
