package geneticAlgorithm;

import java.util.ArrayList;
import java.util.List;

import model.Song;
import mutation.Mutation;
import crossover.Crossover;

/**
 * 
 * A class responsible for generating each generation of individuals.
 * 
 */
public class GeneticAlgorithm {

    private Song[]     parents;
    private Mutation   mutation;
    private Crossover  crossover;
    // private Rater rater;
    private int        numberOfParents;
    private int        generationSize;
    private boolean    throwAwayFirstParents;
    private boolean    usingElitism;
    private double     minimumRating;
    private int        minimumIterations;

    private List<Song> currentGeneration;
    private int        numberOfIterations;

    /**
     * Creates a new instance of this class.
     * 
     * @param parents
     * @param mutation
     * @param crossover
     * @param rater
     */
    public GeneticAlgorithm(Song[] parents, Mutation mutation,
            Crossover crossover) {// , Rater rater) {
        this.parents = parents;
        setMutation(mutation);
        setCrossover(crossover);
        // this.rater = rater;
        setNumberOfParents(this.parents.length);
        setGenerationSize(3);
        setThrowAwayFirstParents(true);
        setElitism(true);
        setMinimumRating(0);
        setMinimumIterations(0);
        this.numberOfIterations = 0;
        this.currentGeneration = new ArrayList<Song>();

    }

    public void setMutation(Mutation mutation) {
        this.mutation = mutation;
    }

    public void setCrossover(Crossover crossover) {
        this.crossover = crossover;
        this.crossover.setParents(parents);
    }

    public void setRater() {
        // this.rater = rater;
    }

    /**
     * Sets the number of parents that will be picked for the next generation.
     * Does not change the current set of parents.
     * 
     * @param numberOfParents
     *            The desired number of parents to be used.
     */
    public void setNumberOfParents(int numberOfParents) {
        this.numberOfParents = numberOfParents;
    }

    public void setGenerationSize(int generationSize) {
        this.generationSize = generationSize;
    }

    public void setThrowAwayFirstParents(boolean throwAwayFirstParents) {
        this.throwAwayFirstParents = throwAwayFirstParents;
    }

    public void setElitism(boolean usingElitism) {
        this.usingElitism = usingElitism;
    }

    public void setMinimumRating(double minimumRating) {
        this.minimumRating = minimumRating;
    }

    public void setMinimumIterations(int minimumIterations) {
        this.minimumIterations = minimumIterations;
    }

    public void iterate() {
        double highestRating = 0;
        while (highestRating < minimumRating
                || numberOfIterations < minimumIterations) {

        }
    }

    public void iterate(int n) {
        while (numberOfIterations < n) {

        }
    }

    public Song getBestChild() {
        return null;
    }

    public List<Song> getLastGeneration() {
        return currentGeneration;
    }

    private void createGeneration() {
        currentGeneration.clear();
        for (int i = 0; i < generationSize; i++) {
            currentGeneration.add(crossover.makeCrossover());
        }
    }

    private void setNewParents() {
        // TODO find suitable parents with rater and set them to the crossover
    }
}
