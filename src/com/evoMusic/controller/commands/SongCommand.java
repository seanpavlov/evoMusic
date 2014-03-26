package com.evoMusic.controller.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import jm.music.data.Part;

import com.evoMusic.controller.AbstractCommand;
import com.evoMusic.database.MongoDatabase;
import com.evoMusic.model.Song;
import com.evoMusic.util.TrackTag;
import com.evoMusic.util.Translator;
import com.google.common.collect.Sets;

/**
 * Display available songs
 */
public class SongCommand extends AbstractCommand {

    private List<Song> songs = MongoDatabase.getInstance().retrieveSongs();
    private Map<String, AbstractCommand> songArgs = new HashMap<String, AbstractCommand>();
    private List<Song> selectedSongs;

    /**
     * Creates the song command
     * @param selectedSongs
     *              reference to list of songs selected to be used for generating
     *              individuals
     */
    public SongCommand(List<Song> selectedSongs) {
        this.selectedSongs = selectedSongs;
        setUpArgs();
    }

    private void setUpArgs() {
        
        /*
         * List available and selected songs
         */
        songArgs.put("list", new AbstractCommand() {
            @Override
            public boolean execute(String[] args) {
                System.out.println("Available songs:");
                for (int i = 0; i < songs.size(); i++) {
                    System.out.println(i + " -> " + songs.get(i).getTitle());
                }
                System.out.println("Selected songs:");
                for (Song song : selectedSongs) {
                    System.out.println(song.getTitle());;
                }
                return true;
            }
        });

        /*
         * Shows a song and the TrackTags
         */
        songArgs.put("tags", new AbstractCommand() {
            @Override
            public boolean execute(String[] args) {
                boolean success = false;
                if (args.length < 1) {
                    System.out.println("Insufficient arguments provided!");
                } else {
                    int songIndex = -1;
                    try {
                        for (String arg : args) {
                            songIndex = Integer.parseInt(arg);
                            if (songIndex >= 0 && songIndex < songs.size()) {
                                Song song = showSong(songIndex);
                                for (Part p : song.getScore().getPartArray()){
                                    System.out.println(song.getTrackTags(p));
                                }
                            } else {
                                System.out.println("Unable to select song "
                                        + songIndex);
                            }
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("ERROR: Not an integer: "
                                + e.getMessage());
                        return false;
                    }
                    success = true;
                }
                return success;
            }
        });
        
        /*
         * Select a song to be used for generating individuals
         */
        songArgs.put("select", new AbstractCommand() {
            @Override
            public boolean execute(String[] args) {
                boolean success = false;
                if (args.length < 1) {
                    System.out.println("Insufficient arguments provided! "
                            +"Use \"song list\" for available and selected songs");
                } else {
                    int songIndex = -1;
                    try {
                        for (String arg : args) {
                            songIndex = Integer.parseInt(arg);
                            if (songIndex >= 0 && songIndex < songs.size()) {
                                selectSong(songIndex);
                                System.out.println("Selected song "
                                        + songIndex);
                            } else {
                                System.out.println("Unable to select song "
                                        + songIndex);
                            }
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("ERROR: Not an integer: "
                                + e.getMessage());
                        return false;
                    }
                    success = true;
                }
                return success;
            }
        });
        
        /*
         * play a Song
         */
        songArgs.put("play", new AbstractCommand() {
            @Override
            public boolean execute(String[] args) {
                System.out.println(args[0]);
                Translator.INSTANCE.playSong(songs.get(Integer
                        .parseInt(args[0])));
                return true;
            }
        });

        /*
         * translate a midifile to a Song
         */
        songArgs.put("translate", new AbstractCommand() {
            @Override
            public boolean execute(String[] args) {
                if(args.length < 1) {
                    System.out.println("insufficient arguments provided");
                    return false;
                }
                Song song = null;
                song = Translator.INSTANCE.loadMidiToSong(args[0]);
                if (song == null) {
                    System.out.println("Error loading song at: "+args[0]);
                    return false;
                }
                System.out.println("Loaded song: "+ song.getTitle());
                tagTracks(song);
                storeSong(song);
                return true;
            }
            
            @Override
            public Set<String> getArgSet() {
                return Sets.newHashSet("path");
            }
            
        });
    }

    @Override
    public boolean execute(String[] args) {
        if (songArgs.keySet().contains(args[0])) {
            String[] songListArgs = args;
                songListArgs = Arrays.copyOfRange(args, 1, args.length);
            songArgs.get(args[0]).execute(songListArgs);
            return true;
        }
        return false;
    }

    @Override
    public Set<String> getArgSet() {
        return songArgs.keySet();
    }
    
    @Override
    public Map<String, AbstractCommand> getSubCommands() {
        return songArgs;
    }

    public void selectSong(int songIndex) {
        selectedSongs.add(songs.get(songIndex));
    }
    
    public Song showSong(int songIndex) {
        return songs.get(songIndex);
    }
    
    
    private void storeSong(Song song) {
        if (MongoDatabase.getInstance().insertSong(song)) {
            System.out.println("Song inserted into db, \"song list\" to view it");
            songs.add(song);
        } else {
            System.out.println("Failed to insert song into database");
        }
    }
    
    private void tagTracks(Song song) {
        System.out.println(song.getNbrOfTracks() + " tracks to tag.");
        System.out.println("Available track tags: ");
        TrackTag[] trackTags = TrackTag.values();
        for(int i = 0; i < trackTags.length; i++) {
            System.out.print(i+": "+trackTags[i].toString() + ", ");
        }
        System.out.println();
        System.out.println("--------------------------");
        System.out.println("Add multiple tags by separating them with space");
        Scanner sc = new Scanner(System.in);
        String[] trackIndexes = new String[0];
        
        for(int i = 0; i < song.getNbrOfTracks(); i++) {
            System.out.print("Please Tag track "+i+": ");
            trackIndexes = sc.nextLine().split(" ");
            for (String index : trackIndexes) {
                song.addTagToTrack(i, trackTags[Integer.parseInt(index)]);
            }
        }
    }

}