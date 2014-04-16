package com.evoMusic.model.geneticAlgorithm;

import java.util.List;

import com.evoMusic.model.Song;
import com.evoMusic.model.geneticAlgorithm.mutation.Mutator;
import com.evoMusic.model.geneticAlgorithm.rating.Rater;

public class GeneticAlgorithm {
    private List<Song> originalSongs;
    private Song[] population, bestSongs;
    private double[] populationRating, bestSongsRating;
    private Mutator mutator;
    private Crossover crossover;
    private Rater rater;
    int populationSize, nbrOfGenerations, nbrOfBestIndividuals;
    double allowedRating;

    public GeneticAlgorithm(List<Song> inputSongs, Mutator mutator,
            Crossover crossover, Rater rater, int populationSize,
            int nbrOfGenerations, int nbrOfBestIndividuals) {
        originalSongs = inputSongs;
        this.mutator = mutator;
        this.crossover = crossover;
        this.rater = rater;
        this.populationSize = populationSize;
        this.nbrOfGenerations = nbrOfGenerations;
        this.nbrOfBestIndividuals = nbrOfBestIndividuals;
        population = new Song[populationSize];
        populationRating = new double[populationSize];
        bestSongs = new Song[nbrOfBestIndividuals];
        bestSongsRating = new double[nbrOfBestIndividuals];

        // TODO: Initialize weight based on input songs. Decide how we are going
        // to do this.

        // TODO: Fix generating first generation
        generateFirstGeneration();

        ratePopulation();

        selectBestIndividuals();
        
        for(int generation = 0; generation < nbrOfGenerations; generation++){
            // TODO: Fix input into rate, elitism etc for temp population to work
        }
        // TODO: Fix selection here
    }

    private void generateFirstGeneration() {
        // TODO: Insert markov initialization here

        for (int individual = 0; individual < populationSize; individual++) {
            // TODO: Insert generate individual and put in list here
            // population[individual] = markov
        }
    }

    private void ratePopulation() {
        for (int individual = 0; individual < populationSize; individual++) {
            populationRating[individual] = rater.rate(population[individual]);
        }
    }

    private void selectBestIndividuals() {
        for (int individual = 0; individual < populationSize; individual++) {
            check_if_better_loop: for (int bestIndividual = 0; bestIndividual < nbrOfBestIndividuals; bestIndividual++) {
                if(populationRating[individual] > bestSongsRating[bestIndividual]){
                    for(int shift = nbrOfBestIndividuals; shift > bestIndividual; shift--){
                        bestSongs[shift] = bestSongs[shift-1];
                        bestSongsRating[shift] = bestSongsRating[shift-1];
                    }
                    bestSongs[bestIndividual] = population[individual];
                    bestSongsRating[bestIndividual] = populationRating[individual];
                    break check_if_better_loop;
                }
            }
        }
    }
}