package com.catch42.Markov_Chatbot.service;

import java.util.Collection;
import java.util.List;

import com.catch42.Markov_Chatbot.model.TextEntry;

public interface MarkovGeneratorService {
	public Collection<TextEntry> generateMarkovChains(Collection<String> stringCollection);
	public Collection<TextEntry> generateMarkovChains(String str);
	public List<TextEntry> convertHibernateResultsToTextEntryList(List<Object[]> queryResults);
}
