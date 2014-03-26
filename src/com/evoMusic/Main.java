package com.evoMusic;

import jm.music.data.Part;
import jm.util.View;

import com.evoMusic.controller.InputController;
import com.evoMusic.model.Song;
import com.evoMusic.model.geneticAlgorithm.rating.ChordRepetitionRater;
import com.evoMusic.util.Translator;

public class Main {
    
    public final static String VERSION = "0.1";

    public static void main(String[] args) {
        //new InputController(args);
        
        Song song = Translator.INSTANCE.loadMidiToSong("midifiles/Elton_John-Candle_in_the_Wind.mid");
        ChordRepetitionRater rater = new ChordRepetitionRater();
        Part part = song.getScore().getPart(0);
        rater.ratePart(part);
    }
}
