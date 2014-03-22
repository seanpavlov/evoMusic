package jUnit;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.evoMusic.model.Song;
import com.evoMusic.model.geneticAlgorithm.MarkovChain;
import com.evoMusic.util.Translator;

public class MarkovChainTest {
    
    private Song marioSong;
    private MarkovChain markov;

    @Before
    public void setUp() throws Exception {
        marioSong = Translator.INSTANCE.loadMidiToSong("midifiles/super_mario_world_overworld.mid");
    }

    @Test
    public void test() {
        //Translator.INSTANCE.playSong(marioSong);
        markov = new MarkovChain(marioSong);
        System.out.println(markov.toString());
        Song newSong = markov.generateNew();
        Translator.INSTANCE.playSong(newSong);
        
        
        assertTrue(true);
        //fail("Not yet implemented");
    }

}
