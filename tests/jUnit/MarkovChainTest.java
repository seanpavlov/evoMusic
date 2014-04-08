package jUnit;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.evoMusic.model.Song;
import com.evoMusic.model.Translator;
import com.evoMusic.model.geneticAlgorithm.blending.IntervalSong;
import com.evoMusic.model.geneticAlgorithm.blending.MarkovChain;
import com.evoMusic.util.TrackTag;

public class MarkovChainTest {
    
    private Song marioSong;
    private Song marioTheme;
    private List<Song> flutes;
    private Song moonlight;
    private MarkovChain markov;
    private Song nyanCat;

    @Before
    public void setUp() throws Exception {
        flutes = new ArrayList<Song>();
        flutes.add(Translator.INSTANCE.loadMidiToSong("midifiles/fp-1all.mid"));
        flutes.add(Translator.INSTANCE.loadMidiToSong("midifiles/fp-2cou.mid"));
        flutes.add(Translator.INSTANCE.loadMidiToSong("midifiles/fp-3sar.mid"));
        flutes.add(Translator.INSTANCE.loadMidiToSong("midifiles/fp-4bou.mid"));
        for(Song song : flutes) {
            song.addTagToTrack(0, TrackTag.MELODY);
        }
        
        marioSong = Translator.INSTANCE.loadMidiToSong("midifiles/super_mario_world_overworld.mid");
        marioTheme = Translator.INSTANCE.loadMidiToSong("midifiles/super_mario_bros_theme.mid");
        moonlight = Translator.INSTANCE.loadMidiToSong("midifiles/mond_3.mid");
        nyanCat = Translator.INSTANCE.loadMidiToSong("midifiles/nyan_cat_cut.mid");
        nyanCat.addTagToTrack(0, TrackTag.MELODY);
        
    }

    @Test
    public void test() {
        //IntervalSong intervalSong = new IntervalSong(marioSong);
        //Song newSong = intervalSong.toSong();
        //markov = new MarkovChain(flutes.get(2));
        List<Song> nyanList = new ArrayList<Song>(1);
        nyanList.add(nyanCat);
        nyanList.add(flutes.get(2));
        markov = new MarkovChain(3, nyanList);
        Song newSong = markov.generateNew(1000);
        //Translator.INSTANCE.saveSongToMidi(newSong, "NyanBach");
        Translator.INSTANCE.playPart(newSong.getTrack(0).getPart());
        //Translator.INSTANCE.playPart(newSong, 0);
        //assertTrue(true);
        //fail("Not yet implemented");
    }

}
