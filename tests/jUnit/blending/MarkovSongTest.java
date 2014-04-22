package jUnit.blending;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.evoMusic.model.Song;
import com.evoMusic.model.Track;
import com.evoMusic.model.Translator;
import com.evoMusic.model.geneticAlgorithm.blending.MarkovSong;
import com.evoMusic.util.TrackTag;

public class MarkovSongTest {
    
    private static List<Song> songList;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        songList = new ArrayList<Song>();
        songList.add(Translator.INSTANCE.loadMidiToSong("midifiles/super_mario_world_overworld.mid"));
        songList.add(Translator.INSTANCE.loadMidiToSong("midifiles/zeldaALinkToThePast.mid"));
        songList.get(0).addTagToTrack(0, TrackTag.MELODY);
        songList.get(1).addTagToTrack(0, TrackTag.BASELINE);
        songList.get(1).addTagToTrack(3, TrackTag.MELODY);
    }

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void test() {
        double eps = 0.0000001;
        
        MarkovSong markovSong = new MarkovSong(3, songList);
        Song newSong = markovSong.generateNew(1000);
        
        // Assert that time is kept
        assertEquals(1000, newSong.getScore().getEndTime(), eps);
        
        // Assert reasonable number of notes
        assertTrue(newSong.getScore().getPart(0).getPhrase(0).size() > 10);
        
        Set<TrackTag> ttSet = new HashSet<TrackTag>();
        for (Track track : newSong.getTracks()) {
            ttSet.add(track.getTag());
        }
        assertTrue(ttSet.contains(TrackTag.MELODY));
        assertTrue(ttSet.contains(TrackTag.BASELINE));
        assertFalse(ttSet.contains(TrackTag.NONE));
        assertFalse(ttSet.contains(null));
        
        // Assert tempo is reasonable
        double upperTempo = songList.get(0).getTempo();
        double lowerTempo = songList.get(1).getTempo();
        if (upperTempo < lowerTempo) {
            double temp = upperTempo;
            upperTempo = lowerTempo;
            lowerTempo = temp;
        }
        double newTempo = newSong.getTempo();
        
        assertTrue(newTempo <= upperTempo && newTempo >= lowerTempo);
    }
}
