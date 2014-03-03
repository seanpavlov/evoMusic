package com.evoMusic.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.evoMusic.database.MongoDatabase;
import com.evoMusic.model.Song;
import com.evoMusic.model.Translator;
import com.evoMusic.model.geneticAlgorithm.Crossover;
import com.evoMusic.model.geneticAlgorithm.GeneticAlgorithm;
import com.evoMusic.model.geneticAlgorithm.mutation.ISubMutator;
import com.evoMusic.model.geneticAlgorithm.mutation.Mutator;
import com.evoMusic.model.geneticAlgorithm.mutation.OctaveMutator;
import com.evoMusic.model.geneticAlgorithm.mutation.ReverseMutator;
import com.evoMusic.model.geneticAlgorithm.mutation.ScaleOfFifthMutator;
import com.evoMusic.model.geneticAlgorithm.mutation.SimplifyMutator;
import com.evoMusic.model.geneticAlgorithm.rating.Rater;
import com.evoMusic.model.geneticAlgorithm.rating.SubRater;
import com.evoMusic.model.geneticAlgorithm.rating.UserRater;

/**
 * Wrapper class for our commands. Single since there's no reason for having
 * multiple instances of it.
 */
public class Commands {

    private Map<String, ICommand> commands = new HashMap<String, ICommand>();
    private static Commands instance;
    private List<Song> selectedSongs = new LinkedList<Song>();

    private Commands() {
        commands.put("?", new HelpCommand());
        commands.put("song", new SongCommand());
        commands.put("translate", new TranslateCommand());
        commands.put("generate", new GenerateCommand());
    }

    public static Commands getInstance() {
        if (instance == null) {
            instance = new Commands();
        }
        return instance;
    }

    /**
     * Gets the user selected songs for initial parents
     * 
     * @return the list of songs selected for crossover-mutation
     */
    public List<Song> getSelectedSongs() {
        return selectedSongs;
    }

    /**
     * @return the command map, populated with all available commands
     */
    public Map<String, ICommand> getCommandMap() {
        return commands;
    }

    /**
     * Displays help text
     */
    private class HelpCommand implements ICommand {

        @Override
        public void execute(String[] args) {
            System.out.println("Available commands: ");
            for (String command : commands.keySet()) {
                System.out.println(command);
            }
        }
    }
    
    private class TranslateCommand implements ICommand {
        
        @Override
        public void execute(String[] args) {
            System.out.println("Not implemented");
        }
    }
    
    private class GenerateCommand implements ICommand {

        @Override
        public void execute(String[] args) {
            System.out.println(selectedSongs);
            if(selectedSongs.size() >= 2) {
                List<ISubMutator> allMut = new ArrayList<ISubMutator>();
                allMut.add(new OctaveMutator(0.1, 1));
                allMut.add(new ReverseMutator(0.1, 4, 4, true));
                allMut.add(new ScaleOfFifthMutator(0.1, 3));
                allMut.add(new SimplifyMutator(0.1, 4, 0.1));
                List<SubRater> subRaters = new LinkedList<SubRater>();
                subRaters.add(new UserRater(1));
                GeneticAlgorithm ga = new GeneticAlgorithm(selectedSongs, new Mutator(allMut, 1), new Crossover(20), new Rater(subRaters));
                ga.setMinimumIterations(10);
                ga.iterate();
                Translator.INSTANCE.playSong(ga.getBest());
            }
        }
    }


    /**
     * Display available songs
     */
    private class SongCommand implements ICommand {

        private List<Song> songs = MongoDatabase.getInstance().retrieveSongs();
        private Map<String, ICommand> songArgs = new HashMap<String, ICommand>();

        public SongCommand() {
            songArgs.put("list", new ICommand() {
                @Override
                public void execute(String[] args) {
                    for (int i = 0; i < songs.size(); i++) {
                        System.out
                                .println(i + " -> " + songs.get(i).getTitle());
                    }
                }
            });

            songArgs.put("select", new ICommand() {

                @Override
                public void execute(String[] args) {
                    if (args.length >= 2) {
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
                            return;
                        }
                    }
                }
            });
            songArgs.put("play", new ICommand() {
                @Override
                public void execute(String[] args) {
                    System.out.println(args[0]);
                    Translator.INSTANCE.playSong(songs.get(Integer.parseInt(args[0])));
                }
            });
            
            songArgs.put("translate", new ICommand() {
                @Override
                public void execute(String[] args) {
                    System.out.println("Not implemented");
                }
            });
        }

        @Override
        public void execute(String[] args) {
            if (songArgs.keySet().contains(args[0])) {
                String[] songListArgs = args;
                if (args.length >= 2) {
                    songListArgs = Arrays.copyOfRange(args, 1, args.length);
                }
                songArgs.get(args[0]).execute(songListArgs);
            } else {
                commands.get("?").execute(args);
            }
        }

        public void selectSong(int songIndex) {
            selectedSongs.add(songs.get(songIndex));
        }
    }
}
