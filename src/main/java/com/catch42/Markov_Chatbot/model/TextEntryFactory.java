package com.catch42.Markov_Chatbot.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.CharMatcher;

public class TextEntryFactory {
	private static final Logger log = LoggerFactory.getLogger(TextEntryFactory.class);
	
	public static final int maxLength = 15;
	
	private static final String[] filteredPunctuation = new String[] {"\\", "!", ".", "?", ",", "/", ")", "(", "*", "-", "~", "\n", "\r", "\t", ">", "\""};
	private static final List<String> sentenceRuiningWords = Arrays.asList(new String[] {"raid", "fuck", "shit", "bitch", "ass", "damn", "user"});
	
	private static final List<String> simpleEmoteList = Arrays.asList(new String[] {":)", ":(", ":o", ":z", "B)", ":\\", ";)", ";p", "R)", "o_O", ":D",
			">(", "<3", "R)", ":>", "<]", ":7", ":(", ":P", ";P", ":O", ":\\", ":|", ":s",":D", ">(", "#/"});
	
	private static Set<String> emoteFilterSet = new LinkedHashSet<String>();
	
	static {
		emoteFilterSet.addAll(simpleEmoteList);
		loadHashSet(emoteFilterSet, "emotes.txt");
		loadHashSet(emoteFilterSet, "subscriberEmotes.txt");
	}
	 
	public static Collection<TextEntry> generateMarkovChains(Collection<String> stringCollection) {
		Collection<TextEntry> results = new LinkedList<>();
		
		for(String str: stringCollection) {
			results.addAll(generateMarkovChains(str));
		}
	
		return results;
	}
	
	public static Collection<TextEntry> generateMarkovChains(String str) {
		Collection<TextEntry> results = new LinkedList<>();
		
		String filteredString = filterString(str);
		
		if(!isStringGoodSource(filteredString)) {
			return new LinkedList<>(); //if whole string is no good, return nothing
		}
		
		if(!StringUtils.isBlank(filteredString)) {
			List<String> splitStrings = Arrays.asList(StringUtils.split(filteredString));
			
			//if we find an emote, abort.  those strings also typically suck.
			for(String uncheckedString: splitStrings) {
				if(!isWordGoodSource(uncheckedString)) {
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
	public static List<TextEntry> convertHibernateResultsToTextEntryList(List<Object[]> queryResults)
	{
		List<TextEntry> results = queryResults.stream().map(name -> new TextEntry(name))
				.collect(Collectors.toList());
		
		return results;
	}
	
	/**
	 * Filter strings to remove things like punctuation.  These are strings that have promise.
	 * 
	 * @param str
	 * @return
	 */
	protected static String filterString(String str) {
		String result = StringUtils.lowerCase(str);
		result = StringUtils.replaceEach(result, filteredPunctuation, 
				(String[]) Collections.<String>nCopies(filteredPunctuation.length, "").toArray(new String[0])); //creates an array duplicate "" values
		return result;
	}
	
	/**
	 * Removing strings we do not want in our repo.  These are typically strings that break immersion for humans
	 * or will get a bot banned if used on a real Twitch chatroom.
	 * 
	 * @param str
	 * @return
	 */
	protected static boolean isStringGoodSource(String str) {
		boolean result = true;
		if(StringUtils.contains(str, "@")) {
			//string is a private message to someone.  These are red flags for humans in Twitch.
			result = false;
		} else if(!CharMatcher.ASCII.matchesAllOf(str)) {
			//try to prevent wierd characters from leaking in
			result = false;
		} else if(StringUtils.contains(str, "http")) {
			//tired of all the url results I am getting
			result = false;
		} else if(!StringUtils.contains(str,  " ")) {
			//if there is no whitespace, then we have no sentence
			result = false;
		}
		
		return result;
	}
	
	protected static boolean isWordGoodSource(String str) {
		boolean result = true;
		if(sentenceRuiningWords.contains(str)) {
			//vulgar language filter
			result = false;
		} else if(emoteFilterSet.contains(str)) {
			result = false;
		} else if(str.length() > maxLength) {
			result = false;
		}
		
		return result;
	}
	
	protected static void loadHashSet(Set<String> set, String filename) {
		try(BufferedReader reader = new BufferedReader(new FileReader(new File(TextEntryFactory.class.getClassLoader().getResource(filename).getFile())))) {
			log.info("Loading the emote filter file.");
			String text = null;
			while((text = reader.readLine()) != null) {
				set.add(StringUtils.lowerCase(text));
			}
			log.info("Emote Filter log file load complete");
		} catch (IOException e) {
			log.error("Error loading the emote file.", e);
		} 
		
		return;
	}

}
