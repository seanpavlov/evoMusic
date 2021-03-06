package jUnit;

import java.util.logging.Level;

import jUnit.blending.IntervalSongTest;
import jUnit.blending.IntervalTrackTest;
import jUnit.blending.MarkovSongTest;
import jUnit.blending.MarkovTrackTest;
import jUnit.crossover.DrCrossTest;
import jUnit.database.MongoDatabaseTest;
import jUnit.mutator.OctaveMutatorTest;
import jUnit.mutator.RandomNoteMutatorTest;
import jUnit.mutator.SimplifyMutatorTest;
import jUnit.mutator.SwapSegmentMutatorTest;
import jUnit.rater.BeatRaterTest;
import jUnit.rater.ChordRepetitionRaterTest;
import jUnit.rater.CrazyNoteOctaveRaterTest;
import jUnit.rater.LcmPitchRaterTest;
import jUnit.rater.MelodyDirectionRaterTest;
import jUnit.rater.MelodyDirectionStabilityRaterTest;
import jUnit.rater.MelodyNoteDensityRaterTest;
import jUnit.rater.MelodyNoteSyncopationRaterTest;
import jUnit.rater.MelodyPitchRangeRaterTest;
import jUnit.rater.MelodyRepetitionRaterTest;
import jUnit.rater.MelodyRestDensityRaterTest;
import jUnit.rater.NoSilenceRaterTest;
import jUnit.rater.PitchVarietyRaterTest;
import jUnit.rater.RepeatedPitchDensityRaterTest;
import jUnit.rater.RhythmVarietyRaterTest;
import jUnit.rater.ScaleWhizzTest;
import jUnit.rater.ZipfsLawRaterTest;
import jUnit.translator.TranslatorTest;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.evoMusic.parameters.P;
import com.evoMusic.util.Helpers;


@RunWith(Suite.class)
@SuiteClasses({TranslatorTest.class, 
    MongoDatabaseTest.class, 
    SongTest.class,
    TrackTest.class,
    GeneticAlgorithmTest.class,
    DrCrossTest.class,
    
    /*
     * Raters
     */
    MelodyRepetitionRaterTest.class,
    ScaleWhizzTest.class,
    BeatRaterTest.class,
    NoSilenceRaterTest.class,
    ChordRepetitionRaterTest.class,
    CrazyNoteOctaveRaterTest.class,
    MelodyDirectionStabilityRaterTest.class,
    PitchVarietyRaterTest.class,
    MelodyDirectionRaterTest.class,
    RhythmVarietyRaterTest.class,
    MelodyNoteDensityRaterTest.class,
    MelodyPitchRangeRaterTest.class,
    RepeatedPitchDensityRaterTest.class,
    MelodyRestDensityRaterTest.class,
    ZipfsLawRaterTest.class,
    MelodyNoteSyncopationRaterTest.class,
    LcmPitchRaterTest.class,
    
    /*
     * Mutators
     */
    RandomNoteMutatorTest.class,
    OctaveMutatorTest.class,
    SimplifyMutatorTest.class,
    SwapSegmentMutatorTest.class,
    
    /*
     * Markov
     */
    IntervalSongTest.class,
    IntervalTrackTest.class,
    MarkovTrackTest.class,
    MarkovSongTest.class
    })

/**
 * This is our test suite class. This class runs all our test classes. 
 * To get started with jUnit 4.x, here's a good article: 
 * http://www.vogella.com/tutorials/JUnit/article.html especially sections 2.2, 
 * 3.1, 3.2, 3.3, 5.5.
 * 
 */
public class TestSuite {

    // (\_(\
    // (^*^ )
    // (")(")
    @BeforeClass
    public static void setUp() {
        Helpers.LOGGER.setLevel(P.IN_DEBUG_MODE ? Level.ALL : Level.SEVERE);
    }

}
