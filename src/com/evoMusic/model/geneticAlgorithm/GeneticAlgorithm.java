package com.evoMusic.model.geneticAlgorithm;

import java.security.AlgorithmConstraints;
import java.util.ArrayList;
import java.util.List;

import com.evoMusic.model.Song;
import com.evoMusic.model.geneticAlgorithm.mutation.Mutator;
import com.evoMusic.model.geneticAlgorithm.rating.Rater;
import com.evoMusic.model.geneticAlgorithm.selection.RouletteWheelSelection;

public class GeneticAlgorithm {
    private List<Song> inputSongs;
    private List<Individual> elitismSongs, nextPopulation;
    private RouletteWheelSelection rwSelect;
    private DrCross crossover;
    private Mutator mutator;
    private Rater rater;
    double ratingThreshold;
    int populationSize, nbrOfElitismSongs, nbrOfCrossoverSongs,
            currentIteration, nbrOfGenerations;

    /**
     * Initiate the genetic algorithm with the required objects.
     * 
     * @param inputSongs
     *            is the songs that is going to be used to initiate first
     *            generation
     * @param mutator
     *            is the mutator class with its sub mutators which are going to
     *            mutate the individuals
     * @param crossover
     *            is the crossover class that is used to make crossover
     * @param rater
     *            is the rater class with its sub raters which are going to rate
     *            the individuals
     * @param populationSize
     *            is the number of individuals in the population
     * @param nbrOfElitismSongs
     *            is the number of songs going to the next generation through
     *            elitism
     * @param nbrOfCrossoverSongs
     *            is the number of songs selected to make crossover on
     */
    public GeneticAlgorithm(List<Song> inputSongs, Mutator mutator,
            DrCross crossover, Rater rater, int populationSize,
            int nbrOfElitismSongs, int nbrOfCrossoverSongs) {
        this.inputSongs = inputSongs;
        this.crossover = crossover;
        this.mutator = mutator;
        this.rater = rater;
        this.populationSize = populationSize;
        this.nbrOfElitismSongs = nbrOfElitismSongs;
        this.nbrOfCrossoverSongs = nbrOfCrossoverSongs;
        currentIteration = 0;
    }

    /**
     * Generation a song through a given number of generation
     * 
     * @param nbrOfGenerations
     *            is the number of generation it is going to generate the song
     * @return the song as an Individual
     */
    public Individual generateGenerations(int nbrOfGenerations) {
        this.nbrOfGenerations = nbrOfGenerations;
        elitismSongs = new ArrayList<Individual>();
        currentIteration = 0;

        // TODO: Initialize weight based on input songs. Decide how we are going
        // to do this.

        // TODO: Change in this method so that markov chain is used.
        List<Song> firstGeneration = generateFirstGeneration();

        nextPopulation = ratePopulation(firstGeneration);

        selectElitismSongs(nextPopulation);

        for (int generation = 0; generation < nbrOfGenerations; generation++) {
            generateCurrentGeneration();
        }
        /*
         * Return the best song.
         */
        return getBestIndividual();
    }

    /**
     * Generate a song until a given rating is achieved.
     * 
     * @param ratingThreshold
     *            the rating that the best song should achieve
     * @return the best song as an Individual
     */
    public Individual generateUntilRating(double ratingThreshold) {
        this.ratingThreshold = ratingThreshold;
        elitismSongs = new ArrayList<Individual>();
        currentIteration = 0;

        // TODO: Initialize weight based on input songs. Decide how we are going
        // to do this.

        // TODO: Change in this method so that markov chain is used.
        List<Song> firstGeneration = generateFirstGeneration();

        List<Individual> ratedFirstGeneration = ratePopulation(firstGeneration);

        selectElitismSongs(ratedFirstGeneration);

        while (getBestIndividual().getRating() < ratingThreshold) {
            generateCurrentGeneration();
        }
        /*
         * Return the best song.
         */
        return getBestIndividual();
    }

    /**
     * Get the best individual. If there are no songs in 
     * @return
     */
    public Individual getBestIndividual() {
        if (elitismSongs.size() == 0) {
            return new Individual(null, 0);
        } else {
            return elitismSongs.get(0);
        }
    }

    public int getCurrentIteration() {
        return currentIteration;
    }

    private List<Song> generateFirstGeneration() {
        // TODO: Insert markov initialization here

        List<Song> population = new ArrayList<Song>();
        // for (int individual = 0; individual < populationSize; individual++) {
        // TODO: Insert generate individual and put in list here
        // population[individual] = markov
        // }
        crossover.setParents(inputSongs);
        List<Song> tempList = new ArrayList<Song>();
        for (int i = 0; i < populationSize; i++) {
            if (tempList.size() == 0) {
                tempList = crossover.crossIndividuals();
            }
            population.add(tempList.remove(0));
        }
        return population;
    }

    private void generateCurrentGeneration() {
        currentIteration++;
        List<Individual> currentPopulation = nextPopulation;
        for (int i = 0; i < nbrOfElitismSongs; i++) {
            currentPopulation.add(new Individual(elitismSongs.get(i).getSong().copy(),elitismSongs.get(i).getRating()));
        }

        /*
         * Select individuals from the new population. Perform crossover on
         * selected individuals. Mutate each individual in list. Rate each
         * individual.
         */
        List<Song> unratedPopulation = new ArrayList<Song>();
        List<Song> tempList = new ArrayList<Song>();
        rwSelect = new RouletteWheelSelection(nextPopulation);
        for (int i = 0; i < populationSize; i++) {
            if (tempList.size() == 0) {
                for (int j = 0; j < nbrOfCrossoverSongs; j++) {
                    tempList.add(nextPopulation.get(rwSelect.select()).getSong());
                }
                crossover.setParents(tempList);
                tempList = crossover.crossIndividuals();
            }
            unratedPopulation.add(tempList.remove(0));
            mutator.mutate(unratedPopulation.get(i));
        }
        nextPopulation = ratePopulation(unratedPopulation);
        selectElitismSongs(nextPopulation);
    }

    private List<Individual> ratePopulation(List<Song> population) {
        List<Individual> ratedPopulation = new ArrayList<Individual>();
        for (int individual = 0; individual < populationSize; individual++) {
            ratedPopulation.add(new Individual(population.get(individual),
                    rater.rate(population.get(individual))));
        }
        return ratedPopulation;
    }

    private void selectElitismSongs(List<Individual> population) {
        int nbrOfIndividuals = population.size();
        int startIndividual = 0;
        if (elitismSongs.size() == 0) {
            elitismSongs.add(population.get(0));
            startIndividual = 1;
        }
        boolean isAdded;
        for (int individual = startIndividual; individual < nbrOfIndividuals; individual++) {
            isAdded = false;
            for (int i = 0; i < elitismSongs.size(); i++) {
                if (population.get(individual).getRating() > elitismSongs
                        .get(i).getRating()) {
                    isAdded = true;
                    if (elitismSongs.size() < nbrOfElitismSongs) {
                        elitismSongs.add(i, population.get(individual));
                    } else {
                        elitismSongs.add(i, population.get(individual));
                        elitismSongs.remove((elitismSongs.size() - 1));
                    }
                }
            }
            if (elitismSongs.size() < nbrOfElitismSongs && !isAdded) {
                elitismSongs.add(population.get(individual));
            }
        }
    }

    private List<Song> individualToSong(List<Individual> individuals) {
        List<Song> songList = new ArrayList<Song>();
        for (int i = 0; i < individuals.size(); i++) {
            songList.add(individuals.get(i).getSong());
        }
        return songList;
    }
}