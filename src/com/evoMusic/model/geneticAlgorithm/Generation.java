package com.evoMusic.model.geneticAlgorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.evoMusic.model.Song;
import com.evoMusic.model.geneticAlgorithm.mutation.Mutator;
import com.evoMusic.model.geneticAlgorithm.rating.Rater;

public class Generation {

    private List<Individual> parents;
    private List<Individual> children;

    /**
     * Creates a new generation with a given list of parents.
     * 
     * @param parents
     *            The individuals that will create this generation.
     */
    public Generation(List<Individual> parents) {
        this.parents = new ArrayList<Individual>(parents);
    }

    public List<Individual> getParents() {
        return new ArrayList<Individual>(parents);
    }

    /**
     * Gets all the children of this generation.
     * 
     * @return A list containing all children of this generation if any,
     *         otherwise null.
     */
    public List<Individual> getChildren() {
        if (children == null) {
            return null;
        } else {
            return new ArrayList<Individual>(children);
        }
    }

    /**
     * Given a crossover, a mutator and a rater, creates new children with
     * desired number of children.
     * 
     * @param crossover
     *            The given crossover to create the children.
     * @param mutator
     *            The mutator that will mutate the children.
     * @param rater
     *            The given rater to rate the children.
     * @param numberOfChildren
     *            The desired number of children for this generation.
     */
    public void makeChildren(Crossover crossover, Mutator mutator, Rater rater,
            int numberOfChildren) {
        this.children = new ArrayList<Individual>(numberOfChildren);
        for (int i = 0; i < numberOfChildren; i++) {
            Song song = crossover.makeCrossover(parents);
            mutator.mutate(song);
            children.add(new Individual(song, rater.rate(song)));
        }
        // Updating the mutation probability if mutator use decreasing
        // probability ratio.
        mutator.updateMutationProbability();
    }

    /**
     * Gets the best children of this generation. If the desired number of
     * children is less than what is available, it will return all children in
     * this generation. If no children has been created it will return null.
     * 
     * @param wantedChildren
     *            The desired number of children.
     * @return A list containing at most the desired number of best children
     *         from this generation if any, null if no children has been
     *         created.
     */
    public List<Individual> getBestChildren(int wantedChildren) {
        if (children == null) {
            return null;
        } else {
            return getBestFromList(children, wantedChildren);
        }
    }

    /**
     * Gets the best individuals of this generation, including both parents and
     * children. If children has not been created yet, it will only choose among
     * the parents. If the desired number of individuals is less than what is
     * available, it will return all individuals in this generation.
     * 
     * @param wantedIndividuals
     *            The desired number of individuals.
     * @return A list containing at most the desired number of best individuals
     *         from this generation.
     */
    public List<Individual> getBestIndividuals(int wantedIndividuals) {
        List<Individual> allIndividuals = new ArrayList<Individual>();
        if (children != null) {
            allIndividuals.addAll(children);
        }
        allIndividuals.addAll(parents);
        return getBestFromList(allIndividuals, wantedIndividuals);
    }

    /**
     * Takes the best individuals from a given list. If wanted individuals is
     * less than or equal to the size of the list, it will return the same list
     * which was given.
     * 
     * @param individuals
     *            The list of individuals which the best will taken from.
     * @param wantedIndividuals
     *            The number of wanted individuals.
     * @return A list containing at most the desired number of best individuals
     *         from the given list.
     */
    private List<Individual> getBestFromList(List<Individual> individuals,
            int wantedIndividuals) {
        if (individuals.size() <= wantedIndividuals) {
            return individuals;
        }
        List<Individual> bestIndividuals = new ArrayList<Individual>(
                wantedIndividuals);
        Collections.sort(individuals);
        for (int i = 0; i < wantedIndividuals; i++) {
            bestIndividuals.add(individuals.get(individuals.size() - i - 1));
        }
        return bestIndividuals;
    }

}
