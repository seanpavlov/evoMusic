package jUnit.raters;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import jm.music.data.Part;

import org.junit.Before;
import org.junit.Test;

import com.evoMusic.model.Song;
import com.evoMusic.model.Translator;
import com.evoMusic.model.enumerators.TrackTag;
import com.evoMusic.model.geneticAlgorithm.rating.BeatRater;
import com.evoMusic.model.geneticAlgorithm.rating.SubRater;

public class BeatRaterTest {
    
    private Song testSong;
    
    /**
     * Make sure that we always have a fresh working song instance to work with
     * before each test.
     * 
     * @throws IOException
     *             if any problems with loading the MIDI occurrs.
     */
    @Before
    public void setUpSong() throws IOException {
        testSong = Translator.INSTANCE.loadMidiToSong("midifiles/mm2wily1.mid");
        int count = 0;
        for(Part part : testSong.getScore().getPartArray()){
            count++;
            if(count % 5 == 0)
                testSong.addTagToTrack(part, TrackTag.RHYTHM);
        }
    }
    
    @Test
    public void testSameRating(){
        SubRater rater = new BeatRater();
        double rating1 = rater.rate(testSong);
        double rating2 = rater.rate(testSong);
        assertTrue("Rating value should be same for same song twice", rating1 == rating2);
    }

}
