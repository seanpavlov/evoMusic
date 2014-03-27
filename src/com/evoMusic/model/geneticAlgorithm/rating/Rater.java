package com.evoMusic.model.geneticAlgorithm.rating;

import java.util.ArrayList;
import java.util.List;

import com.evoMusic.model.Song;

public class Rater {
    List<SubRater> subraters = new ArrayList<SubRater>();

    /**
     * Creates a new Rater with no subraters.
     */
    public Rater() {
    }

    /**
     * Creates a new Rater with a given list of ISubRater.
     * 
     * @param subraters
     *            The given list of ISubRater.
     */
    public Rater(List<SubRater> subraters) {
        this.addSubRater(subraters);
    }

    /**
     * rates a song with the help of all subraters in the list
     * 
     * @param song
     *            to be rater
     * @return combined rating of the song
     */
    public double rate(Song song) {

        double rating = 0.0;
        double sumOfweights = 0.0;
        double subRating = 0.0;
        for (SubRater subRater : subraters) {
            sumOfweights += subRater.getWeight();
            if (subRater.shouldRate()) {
                subRating = subRater.rate(song);
                if (subRating < 0 || subRating > 1) {
                    System.err.println("WARNING: rater: '"
                            + subRater.getClass().getSimpleName() + "' returned an"
                            + "invalid rating of: " + subRating);
                }
                rating += subRating * subRater.getWeight();
            }
        }
        rating = rating / sumOfweights;
        return rating;
    }

    /**
     * Adds a subrater to this rater's list of raters.
     * 
     * @param subRater
     *            The ISubRater to be added.
     */
    public void addSubRater(SubRater subRater) {
        this.subraters.add(subRater);
    }

    /**
     * Adds a list of subraters to this rater's list of subraters.
     * 
     * @param subraters
     *            The list of ISubRater to be added.
     */
    public void addSubRater(List<SubRater> subraters) {
        this.subraters.addAll(subraters);
    }

    /**
     * Takes a list of songs and initiate the raters weights depending on the
     * rating of each song.
     * 
     * @param songs
     *            songs to initiate the rating
     */
    public void initSubRaterWeights(List<Song> songs) {
        double rating;
        for (SubRater subRater : subraters) {
            rating = 0;
            for (Song s : songs) {
                rating += subRater.rate(s);
            }
            rating = rating / songs.size();
            subRater.setWeight(rating);
        }
    }
}