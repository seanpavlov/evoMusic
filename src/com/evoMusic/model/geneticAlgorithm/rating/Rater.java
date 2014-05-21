package com.evoMusic.model.geneticAlgorithm.rating;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.evoMusic.model.Song;
import com.evoMusic.parameters.P;
import com.evoMusic.util.Helpers;

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
     *            to be rated
     * @return combined rating of the song
     */
    public double rate(Song song) {
        double totalDelta = 0;
        double totalInfluence = 0;
        double currentRating;
        for (SubRater subRater : subraters) {
            currentRating = subRater.rate(song);
            // System.out.println(subRater + " : " + currentRating);
            if (currentRating > 1.0 || currentRating < 0.0) {
                throw new NumberFormatException("rater: '"
                        + subRater.getClass().getSimpleName() + "' returned an"
                        + " invalid rating of: " + currentRating);
            } else {
                totalDelta += Math.abs(subRater.getTargetRating()
                        - currentRating)
                        * subRater.getInfluence();
                totalInfluence += subRater.getInfluence();
            }
        }
        return 1.0 - (totalDelta / totalInfluence);
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
        double maxRating;
        double minRating;

        double totalRating;
        double currentRating;
        SubRater currentRater;
        Iterator<SubRater> iter = subraters.iterator();
        while (iter.hasNext()) {
            currentRater = iter.next();
            totalRating = 0;
            maxRating = 0.0;
            minRating = 1.0;
            for (Song s : songs) {
                currentRating = currentRater.rate(s);
                if (currentRating < 0.0 || currentRating > 1.0) {
                    throw new NumberFormatException(
                            "Subrating must be between 0 and 1");
                }
                totalRating += currentRating;
                if (P.APPLY_INFLUENCE) {
                    if (currentRating < minRating) {
                        minRating = currentRating;
                    }
                    if (currentRating > maxRating) {
                        maxRating = currentRating;
                    }
                }
            }
            totalRating = totalRating / songs.size();

            // TODO Might want to disable this...
            if (totalRating == 0) {
                iter.remove();
            } else {
                currentRater.setTargetRating(totalRating);
                if (P.APPLY_INFLUENCE) {
                    currentRater.setInfluence(1 - (Math.min(maxRating
                            - totalRating, totalRating - minRating) * 2));
                    Helpers.LOGGER.info("max: " + maxRating + ", min: "
                            + minRating + "\n" + "influence: "
                            + currentRater.getInfluence());
                }
            }

        }
    }
}