package com.evoMusic.model;

import java.util.ArrayList;
import java.util.List;

import jm.JMC;
import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;

import com.evoMusic.util.TrackTag;

/**
 * A tracks wraps the jMusic Part class
 * Apart from the Part class is also has TrackTags to describe it.
 *
 */
public class Track {
    private Part songPart;
    private TrackTag tag;
    
    /**
     * @param part The tracks Part class
     */
    public Track(Part part) {
        this.songPart = part;
    }

    /**
     * 
     * @return The tag that this track has been tagged with
     */
    public TrackTag getTag(){
        return tag;
    }
    
    /**
     * Adds a tag to this track
     * @param tag Tag to be added
     */
    public void addTag(TrackTag tag){
        this.tag = tag;  
    }

    /**
     * Checks if the track has the given tag
     * @param tag The tag to look for
     * @return true if it the track has the given tag, false otherwise
     */
    public boolean hasTag(TrackTag tag){
        return (this.tag == null) ? false :
                                    this.tag.equals(tag);
    }
    
    /**
     * 
     * @return The Part object that the Track is wrapping
     */
    public Part getPart() {
        return songPart;
    }
    
    /**
     * Merges other track into this track
     * @param other track to mutate into this track
     * @param rhythmVal where to merge
     */
    public void merge(Track other, double rhythmVal) {
        Part otherP = other.getPart();
        for (Phrase p : otherP.getPhraseArray()) {
            p.setStartTime(p.getStartTime() + rhythmVal);
        }
        songPart.addPhraseList(otherP.getPhraseArray());
    }
    
    /**
     * Inserts a track into this track
     * Existing notes in this track will be will be shifted so that notes
     * 
     * @param other Track to be inserted into this track
     * @param rhythmVal position where the first note should be inserted
     */
    public void insert(Track other, double rhythmVal) {
        Part otherPart = other.getPart();
        if (songPart.getEndTime() <= rhythmVal) {
            
            for (Phrase p : otherPart.getPhraseArray()) {
                p.setStartTime(p.getStartTime() + rhythmVal);
            }
            songPart.addPhraseList(otherPart.getPhraseArray());
        } else {
            Part start = songPart.copy(0, rhythmVal, true, true, false);
            Part end = songPart.copy(rhythmVal, songPart.getEndTime(), true, true, false);
            songPart = start;
            appendPhraseList(otherPart.getPhraseArray(), start.getEndTime());
            appendPhraseList(end.getPhraseArray(), songPart.getEndTime());
        }
    }
    
    private void appendPhraseList(Phrase[] phrases, double start) {
        for (Phrase p : phrases) {
            p.setStartTime(start + p.getStartTime());
            songPart.add(p);
        }
    }
    
    public void printRoll() {
        printRoll(songPart);
    }
    
    public static void printRoll(Part part) {
        char restChar = '-';
        char emptyChar = '.';
        char noteChar = 'x';
        char useChar = 0;
        double gridSize = part.getShortestRhythmValue();
        for (Phrase p : part.getPhraseArray()) {
            
            for (double pos = 0; pos < p.getStartTime(); pos+=gridSize ) {
                System.out.print(emptyChar);
            }
            for (Note n : p.getNoteArray()) {
                if (n.getPitch() == JMC.REST) {
                    useChar = restChar;
                } else {
                    useChar = noteChar;
                }
                for(double len = 0; len < n.getRhythmValue(); len += gridSize) {
                    System.out.print(useChar);
                }
            }
            for(double pos = p.getEndTime(); pos < part.getEndTime(); pos += gridSize) {
                System.out.print(emptyChar);
            }
            System.out.println();
        }
    }

    /**
     * Returns a new Track containing all notes from parameter "from" 
     * to from+length
     * 
     * @param from, start value of segment
     * @param length, length of segment
     * @return
     */
    public Track getSegment(double from, double length){
        Part part = new Part();
        Phrase[] phrases = getPart().getPhraseArray();
        
        if (from < 0 || from > getPart().getEndTime()){
            throw new IllegalArgumentException("Requested a segment starting at: " + from + ". The segment must be in in the interval 0 and " + songPart.getEndTime() + "!");
        }
        if (phrases.length < 1) {
            throw new IllegalArgumentException("Can't get segment from an empty Track!");
        }
       
        return new Track(getPart().copy(from, from+length, true,true,false));
    }
    
    /**
     * Returns a list of the tracks split into segments of the 
     * length of the parameter bars
     * 
     * @param bars
     * @return list of all segments
     */
    public List<Track> getSegments(double bars){
        List<Track> tracks = new ArrayList<Track>();
        int i = 0;
        
        while(i < getPart().getEndTime()){
            tracks.add(getSegment(i,bars));
            i+=bars;
        }
        return tracks;
    }
}
