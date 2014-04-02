package com.evoMusic.model;

import java.util.HashSet;
import java.util.Set;

import jm.music.data.Part;

import com.evoMusic.util.TrackTag;

public class Track {

    private final Part songPart;
    private Set<TrackTag> ttags = new HashSet<>();
    
    public Track(Part part) {
        this.songPart = part;
    }

    public Set<TrackTag> getTags() {
        return ttags;
    }
    
    public boolean addTag(TrackTag tag) {
        return ttags.add(tag);
    }

    public void setTags(Set<TrackTag> ttags) {
        this.ttags = ttags;
    }
    
    public boolean hasTag(TrackTag tag) {
        return ttags.contains(tag);
    }
    
    public Part getPart() {
        return songPart;
    }
    
    
    
}
