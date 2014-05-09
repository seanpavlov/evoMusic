package jUnit.rater;

import static org.junit.Assert.*;
import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;

import org.junit.BeforeClass;
import org.junit.Test;

import com.evoMusic.model.Song;
import com.evoMusic.model.geneticAlgorithm.rating.MelodyRestDensityVarietyRater;
import com.evoMusic.model.geneticAlgorithm.rating.SubRater;
import com.evoMusic.util.TrackTag;

public class MelodyRestDensityRaterTest {

    private static Song testSong, testSongPerfect, testSongMiddleRating, testSongLowRating;
    private static SubRater rater;
    
    @BeforeClass
    public static void setUp(){
        rater = new MelodyRestDensityVarietyRater(1);
        
        Phrase phrase = new Phrase(0,0);
        Phrase phrase2 = new Phrase(0.0);
        Phrase phrase3 = new Phrase(0.0);
        Phrase phrase4 = new Phrase(0.0);
        
        for(int i = 0; i < 20; i++){
            phrase.addNote(Note.REST, Math.random());
            phrase2.addNote(Note.REST, 0.5);
            
            if(i == 0){
                phrase4.addNote(Note.REST, 1.0);
            }else{
                phrase4.addNote(Note.REST, 0.05);
            }
            
            if(i < 3)
                phrase3.addNote(Note.REST, 0.5);
            else
                phrase3.addNote(Note.REST, 0.2);
            
                
        }
        
        
        testSong = new Song(new Score(new Part(phrase)));
        testSong.addTagToTrack(0, TrackTag.MELODY);
        testSongPerfect = new Song(new Score(new Part(phrase2)));
        testSongPerfect.addTagToTrack(0, TrackTag.MELODY);
        testSongMiddleRating = new Song(new Score(new Part(phrase3)));
        testSongMiddleRating.addTagToTrack(0, TrackTag.MELODY);
        testSongLowRating = new Song(new Score(new Part(phrase4)));
        testSongLowRating.addTagToTrack(0, TrackTag.MELODY);     
    }
    
    @Test
    public void shouldRateSame(){
        double rating1 = rater.rate(testSong);
        double rating2 = rater.rate(testSong);
        assertTrue("Rater should give same rating for same song", rating1 == rating2);
    }
    
    @Test
    public void shouldRatePerfect(){
        double rating = rater.rate(testSongPerfect);
        assertTrue("Should get rating 1.0", rating == 1.0);
    }
    
    @Test
    public void shouldGetMiddleRating(){
        double rating = rater.rate(testSongMiddleRating);
        assertTrue("should get rating around 0.5", rating > 0.4 && rating < 0.6);
    }
    
    @Test
    public void shouldGetLowRating(){
        double rating = rater.rate(testSongLowRating);
        assertTrue("should get rating 0.0", rating < 0.2 && rating > 0.0);
    }
}
