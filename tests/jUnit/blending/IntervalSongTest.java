package jUnit.blending;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import com.evoMusic.model.Song;
import com.evoMusic.model.Translator;
import com.evoMusic.model.geneticAlgorithm.blending.IntervalSong;
import com.evoMusic.model.geneticAlgorithm.blending.IntervalTrack;

public class IntervalSongTest {
    
    private static Song testSong;
    private static final double eps = 0.000001;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        testSong = Translator.INSTANCE.loadMidiToSong("midifiles/super_mario_world_overworld.mid");
    }

    @Test
    public void test() {
        
        // test constructors
        double tempo = 120;
        IntervalSong testISong;
        testISong = new IntervalSong(tempo);
        
        assertEquals(tempo, testISong.getTempo(), eps);
        assertEquals(0, testISong.getTracks().size());
        
        testISong.addTrack(new IntervalTrack(testSong.getTrack(0)));

        assertEquals(1, testISong.getTracks().size());
        
        testISong = new IntervalSong(testSong);
        
        assertEquals(testSong.getNbrOfTracks(), testISong.getTracks().size());
        assertEquals(testSong.getTempo(), testISong.getTempo(), eps);
        
        // test toSong
        Song newSong = testISong.toSong();
        
        assertEquals(testSong.getNbrOfTracks(), newSong.getNbrOfTracks());
        assertEquals(testSong.getTempo(), newSong.getTempo(), eps);
        
        for (int i = 0; i < testSong.getNbrOfTracks(); i++) {
            assertEquals(testSong.getTrack(i).getPart().getPhrase(0).getEndTime() , newSong.getTrack(i).getPart().getPhrase(0).getEndTime(), eps);
        }
    }
}
