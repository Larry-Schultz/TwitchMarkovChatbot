package com.catch42.Markov_Chatbot.model.generator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SentenceGeneratorManager {

	public static Integer maxNumberOfThreads = 25;
	
	public static String getBestGeneratedText(SentenceGenerator generatorRef, Integer numberOfTries, String starter) {
		//leave if we don't need to try
		Integer actualNumberOfTries = numberOfTries;
		if(numberOfTries == null || numberOfTries <= 0) {
			return null;
		} else if(numberOfTries > maxNumberOfThreads) {
			//ensure we don't generate too many threads
			actualNumberOfTries = maxNumberOfThreads;
		}
		//set up threads to create sentences
		List<Future<String>> futureResultsList = new ArrayList<Future<String>>();
		ExecutorService service = Executors.newFixedThreadPool(actualNumberOfTries, Executors.defaultThreadFactory());
		int numberOfNonStarterRuns = numberOfTries;
		
		//if we have a starter, use it for the first value
		if(starter != null) {
			Future<String> futureResult = service.submit(new SentenceGeneratorCallable(generatorRef, starter));
			futureResultsList.add(futureResult);
			numberOfNonStarterRuns--;
		}
		
		//the rest of the attempts
		for(int i = 0; i<numberOfNonStarterRuns; i++) {
			Future<String> futureResult = service.submit(new SentenceGeneratorCallable(generatorRef));
			futureResultsList.add(futureResult);
		}
		
		//wait for the sentences to be done generating
		service.shutdown();
		
		//convert to real value
		List<String> results = new ArrayList<String>();
		for(Future<String> future: futureResultsList) {
			try {
				results.add(future.get());
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch(NullPointerException e) {
				//do nothing, since having null results is ok
			}
		}
		
		//find the longest result
		Optional<String> longestResult = results.stream().max(Comparator.comparingInt(String::length));
		if(longestResult.isPresent()) {
			return longestResult.get();
		} else {
			return null;
		}
		
	}
}

class SentenceGeneratorCallable implements Callable<String> {

	private SentenceGenerator sentenceGeneratorRef;
	private String starter;
	
	public SentenceGeneratorCallable(SentenceGenerator generatorRef) {
		this.sentenceGeneratorRef = generatorRef;
	}
	
	public SentenceGeneratorCallable(SentenceGenerator generatorRef, String starter) {
		this.sentenceGeneratorRef = generatorRef;
		this.starter = starter;
	}
	
	@Override
	public String call() throws Exception {
		if(starter == null) {
			starter = this.sentenceGeneratorRef.getRandomStarterString(); 
		}
		String sentence = this.sentenceGeneratorRef.generateString(this.starter);
		
		return sentence;
	}
	
}
