package com.evoMusic.model.geneticAlgorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import jm.music.data.Part;
import jm.music.data.Score;

import com.evoMusic.model.Song;
import com.evoMusic.model.Track;
import com.evoMusic.util.TrackTag;

/**
 * DrCross crosses children by setting every child's length to the shortest
 * parents length, giving all the children the same amount of tracks and tags
 * as the least number of tracks and tags found in the parents.
 * 
 * If a parent has more tracks with a tag than the other(s). These tracks will
 * be merged until the parents have the same number of tracks. 
 * 
 * If a parent doesn't have a track with a certain tag, neither of the children
 * is going to have it, even if other parents may have tracks with that tag.
 * 
 */
public class DrCross {
    private double segLen;
    private double childLen;
    private List<Song> parents;
    private List<Song> children;

    // every child's segment JVM instantiates every element to false
    // x is track
    // y is segment
    // z children
    private boolean[][][] occupiedSegmentSpots;

    /**
     * Creates a new instance of the crossover object, before breeding children
     * setParents must be issued. 
     * @param segLen lengths of the segments in the children given in 
     * rhythm values. 
     */
    public DrCross(double segLen) {
        this.segLen = segLen;
    }

    /**
     * Creates a new instance of the crossover object.
     * @param segLen lengths of the segments in the children given in 
     * rhythm values. 
     */
    public DrCross(double segLen, List<Song> parents) {
        this(segLen);
        setParents(parents);

    }

    /**
     * Set the parents used for breeding children. 
     * @param parents
     */
    public void setParents(List<Song> parents) {
        this.parents = new ArrayList<Song>();
        for (Song parent : parents) {
            this.parents.add(parent.copy());
        }
        for (Song parent : this.parents) {
            for (Iterator<Track> it = parent.getTracks().iterator(); 
                    it.hasNext(); ) {
                Track t = it.next();
                if (t.getTag() == TrackTag.NONE) {
                    it.remove();
                }
            }
        }
        // new parents, find the new length for children
        this.childLen = findChildEndTime();
    }

    /**
     * Crosses the the instance's parents into the same amount of children
     * The parents are set on instantiation or using the setParents
     * @return the children created from the parents
     */
    public List<Song> crossIndividuals() {
        this.children = initChildren();
        occupiedSegmentSpots = new boolean[children.get(0).getNbrOfTracks()][(int) (childLen / segLen) + 1][children
                .size()];
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
                            parentTaggedTracks.get(0).merge(
                                    parentTaggedTracks.get(1), 0);
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
            distributeParentTrackInChildrenTracks(parent.getTrack(i),
                    childrenTracks, occupiedSegmentSpots[i]);
        }
    }

    /**
     * Distributes a track from a parent into the given childen's tracks
     * 
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
     * amount and ordering of tracks with every TrackTag. For consistency 
     * reasons, the least number of tracks with the tag found is created for 
     * the children. If a parent that does not have a track with a certain tag, 
     * no children will have this tracks with this tag
     * 
     * @param parents to search through
     * @return children to be created
     */
    private List<Song> initChildren() {
        List<Song> children = new ArrayList<Song>();

        // Create children
        for (int i = 0; i < parents.size(); i++) {
            Song song = new Song(new Score());
            children.add(song);
        }

        // Create and tag tracks
        int toAdd = 0;
        int withTag = 0;
        for (TrackTag tag : TrackTag.values()) {
            if (tag != TrackTag.NONE) {
                toAdd = Integer.MAX_VALUE;
                withTag = 0;
                
                // Find how many tracks of the tag that should be added
                for (Song parent : parents) {
                    withTag = parent.getTaggedTracks(tag).size();
                    toAdd = Math.min(toAdd, withTag);
                }
                
                // Add the tracks to the children
                for (Song child : children) {
                    for (int i = 0; i < toAdd; i++) {
                        Track track = new Track(new Part());
                        track.setTag(tag);
                        child.addTrack(track);
                    }
                }
            }
        }

        return children;
    }
}
