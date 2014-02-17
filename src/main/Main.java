package main;

import java.io.Console;
import java.io.IOException;

import db.MongoAdapter;
import model.Song;
import model.translator.Translator;


public class Main {
            
    public static void main(String[] args) throws IOException {
    	String megamanPath = "midifiles/mm2wily1.mid";
    	Song testSong = Translator.INSTANCE.loadMidiToSong(megamanPath);
    	MongoAdapter mongo = new MongoAdapter();
    	mongo.insertSong(testSong);
    	
    	System.out.println("insert!?");
    	//Translator.INSTANCE.playSong(testSong);
    }

}
