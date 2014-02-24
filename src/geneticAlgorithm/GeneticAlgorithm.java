package geneticAlgorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import model.Song;
import mutation.Mutator;
import rating.Rater;
import crossover.Crossover;

/**
 * 
 * A class responsible for generating each generation of individuals.
 * 
 */
public class GeneticAlgorithm {

    private ArrayList<Individual> parents;
    private List<Individual> currentGeneration;
    private Mutator mutator;
    private Crossover crossover;
    private Rater rater;
    private int numberOfParents;
    private int generationSize;
    private boolean throwAwayFirstParents;
    private boolean usingElitism;
    private double minimumRating;
    private int minimumIterations;
    private int numberOfIterations;
    private int iterationsDone;

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
        setCrossover(crossover);
        setRater(rater);

        this.parents = new ArrayList<Individual>();
        this.currentGeneration = new ArrayList<Individual>();
        addIndividuals(parents, this.parents);

        setNumberOfParents(this.parents.size());
        setGenerationSize(3);
        setThrowAwayFirstParents(true);
        setElitism(true);
        setMinimumRating(0);
        setMinimumIterations(0);
        this.numberOfIterations = 0;

    }
    
    public int getIterationsDone() {
        return iterationsDone;
    }

    private void addIndividuals(List<Song> songList,
            List<Individual> individualList) {
        for (Song song : songList) {
            individualList.add(new Individual(song, rater.rate(song)));
        }
    }

    private Song[] getSongfromIndividuals(List<Individual> individuals) {
        Song[] songs = new Song[individuals.size()];
        for (int i = 0; i < songs.length; i++) {
            songs[i] = individuals.get(i).getSong();
        }
        return songs;
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
        this.crossover.setParents(getSongfromIndividuals(parents));
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
        createMutateRateGeneration();
        if(throwAwayFirstParents) {
            parents.clear();
        }
        while (highestRating < minimumRating
                || numberOfIterations < minimumIterations) {
            
            setNewParents();
            createMutateRateGeneration();
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
        createMutateRateGeneration();
        if(throwAwayFirstParents) {
            parents.clear();
        }
        while (numberOfIterations < n) {
            setNewParents();
            createMutateRateGeneration();
        }
    }

    /**
     * Gets the first child which has the highest rating of the current generation.
     * 
     * @return The child of the current generation with the highest rating.
     */
    public Song getBestChild() {
        Collections.sort(currentGeneration);
        return currentGeneration.get(0).getSong();
    }

    /**
     * Gets all children of the current generation with their rating.
     * 
     * @return All children of the current generation with their rating.
     */
    public List<Individual> getLastGeneration() {
        return currentGeneration;
    }

    private void createMutateRateGeneration() {
        currentGeneration.clear();
        Song newChild;
        for (int i = 0; i < generationSize; i++) {
            newChild = crossover.makeCrossover();
            currentGeneration.add(new Individual(newChild, rater.rate(newChild)));
            // mutator.mutate(newChild);
        }
    }

    private void setNewParents() {
        // TODO Optimize. Currently over-using of sorting.
        List<Individual> newParents = new LinkedList<Individual>();
        double lowestRating = 0;
        if(usingElitism) {
            Collections.sort(parents);
            for(Individual oldParent : parents) {
                if(oldParent.getRating() > lowestRating) {
                    if(newParents.size() == numberOfParents) {
                        Collections.sort(newParents);
                        newParents.remove(newParents.size()-1);
                        newParents.add(oldParent);
                        Collections.sort(newParents);
                        lowestRating = newParents.get(newParents.size()-1).getRating();
                    } else {
                        newParents.add(oldParent);
                        Collections.sort(newParents);
                        lowestRating = newParents.get(newParents.size()-1).getRating();
                    }
                }
            }
        }
        for(Individual child : currentGeneration) {
            if(newParents.size() == numberOfParents) {
                if(child.getRating() > lowestRating) {
                    Collections.sort(newParents);
                    newParents.remove(newParents.size()-1);
                    newParents.add(child);
                    Collections.sort(newParents);
                    lowestRating = newParents.get(newParents.size()-1).getRating();
                }
            } else {
                newParents.add(child);
                Collections.sort(newParents);
                lowestRating = newParents.get(newParents.size()-1).getRating();
            }
        }
    }
}
