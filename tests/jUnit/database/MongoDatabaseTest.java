package jUnit.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import jUnit.TestSuite;

import java.io.IOException;
import java.util.List;

import model.Song;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import translator.Translator;
import database.MongoDatabase;

public class MongoDatabaseTest {

    private Song testSong;
    private static MongoDatabase mDb;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        // if a mongo db has not been installed or set up properly, we should
        // get to know about this before any tests are run. Also save instance
        // to mDb to save some keystrokes for the tests.
        mDb = MongoDatabase.getInstance();
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
        mDb.removeSong(testSong);
        songs = mDb.retrieveSongs();
        assertTrue("The number of songs should be the same as before",
                nbrOfSongs == songs.size());
    }


    @Test
    public void testUpdateSong() throws IOException {
        mDb.dropDb(TestSuite.TEST_DB);
        
        mDb.insertSong(testSong);
        Song newSong = Translator.INSTANCE.loadMidiToSong("midifiles/mm2wily1.mid");
        newSong.getUserTags().add("GOOD");
        mDb.updateSong(testSong, newSong);
        assertEquals("GOOD", mDb.retrieveSongs().get(0).getUserTags().get(0));

    }

}
