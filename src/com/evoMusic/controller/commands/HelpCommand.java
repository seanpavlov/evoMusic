package com.evoMusic.controller.commands;

import java.util.HashMap;
import java.util.Map;

import com.evoMusic.controller.AbstractCommand;

/**
 * Displays help text
 */
public class HelpCommand extends AbstractCommand {

    private Map<String, AbstractCommand> commands = new HashMap<String, AbstractCommand>();

    /**
     * Creates the help command
     * 
     * @param commands
     *            Map holding the available commands. used to display available
     *            commands and arguments.
     */
    public HelpCommand(Map<String, AbstractCommand> commands) {
        this.commands = commands;
    }

    @Override
    public boolean execute(String[] args) {
        System.out.println("Available commands: ");
        for (String command : commands.keySet()) {
            System.out.print(command + " ");
            String[] commandArgs = commands.get(command).getArguments();
            if (commandArgs.length != 0) {
                System.out.print("[");
            }
            for (int i = 0; i < commandArgs.length; i++) {
                System.out.print(commandArgs[i]
                        + (i != commandArgs.length - 1 ? "|" : ""));
            }
            if (commandArgs.length != 0) {
                System.out.print("]");
            }
            System.out.println();
        }
        return true;
    }
}