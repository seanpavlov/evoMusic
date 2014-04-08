package com.evoMusic.model;

import java.io.File;
import java.io.IOException;

import jm.audio.Instrument;
import jm.music.data.Part;
import jm.music.data.Score;
import jm.util.Play;
import jm.util.Read;
import jm.util.View;
import jm.util.Write;

public enum Translator {
    INSTANCE;

    /**
     * Loads a MIDI file and call the constructor of song object on load
     * success.
     * 
     * @param path
     *            the relative path to MIDI file
     * @return song, if a new song object or null if file was not found
     * @throws IOException
     */
    public Song loadMidiToSong(String path) {

        // split "./something/etc/file.midi"
        final String[] splitPath = path.split("/");
        // last item is "file.midi" and then "file"
        final String name = splitPath[splitPath.length - 1].split("\\.")[0];

        final File midiFile = new File(path);
        if (!midiFile.exists()) {
            return null;
        }

        // Using this method to avoid some annoying printing by jMusic.
        // It still prints "Convert SMF to JM". It appears to be hardcoded
        final Score score = Read.midiOrJmWithNoMessaging(midiFile);
        score.setTitle(name);
        return new Song(score);
    }

    /**
     * Saves a song to a specified location
     * 
     * @param song
     *            the song object to save
     * @param name
     *            the preferred name. if taken append with a number
     * @param dir
     *            the directory to save it to
     * @return the path to the saved MIDI file
     */
    public String saveSongToMidi(Song song, String name, String dir) {
        File theDir = new File(dir);
        if (!theDir.exists()) {
            theDir.mkdir();
        }

        int copy = 0;
        File outputFile = null;
        String path = "";
        do {
            path = dir + "/" + name + (copy != 0 ? "-" + copy : "") + ".mid";
            outputFile = new File(path);
            // if dupe, filename is appended "-1"
            copy++;
        } while (outputFile.exists());
        Write.midi(song.getScore(), path);
        return path;
    }

    /**
     * Saves a song the default location.
     * 
     * @param song
     *            The song object to save
     * @return the path to the saved MIDI file
     */
    public String saveSongToMidi(Song song, String name) {
        return saveSongToMidi(song, name, "./output/");

    }

    /**
     * Play a song object in JMusics built in player
     * 
     * @param song
     *            The song to be played
     */
    public void play(Song song) {
        Play.midi(song.getScore());
    }

    /**
     * Play a single track of a song
     * 
     * @param song
     */
    public void play(Part part) {
        Play.midi(part);
    }

    /**
     * Play a single track of a song.
     * 
     * @param track
     */
    public void play(Track track) {
        play(track.getPart());
    }

    /**
     * Show the structure of the song in JMusics built in MIDI display.
     * 
     * @param song
     *            The song to be shown
     */
    public void show(Song song) {
        View.show(song.getScore());
    }

    /**
     * Show the structure of the part in JMusics built in MIDI display.
     * 
     * @param part
     *            The song to be shown
     */
    public void show(Part part) {
        View.sketch(part);
    }

    /**
     * Closes all showing windows.
     */
    public void closeDisplayWindow() {
        // View.
    }
}