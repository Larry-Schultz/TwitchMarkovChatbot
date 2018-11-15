package com.catch42.Markov_Chatbot.service.sentence;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.catch42.Markov_Chatbot.model.MarkovChain;
import com.catch42.Markov_Chatbot.repository.MarkovChainRepository;
import com.catch42.Markov_Chatbot.service.MarkovGeneratorService;
import com.catch42.Markov_Chatbot.util.Util;

@Service
public class SentenceGeneratorService {
	private Logger log = LoggerFactory.getLogger(SentenceGeneratorService.class);
	
	@Autowired
	private MarkovChainRepository markovChainRepository;
	
	@Autowired
	private MarkovGeneratorService markovGeneratorService;
	
	@Value("20")
	private Integer nFactor;
	@Value("3")
	private Integer variance;
	
	/**
	 * Generates a string by looking up markov chain links from the database.
	 * 
	 * @param starter
	 * @return
	 */
	public String generateString(String starter) {
		List<String> chains = new ArrayList<String>();
		boolean blankNextKeyEntryNotFound = true;
		String currentKey = starter;
		//generate a random max range, for more interesting results
		List<Integer> intList = IntStream.range(this.nFactor-this.variance, this.nFactor+this.variance).boxed().collect(Collectors.toList());
		Integer actualLength = Util.<Integer>getRandomElementFromList(intList);
		
		//iterate, generate chains
		while(actualLength > 0 && blankNextKeyEntryNotFound) { 
			List<Object[]> queryResults = new ArrayList<Object[]>();
			if(chains.size() > 0) {
				queryResults = this.markovChainRepository.getNextMarkovchain(currentKey, chains);
			} else {
				queryResults = this.markovChainRepository.getNextMarkovchain(currentKey);
			}
			//convert the queryResults to actual results
			List<MarkovChain> currentEntries = this.markovGeneratorService.convertHibernateResultsToTextEntryList(queryResults);
			log.debug("start list");
			for(MarkovChain entry : currentEntries) {
				log.debug(entry.toString());
			}
			log.debug("end list");
			MarkovChain entry = Util.<MarkovChain>getRandomElementFromList(currentEntries);
			
			if(entry == null || entry.getNextKey() == null) {
				blankNextKeyEntryNotFound = false;
			} else {
				currentKey = entry.getNextKey();
				actualLength--;
				chains.add(entry.getKey());
			}
		}
		
		//add the chains together
		String result = StringUtils.join(chains, ' ');
		result = this.fancyString(result);
		
		return result;
	}
	
	/**
	 * Looks up a random string from the database to serve as a first chain.
	 * 
	 * @return
	 */
	public String getRandomStarterString() {
		List<Object> queryResults = this.markovChainRepository.getMarkovChainCount();
		Long count = (Long) queryResults.get(0);
		Long randomlySelectedTextEntryId = new Long(Util.getRandomLong(count));
		
		String randomStarter = null;
		if(randomlySelectedTextEntryId != null) {
			List<Object[]> randomMarkovChain = this.markovChainRepository.getMarkovChainById(randomlySelectedTextEntryId);
			List<MarkovChain> randomEntries = this.markovGeneratorService.convertHibernateResultsToTextEntryList(randomMarkovChain);
			if(randomEntries.size() > 0) {
				randomStarter = randomEntries.get(0).getKey();
			}
		}
		return randomStarter;
	}
	
	/**
	 * Format strings so they are more esthetically pleasing.
	 * 
	 * @param str
	 * @return
	 */
	public String fancyString(String str) {
		String result = StringUtils.capitalize(str);
		
		//replace lowercase i with I, includes I'm and I've.
		result = StringUtils.replacePattern(result, 
				"((^i(?=\\s))|((?<=\\s)i($|(?=\\.)|(?=\\?)|(?=!)))|((?<=\\s)i(?=\\s)))|(i(?=\\'m))|(i(?=\\'ve))", 
				"I");
		
		//find question
		Pattern pattern = Pattern.compile("((^Who)|(^What)|(^Where)|(^Why)|(^Wow))");
		Matcher matcher = pattern.matcher(result);
		if(matcher.matches()) {
			result = result + "?";
		} else if(Util.getRandomInt(101) == 100) {
			result = result + "!"; //1 in 100 chance of the sentence ending in !
		} else {
			result = result + "."; //normal boring sentence
		}
		
		return result;
	}
	
	public MarkovChainRepository getRepository() {
		return this.markovChainRepository;
	}
}
