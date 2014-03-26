package jUnit.rater;


import static org.junit.Assert.assertTrue;
import jm.JMC;
import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;

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

    private Song createSongWithMelody(int[] pitches) {

        Note[] notes = new Note[pitches.length];
        for (int i = 0; i < pitches.length; i++) {
            notes[i] = new Note(pitches[i], JMC.QUARTER_NOTE);
        }
        
        return new Song(new Score(new Part(new Phrase(notes))));
        
    }
    @Test
    public void testHigh() {
        System.out.println("---- HIGH ----");
        Song minorScaleSong = createSongWithMelody(new int[] { 0, 2, 3, 5, 7, 8, 10 });
        minorScaleSong.addTagToTrack(0, TrackTag.MELODY);
        double rating = testRater.rate(minorScaleSong);
        assertTrue(rating > 0.8 && rating <= 1);
    }

    @Test
    public void testLow() {
        System.out.println("---- LOW ----");
        Song noGood = createSongWithMelody(new int[] {0, 1, 3, 4, 6, 7, 9, 10});
        noGood.addTagToTrack(0, TrackTag.MELODY);
        double rating = testRater.rate(noGood);
        assertTrue("expecting rating to be low but got: "+rating, rating < 0.3 && rating >= 0);
    }
    
    @Test
    public void testMid() {
        System.out.println("---- MID ----");
        Song allNotes= createSongWithMelody(new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 });
        allNotes.addTagToTrack(0, TrackTag.MELODY);
        double rating = testRater.rate(allNotes);
        assertTrue("expecting rating somewhere in the middle but got: "+rating, rating > 0.2 && rating < 0.8 );
    }

}
