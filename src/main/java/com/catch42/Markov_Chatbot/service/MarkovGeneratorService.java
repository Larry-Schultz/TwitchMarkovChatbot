package com.catch42.Markov_Chatbot.service;

import java.util.Collection;
import java.util.List;

import com.catch42.Markov_Chatbot.model.MarkovChain;

public interface MarkovGeneratorService {
    public Collection<MarkovChain> generateMarkovChains(Collection<String> stringCollection);

    public Collection<MarkovChain> generateMarkovChains(String str);

    public List<MarkovChain> convertHibernateResultsToTextEntryList(List<Object[]> queryResults);
}
