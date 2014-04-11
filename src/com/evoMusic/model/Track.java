package com.evoMusic.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private Set<TrackTag> ttags = new HashSet<>();
    
    /**
     * @param part The tracks Part class
     */
    public Track(Part part) {
        this.songPart = part;
    }

    /**
     * 
     * @return The set of tags that this track has been tagged with
     */
    public Set<TrackTag> getTags() {
        return ttags;
    }
    
    /**
     * Adds a tag to the set if it doesn't already exist
     * @param tag Tag to be added
     * @return true if the tag was added, false otherwise
     */
    public boolean addTag(TrackTag tag) {
        return ttags.add(tag);
    }

    /**
     * Checks if the track has the given tag
     * @param tag The tag to look for
     * @return true if it the track has the given tag, false otherwise
     */
    public boolean hasTag(TrackTag tag) {
        return ttags.contains(tag);
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
//            Part start = songPart.copy(0, rhythmVal);
            Part start = songPart.copy(0, rhythmVal, true, true, false);
            Part end = songPart.copy(rhythmVal, songPart.getEndTime(), true, true, false);
//            Part end = songPart.copy(rhythmVal, songPart.getEndTime());
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
        char restChar = '.';
        char noteChar = 'x';
        char useChar = 0;
        double gridSize = part.getShortestRhythmValue();
        for (Phrase p : part.getPhraseArray()) {
            
            for (double pos = 0; pos < p.getStartTime(); pos+=gridSize ) {
                System.out.print(restChar);
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
                System.out.print(restChar);
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
        
        if (from < 0 || from >= getPart().getEndTime() || phrases.length < 1){
            System.err.println("nope");
        }
       
        for (Phrase phrase : phrases){
            part.add(phrase.copy(from, from + length, true, true, true));
        }
        
        return new Track(part);
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
