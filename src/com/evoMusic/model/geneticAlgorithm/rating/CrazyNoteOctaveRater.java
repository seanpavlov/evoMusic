package com.evoMusic.model.geneticAlgorithm.rating;


import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;

import com.evoMusic.model.Song;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

public class CrazyNoteOctaveRater extends SubRater{
    
    private final double RATING_LOW_CONSTANT = 0.0005;
    private final double RATING_HIGH_CONSTANT = 0.001;
    private final int    OCTAVE_LOW_CONSTANT = 24;
    private final int    OCTAVE_HIGH_CONSTANT = 84;
    
    public CrazyNoteOctaveRater(double weight){
        super.setWeight(weight);
    }

    public double rate(Song song){
        
        /**Get list of lists of sorted notes by their start time*/
        List<List<Note>> sortedNotesLists = this.getSortedNoteList(song);
        
        /**Set inital rating to 1*/
        double rating = 1;
        
        /**Counters to keep track of how many crazy notes found and total number of notes*/
        double amountOfNotes = 0;
        double crazyCount = 0;
        /**Counter to keep track of how many crazy notes in a row*/
        int crazyInRow = 0;
        
        /**Iterate through every sorted notes list*/
        for(int position = 0; position < sortedNotesLists.size() - 1; position++){
            List<Note> notes = sortedNotesLists.get(position);
            /**For every note with current start time, check against every note in next start time list*/
            for(Note currentNote :  notes){
                /**If current note is in rest, jump to next itaration*/
                int currentNotePitch = currentNote.getPitch();
                if(currentNotePitch == Note.REST)
                    continue;
                for(Note nextNote : sortedNotesLists.get(position + 1)){
                    int nextNotePitch = nextNote.getPitch();
                    /**If next note is in rest, jump to next itaration*/
                    if(nextNotePitch == Note.REST)
                        continue;
                    /**Calculate difference in current note pitch and next note pitch*/
                    int difference = Math.abs(currentNotePitch - nextNotePitch);
                    /**Calculate subtration of rating depending on how big the difference is*/
                    if(difference > OCTAVE_HIGH_CONSTANT){
                        double negRate  = difference * RATING_HIGH_CONSTANT;
                        /**Keep track how many crazy notes in row, subtracks more from rating if many in a row*/
                        crazyInRow++;
                        rating -= (crazyInRow > 1) ? negRate *= crazyInRow : negRate;
                        crazyCount++;                       
                    }else if(difference >= OCTAVE_LOW_CONSTANT){
                        double negRate  = difference * RATING_LOW_CONSTANT;
                        crazyInRow++;
                        rating -= (crazyInRow > 1) ? negRate *= crazyInRow : negRate;
                        crazyCount++;
                    }else{
                        crazyInRow = 0;
                    }                   
                }
                /**Keep track of how many note in total*/
                amountOfNotes++;
            }
        }
        /**Calculate difference between crazy notes found and total nbr of notes*/
        double amountOf = crazyCount/amountOfNotes;
        /**If difference between total and crazy notes found is larger than rating, set rating to 0*/
        rating = (rating <= amountOf) ? 0 : (rating - amountOf);
        
        return  rating;
    }
    
    
    
    /**Sorts every note in song according to their start time.
     * @param song Song containging the notes to sort
     * @return List list of list containing the sorted notes*/
    private List<List<Note>> getSortedNoteList(Song song) {
        
        ListMultimap<Double, Note> noteMap = ArrayListMultimap.create();
        SortedSet<Double> startTimeList = new TreeSet<Double>();
        List<List<Note>> sortedNoteList = new ArrayList<List<Note>>();
        double songLength;
        for(Part part : song.getScore().getPartArray()){
            for (Phrase phrase : part.getPhraseArray()) {
                songLength = phrase.getStartTime();
                for (Note note : phrase.getNoteArray()) {
                    songLength += note.getRhythmValue();
                    noteMap.put(songLength, note);
                    startTimeList.add(songLength);                
                }
            }
        }
        for(Double startTime : startTimeList) {
            sortedNoteList.add(noteMap.get(startTime));
        }    
        return sortedNoteList;
    }
}
