package jUnit.translator;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.evoMusic.model.Song;
import com.evoMusic.model.Translator;

public class TranslatorTest {

    @Before
    public void setUpSong() throws IOException {
        
    }
    
    @Test
    public void testSaveAndLoadEq(){
        String name = "Norway";
        String path1 = "./midifiles/" + name + ".mid";
        String path2 = "./output/" + name + ".mid";
        
        Song norway1 = Translator.INSTANCE.loadMidiToSong(path1);

        Translator.INSTANCE.saveSongToMidi(norway1, name);
        
        Song norway2 = Translator.INSTANCE.loadMidiToSong(path2);     
        
        assertTrue("Check if songs are equal", norway1.getNbrOfTracks() == norway2.getNbrOfTracks());
        assertTrue("Check if songs are equal", norway1.getScore().length() == norway2.getScore().length());
        assertEquals(norway2.getTempo(), norway1.getTempo(), 0.005);
    }
    
    @Test    
    public void testSaveAndLoadNEq(){
        String name1 = "Norway";
        String name2 = "Sweden";
        String path1 = "./midifiles/" + name1 + ".mid";
        String path2 = "./midifiles/" + name2 + ".mid";
        
        Song norway = Translator.INSTANCE.loadMidiToSong(path1);
        Song sweden = Translator.INSTANCE.loadMidiToSong(path2);
  
        assertTrue("Check if songs are equal", norway.getScore() != sweden.getScore());
        assertTrue("Check if songs are equal", sweden.toString() != norway.toString());
    }
    
}
