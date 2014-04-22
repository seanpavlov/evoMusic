package jUnit.blending;

import static org.junit.Assert.*;

import java.util.Vector;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.evoMusic.model.geneticAlgorithm.blending.IntervalTrack;
import com.evoMusic.model.geneticAlgorithm.blending.MarkovTrack;
import com.evoMusic.util.TrackTag;

public class MarkovTrackTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testMarkovTrack() {
        double eps = 0.000001;
        
        Vector<Integer> testSequence;
        MarkovTrack testMTrack;
        testSequence = new Vector<Integer>();
        testMTrack = new MarkovTrack(3);
        
        testMTrack.addCountInterval(testSequence, 1);
        testMTrack.addCountToDuration(testSequence, 1.0);
        testMTrack.addCountToRhythmValue(testSequence, 1.00);
        testMTrack.addCountToDynamic(testSequence, 100);
        
        testSequence = new Vector<Integer>();
        testSequence.add(1);
        
        testMTrack.addCountInterval(testSequence, 2);
        testMTrack.addCountToDuration(testSequence, 1.1);
        testMTrack.addCountToRhythmValue(testSequence, 1.01);
        testMTrack.addCountToDynamic(testSequence, 101);

        testSequence = new Vector<Integer>();
        testSequence.add(1);
        testSequence.add(2);
        
        testMTrack.addCountInterval(testSequence, 3);
        testMTrack.addCountToDuration(testSequence, 1.2);
        testMTrack.addCountToRhythmValue(testSequence, 1.02);
        testMTrack.addCountToDynamic(testSequence, 102);

        testSequence = new Vector<Integer>();
        testSequence.add(1);
        testSequence.add(2);
        testSequence.add(3);
        
        testMTrack.addCountInterval(testSequence, 4);
        testMTrack.addCountToDuration(testSequence, 1.3);
        testMTrack.addCountToRhythmValue(testSequence, 1.03);
        testMTrack.addCountToDynamic(testSequence, 103);
        
        for (int i = 4; i < 9; i++) {
            testSequence = new Vector<Integer>();
            testSequence.add(i-2);
            testSequence.add(i-1);
            testSequence.add(i);
            
            testMTrack.addCountInterval(testSequence, i+1);
            testMTrack.addCountToDuration(testSequence, 1.0 + (double)(i)/10);
            testMTrack.addCountToRhythmValue(testSequence, 1.0 + (double)(i)/100);
            testMTrack.addCountToDynamic(testSequence, 100 + i);
        }

        testSequence = new Vector<Integer>();
        testSequence.add(7);
        testSequence.add(8);
        testSequence.add(9);
        
        testMTrack.addCountToDuration(testSequence, 1.0);
        testMTrack.addCountToRhythmValue(testSequence, 1.0);
        testMTrack.addCountToDynamic(testSequence, 100);
        
        testMTrack.initProbabilities();
        
        IntervalTrack iTrack = testMTrack.generateNew(5, 1, 2, 10, TrackTag.MELODY);
        iTrack = testMTrack.generateNew(8, 1, 2, 10, TrackTag.MELODY);
        iTrack = testMTrack.generateNew(9, 1, 2, 10, TrackTag.MELODY);
        
        assertEquals(1, iTrack.getInstrument());
        assertEquals(2, iTrack.getChannel());
        assertEquals(TrackTag.MELODY, iTrack.getTag());
        
        int[] notes = iTrack.getIntervals();
        double[] durations = iTrack.getDurations();
        double[] rhythmValues = iTrack.getRythmValues();
        int[] dynamics = iTrack.getDynamics();
        for (int i = 0; i < 7; i++) {
            assertEquals(i+1, notes[i]);
            assertEquals(1.0+(double)(i)/10, durations[i], eps);
            assertEquals(1.0+(double)(i)/100, rhythmValues[i], eps);
            assertEquals(100+i, dynamics[i], eps);
        }
    }
}
