package com.evoMusic.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import jm.music.data.Part;
import jm.music.data.Phrase;
import com.evoMusic.util.TrackTag;

/**
 * A tracks wraps the jMusic Part class
 * Apart from the Part class is also has TrackTags to describe it.
 *
 */
public class Track {

    private final Part songPart;
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
            Phrase newPhrase = new Phrase();
            double[] rythms = phrase.getRhythmArray();
            double totalRythm = 0;
            int noteIndex = 0;
            
            while (totalRythm < from && noteIndex < rythms.length){
                totalRythm += rythms[noteIndex++];
            }
            
            while (totalRythm < (from + length) && noteIndex < rythms.length){
                totalRythm += rythms[noteIndex];
                newPhrase.add(phrase.getNote(noteIndex++));
            }
            
            part.add(newPhrase);
        }
        
        return new Track(part);
    }
    
    /**
     * Returns a list of the tracks split into segments of the 
     * length of the parameter bars
     * 
     * @param bars
     * @return list of all segements
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
