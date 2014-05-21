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
    public void setUpSong(){
       // create phrase songs
       rater = new NoSilenceRater(1);
       
       Score goodScore = new Score();
       Score badScore = new Score();
       
       
       Part badPart = new Part();
       Part goodPart = new Part();
       
       for (int i = 0 ; i < 10; i++){
           Phrase good = new Phrase();
           good.setStartTime(i);
           good.setDuration(1);
           
           Phrase bad = new Phrase();
           bad.setStartTime(i);
           bad.setDuration(i);
           if (i == 9){
               bad.setStartTime(i*10);
           }

           badPart.add(bad);
           goodPart.add(good);
       }
       
       goodScore.add(goodPart);
       badScore.add(badPart);
       
       badPhraseSong = new Song(badScore);
       goodPhraseSong = new Song(goodScore);
       
       //create rest songs
       Phrase good1 = new Phrase();
       good1.setStartTime(0);
       Phrase good2 = new Phrase();
       good2.setStartTime(0);
       Phrase bad1 = new Phrase();
       bad1.setStartTime(0);
       Phrase bad2 = new Phrase();
       bad2.setStartTime(0);
       for (int i = 0; i < 100; i++){
           if (i <= 60 && i >= 40){
               Note badN = new Note(Note.REST, 1);
               bad1.addNote(badN);
               bad2.addNote(badN);
            
               Note goodN = new Note(64, 1);
               good1.addNote(goodN);
               good2.addNote(goodN);
           
           } else {
               Note n = new Note(64, 1);
               bad1.addNote(n);
               bad2.addNote(n);
               good1.addNote(n);
               good2.addNote(n);
            }
       }
       
       Part goodP = new Part();
       goodP.add(good1);
       goodP.add(good2);
       
       Score goodS = new Score(goodP);
       goodRestSong = new Song(goodS);
       goodRestSong.addTagToTrack(0, TrackTag.MELODY);

       Part badP = new Part();
       badP.add(bad1);
       badP.add(bad2);
       Score badS = new Score(badP);
       badRestSong = new Song(badS);
       badRestSong.addTagToTrack(0, TrackTag.MELODY);       
    }    
    
    @Test
    public void test() {
        double goodRating;
        double badRating;
        
        Score goodScore = new Score();
        Score badScore = new Score();
        
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
        
        
        
    }
    
    /**
     * Test that the same song always gets the same rating
     * */
//    @Test
//    public void testSameRating(){
//        double rating1 = rater.rate(goodPhraseSong);
//        double rating2 = rater.rate(goodPhraseSong);
//        assertTrue("Rating value should be same for same song twice", rating1 == rating2);
//    
//        double rating3 = rater.rate(goodRestSong);
//        double rating4 = rater.rate(goodRestSong);
//        assertTrue("Rating value should be same for same song twice", rating3 == rating4);
//    }
    
    /**
     * Test that good song is better than bad song
     * */
//    @Test
//    public void testBetterRating(){
//        double rating1 = rater.rate(goodPhraseSong);
//        double rating2 = rater.rate(badPhraseSong);
//        System.out.println("good: " + rating1 + ", length: " + goodPhraseSong.getScore().getEndTime());
//        System.out.println("bad: " + rating2);
//        assertTrue("", rating1 > rating2);
//
//        double rating3 = rater.rate(goodRestSong);
//        double rating4 = rater.rate(badRestSong);
//        System.out.println("good: " + rating3);
//        System.out.println("bad: " + rating4);
//        assertTrue("", rating3 > rating4);
//    }
}
