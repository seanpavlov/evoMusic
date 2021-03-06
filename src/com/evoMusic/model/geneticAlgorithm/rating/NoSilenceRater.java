package com.evoMusic.model.geneticAlgorithm.rating;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;

import com.evoMusic.model.Song;
import com.evoMusic.parameters.P;
import com.evoMusic.util.Sort;

public class NoSilenceRater extends SubRater {
    
    private class NoteEvent implements Comparable<NoteEvent> {
        
        private static final double PRECISION = 0.00001;
        
        /** If false, means note off. */
        private boolean noteOn;
        private double startTime;
        
        public NoteEvent(boolean noteOn, double startTime) {
            this.noteOn = noteOn;
            this.startTime = startTime;
        }

        @Override
        public int compareTo(NoteEvent arg0) {
            double diff = this.startTime - arg0.startTime;
            if (Math.abs(diff) < PRECISION) {
                return 0;
            }
            if (diff < 0) {
                return -1;
            } else {
                return 1;
            }
        }
        
    }
    
    public NoSilenceRater(double targetRating){
        this.setTargetRating(targetRating);
        super.setInfluenceMultiplier(P.RATER_NO_SILENCE_INFLUENCE_MUL);
    }
    
    /**
     * This rater rates the amount of silence in a song, this includes silence between 
     * phrases and silence generated by only rest notes played tohether
     */
    @Override
    public double rate(Song song){
        if (song.getScore().getEndTime() == 0.0) {
            return 0;
        }
        List<NoteEvent> noteEvents = new ArrayList<NoteEvent>();
        
        double currentStartTime;
        for (Part part : song.getScore().getPartArray()) {
            for (Phrase phrase : part.getPhraseArray()) {
                currentStartTime = phrase.getStartTime();
                for (Note note : phrase.getNoteArray()) {
                    if (!note.isRest()) {
                        noteEvents.add(new NoteEvent(true, currentStartTime));
                        noteEvents.add(new NoteEvent(false, currentStartTime+note.getDuration()));
                    }
                    currentStartTime += note.getRhythmValue();
                }
            }
        }
        
        Collections.sort(noteEvents);
        double totalSilence = 0;
        double lastStartTime = 0;
        int notesPlaying = 0;

        for (NoteEvent noteEvent : noteEvents) {
            if (notesPlaying < 1) {
                totalSilence += noteEvent.startTime - lastStartTime;
            }
            if (noteEvent.noteOn) {
                notesPlaying++;
            } else {
                notesPlaying--;
            }
            lastStartTime = noteEvent.startTime;
        }
        // adding silence for last notes.
        totalSilence += song.getScore().getEndTime() - lastStartTime;
        return 1 - (totalSilence/song.getScore().getEndTime());
        
//        double songDuration = song.getScore().getEndTime();
        
//        double silenceRest = restSilence(song);
//        double silencePhrase = phraseSilence(song);
        
//        if (silencePhrase + silenceRest > songDuration){
//            return 0;
//        } else {
//            return 1 - (silencePhrase + silenceRest) / songDuration;
//        }
    }
    
    /**
     * Check the amount of silence generated by rest notes
     * 
     * @param song
     * @return
     */
    private double restSilence(Song song){
        double silence = 0;
        double duration = 0;
        boolean firstIteration = true;
        
        for (List<Note> simNotes : Sort.getSortedNoteList(song)){
            double tmpDuration = 0;
            boolean allRest = true;
            double startTime = 0;
            for (Note n : simNotes){
                Phrase thisPhrase = n.getMyPhrase();
                if (!n.isRest()){
                    startTime =
                            thisPhrase.getStartTime() + 
                            thisPhrase.getNoteStartTime(thisPhrase.getNoteList().indexOf(n));
                    System.out.println(startTime);
                    allRest = false;
                    if (n.getDuration() > tmpDuration){
                        tmpDuration = n.getDuration();
                    }
                }
            }
            
            if(!allRest){ 
                if (firstIteration){
                    firstIteration = false;
                    duration = startTime + tmpDuration;
                } else if (startTime > duration){
                    silence += startTime - duration;
                    duration = startTime + tmpDuration;
                } else if (startTime == duration){
                    duration = startTime + tmpDuration;
                }
            }
        }

        return silence;
    }

    /**
     * Check the amount of silence generated between phrases
     * 
     * @param song
     * @return
     */
    private double phraseSilence(Song song) {
        List<Phrase> allPhrases = Sort.sortPhrases(song, Sort.PHRASE_START_TIME_COMPARATOR);
        double longestSilence = 0.0;
        
        for (int i = 0; i < allPhrases.size()-1; i++){
            Phrase thisPhrase = allPhrases.get(i);
            Phrase nextPhrase = allPhrases.get(i+1);
            double silenceInterval = nextPhrase.getStartTime() - thisPhrase.getEndTime();
            
            if (silenceInterval > longestSilence){
                longestSilence = silenceInterval;
            }
        }
        
        return longestSilence;
    }
}

        