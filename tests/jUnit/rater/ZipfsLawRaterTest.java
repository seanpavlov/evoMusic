package jUnit.rater;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import com.evoMusic.database.MongoDatabase;
import com.evoMusic.model.Song;
import com.evoMusic.model.geneticAlgorithm.rating.SubRater;
import com.evoMusic.model.geneticAlgorithm.rating.ZipfsLawRater;

public class ZipfsLawRaterTest {
    private static Song goodSong, badSong;
    private static SubRater rater;
    
    @BeforeClass
    public static void setUp(){
        rater = new ZipfsLawRater(1.0);
        goodSong = MongoDatabase.getInstance().retrieveSongs().get(0);
        badSong = MongoDatabase.getInstance().retrieveSongs().get(5);
        
    }
    
    @Test
    public void test() {
        List<Song> songs = MongoDatabase.getInstance().retrieveSongs();
        for (int x = 0; x < songs.size(); x++){
            rater.rate(songs.get(x));
        }
    }

}
