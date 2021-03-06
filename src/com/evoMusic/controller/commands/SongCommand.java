package com.evoMusic.controller.commands;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.bson.types.ObjectId;

import com.evoMusic.database.MongoDatabase;
import com.evoMusic.model.Song;
import com.evoMusic.model.Translator;
import com.evoMusic.util.TrackTag;
import com.google.common.collect.Sets;

/**
 * Display available songs
 */
public class SongCommand extends AbstractCommand {

    private List<Song> songs;
    private Map<String, AbstractCommand> songArgs = new HashMap<String, AbstractCommand>();
    private List<Song> selectedSongs;
    private Scanner sc;

    /**
     * Creates the song command
     * @param selectedSongs
     *              reference to list of songs selected to be used for generating
     *              individuals
     */
    public SongCommand(List<Song> selectedSongs, Scanner sc) {
        this.selectedSongs = selectedSongs;
        this.sc = sc;
        this.songs = MongoDatabase.getInstance().retrieveSongs();
        Map<ObjectId, String> brokenPaths = MongoDatabase.getInstance().getBrokenPaths();
        for (ObjectId id : brokenPaths.keySet()) {
            fixSongNotFound(id, brokenPaths.get(id));
        }
        setUpArgs();
    }
    
    private void fixSongNotFound(ObjectId songRef, String path) {
        String newPath = "";
        String dbPath = path;
        System.out.println("I could not find this song at "
            + dbPath);
        
        do {
            System.out.print("Fix path (blank = remove db record): ");
            newPath = sc.nextLine();
        } while (!newPath.equals("") && !new File(newPath).exists());
        
        if ("".equals(newPath)) {
            MongoDatabase.getInstance().removeSong(songRef);
            System.out.println("Song removed!");
            return;
        }
        try {
            FileUtils.copyFile(new File(newPath), new File(dbPath));
        } catch (IOException e) {
            System.out.println("ERR: Cant create file "+dbPath);
            return;
        }
        songs.add(MongoDatabase.getInstance().getSong(songRef));
    }
    
    @Override
    public String help() {
        StringBuilder sb = new StringBuilder();
        for (String arg : songArgs.keySet()) {
            sb.append("song " + arg + "\t- " + songArgs.get(arg).help() + "\n");
        }
        return sb.toString();
    };

    private void setUpArgs() {
        
        /*
         * List available and selected songs
         */
        songArgs.put("list", new AbstractCommand() {
            @Override
            public boolean execute(String[] args) {
                System.out.println("Available songs(BPM):");
                for (int i = 0; i < songs.size(); i++) {
                    System.out.println(i + " -> " + songs.get(i).getTitle() + "(" + songs.get(i).getTempo() + ")");
                }
                System.out.println("Selected songs(BPM):");
                for (Song song : selectedSongs) {
                    System.out.println(song.getTitle() + "(" + song.getTempo() + ")");
                }
                return true;
            }
            
            @Override
            public String help() {
                return "List available and currently selected songs if any";
            }
        });
        
        /*
         * Delete a song given a number
         */
        songArgs.put("delete", new AbstractCommand() {
            
            @Override
            public boolean execute(String[] args) {
                int songIndex = -1;
                try {
                    songIndex = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    return false;
                }
                Song song = songs.remove(songIndex);
                System.out.println("Removing song '"+song.getTitle()+"'...");
                return MongoDatabase.getInstance().removeSong(song);
            }
            
            @Override
            public String help() {
                return "Deletes a song given its list index given by 'song list'";
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
                                for (int i = 0; i < song.getNbrOfTracks(); i++){
                                    System.out.println(song.getTrack(i).getTag());
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
                Translator.INSTANCE.play(songs.get(Integer
                        .parseInt(args[0])));
                return true;
            }
        });
        
        /*
         * Deselect all songs
         */
        songArgs.put("deselect", new AbstractCommand() {
            @Override
            public boolean execute(String[] args) {
                selectedSongs.clear();
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
        if (args.length > 0 && songArgs.keySet().contains(args[0])) {
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
        String trackIndex;
       
        for(int i = 0; i < song.getNbrOfTracks(); i++) {
            System.out.print("Please Tag track "+i+": ");
            Translator.INSTANCE.show(song.getTrack(i).getPart());
            trackIndex = sc.nextLine();
            song.addTagToTrack(i, trackTags[Integer.parseInt(trackIndex)]);
        }
    }
}