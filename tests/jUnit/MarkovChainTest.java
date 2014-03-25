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
    private MarkovChain markov;

    @Before
    public void setUp() throws Exception {
        marioSong = Translator.INSTANCE.loadMidiToSong("midifiles/super_mario_world_overworld.mid");
        other = Translator.INSTANCE.loadMidiToSong("midifiles/mm2wily1.mid");
        marioTheme = Translator.INSTANCE.loadMidiToSong("midifiles/super_mario_bros_theme.mid");
        
    }

    @Test
    public void test() {
        //Translator.INSTANCE.playSong(other);
        //IntervalSong intervalSong = new IntervalSong(marioSong);
        //System.out.println(intervalSong.toString());
        markov = new MarkovChain(marioSong);
        Song song = markov.generateNew();
        //Translator.INSTANCE.playSong(song);
        //Song newSong = intervalSong.toSong();
        //Translator.INSTANCE.playPart(newSong, 5);
        //Translator.INSTANCE.playSong(newSong);
        
        
        assertTrue(true);
        //fail("Not yet implemented");
    }

}
