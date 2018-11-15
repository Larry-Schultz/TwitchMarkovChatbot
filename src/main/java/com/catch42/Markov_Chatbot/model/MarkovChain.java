package com.catch42.Markov_Chatbot.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "MarkovChain", 
		indexes= {@Index(name="markovChainIdIndex", columnList="id", unique=true), @Index(name="markovChainKeyIndex", columnList="key", unique=false)},
		uniqueConstraints = {@UniqueConstraint(columnNames = {"key", "nextKey"})})
@Data
@AllArgsConstructor 
@NoArgsConstructor
public class MarkovChain {

	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id", nullable=false)
	@JsonIgnore
    private Long id;
	
	@Column(name="key", nullable=true)
	private String key;
	
	@Column(name="nextKey", nullable=true)
	private String nextKey;
	
	public MarkovChain(String key, String text, Integer remainingTextLength) {
		this.key = key;
		this.nextKey = text;
	}
	
	public MarkovChain(Object[] obj) {
		this.id = (Long) obj[0];
		this.key = (String)obj[1];
		this.nextKey = (String)obj[2];
	}
	
	
}
