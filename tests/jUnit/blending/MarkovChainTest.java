package jUnit.blending;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import jm.music.data.Score;

import org.junit.Before;
import org.junit.Test;

import com.evoMusic.model.Song;
import com.evoMusic.model.Track;
import com.evoMusic.model.Translator;
import com.evoMusic.model.geneticAlgorithm.blending.IntervalSong;
import com.evoMusic.model.geneticAlgorithm.blending.IntervalTrack;
import com.evoMusic.model.geneticAlgorithm.blending.MarkovSong;
import com.evoMusic.model.geneticAlgorithm.blending.multiMarkov.MarkovStateMachine;
import com.evoMusic.model.geneticAlgorithm.blending.multiMarkov.State;
import com.evoMusic.model.geneticAlgorithm.blending.multiMarkov.StateSong;
import com.evoMusic.util.TrackTag;

public class MarkovChainTest {
    
    private Song marioSong;
    private Song marioTheme;
    private List<Song> flutes;
    private Song moonlight;
    private Song nyanCat;
    private Song zelda;
    private Song wilyStage;
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
        
//        zelda = Translator.INSTANCE.loadMidiToSong("midifiles/pieces/zelda/ALTTP_middle.mid");
        zelda = Translator.INSTANCE.loadMidiToSong("midifiles/zeldaALinkToThePast.mid");
        zelda.addTagToTrack(0, TrackTag.BASELINE);
        zelda.addTagToTrack(1, TrackTag.CHORDS);
        zelda.addTagToTrack(3, TrackTag.MELODY);
        
        wilyStage = Translator.INSTANCE.loadMidiToSong("midifiles/mm2wily1.mid");
        wilyStage.addTagToTrack(0, TrackTag.MELODY);
        
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
        List<Song> songList = new ArrayList<Song>();
        songList.add(marioTheme);
//        StateSong ss = new StateSong(marioSong);
//        Song newSong = ss.toSong();
        MarkovStateMachine markov = new MarkovStateMachine(3, songList);
        Song newSong = markov.generateNew(50);
        Translator.INSTANCE.play(newSong);
    }

}
