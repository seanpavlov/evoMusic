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
     * MUTATION PARAMETERS
     */
    public double MUTATION_INITIAL_PROBABILITY = 0.3;
    public double MUTATION_MINIMUM_PROBABILITY = 0.05;
    public double MUTATION_PROBABILITY_RATIO = 0.01;

    public double MUTATOR_RANDOM_NOTE_PROBABILITY = 1;
    public int MUTATOR_RANDOM_NOTE_STEP_RANGE = 12;

    public double MUTATOR_OCTAVE_PROBABILITY = 0;
    public int MUTATOR_OCTAVE_RANGE = 1;

    public double MUTATOR_REVERSE_PROBABILITY = 0;
    public int MUTATOR_REVERSE_NBR_OF_NEIGHBORS = 4;
    public int MUTATOR_REVERSE_RANGE = 4;
    public boolean MUTATOR_REVERSE_WITH_RHYTHM_VALUE = true;

    public double MUTATOR_SCALE_OF_FIFTH_PROBABILITY = 0;
    public int MUTATOR_SCALE_OF_FIFTH_RANGE = 3;

    public double MUTATOR_SIMPLIFY_PROBABILITY = 0;
    public int MUTATOR_SIMPLIFY_NBR_OF_NEIGHBORS = 4;
    public double MUTATOR_SIMPLIFY_NEIGHBOR_PROBABILITY = 0.1;

    /*
     * RATING PARAMETERS
     */
    public double RATER_MELODY_REPETITION_WEIGHT = 1;
    public double RATER_BEAT_REPETITION_WEIGHT = 1;
    public double RATER_CHORD_REPETITION_WEIGHT = 1;
    public double RATER_SCALE_WEIGHT = 1;

    /*
     * CROSSOVER PARAMETERS
     */
    public int CROSSOVER_NBR_OF_INTERSECTS = 4;
    public int CROSSOVER_MIN_DURATION = 50;
    public int CROSSOVER_MAX_DURATION = 200;

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