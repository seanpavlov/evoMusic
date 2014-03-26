package com.evoMusic;

import com.evoMusic.controller.InputController;
import com.evoMusic.util.Translator;

public class Main {
    
    public final static String VERSION = "0.1";

    public static void main(String[] args) {
        //new InputController(args);
        
        Song song = Translator.INSTANCE.loadMidiToSong(path);
        
    }
}
