package com.catch42.Markov_Chatbot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor 
@NoArgsConstructor
public class ApplicationStatistics {

	public String cpuUsage;
	public String ramUsage;
	public String markovChainCount;

}
