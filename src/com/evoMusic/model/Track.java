package com.evoMusic.model;

import java.util.HashSet;
import java.util.Set;

import jm.music.data.Part;

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
    
}
