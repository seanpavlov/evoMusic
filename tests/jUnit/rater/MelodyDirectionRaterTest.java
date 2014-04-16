package jUnit.rater;

import static org.junit.Assert.*;
import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;

import org.junit.BeforeClass;
import org.junit.Test;

import com.evoMusic.model.Song;
import com.evoMusic.model.geneticAlgorithm.rating.MelodyDirectionRater;
import com.evoMusic.model.geneticAlgorithm.rating.SubRater;
import com.evoMusic.util.TrackTag;

public class MelodyDirectionRaterTest {
    private static Song testSongPerfect, testSongMiddleRating, testSongWorstRating, testSongRestNotes,
                        testSongEmpty, testSongNoMelodyTag;
    private static SubRater rater;
    
    /**Set rater variable and build the test songs 
     * to be used throughout this test class */
    @BeforeClass
    public static void setUp(){
        rater = new MelodyDirectionRater(1);
        
        Phrase phrase = new Phrase(0.0);
        Phrase phrase2 = new Phrase(0.0);
        Phrase phrase3 = new Phrase(0.0);
        Phrase phrase4 = new Phrase(0.0);
        Phrase phrase5 = new Phrase(0.0);
        
        
        for(int i = 1; i < 22; i++){
            phrase.addNote(i, 1.0);
            if(i % 2 == 0){
                phrase2.addNote(i-1, 1.0);
                phrase4.addNote(Note.REST, 1.0);
            }else{
                phrase2.addNote(i, 1.0);
                phrase4.addNote(i, 1.0);
            }
            phrase3.addNote(22-i, 1.0);
        }

        testSongPerfect = new Song(new Score(new Part(phrase)));
        testSongPerfect.addTagToTrack(0, TrackTag.MELODY);

        testSongMiddleRating = new Song(new Score(new Part(phrase2)));
        testSongMiddleRating.addTagToTrack(0, TrackTag.MELODY);

        testSongWorstRating = new Song(new Score(new Part(phrase3)));
        testSongWorstRating.addTagToTrack(0, TrackTag.MELODY);

        testSongRestNotes = new Song(new Score(new Part(phrase4)));
        testSongRestNotes.addTagToTrack(0, TrackTag.MELODY);
        
        testSongEmpty = new Song(new Score(new Part(phrase5)));
        testSongEmpty.addTagToTrack(0, TrackTag.MELODY);
        
        testSongNoMelodyTag = new Song(new Score(new Part(phrase)));
        
    }
    
    /**Test with song whose pitch values are allways higher 
     * than the one before and should get rating 1.0*/
    @Test
    public void shouldGetPerfectRating(){
        double rating = rater.rate(testSongPerfect);
        assertTrue("Should get rating 1.0", rating == 1.0);
    }
    
    /**Test with song whose pitch values half the 
     * time is higher than the one before it 
     * and shoulg get rating 0.5*/
    @Test
    public void shouldGetMiddleRating(){
        double rating = rater.rate(testSongMiddleRating);
        assertTrue("Should get rating 0.5", rating == 0.5);
    }
    
    /**Test with song whose pitch values are allways lower than
     * the one before it and should get rating 0.0*/
    @Test
    public void shouldGetWorstRating(){
        double rating = rater.rate(testSongWorstRating);
        assertTrue("Should get rating 0.0", rating == 0.0);
    }
    
    /**Test with song whose pitch values are rest for 
     * every other note and for the non rest not is allways
     * higer than the one before and should get rating 1.0
     * because rest notes are not counted*/
    @Test
    public void shouldNoteCountRestNotes(){
        double rating = rater.rate(testSongRestNotes);
        assertTrue("Should get rating 1.0, because it won't count with rest notes", rating == 1.0);
    }
    
    /**Test with song that is empty, (Has no notes) should get 
     * rating 0.0*/
    @Test
    public void emptySongShouldGetWorstRating(){
        double rating = rater.rate(testSongEmpty);
        assertTrue("An empty song should get rating 0.0", rating == 0.0);
    }
    
    /**test with song that does not have a track tagged with melody
     * should get rating 0.0*/
    @Test
    public void noMelodyTagShouldGetWorstRating(){
        double rating = rater.rate(testSongNoMelodyTag);
        assertTrue("Song without melody tag should get rating 0.0", rating == 0.0);
    }
}
