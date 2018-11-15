package com.catch42.Markov_Chatbot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.catch42.Markov_Chatbot.service.MarkovGeneratorService;
import com.catch42.Markov_Chatbot.service.MarkovGeneratorServiceImpl;

@Configuration
public class ServiceConfig {

    @Bean
    public MarkovGeneratorService markovGeneratorService(@Value("${markovChain.maxLength}") int maxWordLength) {
        return new MarkovGeneratorServiceImpl(maxWordLength);
    }

}
