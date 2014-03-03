package jUnit.database;

import static org.junit.Assert.*;
import jUnit.TestSuite;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import jm.music.data.Part;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.evoMusic.database.MongoDatabase;
import com.evoMusic.model.Song;
import com.evoMusic.model.Translator;
import com.evoMusic.model.enumerators.TrackTag;


public class MongoDatabaseTest {

    private Song testSong;
    private static List<Song> testSongs;
    private static MongoDatabase mDb;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        // if a mongo db has not been installed or set up properly, we should
        // get to know about this before any tests are run. Also save instance
        // to mDb to save some keystrokes for the tests.
        mDb = MongoDatabase.getInstance();
        testSongs = new LinkedList<Song>();
    }
    
    /**
     * Make sure that we always have a fresh working song instance to work with
     * before each test.
     * 
     * @throws IOException
     *             if any problems with loading the MIDI occurrs.
     */
    @Before
    public void setUpSong() throws IOException {
        testSong = Translator.INSTANCE.loadMidiToSong("midifiles/mm2wily1.mid");
        for(Part part : testSong.getScore().getPartArray()){
            testSong.addTagToTrack(part, TrackTag.MELODY);
        }
    }
    
    

    @Test
    public void testSingleton() {
        MongoDatabase otherMDb = MongoDatabase.getInstance();
        assertEquals("Broken singleton pattern", otherMDb, mDb);
    }

    @Test
    public void testInsertRetrieveRemoveSong() {
        final int nbrOfSongs = mDb.retrieveSongs().size();
        final boolean result = mDb.insertSong(testSong);
        assertTrue(result);
        List<Song> songs = mDb.retrieveSongs();
        assertTrue("The number of songs should have increased ",
                nbrOfSongs < songs.size());
        Song dbSong = songs.get(0);
        
        //Tests tracktags in retrieved song
        List<Part> taggedTrackes = dbSong.getTaggedTracks(TrackTag.MELODY);
        assertTrue("Tracks tagged with MELODY should be same size",
                taggedTrackes.size() == testSong.getTaggedTracks(TrackTag.MELODY).size());
        taggedTrackes = dbSong.getTaggedTracks(TrackTag.NONE);
        assertFalse("Tracks tagged with NONE should not be same size as MELODY in before song",
                taggedTrackes.size() == testSong.getTaggedTracks(TrackTag.MELODY).size());
        
        boolean removeResult = mDb.removeSong(testSong);
        assertTrue(removeResult);
        songs = mDb.retrieveSongs();
        assertTrue("The number of songs should be the same as before", nbrOfSongs == songs.size());
    }


    @Test
    public void testUpdateSong() throws IOException {
        mDb.dropDb(TestSuite.TEST_DB);
        
        mDb.insertSong(testSong);
        Song newSong = Translator.INSTANCE.loadMidiToSong("midifiles/mm2wily1.mid");
        for(Part part : newSong.getScore().getPartArray()){
            newSong.addTagToTrack(part, TrackTag.BEAT);
        }
        testSongs.add(newSong);
        newSong.addUserTag("GOOD");
        boolean result = mDb.updateSong(testSong, newSong);
        assertTrue(result);
        Song dbSong = mDb.retrieveSongs().get(0);
        assertEquals("GOOD", dbSong.getUserTags().get(0));
        assertEquals(TrackTag.BEAT.toString(), 
                dbSong.getTrackTags(dbSong.getTrack(0)).get(0).toString());
    }
    
    
    /**
     * Clean up after the test class so we don't leave test songs in folders
     * 
     * */
    @After
    public void cleanUpSongs(){
        for(Song song : testSongs){
            mDb.removeFile(song.getTitle());
        }
    }

}
