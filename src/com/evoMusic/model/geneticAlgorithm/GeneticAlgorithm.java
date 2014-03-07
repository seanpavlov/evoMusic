package com.evoMusic.model.geneticAlgorithm;

import java.util.ArrayList;
import java.util.List;

import com.evoMusic.model.Song;
import com.evoMusic.model.geneticAlgorithm.mutation.Mutator;
import com.evoMusic.model.geneticAlgorithm.rating.Rater;

/**
 * 
 * A class responsible for generating each generation of individuals.
 * 
 */
public class GeneticAlgorithm {

    private ArrayList<Individual> firstParents;
    private Mutator mutator;
    private Crossover crossover;
    private Rater rater;
    private int parentsPerGeneration;
    private int childrenPerGeneration;
    private boolean throwAwayFirstParents;
    private boolean usingElitism;
    private double minimumRating;
    private int minimumIterations;
    private int iterationsDone;

    private Generation generation;

    /**
     * Creates a new instance of this class. Defaults elitism to true, to throw
     * away first parents to true, generation size to 3, minimum rating and
     * iterations both to 0 and the number of parents to choose to the number of
     * parents given.
     * 
     * @param parents
     *            The individuals to use for generating the first generation.
     * @param mutator
     *            The mutator that will mutate all children in each generation.
     * @param crossover
     *            The crossover that will create each generation from a set of
     *            parents.
     * @param rater
     *            The rater that will rate all individuals for each generation.
     */
    public GeneticAlgorithm(List<Song> parents, Mutator mutator,
            Crossover crossover, Rater rater) {

        setMutator(mutator);
        setRater(rater);
        setCrossover(crossover);

        this.firstParents = new ArrayList<Individual>();
        addIndividuals(parents, this.firstParents);
        generation = new Generation(firstParents);


        setParentsPerGeneration(this.firstParents.size());
        setChildrenPerGeneration(this.firstParents.size() * 3);
        setThrowAwayFirstParents(true);
        setElitism(true);
        setMinimumRating(0);
        setMinimumIterations(0);

    }

    public int getIterationsDone() {
        return iterationsDone;
    }

    /**
     * Gets a list with the parents used for generating the first generation of
     * children.
     * 
     * @return A list with the parents used for generating the first generation
     *         of children.
     */
    public List<Individual> getFirstParents() {
        return this.firstParents;
    }

    /**
     * Takes a list of song and add them to a list of individuals while rating
     * them in the process.
     * 
     * @param songList
     *            The list from where the songs will be taken.
     * @param individualList
     *            The list to where the individuals will be added.
     */
    private void addIndividuals(List<Song> songList,
            List<Individual> individualList) {
        for (Song song : songList) {
            individualList.add(new Individual(song, rater.rate(song)));
        }
    }

    /**
     * Sets the mutator that will execute the mutations on all the individuals
     * during the iterations.
     * 
     * @param mutator
     *            The mutator that will execute all the mutations on the
     *            individuals during the iterations.
     */
    public void setMutator(Mutator mutator) {
        this.mutator = mutator;
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
     * @param parentsPerGeneration
     *            The desired number of parents to be used.
     */
    public void setParentsPerGeneration(int parentsPerGeneration) {
        this.parentsPerGeneration = parentsPerGeneration;
    }

    /**
     * Sets the number of children that will be generated for each generation.
     * 
     * @param childrenPerGeneration
     *            The number of children that will be generated for each
     *            generation.
     */
    public void setChildrenPerGeneration(int childrenPerGeneration) {
        this.childrenPerGeneration = childrenPerGeneration;
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
        double currentRating = 0;
        while (highestRating < minimumRating
                || iterationsDone < minimumIterations) {
            currentRating = nextGeneration();            
            if (currentRating > highestRating) {
                highestRating = currentRating;
            }
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
        while (iterationsDone < n) {
            nextGeneration();
        }
    }
    
    private double nextGeneration() {
        if (iterationsDone == 0) {
            generation.makeChildren(crossover, mutator, rater,
                    childrenPerGeneration);
            iterationsDone++;
        }
        if (iterationsDone == 1 && throwAwayFirstParents) {
            generation = new Generation(
                    generation.getBestChildren(parentsPerGeneration));
            generation.makeChildren(crossover, mutator, rater, childrenPerGeneration);
            iterationsDone++;
        }
        if (usingElitism) {
            generation = new Generation(
                    generation.getBestIndividuals(parentsPerGeneration));
        } else {
            generation = new Generation(
                    generation.getBestChildren(parentsPerGeneration));
        }
        double currentRating = generation.getBestIndividuals(1).get(0).getRating();
        generation.makeChildren(crossover, mutator, rater,
                childrenPerGeneration);
        iterationsDone++;
        return currentRating;
    }

    /**
     * Gets the child which has the highest rating of the current generation.
     * 
     * @return The child of the current generation with the highest rating.
     */
    public Song getBestChild() {
        return generation.getBestChildren(1).get(0).getSong();
    }

    /**
     * Gets the child or parent which has the highest rating of the current
     * generation.
     * 
     * @return The child or parent of the current generation with the highest
     *         rating.
     */
    public Song getBest() {
        return generation.getBestIndividuals(1).get(0).getSong();
    }

    public Generation getGeneration() {
        return generation;
    }

}
