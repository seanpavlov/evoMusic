package jUnit.blending;

import static org.junit.Assert.*;
import jm.music.data.Note;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.evoMusic.model.Song;
import com.evoMusic.model.Track;
import com.evoMusic.model.Translator;
import com.evoMusic.model.geneticAlgorithm.blending.IntervalTrack;
import com.evoMusic.util.TrackTag;

public class IntervalTrackTest {
    
    private static Song testSong;
    private static Track testTrack;
    private static IntervalTrack testIntervalTrack;
    

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        testSong = Translator.INSTANCE.loadMidiToSong("midifiles/super_mario_world_overworld.mid");
        testTrack = testSong.getTrack(0);
        testTrack.setTag(TrackTag.MELODY);
    }

    @Before
    public void setUp() throws Exception {
        testIntervalTrack = new IntervalTrack(testTrack);
    }

    @Test
    public void test() {
        double eps = 0.00001;
        
        Track newTrack = testIntervalTrack.toTrack();
        assertEquals(testTrack.getTag(), newTrack.getTag());
        assertEquals(testTrack.getPart().getInstrument(), newTrack.getPart().getInstrument());
        assertEquals(testTrack.getPart().getChannel(), newTrack.getPart().getChannel());
        
        Note[] originalNotes = testTrack.getPart().getPhrase(0).getNoteArray();
        Note[] newNotes = newTrack.getPart().getPhrase(0).getNoteArray();
        assertEquals(originalNotes.length, newNotes.length);
        for (int i = 0; i < originalNotes.length; i++) {
            assertEquals(originalNotes[i].getPitch(), newNotes[i].getPitch());
            assertEquals(originalNotes[i].getRhythmValue(), newNotes[i].getRhythmValue(), eps);
            assertEquals(originalNotes[i].getDuration(), newNotes[i].getDuration(), eps);
            assertEquals(originalNotes[i].getDynamic(), newNotes[i].getDynamic());
        }
        
        // Testing large constructor
        int firstNote = originalNotes[0].getPitch();
        int instrument = testTrack.getPart().getInstrument();
        int channel = testTrack.getPart().getChannel();
        TrackTag tag = testTrack.getTag();
        int[] intervals = new int[originalNotes.length - 1];
        double[] rhythmValues = new double[originalNotes.length];
        double[] durations = new double[originalNotes.length];
        int[] dynamics = new int[originalNotes.length];
        
        int prevPitch = originalNotes[0].getPitch();
        int currentPitch;
        rhythmValues[0] = originalNotes[0].getRhythmValue();
        durations[0] = originalNotes[0].getDuration();
        dynamics[0] = originalNotes[0].getDynamic();
        for (int i = 1; i < originalNotes.length; i++) {
            currentPitch = originalNotes[i].getPitch();
            intervals[i - 1] = currentPitch - prevPitch;
            prevPitch = currentPitch;
            
            rhythmValues[i] = originalNotes[i].getRhythmValue();
            durations[i] = originalNotes[i].getDuration();
            dynamics[i] = originalNotes[i].getDynamic();
        }
        newTrack = new IntervalTrack(firstNote, instrument, channel, intervals, rhythmValues, durations, dynamics, tag).toTrack();
        newNotes = newTrack.getPart().getPhrase(0).getNoteArray();
        assertEquals(testTrack.getTag(), newTrack.getTag());
        assertEquals(testTrack.getPart().getInstrument(), newTrack.getPart().getInstrument());
        assertEquals(testTrack.getPart().getChannel(), newTrack.getPart().getChannel());
        
        for (int i = 0; i < originalNotes.length; i++) {
            assertEquals(originalNotes[i].getPitch(), newNotes[i].getPitch());
            assertEquals(originalNotes[i].getRhythmValue(), newNotes[i].getRhythmValue(), eps);
            assertEquals(originalNotes[i].getDuration(), newNotes[i].getDuration(), eps);
            assertEquals(originalNotes[i].getDynamic(), newNotes[i].getDynamic());
        }
        
    }
}
