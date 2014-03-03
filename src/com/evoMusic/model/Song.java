package com.evoMusic.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.evoMusic.util.TrackTag;

import jm.music.data.Part;
import jm.music.data.Score;

/**
 * A class that uses the properties from the Score class.
 * 
 */
public class Song {

    private final List<String> userTags = new ArrayList<String>();
    private final Map<Part, List<TrackTag>> trackTags = new HashMap<Part, List<TrackTag>>();

    private final Score score;

    /**
     * Creates a new Song object by copying all content from the given score.
     * 
     * @param score
     *            , where all musical notation will be copied from.
     */
    public Song(Score score) {
        this.score = score;
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
        addTagToTrack(getTrack(trackIndex), tag);
    }

    /**
     * Adds a TrackTag to the specified track. If the track already has the
     * given tag, no change will be made.
     * 
     * @param track
     *            The track in question.
     * @param tag
     *            The tag to be applied to the track.
     */
    public void addTagToTrack(Part track, TrackTag tag) {
        if (!trackTags.containsKey(track)) {
            trackTags.put(track, new ArrayList<TrackTag>());
        }
        if (!trackTags.get(track).contains(tag)) {
            trackTags.get(track).add(tag);
        }
    }

    /**
     * Given a track index, returns the tag(a) of that track.
     * 
     * @param trackIndex
     *            The index of the track from which the tag is requested.
     * @return A list with the corresponding tags for the given track. Returns
     *         an empty list if track has no tags.
     * @throws IndexOutOfBoundsException
     *             if the track index is outside the the number of tracks
     *             boundary.
     */
    public List<TrackTag> getTrackTags(int trackIndex)
            throws IndexOutOfBoundsException {
        if (trackIndex >= score.getPartList().size()) {
            throw new IndexOutOfBoundsException();
        }
        return getTrackTags(getTrack(trackIndex));
    }

    /**
     * Given a track, returns the tag(s) of that track.
     * 
     * @param track
     *            The track from which the tags is requested.
     * @return A list with the corresponding tags for the given track. Returns
     *         an empty list if track has no tags.
     */
    public List<TrackTag> getTrackTags(Part track) {
        if (!trackTags.containsKey(track)) {
            return new ArrayList<TrackTag>();
        } else {
            return trackTags.get(track);
        }
    }

    /**
     * Given a tag, returns all tracks that has that tag.
     * 
     * @param tag
     *            The tag which will be looked up.
     * @return A list with all tracks corresponding to the given track if any,
     *         else an empty list.
     */
    public List<Part> getTaggedTracks(TrackTag tag) {
        List<Part> trackList = new ArrayList<Part>();
        for (Part track : getScore().getPartArray()) {
            if (trackTags.containsKey(track)) {
                if (trackTags.get(track).contains(tag)) {
                    trackList.add(track);
                }
            }
        }
        return trackList;
    }

    // TODO this method is not possible to implement...
    public void setTrackTags(List<TrackTag> trackTags) {

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

}
