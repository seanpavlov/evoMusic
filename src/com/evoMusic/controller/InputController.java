package com.evoMusic.controller;

import java.util.Map;
import java.util.Scanner;

import com.evoMusic.Main;
import com.evoMusic.controller.commands.AbstractCommand;

public enum InputController {
    
    INSTANCE;
    
    private boolean isRunning = true;
    private String command;
    private String[] args;
    private String prompt = "> ";
    private Commands commands;
    private final Scanner SCANNER = new Scanner(System.in);

    private InputController() {
        commands = new Commands(SCANNER);
        setPrompt("evomusic> ");
    }

    public void run(String[] runArgs) {
        System.out.println("--- EvoMusic shell version " + Main.VERSION
                + " --- ");
        System.out.println("Enter '?' for help.\n");
        final Map<String, AbstractCommand> commandMap = commands.getCommandMap();
        System.out.print(prompt);
        String argStr = "";
        while (isRunning && SCANNER.hasNext()) {
            command = SCANNER.next();
            argStr = SCANNER.nextLine().trim();
            args = argStr.isEmpty() ? new String[0] : argStr.split(" ");
            if (commandMap.containsKey(command)) {
                if (!commandMap.get(command).execute(args)) {
                    System.out.println("evomusic: Something went wrong when "
                        + "executing command '"
                        + command + "' with arguments: '" + argStr + "'.");
                }
            } else {
                System.out.println("evomusic: command not found: " + command);
            }
            System.out.print(prompt);
        }
        SCANNER.close();
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public Scanner getScanner() {
        return SCANNER;
    }

}
