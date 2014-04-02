package com.evoMusic.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import jm.music.data.Part;
import jm.music.data.Score;

import org.bson.types.ObjectId;

import com.evoMusic.util.TrackTag;

/**
 * A class that uses the properties from the Score class.
 * 
 */
public class Song {

    private final List<String> userTags = new ArrayList<String>();

    private List<Track> tracks = new LinkedList<>();
    private final Score score;
    
    private ObjectId dbRef = new ObjectId();

    /**
     * Creates a new Song object by copying all content from the given score.
     * 
     * @param score
     *            , where all musical notation will be copied from.
     */
    public Song(Score score) {
        this.score = score;
        for (Part p : score.getPartArray()) {
            tracks.add(new Track(p));
        }
        
    }
    
    public ObjectId getDbRef() {
        return dbRef;
    }

    public void setDbRef(ObjectId dbRef) {
        this.dbRef = dbRef;
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
     * @param index
     *            the index of the track in the score
     * @return the track as a Part object with the specified index.
     */
    public Track getTrack(int index) {
        return tracks.get(index);
    }
    
    public List<Track> getTracks() {
        return tracks;
    }
    
    /**
     * 
     * @return the number of tracks in this song's score.
     */
    public int getNbrOfTracks() {
        return tracks.size();
    }

    /**
     * 
     * @return the tempo for this song's score
     */
    public double getTempo() {
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
     * @return the tags describing the song, created by the user.
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
        getTrack(trackIndex).addTag(tag);
    }

    /**
     * Given a track index, returns the tag(a) of that track.
     * 
     * @param trackIndex
     *            The index of the track from which the tag is requested.
     * @return A set with the corresponding tags for the given track. Returns
     *         an empty list if track has no tags.
     */
    public Set<TrackTag> getTrackTags(int trackIndex)
            throws IndexOutOfBoundsException {
        return tracks.get(trackIndex).getTags();
    }

    /**
     * Given a tag, returns all tracks that has that tag.
     * 
     * @param tag
     *            The tag which will be looked up.
     * @return A list with all tracks corresponding to the given track if any,
     *         else an empty list.
     */
    public List<Track> getTaggedTracks(TrackTag tag) {
        List<Track> trackList = new ArrayList<Track>();
        for (Track track : tracks) {
            if (track.hasTag(tag)) {
                trackList.add(track);
            }
        }
        return trackList;
    }

    /**
     * Clears all user tags for this song and adds the given tags to it.
     * 
     * @param userTags
     *            The list of user tags to be assigned to this song.
     */
    public void setUserTags(List<String> userTags) {
        this.userTags.clear();
        for (String tag : userTags) {
            this.userTags.add(tag);
        }
    }

    /**
     * Adds an user tag to this song's list of user tags.
     * 
     * @param userTag
     *            The user tag to be added to this song.
     */
    public void addUserTag(String userTag) {
        this.userTags.add(userTag);
    }

    /**
     * Adds a new track to the song's score
     * @param newTagPart
     */
    public void addTrack(Track newTagPart) {
        score.add(newTagPart.getPart());
        tracks.add(newTagPart);
    }

}
