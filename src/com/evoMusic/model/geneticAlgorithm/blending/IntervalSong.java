package com.evoMusic.model.geneticAlgorithm.blending;

import java.util.ArrayList;
import java.util.List;

import jm.music.data.Part;
import jm.music.data.Score;

import com.evoMusic.model.Song;

public class IntervalSong {

    private double tempo;
    private List<IntervalTrack> intervalTracks;
    
    public IntervalSong(double tempo) {
        this.tempo = tempo;
        this.intervalTracks = new ArrayList<IntervalTrack>();
    }

    public IntervalSong(List<int[]> intervals, List<double[]> rythmValues,
            List<double[]> durations, int[] instruments, int[] channels,
            double tempo, int[] firstNotes) {
        this.intervalTracks = new ArrayList<IntervalTrack>();
        for(int i = 0; i < intervals.size(); i++) {
            this.intervalTracks.add(new IntervalTrack(firstNotes[i], instruments[i],
                    channels[i], intervals.get(i), rythmValues.get(i), durations.get(i)));
        }
    }

    public IntervalSong(Song song) {
        this.intervalTracks = new ArrayList<IntervalTrack>();
        Part[] allParts = song.getScore().getPartArray();
        this.tempo = song.getTempo();

        for (int partIndex = 0; partIndex < allParts.length; partIndex++) {
            
            intervalTracks.add(new IntervalTrack(song.getTrack(partIndex)));
        }
    }
    
    public void addTrack(IntervalTrack track) {
        this.intervalTracks.add(track);
    }
    
    public List<IntervalTrack> getTracks() {
        return intervalTracks;
    }
    
    public IntervalTrack getTrack(int trackIndex) {
        return intervalTracks.get(trackIndex);
    }
    
    public double getTempo() {
        return tempo;
    }

    public Song toSong() {
        Song song = new Song(new Score());
        for (int i = 0; i < intervalTracks.size(); i++) {
            song.addTrack(intervalTracks.get(i).toTrack());
        }
        song.getScore().setTempo(tempo);
        return song;
    }
}
