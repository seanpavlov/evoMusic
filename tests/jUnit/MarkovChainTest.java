package jUnit;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.evoMusic.model.Song;
import com.evoMusic.model.Track;
import com.evoMusic.model.Translator;
import com.evoMusic.model.geneticAlgorithm.blending.IntervalSong;
import com.evoMusic.model.geneticAlgorithm.blending.IntervalTrack;
import com.evoMusic.model.geneticAlgorithm.blending.MarkovChain;
import com.evoMusic.util.TrackTag;

public class MarkovChainTest {
    
    private Song marioSong;
    private Song marioTheme;
    private List<Song> flutes;
    private Song moonlight;
    private MarkovChain markov;
    private Song nyanCat;
    private Song zelda;
    private List<Song> multiSongTestList;

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
        
        multiSongTestList = new ArrayList<Song>();
        
        zelda = Translator.INSTANCE.loadMidiToSong("midifiles/zeldaALinkToThePast.mid");
        zelda.addTagToTrack(0, TrackTag.BASELINE);
        zelda.addTagToTrack(1, TrackTag.CHORDS);
        zelda.addTagToTrack(3, TrackTag.MELODY);
        
        marioSong = Translator.INSTANCE.loadMidiToSong("midifiles/super_mario_world_overworld.mid");
        marioSong.addTagToTrack(0, TrackTag.MELODY);
        marioTheme = Translator.INSTANCE.loadMidiToSong("midifiles/super_mario_bros_theme.mid");
        marioTheme.addTagToTrack(0, TrackTag.MELODY);
        moonlight = Translator.INSTANCE.loadMidiToSong("midifiles/mond_3.mid");
        moonlight.addTagToTrack(0, TrackTag.MELODY);
        nyanCat = Translator.INSTANCE.loadMidiToSong("midifiles/nyan_cat_cut.mid");
        nyanCat.addTagToTrack(0, TrackTag.MELODY);
        
    }

    @Test
    public void test() {
        //IntervalSong intervalSong = new IntervalSong(marioSong);
        //Song newSong = intervalSong.toSong();
        //Translator.INSTANCE.play(newSong);
        
        //markov = new MarkovChain(flutes.get(2));
        List<Song> nyanList = new ArrayList<Song>(1);
        nyanList.add(nyanCat);
        nyanList.add(flutes.get(2));
        nyanList.add(marioSong);
        //nyanList.add(marioTheme);
        markov = new MarkovChain(2, nyanList);
        Song newSong = markov.generateNew(100);
        //Translator.INSTANCE.saveSongToMidi(newSong, "TestSaveNatan");
        Translator.INSTANCE.play(newSong);
        //Translator.INSTANCE.play(zelda.getTrack(3).getPart());
        //assertTrue(true);
        //fail("Not yet implemented");
    }

}
