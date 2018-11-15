package com.catch42.Markov_Chatbot.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.CharMatcher;

public abstract class MarkovGeneratorServiceAbstract implements MarkovGeneratorService {
    private static final Logger log = LoggerFactory.getLogger(MarkovGeneratorServiceImpl.class);

    protected int maxWordLength;

    protected String[] filteredPunctuation;
    protected Set<String> sentenceRuiningWordsSet = new HashSet<>();
    protected Set<String> emoteFilterSet = new LinkedHashSet<>();

    public MarkovGeneratorServiceAbstract() {
        this.loadFilters();
    }

    /**
     * Filter strings to remove things like punctuation. These are strings that have promise.
     * 
     * @param str
     * @return
     */
    protected String filterString(String str) {
        String result = StringUtils.lowerCase(str);
        result = StringUtils.replaceEach(result, this.filteredPunctuation,
                (String[]) Collections.<String> nCopies(this.filteredPunctuation.length, "").toArray(new String[0])); // creates
                                                                                                                      // an
                                                                                                                      // array
                                                                                                                      // duplicate
                                                                                                                      // ""
                                                                                                                      // values
        return result;
    }

    /**
     * Removing strings we do not want in our repo. These are typically strings that break immersion for humans or will
     * get a bot banned if used on a real Twitch chatroom.
     * 
     * @param str
     * @return
     */
    protected boolean isStringGoodSource(String str) {
        boolean result = true;
        if (StringUtils.contains(str, "@")) {
            // string is a private message to someone. These are red flags for humans in Twitch.
            result = false;
        } else if (!CharMatcher.ASCII.matchesAllOf(str)) {
            // try to prevent wierd characters from leaking in
            result = false;
        } else if (StringUtils.contains(str, "http")) {
            // tired of all the url results I am getting
            result = false;
        } else if (!StringUtils.contains(str, " ")) {
            // if there is no whitespace, then we have no sentence
            result = false;
        }

        return result;
    }

    /**
     * Checks against a list of words to see if this particular word should be used as part of a markov chain.
     * 
     * @param str
     * @return
     */
    protected boolean isWordGoodSource(String str) {
        boolean result = true;
        if (sentenceRuiningWordsSet.contains(str)) {
            // vulgar language filter
            result = false;
        } else if (emoteFilterSet.contains(str)) {
            result = false;
        } else if (str.length() > maxWordLength) {
            result = false;
        }

        return result;
    }

    /**
     * Load filters from files.
     */
    protected void loadFilters() {
        this.loadFileAndPutIntoCollection(this.emoteFilterSet, "markovGeneratorFilters/emotes/simpleEmotes.txt");
        this.loadFileAndPutIntoCollection(this.emoteFilterSet, "markovGeneratorFilters/emotes/emotes.txt");
        this.loadFileAndPutIntoCollection(this.emoteFilterSet, "markovGeneratorFilters/emotes/subscriberEmotes.txt");
        log.info("All filtered emotes loaded, count: {}", new Integer(this.emoteFilterSet.size()).toString());

        this.loadFileAndPutIntoCollection(this.sentenceRuiningWordsSet,
                "markovGeneratorFilters/sentenceRuiningWords.txt");
        log.info("Sentence ruining words loaded.  Count: {}",
                new Integer(this.sentenceRuiningWordsSet.size()).toString());

        List<String> filteredPunctuationList = new ArrayList<>();
        this.loadFileAndPutIntoCollection(filteredPunctuationList, "markovGeneratorFilters/filteredPunctuation.txt");
        this.filteredPunctuation = filteredPunctuationList.toArray(new String[filteredPunctuationList.size()]);
        log.info("Filtered Punctuation file loaded.  Count: {}", new Integer(filteredPunctuation.length).toString());
    }

    /**
     * Loads emotes from a file and places them in the provded Set.
     * 
     * @param collection
     * @param filename
     */
    protected void loadFileAndPutIntoCollection(Collection<String> collection, String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(
                new File(MarkovGeneratorServiceImpl.class.getClassLoader().getResource(filename).getFile())))) {
            log.info("Loading the emote filter file.");
            String text = null;
            while ((text = reader.readLine()) != null) {
                collection.add(StringUtils.lowerCase(text));
            }
            log.info("Emote Filter log file load complete");
        } catch (IOException e) {
            log.error("Error loading the emote file.", e);
        }

        return;
    }

}
