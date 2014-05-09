package com.evoMusic.model;

import java.util.ArrayList;
import java.util.HashSet;
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

    private final Set<String> userTags = new HashSet<String>();

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
    
    public Song copy() {
        Song copy = new Song(score.copy());
        for (int i = 0; i < copy.getNbrOfTracks(); i++) {
            copy.getTrack(i).setTag(tracks.get(i).getTag());
        }
        
        copy.setUserTags(userTags);
        
        return copy;
    }
    
    /**
     * @return the object's reference id in the database.
     */
    public ObjectId getDbRef() {
        return dbRef;
    }

    /**
     * The dbRef is used to uniquely identify a Song in the database
     * An id is created when the song is instantiated.  
     * @param dbRef set the object's reference id
     */
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
    
    /**
     * 
     * @return The song's tracks
     */
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
    public Set<String> getUserTags() {
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
        getTrack(trackIndex).setTag(tag);
    }

    /**
     * Given a track index, returns the tag of that track.
     * 
     * @param trackIndex
     *            The index of the track from which the tag is requested.
     * @return A tag for the given track. Returns
     *         an null if track has no tag.
     */
    public TrackTag getTrackTag(int trackIndex)
            throws IndexOutOfBoundsException{
        return tracks.get(trackIndex).getTag();
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
     *            The set of user tags to be assigned to this song.
     */
    public void setUserTags(Set<String> userTags) {
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

    /**
     * Removes a track at a specific index
     * 
     * @param i Track index
     * @return Track 
     *           Track that whas removed, returns null if track could not be removed
     */
    public Track removeTrack(int i) {
        if(tracks.size() > i && i >= 0){
            Track trackToRemove = tracks.get(i);
            if(tracks.remove(trackToRemove)){
                int nbrBefore = score.length();
                score.removePart(i);
                int nbrAfter = score.length();
                if(nbrBefore - 1 == nbrAfter){
                    return trackToRemove;
                }else{
                    tracks.add(i, trackToRemove);
                    return null;
                }
            }
            return null;
        }
        return null;
    }

    /**
     * Removes a given track from the song
     * 
     * @param track Track to be removed
     * @return boolean true if track was removed false otherwise
     */
    public boolean removeTrack(Track track) {
        if(tracks.remove(track)){
            int nbrBefore = score.length();
            score.removePart(track.getPart());
            int nbrAfter = score.length();
            if(nbrBefore - 1 == nbrAfter){
                return true;
            }else{
                tracks.add(track);
                return false;
            }
        }
        return false;
    }

    /**
     * Flatterns each track in the song
     */
    public void flattern(){
        for (Track track : getTracks()){
            track.flattern();
        }
    }
}
