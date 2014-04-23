package com.evoMusic.model.geneticAlgorithm;

import java.util.ArrayList;
import java.util.List;

import com.evoMusic.model.Song;
import com.evoMusic.model.geneticAlgorithm.mutation.Mutator;
import com.evoMusic.model.geneticAlgorithm.rating.Rater;
import com.evoMusic.model.geneticAlgorithm.selection.RouletteWheelSelection;

public class GeneticAlgorithm {
    private List<Individual> elitismSongs;
    private List<Song> inputSongs;
    private RouletteWheelSelection rwSelect;
    private DrCross crossover;
    private Mutator mutator;
    private Rater rater;
    double ratingThreshold;
    int populationSize, nbrOfElitismSongs, nbrOfCrossoverSongs,
            currentIteration, nbrOfGenerations;

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
     * @param nbrOfCrossoverSongs
     *            is the number of individuals used in a crossover.
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

    public Individual generateGenerations(int nbrOfGenerations) {
        this.nbrOfGenerations = nbrOfGenerations;
        elitismSongs = new ArrayList<Individual>();

        // TODO: Initialize weight based on input songs. Decide how we are going
        // to do this.

        // TODO: Change in this method so that markov chain is used.
        List<Song> firstGeneration = generateFirstGeneration();

        List<Individual> ratedFirstGeneration = ratePopulation(firstGeneration);

        selectElitismSongs(ratedFirstGeneration);

        List<Song> tempPopulation;
        List<Individual> population;
        for (int generation = 0; generation < nbrOfGenerations; generation++) {
            currentIteration++;
            tempPopulation = new ArrayList<Song>();
            for (int i = 0; i < nbrOfElitismSongs; i++) {
                tempPopulation.add(elitismSongs.get(i).getSong().copy());
            }

            // Generate individuals - crossover on best population
            // Rate them and put in best population if better
            List<Song> tempList = new ArrayList<Song>();
            crossover.setParents(individualToSong(elitismSongs));
            for (int i = 0; i < populationSize - nbrOfElitismSongs; i++) {
                if (tempList.size() == 0) {
                    tempList = crossover.crossIndividuals();
                }
                tempPopulation.add(tempList.remove(0));
            }
            population = ratePopulation(tempPopulation);
            selectElitismSongs(population);

            /*
             * Select individuals from the new population. Perform crossover on
             * selected individuals. Mutate each individual in list. Rate each
             * individual.
             */
            tempPopulation = new ArrayList<Song>();
            tempList = new ArrayList<Song>();
            rwSelect = new RouletteWheelSelection(population);
            for (int i = 0; i < populationSize; i++) {
                if (tempList.size() == 0) {
                    for (int j = 0; j < nbrOfCrossoverSongs; j++) {
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
        /*
         * Return the best song.
         */
        return getBestIndividual();
    }
    
    public Individual getBestIndividual() {
        if (elitismSongs.size() == 0){
            return null;
        }else{
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
        for (int individual = startIndividual; individual < nbrOfIndividuals; individual++) {
            for (int i = 0; i < elitismSongs.size(); i++) {
                if (population.get(individual).getRating() > elitismSongs
                        .get(i).getRating()) {
                    if (elitismSongs.size() < nbrOfElitismSongs) {
                        elitismSongs.add(i, population.get(individual));
                    } else {
                        elitismSongs.add(i, population.get(individual));
                        elitismSongs.remove((elitismSongs.size() - 1));
                    }
                }
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