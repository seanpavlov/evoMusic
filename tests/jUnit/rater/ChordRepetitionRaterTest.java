package jUnit.rater;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;

import org.junit.BeforeClass;
import org.junit.Test;

import com.evoMusic.model.Song;
import com.evoMusic.model.geneticAlgorithm.rating.ChordRepetitionRater;
import com.evoMusic.model.geneticAlgorithm.rating.SubRater;
import com.evoMusic.util.TrackTag;

public class ChordRepetitionRaterTest {
    private static Song testSongChordTags, testSongNoChordTags, testSong, perfectSong ;
    
    private static SubRater rater;
    
    @BeforeClass
    public static void setUpSongs(){
       rater = new ChordRepetitionRater(1);
       
       Phrase[] phrases = {new Phrase(0.0),new Phrase(2.0),new Phrase(4.0),new Phrase(6.0), new Phrase(8.0),
               new Phrase(10.0), new Phrase(12.0), new Phrase(14.0)};
       Phrase[] phrases2 = {new Phrase(0.0), new Phrase(2.0), new Phrase(4.0), new Phrase(6.0), new Phrase(8.0),
                            new Phrase(10.0), new Phrase(12.0), new Phrase(14.0)};
       Phrase[] phrases3 = {new Phrase(0.0), new Phrase(2.0), new Phrase(4.0), new Phrase(6.0), new Phrase(8.0),
               new Phrase(10.0), new Phrase(12.0), new Phrase(14.0)};
       
       List<int[]> patterns = new ArrayList<int[]>();
       
       int[] pattern0 = {1, 5, 25, 15, 5};
       int[] pattern1 = {5, 15, 25, 5, 1};
       int[] pattern2 = {20, 5, 15, 5, 15};
       int[] pattern3 = {15, 15, 10, 15, 5};
       int[] pattern;
       int[] patternValues = {1, 5, 10, 15, 25};
       patterns.add(pattern0);
       patterns.add(pattern1);
       patterns.add(pattern2);
       patterns.add(pattern3);
       
       for(int i = 0; i < phrases.length; i++){
           int[] notesPerfect = new int[5];
           int[] notes = new int[5];
           for(int j = 0; j < notes.length; j++){
               notesPerfect[j] = j;
               notes[j] = patternValues[(int)(Math.random()*5)];
           }
           phrases[i].addChord(notesPerfect, 1.0);
           int x = (int)(Math.random()*4);
           pattern = patterns.get(x);
           
           phrases2[i].addChord(pattern, 1.0);
           
           phrases3[i].addChord(notes, 1.0);
       }
       
       
       
       
       Part part = new Part();
       part.addPhraseList(phrases);
       Score score = new Score(part);
       perfectSong = new Song(score);
       perfectSong.addTagToTrack(part, TrackTag.CHORDS);
       
       testSongNoChordTags = new Song(score);
       
       Part part2 = new Part();
       part2.addPhraseList(phrases2);
       Score score2 = new Score(part2);
       testSongChordTags = new Song(score2);
       testSongChordTags.addTagToTrack(part2, TrackTag.CHORDS);
       
       Part part3 = new Part();
       part3.addPhraseList(phrases3);
       Score score3 = new Score(part3);
       testSong = new Song(score3);
       testSong.addTagToTrack(part3, TrackTag.CHORDS);
       
    }
    
    @Test
    public void testSameRating(){
        double rating1 = rater.rate(testSongChordTags);
        double rating2 = rater.rate(testSongChordTags);
        assertTrue("Same song should get same rating", rating1 == rating2);
    }   
    
    @Test
    public void shouldGetRateZero(){
        double rating = rater.rate(testSongNoChordTags);
        assertTrue("should get 0.0 rating", rating == 0.0);
    }
    
    @Test
    public void shouldNotgetZero(){
        double rating = rater.rate(testSongChordTags);
        assertTrue("Song with known chord patterns should nog get rating 0", rating > 0);
    }
    
    @Test
    public void shouldRateLess(){
        double rating1 = rater.rate(testSongChordTags);
        double rating2 = rater.rate(testSong);
        assertTrue("Random added notes should ger lower rating than pattern added notes", rating1 > rating2);
    }
    
    @Test
    public void shouldGetPerfectRating(){
        double rating = rater.rate(perfectSong);
        assertTrue("Should get rating 1.0", rating == 1.0);
    }
    
    
    
}
