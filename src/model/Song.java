package model;

import java.util.ArrayList;
import java.util.List;

import jm.music.data.Part;
import jm.music.data.Score;
import jm.util.Write;
import enumerators.TrackTag;

/**
 * A class that uses the properties from the Score class.
 * 
 */
public class Song {

    private final List<String> userTags = new ArrayList<String>();
    
    private final Score score;
    
    /**
     * Creates a new Song object by copying all content from the given score.
     * 
     * @param score
     *            , where all musical notation will be copied from.
     */
    public Song(Score score) {
        this.score = score.copy();
    }
    
    /**
     * 
     * @return the title of the score
     */
    public String getTitle() {
        return score.getTitle();
    }
    
    /**
     * 
     * @param index the index of the track in the score
     * @return the track as a Part object with the specified index.
     */
    public Part getTrack(int index) {
        return score.getPart(index);
    }
    
    /**
     * 
     * @return the number of tracks in this song's score.
     */
    public int getNbrOfTracks() {
        return score.getPartArray().length;
    }
    
    /**
     * 
     * @return the tempo for this song's score
     */
    public double getTempo(){
        return score.getTempo();
    }
    
    /**
     * 
     * @return the score for this song
     */
    public Score getScore() {
        return score;
    }
    
    /**
     * 
     * @return the tags describing the song, created by the user
     */
    public List<String> getUserTags() {
        return userTags;
    }
    

    /**
     * Adds a TrackTag to the specified track.
     * 
     * @param trackNo
     *            The index of the track in question.
     * @param tag
     *            The tag to be applied to the track.
     */
    public void addTagToTrack(int trackIndex, TrackTag tag) {
        // TODO implement tracktagging.
    }

    /**
     * Given a track, returns the tag of that track.
     * 
     * @param trackIndex
     *            The index of the track from which the tag is requested.
     * @return The corresponding tag for the given track index if any, otherwise
     *         NONE
     */
    public TrackTag getTrackTag(int trackIndex) {
        TrackTag t = TrackTag.NONE;
        return TrackTag.NONE;
        // TODO implement actual code.
        // What happens if trackIndex is out of bounds? throw
        // IndexOutOfBoundsException?
    }

    /**
     * Given a tag, returns all tracks that has that tag.
     * 
     * @param tag
     *            The tag which will be looked up.
     * @return A list with all tracks corresponding to the given track if any,
     *         else an empty list.
     */
    public ArrayList<Part> getTaggedTracks(TrackTag tag) {
        return null;
        // TODO implement actual code.
    }

    public void setTrackTags(List<TrackTag> trackTags) {
        // TODO Auto-generated method stub
        
    }

    public void setUserTags(List<String> userTags) {
        // TODO Auto-generated method stub
        
    }

    
}
