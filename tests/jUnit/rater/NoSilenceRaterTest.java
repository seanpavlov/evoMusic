package jUnit.rater;

import static org.junit.Assert.*;
import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;

import org.junit.Before;
import org.junit.Test;

import com.evoMusic.model.Song;
import com.evoMusic.model.geneticAlgorithm.rating.NoSilenceRater;
import com.evoMusic.model.geneticAlgorithm.rating.SubRater;
import com.evoMusic.util.TrackTag;

public class NoSilenceRaterTest {
 private Song badPhraseSong, goodPhraseSong, badRestSong, goodRestSong;
    
    private SubRater rater;
    
    /**
     * Create songs, scores, part and phrases also initiate the 
     * phrases so that they fit the testing 
     */
    @Before
    public void setUpSong() {
       rater = new NoSilenceRater(1);  
    }    
    
    @Test
    public void test() {
        double goodRating;
        double badRating;
        
        Phrase goodPhrase = new Phrase();
        Phrase badPhrase = new Phrase();
        for (int i = 0; i < 100; i++) {
            goodPhrase.add(new Note(60, 1));
            badPhrase.add(new Note(Note.REST, 1));
        }
        
        goodRating = rater.rate(new Song(new Score(new Part(goodPhrase))));
        badRating = rater.rate(new Song(new Score(new Part(badPhrase))));
        System.out.println("Good rating: " + goodRating);
        System.out.println("Bad rating: " + badRating);
        assertTrue(goodRating <= 1.0 && goodRating >= 0.0);
        assertTrue(badRating <= 1.0 && badRating >= 0.0);
        assertTrue(goodRating > badRating);
        
        // Testing overlapping phrases.
        Note newNote = new Note(60, 4);
        newNote.setDuration(4.0);
        Note restNote = new Note(Note.REST, 4.0);
        
        Phrase phrase1 = new Phrase(4.0);
        
        phrase1.add(newNote.copy());
        phrase1.add(restNote.copy());
        phrase1.add(newNote.copy());
        phrase1.add(newNote.copy());
        phrase1.add(restNote.copy());
        phrase1.add(restNote.copy());
        phrase1.add(newNote.copy());
        
        
        Phrase phrase2 = new Phrase(0.0);
        phrase2.add(restNote.copy());
        
        phrase2.add(newNote.copy());
        phrase2.add(restNote.copy());
        phrase2.add(newNote.copy());
        phrase2.add(newNote.copy());
        phrase2.add(restNote.copy());
        // has added 24 in time by now on phrase2.
        
        double rating1 = rater.rate(new Song(new Score(new Part(phrase1))));
        Part part2 = new Part(phrase2);
        Phrase phrase21 = new Phrase(28.0);
        phrase21.add(newNote.copy());
        part2.add(phrase21);
        
        double rating2 = rater.rate(new Song(new Score(part2)));
        assertEquals(rating1, rating2, 0.00001);
    }
}
