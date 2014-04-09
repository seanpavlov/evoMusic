package jUnit;

import jUnit.crossover.CrossoverTest;
import jUnit.database.MongoDatabaseTest;
import jUnit.mutator.OctaveMutatorTest;
import jUnit.mutator.RandomNoteMutatorTest;
import jUnit.mutator.SimplifyMutatorTest;
import jUnit.rater.BeatRaterTest;
import jUnit.rater.ChordRepetitionRaterTest;
import jUnit.rater.CrazyNoteOctaveRaterTest;
import jUnit.rater.MelodyDirectionRaterTest;
import jUnit.rater.MelodyDirectionStabilityRaterTest;
import jUnit.rater.MelodyNoteDensityRaterTest;
import jUnit.rater.MelodyRepetitionRaterTest;
import jUnit.rater.NoSilenceRaterTest;
import jUnit.rater.PitchVarietyRaterTest;
import jUnit.rater.ScaleWhizzTest;
import jUnit.translator.TranslatorTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({TranslatorTest.class, 
    MongoDatabaseTest.class, 
    CrossoverTest.class, 
    GenerationTest.class,
    SongTest.class,
//    GeneticAlgorithmTest.class,
    
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
    MelodyNoteDensityRaterTest.class,
    
    /*
     * Mutators
     */
    RandomNoteMutatorTest.class,
    OctaveMutatorTest.class,
    SimplifyMutatorTest.class})

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

}
