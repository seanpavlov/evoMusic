package com.evoMusic.model.geneticAlgorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.evoMusic.model.Song;
import com.evoMusic.model.geneticAlgorithm.blending.MarkovSong;
import com.evoMusic.model.geneticAlgorithm.mutation.Mutator;
import com.evoMusic.model.geneticAlgorithm.rating.Rater;
import com.evoMusic.model.geneticAlgorithm.selection.RouletteWheelSelection;
import com.evoMusic.parameters.P;
import com.evoMusic.util.Helpers;

/**
 * The algorithm that provides the iteration of a genetic algorithm. Initiate
 * the input songs and parameters that is going to be used with the constructor.
 * Call either generateGenerations-method or generateUntilRating-method to
 * generate a song. Worth to notice is that current iteration and the best song
 * is reset each time generateGenerations or generateUntilRating-methods are
 * called.
 */
public class GeneticAlgorithm {
    private List<Song> inputSongs;
    private List<Individual> elitismSongs, nextPopulation;
    private RouletteWheelSelection rwSelect;
    private DrCross crossover;
    private Mutator mutator;
    private Rater rater;
    private MarkovSong markovSong;
    private Individual bestIndividual;
    private Logger log = Helpers.LOGGER;
    double ratingThreshold, songDuration;
    int populationSize, nbrOfElitismSongs, nbrOfCrossoverSongs,
            nbrOfMarkovLookbacks, currentIteration, nbrOfGenerations;
    private RandomInitiator randomInitiator = new RandomInitiator(P.RANDOM_INITIATOR_MAX_LENGTH);
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
            int nbrOfElitismSongs, int nbrOfCrossoverSongs, int nbrOfMarkovLookbacks,
            double songDuration) {
        log.warning("GA - Start constructor");
        this.inputSongs = inputSongs;
        this.crossover = crossover;
        this.mutator = mutator;
        this.rater = rater;
        this.populationSize = populationSize;
        this.nbrOfElitismSongs = nbrOfElitismSongs;
        this.nbrOfCrossoverSongs = nbrOfCrossoverSongs;
        this.songDuration = songDuration;
        nextPopulation = new ArrayList<Individual>();
        bestIndividual = new Individual(null, 0);
        currentIteration = 0;
        log.warning("GA - End constructor");
    }

    /**
     * Initiates variables and generates first generation.
     */
    private void prepareToGenerate() {
        
        log.warning("GA - Start generate");
        nextPopulation = new ArrayList<Individual>();
        elitismSongs = new ArrayList<Individual>();
        bestIndividual = new Individual(null, 0);
        currentIteration = 0;

        List<Song> firstGeneration;
        rater.initSubRaterWeights(inputSongs);
        
        switch (P.initiator) {
        case CROSSOVER:
            firstGeneration = generateFirstCrossoverGeneration();
            break;
        case MARKOV:
            firstGeneration = generateFirstMarkovGeneration();
            break;
        case RANDOM:
            firstGeneration = generateFirstRandomGeneration();
            break;
        default:
            firstGeneration = generateFirstCrossoverGeneration();
            break;
        }
        
        nextPopulation = ratePopulation(firstGeneration);
        selectElitismSongs(nextPopulation);
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
        
        prepareToGenerate();
        
        for (int generation = 0; generation < nbrOfGenerations; generation++) {
            generateCurrentGeneration();
        }
        /*
         * Return the best song.
         */
        //getBestIndividual().getSong().flatternPan();
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
        // TODO fix so that there is possible for progress bar here also.
        this.ratingThreshold = ratingThreshold;
        
        prepareToGenerate();
        
        while (getBestIndividual().getRating() < ratingThreshold) {
            generateCurrentGeneration();
        }
        /*
         * Return the best song.
         */
        getBestIndividual().getSong().flatternPan();
        return getBestIndividual();
    }

    /**
     * Get the best individual.
     * 
     * @return the best individual
     */
    public Individual getBestIndividual() {
        Individual currentBest = nextPopulation.get(0);
        for(int i=1; i < nextPopulation.size()-P.GA_NBR_OF_ELITISM_SONGS i++){
            
        }
    }

    /**
     * Return the current iteration. Resets each time generateUntilRating() and
     * generateGeneration() is called.
     * 
     * @return the current iteration
     */
    public int getCurrentIteration() {
        return currentIteration;
    }

    private List<Song> generateFirstCrossoverGeneration(){
        log.warning("GA - Generate first generation");
        List<Song> population = new ArrayList<Song>();
        for (int i = 0; i < populationSize; i++) {
            log.warning("Markov - Generate individual: " + i);
            crossover.setParents(inputSongs);
            population.add(crossover.crossIndividuals().get(0));
        }
        log.warning("GA - Generated first generation");
        return population;
    }
    
    private List<Song> generateFirstMarkovGeneration() {
        markovSong = new MarkovSong(2, inputSongs);
        log.warning("GA - Generate first generation");
        List<Song> population = new ArrayList<Song>();
        for (int i = 0; i < populationSize; i++) {
            log.warning("Markov - Generate individual: " + i);
            population.add(markovSong.generateNew(songDuration));
        }
        log.warning("GA - Generated first generation");
        return population;
    }
    
    private List<Song> generateFirstRandomGeneration() {
        log.warning("GA - Generate first generation");
        List<Song> population = new ArrayList<Song>();
        for (int i = 0; i < populationSize; i++) {
            log.warning("Markov - Generate individual: " + i);
            population.add(randomInitiator.generateMelody());
        }
        log.warning("GA - Generated first generation");
        return population;
    }

    private void generateCurrentGeneration() {
        log.warning("GA - Start generate generation: " + getCurrentIteration());
        currentIteration++;
        List<Individual> currentPopulation = nextPopulation;
        for (int i = 0; i < nbrOfElitismSongs; i++) {
            currentPopulation.add(new Individual(elitismSongs.get(i).getSong()
                    .copy(), elitismSongs.get(i).getRating()));
        }

        /*
         * Select individuals from the new population. Perform crossover on
         * selected individuals. Mutate each individual in list. Rate each
         * individual.
         */
        log.warning("GA - Selection for generation: " + getCurrentIteration());
        List<Song> unratedPopulation = new ArrayList<Song>();
        List<Song> tempList = new ArrayList<Song>();
        log.warning("Selection - Select for generation: " + getCurrentIteration());
        rwSelect = new RouletteWheelSelection(nextPopulation);
        for (int i = 0; i < populationSize; i++) {
            if (tempList.size() == 0) {
                for (int j = 0; j < nbrOfCrossoverSongs; j++) {
                    tempList.add(nextPopulation.get(rwSelect.select())
                            .getSong());
                }
                log.warning("Crossover - Generation: " + getCurrentIteration() + " Individual: " + i);
                crossover.setParents(tempList);
                tempList = crossover.crossIndividuals();
            }
            log.warning("Mutation - Generation: " + getCurrentIteration() + " Individual: " + i);
            unratedPopulation.add(tempList.remove(0));
            mutator.mutate(unratedPopulation.get(i));
        }
        log.warning("Rate - Generation: " + getCurrentIteration());
        nextPopulation = ratePopulation(unratedPopulation);
        log.warning("Elitism - Generation: " + getCurrentIteration());
        selectElitismSongs(nextPopulation);
        mutator.updateProbabilityMultiplier();
    }

    private List<Individual> ratePopulation(List<Song> population) {
        List<Individual> ratedPopulation = new ArrayList<Individual>();
        double individualRating = 0;
        for (int individual = 0; individual < populationSize; individual++) {
            population.get(individual).flattern();
            individualRating = rater.rate(population.get(individual));
            ratedPopulation.add(new Individual(population.get(individual), individualRating));
            if(individualRating > bestIndividual.getRating()){
                bestIndividual = ratedPopulation.get(ratedPopulation.size()-1);
            }
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
    
    public enum Initiator {
        MARKOV,
        RANDOM,
        CROSSOVER;
    }
}