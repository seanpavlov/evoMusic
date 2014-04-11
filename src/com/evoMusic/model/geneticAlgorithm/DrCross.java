package com.evoMusic.model.geneticAlgorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jm.music.data.Part;
import jm.music.data.Score;

import com.evoMusic.model.Song;
import com.evoMusic.model.Track;
import com.evoMusic.util.TrackTag;

public class DrCross {
    private double segLen;
    private double childLen;
    private List<Song> parents;
    private List<Song> children;

    // every child's segment JVM instantiates every element to false
    // x is track
    // y is segment
    // z children
    boolean[][][] occupiedSegmentSpots;
  
    public DrCross(double segLen) {
        this.segLen = segLen;
    }

    public DrCross(double segLen, List<Song> parents) {
        this.segLen = segLen;
        setParents(parents);

    }

    public void setParents(List<Song> parents) {
        this.parents = parents;
        this.childLen = findChildEndTime();
    }

    public List<Song> crossIndividuals() {
        this.children = initChildren();
        occupiedSegmentSpots = new boolean[children.get(0).getNbrOfTracks()]
                [(int) (childLen / segLen)+1]
                        [children.size()];
        this.children = populateChildren();
        return children;
    }

    private List<Song> populateChildren() {
        Song child = children.get(0);
        List<Track> parentTaggedTracks;
        for (Song parent : parents) {
            for (TrackTag tag : TrackTag.values()) {
                if (tag != TrackTag.NONE) {
                    // merge parent tracks until we have the same amount of
                    // tracks
                    while ((parentTaggedTracks = parent.getTaggedTracks(tag))
                            .size() > child.getTaggedTracks(tag).size()) {
                        if (parentTaggedTracks.size() == 1) {
                            parent.removeTrack(parentTaggedTracks.get(0));
                        } else {
                            parentTaggedTracks.get(0).merge(parentTaggedTracks.get(1), 0);
                            parent.removeTrack(parentTaggedTracks.get(1));
                        }
                    }
                }
            }
            distributeParentInChildren(parent);
        }

        return children;
    }

    private void distributeParentInChildren(Song parent) {

        for (int i = 0; i < parent.getNbrOfTracks(); i++) {
            List<Track> childrenTracks = new ArrayList<Track>();
            for (int j = 0; j < children.size(); j++) {
                childrenTracks.add(children.get(j).getTrack(i));
            }
            distributeParentTrackInChildrenTracks(parent.getTrack(i), childrenTracks, occupiedSegmentSpots[i]);
        }
    }


    /**
     * Distributes a track from a parent into the given childen's tracks
     * @param parentTrack
     * @param childrenTracks
     * @param trackOccupiedSegmentSpots
     */
    private void distributeParentTrackInChildrenTracks(Track parentTrack,
            List<Track> childrenTracks, boolean[][] trackOccupiedSegmentSpots) {

        Track segment = null;
        boolean[] occupiedChildren = null;
        List<Integer> childrenIndeces = null;
        for (int i = 0; i < childLen; i += segLen) {
            childrenIndeces = new ArrayList<Integer>();
            segment = parentTrack.getSegment(i, segLen);
            occupiedChildren = trackOccupiedSegmentSpots[(int) (i / segLen)];

            for (int j = 0; j < occupiedChildren.length; j++) {
                if (!occupiedChildren[j]) {
                    childrenIndeces.add(j);
                }
            }
            Collections.shuffle(childrenIndeces);
            int childIdx = childrenIndeces.get(0);
            childrenTracks.get(childIdx).merge(segment, i);
            occupiedChildren[childIdx] = true;

        }
    }

    /**
     * Find child's final length let it be the shortest parent's length
     * 
     * @return rhythm value length
     */
    private double findChildEndTime() {
        double childEndTime = Double.MAX_VALUE;
        for (Song parent : parents) {
            double parentEndTime = parent.getScore().getEndTime();
            if (parentEndTime < childEndTime) {
                childEndTime = parentEndTime;
            }
        }
        return childEndTime;
    }

    /**
     * Creates as many children as there are parents, every child has the same
     * amount and ordering of tracks with every TrackTag.
     * 
     * @param parents
     * @return children
     */
    private List<Song> initChildren() {
        List<Song> children = new ArrayList<Song>();

        // Create children
        for (int i = 0; i < parents.size(); i++) {
            Song song = new Song(new Score());
            children.add(song);
        }

        // Create and tag tracks
        int parentTracksWithTag = 0;
        int childrenTracksWithTag = 0;
        for (TrackTag tag : TrackTag.values()) {
            if (tag != TrackTag.NONE) {
                childrenTracksWithTag = Integer.MAX_VALUE;
                for (Song parent : parents) {
                    parentTracksWithTag = parent.getTaggedTracks(tag).size();

                    // The parent with the least number of tracks with the tag
                    // sets the number of tracks with the that should be
                    // created for each child
                    if (parentTracksWithTag < childrenTracksWithTag) {
                        childrenTracksWithTag = parentTracksWithTag;
                    }

                    // If there's a parent that has a track with the tag
                    // but some other parents doesn't have it, we should still
                    // include one track with this tag.
                    if (childrenTracksWithTag == 0 && parentTracksWithTag > 0) {
                        childrenTracksWithTag = 1;
                        break;
                    }
                }
            }

            // Add the tracks to the children
            for (Song child : children) {
                for (int i = 0; i < childrenTracksWithTag; i++) {
                    Track track = new Track(new Part());
                    track.addTag(tag);
                    child.addTrack(track);
                }
            }
        }

        return children;
    }
}
