package jUnit.rater;

import org.junit.BeforeClass;
import org.junit.Test;

import com.evoMusic.model.Song;
import com.evoMusic.model.Translator;
import com.evoMusic.model.geneticAlgorithm.rating.LcmFrequencyRater;
import com.evoMusic.model.geneticAlgorithm.rating.SubRater;
import com.evoMusic.util.TrackTag;

public class LcmFrequencyRaterTest {

    private static Song testSong;
    private static SubRater rater;
    
    @BeforeClass
    public static void setUp(){
        rater = new LcmFrequencyRater(1);
        testSong = Translator.INSTANCE.loadMidiToSong("midifiles/mm2wily1.mid");
        testSong.addTagToTrack(1, TrackTag.CHORDS);
        testSong.addTagToTrack(3, TrackTag.CHORDS);
        testSong.addTagToTrack(4, TrackTag.CHORDS);
    }
    
    @Test
    public void test(){
        double rating = rater.rate(testSong);
        System.out.println("RATING: " + rating);
    }
    
}
