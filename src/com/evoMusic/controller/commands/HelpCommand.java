package com.evoMusic.controller.commands;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;

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

    /**
     * Goes through every command and for every command it goes through its
     * sub commands recursively until no sub commands are defined, then print 
     * arguments if any.
     * 
     * @param commands
     *            commands to go through
     * @param separator
     *            separator between commands/args
     */
    private void printCommands(Map<String, AbstractCommand> commands,
            String separator) {
        for (Iterator<String> it = commands.keySet().iterator(); it.hasNext();) {
            String commandKey = it.next();
            System.out.print(commandKey);
            // Get sub commands
            AbstractCommand command = commands.get(commandKey);
            // If there are not sub commands, print the arguments
            if (command.getSubCommands() == AbstractCommand.LAST_LEVEL) {
                Set<String> argSet = command.getArgSet();
                if (!argSet.isEmpty()) {
                    System.out.print(" <");
                    for (Iterator<String> itArgs = argSet.iterator(); itArgs
                            .hasNext();) {
                        System.out.print(itArgs.next()
                                + (itArgs.hasNext() ? "|" : ""));
                    }
                    System.out.print(">");
                }
            } else {
                System.out.print(" [");
                printCommands(command.getSubCommands(), "|");
                System.out.print("]");
            }
            if (it.hasNext()) {
                System.out.print(separator);
            }
        }
    }

    @Override
    public boolean execute(String[] args) {
        if (args.length == 0) {
            System.out.println("Available commands: ");
            printCommands(commands, "\n");
            System.out.println();
        } else {
            printCommands(ImmutableMap.<String, AbstractCommand>builder().put(args[0], commands.get(args[0])).build(), "\n");
            System.out.println();
            System.out.println(commands.get(args[0]).help());
        }
        return true;
    }

}