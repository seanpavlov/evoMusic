package com.evoMusic.controller;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.evoMusic.controller.commands.AbstractCommand;
import com.evoMusic.controller.commands.GenerateCommand;
import com.evoMusic.controller.commands.HelpCommand;
import com.evoMusic.controller.commands.SongCommand;
import com.evoMusic.model.Song;

/**
 * Wrapper class for our commands. Single since there's no reason for having
 * multiple instances of it.
 */
public class Commands {

    private Map<String, AbstractCommand> commands = new HashMap<String, AbstractCommand>();
    private List<Song> selectedSongs = new LinkedList<Song>();

    public Commands(Scanner sc) {
        commands.put("?", new HelpCommand(commands));
        commands.put("song", new SongCommand(selectedSongs, sc));
        commands.put("generate", new GenerateCommand(selectedSongs));
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
    public Map<String, AbstractCommand> getCommandMap() {
        return commands;
    }
    
}
