package com.evoMusic.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A command that can be run in the console. It may be a sub command to a 
 * higher level command. 
 * 
 * If a command has a set of arguments, these arguments may in turn have arguments
 * and consider themselves as commands.
 * 
 * For instance, a "song" command may have the argument "select". "song select" may in turn
 * take arguments such as what song to select. song select is therefore a sub command
 * where the song to select is an argument. 
 *
 */
public abstract class AbstractCommand {

    public final static Map<String, AbstractCommand> LAST_LEVEL 
                    = new HashMap<String, AbstractCommand>();
    /**
     * Executes a command
     * 
     * @param args
     *            arguments for the command
     * @return true if the command appeared to run successfully, otherwise false
     */
    public abstract boolean execute(String[] args);

    /**
     * 
     * @return String set containing available arguments for the command
     */
    public Set<String> getArgSet() {
        return LAST_LEVEL.keySet();
    }
    
    public String help() {
        return "No help this command, sryz";
    }
    
    /**
     * 
     * @return Map containing sub commands and keys.
     */
    public Map<String, AbstractCommand> getSubCommands() {
        return LAST_LEVEL;
    }
}
