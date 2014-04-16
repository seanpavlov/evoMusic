package jUnit.rater;

import static org.junit.Assert.*;
import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;

import org.junit.BeforeClass;
import org.junit.Test;

import com.evoMusic.model.Song;
import com.evoMusic.model.geneticAlgorithm.rating.MelodyNoteSustainRater;
import com.evoMusic.model.geneticAlgorithm.rating.SubRater;
import com.evoMusic.util.TrackTag;

public class MelodyNoteSustainRaterTest {
    private static Song testSong, testSongPerfect, testSongMiddleRating, testSongWorstRating;
    private static SubRater rater;
    
    @BeforeClass
    public static void setUp(){
        rater = new MelodyNoteSustainRater(1);
        Phrase phrase = new Phrase(1.0);
        Phrase phrase2 = new Phrase(1.0);
        Phrase phrase3 = new Phrase(1.0);
        Phrase phrase4 = new Phrase(1.0);
        
        for(int i = 0; i < 1000; i++){
            Note note = new Note(1, 0.5);
            note.setDuration(Math.random());
            phrase.addNote(note);
            Note note2 = new Note(1, 0.5);
            note2.setDuration(Math.random() + 1);
            phrase2.addNote(note2);
            Note note3 = new Note(1, 0.5);
            if(i < 500){          
                note3.setDuration(0.5);
                phrase3.addNote(note3);
            }else{
                note3.setDuration(1.5);
                phrase3.addNote(note3);
            }
            Note note4 = new Note(1, 0.5);
            note4.setDuration(0.5);
            phrase4.addNote(note4);
        }
        
        
        testSong = new Song(new Score(new Part(phrase)));
        testSong.addTagToTrack(0, TrackTag.MELODY);
        testSongPerfect = new Song(new Score(new Part(phrase2)));
        testSongPerfect.addTagToTrack(0, TrackTag.MELODY);
        testSongMiddleRating = new Song(new Score(new Part(phrase3)));
        testSongMiddleRating.addTagToTrack(0, TrackTag.MELODY);
        testSongWorstRating = new Song(new Score(new Part(phrase4)));
        testSongWorstRating.addTagToTrack(0, TrackTag.MELODY);
    }
    
    @Test
    public void shouldRateSame(){
        double rating1 = rater.rate(testSong);
        double rating2 = rater.rate(testSong);     
        assertTrue("Should rate same for same song", rating1 == rating2);
    }
    
    @Test
    public void shouldRatePerfect(){
        double rating = rater.rate(testSongPerfect);
        assertTrue("Should get rating 1.0", rating == 1.0);
    }
    
    @Test
    public void shouldGetMiddleRating(){
        double rating = rater.rate(testSongMiddleRating);
        assertTrue("Should get rating 0.5", rating == 0.5);
    }
    
    @Test
    public void shouldGetWorstRating(){
        double rating = rater.rate(testSongWorstRating);
        assertTrue("Should get rating 0.0", rating == 0.0);
    }
}
