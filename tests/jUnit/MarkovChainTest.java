package jUnit;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.evoMusic.model.Song;
import com.evoMusic.model.geneticAlgorithm.blending.IntervalSong;
import com.evoMusic.model.geneticAlgorithm.blending.MarkovChain;
import com.evoMusic.util.Translator;

public class MarkovChainTest {
    
    private Song marioSong;
    private Song marioTheme;
    private Song other;
    private Song moonlight;
    private MarkovChain markov;

    @Before
    public void setUp() throws Exception {
        marioSong = Translator.INSTANCE.loadMidiToSong("midifiles/super_mario_world_overworld.mid");
        other = Translator.INSTANCE.loadMidiToSong("midifiles/mm2wily1.mid");
        marioTheme = Translator.INSTANCE.loadMidiToSong("midifiles/super_mario_bros_theme.mid");
        moonlight = Translator.INSTANCE.loadMidiToSong("midifiles/mond_3.mid");
        
    }

    @Test
    public void test() {
        //IntervalSong intervalSong = new IntervalSong(marioSong);
        //Song newSong = intervalSong.toSong();
        markov = new MarkovChain(marioSong);
        while(true) {
            Song newSong = markov.generateNew(100);
            Translator.INSTANCE.playSong(newSong);
        }
        
        
        
        //assertTrue(true);
        //fail("Not yet implemented");
    }

}
