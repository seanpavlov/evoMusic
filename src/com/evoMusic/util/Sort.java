package com.evoMusic.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jm.music.data.Part;
import jm.music.data.Phrase;

import com.evoMusic.model.Song;

public abstract class Sort {
    
    public static abstract class PhraseComparator implements Comparator<Phrase> {
 
        public abstract double getDiff(Phrase o1, Phrase o2);
        
        @Override
        public int compare(Phrase o1, Phrase o2) {
            double diff = getDiff(o1, o2);
            if (diff > 0) {
                return 1;
            } else if (diff < 0) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    /**
     * A comparator that compares phrases on their start time.
     */
    public static final Comparator<Phrase> PHRASE_START_TIME_COMPARATOR = new PhraseComparator() {

        @Override
        public double getDiff(Phrase o1, Phrase o2) {
            return o1.getStartTime() - o2.getStartTime();
        }
    };
    
    /**
     * A comparator that compares phrases on their end time.
     */
    public static final Comparator<Phrase> PHRASE_END_TIME_COMPARATOR = new PhraseComparator() {
        
        @Override
        public double getDiff(Phrase o1, Phrase o2) {
            return o1.getEndTime() - o2.getEndTime();
        }
    };


    /**
     * Sorts all phrases in a song on the given comparator and returns the sorted
     * array of phrases.
     * 
     * @param song  The song which hold the phrases to be sorted.
     * @param comparator    The comparator that will be used.
     * @return a list of sorted phrases.
     */
    public static List<Phrase> sortPhrases(Song song, Comparator<Phrase> comparator) {
        List<Phrase> allPhrases = new ArrayList<Phrase>();

        for (Part part : song.getScore().getPartArray()) {
            for (Phrase phrase : part.getPhraseArray()) {
                allPhrases.add(phrase);
            }
        }
        Collections.sort(allPhrases, comparator);
        return allPhrases;
    }
}
