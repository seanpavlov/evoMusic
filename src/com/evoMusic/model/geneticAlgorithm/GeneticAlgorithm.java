package com.evoMusic.model.geneticAlgorithm;

import java.util.ArrayList;
import java.util.List;

import com.evoMusic.model.Song;
import com.evoMusic.model.geneticAlgorithm.mutation.Mutator;
import com.evoMusic.model.geneticAlgorithm.rating.Rater;
import com.evoMusic.model.geneticAlgorithm.selection.RouletteWheelSelection;

public class GeneticAlgorithm {
    private List<Individual> bestIndividuals;

    private DrCross crossover;
    private Mutator mutator;
    private Rater rater;
    private RouletteWheelSelection rwSelect;
    int populationSize, nbrOfGenerations, nbrOfElitismSongs,
            nbrOfCrossoverIndividuals, currentIteration;
    double ratingThreshold;
    private List<Song> inputSongs;

    /**
     * Initiate the genetic algorithm with the necessary parameters.
     * 
     * @param inputSongs
     *            is the songs that the user choose.
     * @param mutator
     *            is the mutator containing all sub mutators.
     * @param crossover
     *            is the crossover used.
     * @param rater
     *            is the rater containing all sub raters.
     * @param populationSize
     *            is the size of the population in each generation
     * @param nbrOfElitismSongs
     *            is the number of songs going to next generation through
     *            elitism.
     * @param nbrOfCrossoverIndividuals
     *            is the number of individuals used in a crossover.
     */
    public GeneticAlgorithm(List<Song> inputSongs, Mutator mutator,
            DrCross crossover, Rater rater, int populationSize,
            int nbrOfElitismSongs, int nbrOfCrossoverIndividuals) {
        this.inputSongs = inputSongs;
        this.crossover = crossover;
        this.mutator = mutator;
        this.rater = rater;
        this.populationSize = populationSize;
        this.nbrOfElitismSongs = nbrOfElitismSongs;
        this.nbrOfCrossoverIndividuals = nbrOfCrossoverIndividuals;
        this.inputSongs = inputSongs;
    }

    public void generateGenerations(int nbrOfGenerations) {
        this.nbrOfGenerations = nbrOfGenerations;
        bestIndividuals = new ArrayList<Individual>();
        currentIteration = 0;

        // TODO: Initialize weight based on input songs. Decide how we are going
        // to do this.

        // TODO: Fix generating first generation
        List<Song> firstGeneration = generateFirstGeneration();

        List<Individual> ratedFirstGeneration = ratePopulation(firstGeneration);

        selectElitismSongs(ratedFirstGeneration);

        List<Song> tempPopulation;
        List<Individual> population;

        for (int generation = 0; generation < nbrOfGenerations; generation++) {
            currentIteration++;
            tempPopulation = new ArrayList<Song>();
            for(int i = 0; i < bestIndividuals.size(); i++){
                tempPopulation.add(bestIndividuals.get(i).getSong().copy());
            }
            population = new ArrayList<Individual>();

            // Generate individuals - crossover on best population
            // Rate them and put in best population if better
            List<Song> tempList = new ArrayList<Song>();
            crossover.setParents(individualToSong(bestIndividuals));
            for (int i = 0; i < populationSize-bestIndividuals.size(); i++) {
                if (tempList.size() == 0) {
                    tempList = crossover.crossIndividuals();
                }
                tempPopulation.add(tempList.remove(0));
            }
            population = ratePopulation(tempPopulation);
            selectElitismSongs(population);

            // Select individuals to be crossovered
            tempPopulation = new ArrayList<Song>();
            tempList = new ArrayList<Song>();
            rwSelect = new RouletteWheelSelection(population);
            for (int i = 0; i < populationSize; i++) {
                if (tempList.size() == 0) {
                    for (int j = 0; j < nbrOfCrossoverIndividuals; j++) {
                        tempList.add(population.get(rwSelect.select())
                                .getSong());
                    }
                    crossover.setParents(tempList);
                    tempList = crossover.crossIndividuals();
                }
                tempPopulation.add(tempList.remove(0));
                mutator.mutate(tempPopulation.get(i));
            }
            population = ratePopulation(tempPopulation);
            selectElitismSongs(population);
        }
    }

    public Song getBestSong() {
        return bestIndividuals.get(0).getSong();
    }

    public double getBestRating() {
        if (bestIndividuals.size() == 0) {
            return 0;
        } else {
            return bestIndividuals.get(0).getRating();
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
        for (int individual = 0; individual < nbrOfIndividuals; individual++) {
            for (int i = 0; i < bestIndividuals.size(); i++) {
                if (population.get(individual).getRating() > bestIndividuals
                        .get(i).getRating()) {
                    if (bestIndividuals.size() < nbrOfElitismSongs) {
                        bestIndividuals.add(i, population.get(individual));
                    } else {
                        bestIndividuals.add(i, population.get(individual));
                        bestIndividuals.remove((bestIndividuals.size() - 1));
                    }
                }
            }
            if (bestIndividuals.size() == 0) {
                bestIndividuals.add(population.get(individual));
            }
        }
    }

    private List<Song> individualToSong(List<Individual> individuals) {
        List<Song> tempList = new ArrayList<Song>();
        for (int i = 0; i < individuals.size(); i++) {
            tempList.add(individuals.get(i).getSong());
        }
        return tempList;
    }
}