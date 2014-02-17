package model;

import java.util.ArrayList;
import java.util.List;

import jm.music.data.Part;
import jm.music.data.Score;
import enumerators.TrackTag;

/**
 * A class that inherits most of its properties from the Score class.
 * 
 */
public class Song extends Score {

    /**
     * Specifies the version of the class so that version mismatch won't occur
     * during deserialization.
     */
    private static final long serialVersionUID = 1L;
    
    private final String path ;

    private final List<String> userTags = new ArrayList<String>();
    
    /**
     * Creates a new Song object by copying all content from the given score.
     * 
     * @param score
     *            , where all musical notation will be copied from.
     */
    public Song(Score score, String title, String path) {
        super();
        this.path = path;
        Score scoreCopy = score.copy();
        this.setTitle(title);
        this.addPartList(scoreCopy.getPartArray());
        this.setTempo(scoreCopy.getTempo());
        this.setTimeSignature(scoreCopy.getNumerator(),
                scoreCopy.getDenominator());
    }
    
    /**
     * 
     * @return MIDI file path that was used to create the song
     */
    public String getPath() {
        return path;
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
}
