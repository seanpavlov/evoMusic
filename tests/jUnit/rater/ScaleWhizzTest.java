package jUnit.rater;


import static org.junit.Assert.assertTrue;
import jUnit.Helpers;

import org.junit.BeforeClass;
import org.junit.Test;

import com.evoMusic.model.Song;
import com.evoMusic.model.geneticAlgorithm.rating.Rater;
import com.evoMusic.model.geneticAlgorithm.rating.ScaleWhizz;
import com.evoMusic.util.TrackTag;

public class ScaleWhizzTest {

    private static Rater testRater; 
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        testRater = new Rater();
        testRater.addSubRater(new ScaleWhizz(1));
    }

 
    /*
     * Test if we can achieve a high rating
     */
    @Test
    public void testHigh() {
        Song minorScaleSong = Helpers.createSongWithMelody(new int[] { 1, 3, 4, 6, 8, 9, 11 });
        minorScaleSong.addTagToTrack(0, TrackTag.MELODY);
        double rating = testRater.rate(minorScaleSong);
        assertTrue("expecting rating to be high but got: "+rating, rating > 0.8 && rating <= 1);
    }

    /*
     * Test if we can achieve a low rating
     */
    @Test
    public void testLow() {
        Song noGood = Helpers.createSongWithMelody(new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11});
        noGood.addTagToTrack(0, TrackTag.MELODY);
        double rating = testRater.rate(noGood);
        assertTrue("expecting rating to be low but got: "+rating, rating < 0.3 && rating >= 0);
    }
    
    /*
     * Test if we can achieve a mid range rating
     */
    @Test
    public void testMid() {
        Song semiGood = Helpers.createSongWithMelody(new int[] { 0, 2, 3, 5, 7, 8, 9, 10, 11 });
        semiGood.addTagToTrack(0, TrackTag.MELODY);
        double rating = testRater.rate(semiGood);
        assertTrue("expecting rating somewhere in the middle but got: "+rating, rating > 0.2 && rating < 0.8 );
    }
}
