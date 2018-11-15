package com.catch42.Markov_Chatbot.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.catch42.Markov_Chatbot.model.TextEntry;

public class MarkovGeneratorServiceImpl extends MarkovGeneratorServiceAbstract {
	private static final Logger log = LoggerFactory.getLogger(MarkovGeneratorServiceImpl.class);
	
	
	public MarkovGeneratorServiceImpl(int maxWordLength) {
		super();
		this.maxWordLength = maxWordLength;
	}
	
	/**
	 * Takes in a list of strings, and returns a colleciton of markov chains.
	 * @param stringCollection
	 * @return
	 */
	public Collection<TextEntry> generateMarkovChains(Collection<String> stringCollection) {
		Collection<TextEntry> results = new LinkedList<>();
		
		for(String str: stringCollection) {
			results.addAll(this.generateMarkovChains(str));
		}
	
		return results;
	}
	
	/**
	 * Takes in a string, and converts it to a collection of markov chains.
	 * @param str
	 * @return
	 */
	public Collection<TextEntry> generateMarkovChains(String str) {
		Collection<TextEntry> results = new LinkedList<>();
		
		String filteredString = filterString(str);
		
		if(!this.isStringGoodSource(filteredString)) {
			return new LinkedList<>(); //if whole string is no good, return nothing
		}
		
		if(!StringUtils.isBlank(filteredString)) {
			List<String> splitStrings = Arrays.asList(StringUtils.split(filteredString));
			
			//if we find an emote, abort.  those strings also typically suck.
			for(String uncheckedString: splitStrings) {
				if(!this.isWordGoodSource(uncheckedString)) {
					return new LinkedList<>(); //return nothing
				}
			}
			
			for(int i = 0; i< splitStrings.size(); i++) {
				String currentString = splitStrings.get(i);
				String nextString = null;
				if(i != (splitStrings.size() - 1) ) {
					nextString = splitStrings.get(i +1);
				}
				
				results.add(new TextEntry(currentString, nextString, (splitStrings.size() -1) - i));
			}
			
		}
		
		return results;
	}
	
	/**
	 * hibernate results come in as List<Object[]>, but to make these results useable it needs to be
	 * in List<TextEntry>.  so let's convert them here in the factory class.
	 * 
	 * @param queryResults
	 * @return
	 */
	public List<TextEntry> convertHibernateResultsToTextEntryList(List<Object[]> queryResults)
	{
		List<TextEntry> results = queryResults.stream().map(name -> new TextEntry(name))
				.collect(Collectors.toList());
		
		return results;
	}
	
	

}
