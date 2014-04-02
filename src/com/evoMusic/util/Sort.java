package com.evoMusic.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;

import com.evoMusic.model.Song;

public abstract class Sort {
    
    private static abstract class PhraseComparator implements Comparator<Phrase> {
 
        protected abstract double getDiff(Phrase o1, Phrase o2);
        
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
            allPhrases.addAll(Arrays.asList(part.getPhraseArray()));
        }
        Collections.sort(allPhrases, comparator);
        return allPhrases;
    }
    
    public static List<List<Note>> getSortedNoteList(Part part) {
        Map<Double, List<Note>> noteMap = new HashMap<Double, List<Note>>();
        List<Double> startTimeList = new ArrayList<Double>();
        List<List<Note>> sortedNoteList = new ArrayList<List<Note>>();
        double songLength;
        for (Phrase phrase : part.getPhraseArray()) {
            songLength = phrase.getStartTime();
            for (Note note : phrase.getNoteArray()) {
                if (!noteMap.containsKey(songLength)) {
                    noteMap.put(songLength, new ArrayList<Note>());
                    startTimeList.add(songLength);
                }
                noteMap.get(songLength).add(note);
                songLength += note.getRhythmValue();
            }
        }
        Collections.sort(startTimeList);
        for(Double startTime : startTimeList) {
            sortedNoteList.add(noteMap.get(startTime));
        }
        return sortedNoteList;
    }
}
