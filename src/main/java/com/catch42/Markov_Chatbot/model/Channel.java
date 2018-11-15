package com.catch42.Markov_Chatbot.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Channels")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Channel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    @JsonIgnore
    private Long id;

    @Column(name = "channelName", nullable = true, unique = true)
    private String channelName;

    public Channel(String channelName) {
        this.channelName = channelName;
    }

    public Channel(Object[] objArray) {
        this.id = (Long) objArray[0];
        this.channelName = (String) objArray[1];
    }
}
