package com.catch42.Markov_Chatbot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.catch42.Markov_Chatbot.model.Channel;

public interface ChannelRepository extends CrudRepository<Channel, Long> {
	
	/**
	 * Get channel by name
	 * @param name
	 * @return
	 */
	@Query(" SELECT id" +
		   " FROM Channel" +
	       " WHERE channelName = :name "
	      )
	public Long getChannelByName(String name);
	
	/**
	 * Get list of all channels
	 * @return
	 */
	@Query(" SELECT id, channelName " +
		   " FROM Channel "
		  )
	public List<Object[]> getAllChannels();
}
