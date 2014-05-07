package com.evoMusic.util;

/**
 * 
 * GENERAL RULES OF USE 
 * --------------------- 
 * Naming is done in the same name a
 * package structure is. Always start with a word that identifies in what kind
 * of package the parameter is used. And then go into more specific words.
 * 
 * For example: I want a parameter for random note probability. Probability
 * belongs to random note. Random note is a mutator. Therefore named
 * MUTATOR_RANDOM_NOTE_PORBABILITY
 * 
 */
public class Parameters {

    /*
     * GENETIC ALGORITHM PARAMETERS
     */
    public int GA_POPULATION_SIZE = 10;
    public int GA_NBR_OF_ELITISM_SONGS = 2;
    public int GA_NBR_OF_CROSSOVER_SONGS = 2;
    
    /*
     * MUTATION PARAMETERS
     */
    public double MUTATION_INITIAL_PROBABILITY = 1;
    public double MUTATION_MINIMUM_PROBABILITY = 0.5;
    public double MUTATION_PROBABILITY_RATIO = 0.0005;

    public double MUTATOR_RANDOM_NOTE_PROBABILITY = 0.05;
    public int MUTATOR_RANDOM_NOTE_STEP_RANGE = 6;
    
    public double MUTATOR_RHYTHM_VALUE_PROBABILITY = 0;
    public double MUTATOR_RHYTHM_VALUE_MOVING_RANGE = 1;

    public double MUTATOR_OCTAVE_PROBABILITY = 0;
    public int MUTATOR_OCTAVE_RANGE = 1;

    public double MUTATOR_REVERSE_PROBABILITY = 0.1;

    public double MUTATOR_SCALE_OF_FIFTH_PROBABILITY = 0;
    public int MUTATOR_SCALE_OF_FIFTH_RANGE = 3;

    public double MUTATOR_SIMPLIFY_PROBABILITY = 0.02;

    /*
     * RATING PARAMETERS
     */
    public double RATER_MELODY_REPETITION_WEIGHT = 0.25;
    public double RATER_BEAT_REPETITION_WEIGHT = 0.25;
    public double RATER_CHORD_REPETITION_WEIGHT = 0.25;
    public double RATER_SCALE_WEIGHT = 0.5;
    public double RATER_CRAZY_OCTAVE_WEIGHT = 0.5;
    public double RATER_MELODY_DIRECTION_WEIGHT = 0.5;
    public double RATER_PITCH_VARIETY_WEIGHT = 0.5;
    public double RATER_PITCH_DIRECTION_WEIGHT = 0.5;
    public double RATER_MELODY_NOTE_DENSITY_WEIGHT = 0.5;
    public double RATER_RHYTHMIC_VARIETY_WEIGHT = 0.5;
    public double RATER_NO_SILENCE_WEIGHT = 1;
    public double RATER_MELODY_PITCH_RANGE_WEIGHT = 0.5;
    public double RATER_REPEATED_PITCH_DENSITY_WEIGTH = 0.5;
    public double RATER_MELODY_REST_DENSITY_WEIGHT = 0.5;
    public double RATER_ZIPFS_LAW_WEIGHT = 0.5;
    public double RATER_MELODY_NOTE_SUSTAIN_WEIGHT = 0.5;
    public double RATER_LCM_PITCH_WEIGHT = 0.5;

    /*
     * CROSSOVER PARAMETERS
     */
    public int CROSSOVER_NBR_OF_INTERSECTS = 2;
    
    /*
     * MARKOV PARAMETERS
     */
    public int MARKOV_LOOKBACKS = 1;
    public double MARKOV_SONGDURATION = 50;

    private static Parameters parameters = new Parameters();

    /*
     * Empty constructor
     */
    public Parameters() {
    }

    // TODO: Implement constructor that takes a list of parameters a input to
    // set custom param values.

    public static Parameters getInstance() {
        return parameters;
    }
}
