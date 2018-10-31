package com.catch42.Markov_Chatbot.util;

import java.util.Arrays;
import java.util.List;
import java.util.Queue;

public class Router<T> {
	
	private List<Queue<T>> queues;
	
	@SafeVarargs
	public Router(Queue<T>... queues) {
		this.queues = Arrays.asList(queues);
	}
	
	public void sendDataToQueues(T data) {
		for(Queue<T> queue: this.queues) {
			if(data != null) {
				queue.add(data);
			}
		}
		
		return;
	}

}
