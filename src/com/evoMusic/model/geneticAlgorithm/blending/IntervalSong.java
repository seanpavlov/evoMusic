package com.evoMusic.model.geneticAlgorithm.blending;

import java.util.ArrayList;
import java.util.List;

import jm.music.data.Part;
import jm.music.data.Score;

import com.evoMusic.model.Song;

public class IntervalSong {

    private double tempo;
    private List<IntervalTrack> intervalTracks;

    /**
     * Creates a new interval song with no tracks.
     * 
     * @param tempo
     *            The tempo that the song should have.
     */
    public IntervalSong(double tempo) {
        this.tempo = tempo;
        this.intervalTracks = new ArrayList<IntervalTrack>();
    }

    /**
     * Creates a new interval song from the given song.
     * 
     * @param song
     *            The song that will be used in creating this interval song.
     */
    public IntervalSong(Song song) {
        this.intervalTracks = new ArrayList<IntervalTrack>();
        Part[] allParts = song.getScore().getPartArray();
        this.tempo = song.getTempo();

        for (int partIndex = 0; partIndex < allParts.length; partIndex++) {

            intervalTracks.add(new IntervalTrack(song.getTrack(partIndex)));
        }
    }

    /**
     * Adds an interval track to this interval song.
     * 
     * @param track
     *            The track that will be added.
     */
    public void addTrack(IntervalTrack track) {
        this.intervalTracks.add(track);
    }

    /**
     * Gets a list of all the interval tracks in this interval song.
     * 
     * @return A list containing all interval tracks in this interval song.
     */
    public List<IntervalTrack> getTracks() {
        return intervalTracks;
    }

    /**
     * Gets a specific interval track from this interval song.
     * 
     * @param trackIndex
     *            The index of the requested interval track.
     * @return The interval track at the given index.
     */
    public IntervalTrack getTrack(int trackIndex) {
        return intervalTracks.get(trackIndex);
    }

    /**
     * Gets the tempo of this interval song.
     * 
     * @return The tempo of this interval song.
     */
    public double getTempo() {
        return tempo;
    }

    /**
     * Creates a Song object converted from this interval song.
     * 
     * @return A Song object with the properties of this interval song.
     */
    public Song toSong() {
        Song song = new Song(new Score());
        for (int i = 0; i < intervalTracks.size(); i++) {
            song.addTrack(intervalTracks.get(i).toTrack());
        }
        song.getScore().setTempo(tempo);
        return song;
    }
}
