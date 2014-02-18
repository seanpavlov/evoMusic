package geneticAlgorithm;

import java.util.ArrayList;
import java.util.List;

import rating.Rater;
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
    private Rater      rater;
    private int        numberOfParents;
    private int        generationSize;
    private boolean    throwAwayFirstParents;
    private boolean    usingElitism;
    private double     minimumRating;
    private int        minimumIterations;
    private List<Song> currentGeneration;
    private int        numberOfIterations;

    /**
     * Creates a new instance of this class. Defaults elitism to true, to throw
     * away first parents to true, generation size to 3, minimum rating and
     * iterations both to 0 and the number of parents to choose to the number of
     * parents given.
     * 
     * @param parents
     *            The individuals to use for generating the first generation.
     * @param mutation
     *            The mutator that will mutate all children in each generation.
     * @param crossover
     *            The crossover that will create each generation from a set of
     *            parents.
     * @param rater
     *            The rater that will rate all individuals for each generation.
     */
    public GeneticAlgorithm(Song[] parents, Mutation mutation,
            Crossover crossover, Rater rater) {
        this.parents = parents;
        setMutator(mutation);
        setCrossover(crossover);
        setRater(rater);
        setNumberOfParents(this.parents.length);
        setGenerationSize(3);
        setThrowAwayFirstParents(true);
        setElitism(true);
        setMinimumRating(0);
        setMinimumIterations(0);
        this.numberOfIterations = 0;
        this.currentGeneration = new ArrayList<Song>();

    }

    /**
     * Sets the mutator that will execute the mutations on all the individuals
     * during the iterations.
     * 
     * @param mutator
     *            The mutator that will execute all the mutations on the
     *            individuals during the iterations.
     */
    public void setMutator(Mutation mutator) {
        this.mutation = mutator;
    }

    /**
     * Sets the crossover that will create all the children for each generation.
     * 
     * @param crossover
     *            The crossover that will create all the children for each
     *            generation.
     */
    public void setCrossover(Crossover crossover) {
        this.crossover = crossover;
        this.crossover.setParents(parents);
    }

    /**
     * Sets the rater that will be rating all individuals during the iterations.
     * 
     * @param rater
     *            The rater that will rate all individuals during the
     *            iterations.
     */
    public void setRater(Rater rater) {
        this.rater = rater;
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

    /**
     * Sets the number of children that will be generated for each generation.
     * 
     * @param generationSize
     *            The number of children that will be generated for each
     *            generation.
     */
    public void setGenerationSize(int generationSize) {
        this.generationSize = generationSize;
    }

    /**
     * Set whether the first set of parents should be allowed to be kept to the
     * next generation. The parents will never be kept if elitism is set to
     * false.
     * 
     * @param throwAwayFirstParents
     *            Whether the first set of parents should be kept or not.
     */
    public void setThrowAwayFirstParents(boolean throwAwayFirstParents) {
        this.throwAwayFirstParents = throwAwayFirstParents;
    }

    /**
     * If set to true, the parents of the current generation may stay as parents
     * to the next generation if they get high enough rating. Otherwise the
     * parents will only stay for one generation.
     * 
     * @param usingElitism
     *            Whether to use elitism or not.
     */
    public void setElitism(boolean usingElitism) {
        this.usingElitism = usingElitism;
    }

    /**
     * Set the lower limit on the rating of one child in a generation, after
     * which the iterations may stop.
     * 
     * @param minimumRating
     *            The lower rating limit.
     */
    public void setMinimumRating(double minimumRating) {
        this.minimumRating = minimumRating;
    }

    /**
     * Set the lower limit on the number of iterations, after which the
     * iterations may stop.
     * 
     * @param minimumIterations
     *            The lower limit on the number of iterations.
     */
    public void setMinimumIterations(int minimumIterations) {
        this.minimumIterations = minimumIterations;
    }

    /**
     * Iterates through generations until both minimumRating and
     * minimumIterations are fulfilled.
     */
    public void iterate() {
        double highestRating = 0;
        while (highestRating < minimumRating
                || numberOfIterations < minimumIterations) {

        }
    }

    /**
     * Iterates over n generations. This ignores the minimumRating and
     * minimumIterations criteria.
     * 
     * @param n
     *            The number of generations to iterate over.
     */
    public void iterate(int n) {
        while (numberOfIterations < n) {

        }
    }

    /**
     * Gets the child which has the highest rating of the current generation.
     * 
     * @return The child of the current generation with the highest rating.
     */
    public Song getBestChild() {
        return null;
        // TODO Implement so that it gets the child from this generation with
        // the highest rating according to the rater.
    }

    /**
     * Gets all children of the current generation.
     * 
     * @return All children of the current generation.
     */
    public List<Song> getLastGeneration() {
        return currentGeneration;
    }

    private void createAndMutateGeneration() {
        currentGeneration.clear();
        for (int i = 0; i < generationSize; i++) {
            currentGeneration.add(crossover.makeCrossover());
            // mutator.mutate(currentGeneration.get(i));
        }
    }

    private void setNewParents() {
        // TODO find suitable parents with rater and set them to the crossover
    }
}
