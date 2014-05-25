package com.evoMusic.parameters;

import com.evoMusic.model.geneticAlgorithm.GeneticAlgorithm.Initiator;

/**
 * 
 * GENERAL RULES OF USE --------------------- Naming is done in the same name a
 * package structure is. Always start with a word that identifies in what kind
 * of package the parameter is used. And then go into more specific words.
 * 
 * For example: I want a parameter for random note probability. Probability
 * belongs to random note. Random note is a mutator. Therefore named
 * MUTATOR_RANDOM_NOTE_PORBABILITY
 * 
 */
public abstract class P {
    
    /*
     * DEBUG MODE
     * 
     * In debug mode, the usual console-output is disabled and the debug is shown.
     */
    public final static boolean IN_DEBUG_MODE = false;

    /*
     * GENETIC ALGORITHM PARAMETERS
     * 
     * Population Size is the size of the population in the GA. 
     * Number of elitism songs is the number of songs that will be carried over to the
     * next generation.
     * Number of crossover songs is the number of songs that will 
     */
    public static int GA_POPULATION_SIZE = 100;
    public static int GA_NBR_OF_ELITISM_SONGS = 1;
    public static int GA_NBR_OF_CROSSOVER_SONGS = 2;
    public static final Initiator initiator = Initiator.CROSSOVER;

    /*
     * MUTATION PARAMETERS
     */
    public static double MUTATION_PROBABILITY = 1;

    public static double MUTATION_LOCAL_PROBABILITY_MULTIPLIER_DECREASE_RATIO = 0.002;
    public static double MUTATION_LOCAL_PROBABILITY_MINIMUM_MULTIPLIER = 0.1;

    public static double MUTATOR_RANDOM_NOTE_PITCH_PROBABILITY = 0.02;
    public static int MUTATOR_RANDOM_NOTE_PITCH_STEP_RANGE = 6;

    public static double MUTATOR_RHYTHM_VALUE_PROBABILITY = 0.02;
    public static double MUTATOR_RHYTHM_VALUE_MOVING_RANGE = 1;

    public static double MUTATOR_OCTAVE_PROBABILITY = 0;
    public static int MUTATOR_OCTAVE_RANGE = 1;

    public static double MUTATOR_REVERSE_PROBABILITY = 0.01;

    public static double MUTATOR_SCALE_OF_FIFTH_PROBABILITY = 0;
    public static int MUTATOR_SCALE_OF_FIFTH_RANGE = 3;
    
    public static double MUTATOR_SIMPLIFY_PROBABILITY = 0.02;
    
    public static double MUTATOR_SWAP_SEGMENT_PROBABILITY = 0.02;

    /*
     * RATING PARAMETERS
     */
    public static double RATER_MELODY_REPETITION_WEIGHT = 0.25;
    public static double RATER_BEAT_REPETITION_WEIGHT = 0.25;
    public static double RATER_CHORD_REPETITION_WEIGHT = 0.25;
    public static double RATER_SCALE_WEIGHT = 0.5;
    public static double RATER_CRAZY_OCTAVE_WEIGHT = 0.5;
    public static double RATER_MELODY_DIRECTION_WEIGHT = 0.5;
    public static double RATER_PITCH_VARIETY_WEIGHT = 0.5;
    public static double RATER_PITCH_DIRECTION_WEIGHT = 0.5;
    public static double RATER_MELODY_NOTE_DENSITY_WEIGHT = 0.5;
    public static double RATER_RHYTHMIC_VARIETY_WEIGHT = 0.5;
    public static double RATER_NO_SILENCE_WEIGHT = 1;
    public static double RATER_MELODY_PITCH_RANGE_WEIGHT = 0.5;
    public static double RATER_REPEATED_PITCH_DENSITY_WEIGTH = 0.5;
    public static double RATER_MELODY_REST_DENSITY_WEIGHT = 0.5;
    public static double RATER_ZIPFS_LAW_WEIGHT = 0.5;
    public static double RATER_MELODY_NOTE_SUSTAIN_WEIGHT = 0.5;
    public static double RATER_LCM_PITCH_WEIGHT = 0.5;

    /*
     * CROSSOVER PARAMETERS
     */
    public static int CROSSOVER_NBR_OF_INTERSECTS = 2;

    /*
     * RANDOM INITIATOR PARAMETER
     */
    public static double RANDOM_INITIATOR_MAX_LENGTH = 100;

    /*
     * MARKOV PARAMETERS
     */
    public static int MARKOV_LOOKBACKS = 2;
    public static double MARKOV_SONGDURATION = 100;

}
