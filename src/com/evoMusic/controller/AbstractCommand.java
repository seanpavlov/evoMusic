package com.evoMusic.controller;

public abstract class AbstractCommand {

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
     * @return String array containing available arguments for the command
     */
    public String[] getArguments() {
        return new String[0];
    }
}
