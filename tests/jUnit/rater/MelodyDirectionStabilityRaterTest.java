package jUnit.rater;

import static org.junit.Assert.*;
import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;

import org.junit.BeforeClass;
import org.junit.Test;

import com.evoMusic.model.Song;
import com.evoMusic.model.geneticAlgorithm.rating.MelodyDirectionStabilityRater;
import com.evoMusic.model.geneticAlgorithm.rating.SubRater;
import com.evoMusic.util.TrackTag;

public class MelodyDirectionStabilityRaterTest {
    private static Song testSongWorst, testSongPerfect, testSong, testSongRestNotes;
    private static SubRater rater;
    
    @BeforeClass
    public static void setUp(){
        rater = new MelodyDirectionStabilityRater(1);
        
        Phrase phrase = new Phrase(0.0);
        Phrase phrase2 = new Phrase(0.0);
        Phrase phrase3 = new Phrase(0.0);
        Phrase phrase4 = new Phrase(0.0);
        
        for(int i = 2; i < 22; i++){
            phrase.addNote(i, 1.0);
            if(i % 2 == 0){
                phrase2.addNote(i, 1.0);
                phrase4.addNote(i, 1.0);
            }else{
                phrase2.addNote(i-2, 1.0);
                phrase4.addNote(Note.REST, 1.0);
            }
            if(i % 9 == 0)
                phrase3.addNote(i-(i-1), 1.0);
            else 
                phrase3.addNote(i, 1.0);
        }
        Part part = new Part(phrase);
        testSongWorst  = new Song(new Score(part));
        testSongWorst.addTagToTrack(0, TrackTag.MELODY);
        
        Part part2 = new Part(phrase2);
        testSongPerfect = new Song(new Score(part2));
        testSongPerfect.addTagToTrack(0, TrackTag.MELODY);
        
        Part part3 = new Part(phrase3);
        testSong = new Song(new Score(part3));
        testSong.addTagToTrack(0, TrackTag.MELODY);
        
        Part part4 = new Part(phrase4);
        testSongRestNotes = new Song(new Score(part4));
        testSongRestNotes.addTagToTrack(0, TrackTag.MELODY);
    }
    
    /**Test with song whose pitch values never change direction
     * should get rating 0
     * */
    @Test
    public void shouldGetWorstRating(){
        double rating = rater.rate(testSongWorst);
        assertTrue("Should get worst rating", rating == 0.0);
    }
    
    /**Test with song whose pitch values change with every note
     * should get perfect rating*/
    @Test
    public void shouldGetPerfectRating(){
        double rating = rater.rate(testSongPerfect);
        assertTrue("Should get perfect rating", rating == 1.0);
    }
    
    /**Test with song whose pitch values change direction twice
     * should not get rating 0.0*/
    @Test
    public void shouldNotGetWorstRating(){
        double rating = rater.rate(testSong);
        assertFalse("should not get rating 0.0", rating == 0.0);
    }
    
    /**Test with song whose pitch values not equal to rest never
     * change pitch direction should get rating 0.0 because 
     * rest note is not counted with when rating the song*/
    @Test
    public void shouldNotCountRestNotes(){
        double rating = rater.rate(testSongRestNotes);
        assertTrue("Rest notes should not be part of rating", rating == 0.0);
    }
}
