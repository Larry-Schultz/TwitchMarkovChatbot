package com.catch42.Markov_Chatbot.util;

import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Util {
    private static Logger log = LoggerFactory.getLogger(Util.class);

    private static final Random random = new Random(System.currentTimeMillis());

    public static <T> T getRandomElementFromList(List<T> list) {
        if (list.size() == 0) {
            return null;
        } else if (list.size() == 1) {
            return list.get(0);
        } else if (list.size() == 2) {
            // since x mod 1 is always 0, let's do something different for size = 2. Going with 50 - 50 odds (literally)
            Integer randValue = Math.abs(getRandom().nextInt()) % 100;
            if (randValue < 50) {
                log.debug("1");
                return list.get(1);
            } else {
                log.debug("0");
                return list.get(0);
            }
        }

        int index = Math.abs(((int) getRandom().nextInt()) % (list.size() - 1));
        log.debug("index: " + new Integer(index).toString());
        T value = null;
        if (index >= 0) {
            value = list.get(index);
        }
        return value;
    }

    public static int getRandomInt(int bound) {
        return getRandom().nextInt(bound);
    }

    public static long getRandomLong(long bound) {
        long result = getRandom().nextLong() % bound;
        return result;
    }

    private static synchronized Random getRandom() {
        return random;
    }
}
