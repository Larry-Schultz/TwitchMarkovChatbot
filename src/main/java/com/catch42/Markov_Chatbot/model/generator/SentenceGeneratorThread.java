package com.catch42.Markov_Chatbot.model.generator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.catch42.Markov_Chatbot.util.Router;

public class SentenceGeneratorThread extends Thread {
	private static Logger log = LoggerFactory.getLogger(SentenceGeneratorThread.class);

	private static final Integer threads = 20;
	
	private Router<String> generatedSentenceRouter;
	private SentenceGenerator sentenceGenerator;
	
	public SentenceGeneratorThread(Router<String> generatedSentenceRouter, SentenceGenerator sentenceGenerator) {
		super(SentenceGeneratorThread.class.getName());
		this.generatedSentenceRouter = generatedSentenceRouter;
		this.sentenceGenerator = sentenceGenerator;
	}
	
	@Value("${sentenceGenerator.numberOfThreads}")
	private int numberOfThreads;
	
	@Override
	public void run() {
		log.info("Starting SentenceGeneratorThread");
		try {
			//let's generate a string
			String starterString = "to";
			boolean first = true;
			while(true) {
				Thread.sleep(10000);
				//use the original starterString the first time.
				if(!first) {
					starterString = sentenceGenerator.getRandomStarterString(); 
				} else {
					first = false;
				}
				
				log.info("starter string: " + starterString);
				String sentence = SentenceGeneratorManager.getBestGeneratedText(sentenceGenerator, this.numberOfThreads, null);
				this.generatedSentenceRouter.sendDataToQueues(sentence);
			}
		} catch(InterruptedException e) {
			log.info("stopping SentenceGeneratorThread");
		}
		
		return;
	}

}
