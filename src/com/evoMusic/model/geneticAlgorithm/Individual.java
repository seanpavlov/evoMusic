package com.evoMusic.model.geneticAlgorithm;

import com.evoMusic.model.Song;

public class Individual implements Comparable<Individual> {

    private Song song;
    private double rating;

    /**
     * Creates an individual with a song and a rating.
     * 
     * @param song  The given song of this individual.
     * @param rating    The given rating of this individual.
     */
    public Individual(Song song, double rating) {
        this.song = song;
        this.rating = rating;
    }

    /**
     * Gets the rating for this individual.
     * 
     * @return  The rating for this individual.
     */
    public double getRating() {
        return rating;
    }

    /**
     * Gets the song for this individual.
     * 
     * @return  The song for this individual.
     */
    public Song getSong() {
        return song;
    }

    @Override
    public int compareTo(Individual o) {
        if (o == null) {
            throw new NullPointerException();
        }
        if (!(o instanceof Individual)) {
            throw new ClassCastException();
        }
        Individual other = (Individual) o;
        double comparison = this.getRating() - other.getRating();
        if (comparison < 0) {
            return -1;
        } else if (comparison > 0) {
            return 1;
        } else {
            return 0;
        }
    }
}
