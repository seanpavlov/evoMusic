package com.evoMusic.controller;

import java.util.Map;
import java.util.Scanner;

import com.evoMusic.Main;

public class InputController {

    private boolean isRunning = true;
    private String command;
    private String[] args;
    private String prompt = "> ";
    private Scanner in;
    
    public InputController(String[] runArgs) {
        this.in = new Scanner(System.in);
        setPrompt("evomusic> ");
        run();
    }

    public void run() {
        System.out.println("--- EvoMusic shell version "+Main.VERSION+" --- ");
        System.out.println("Enter '?' for help.\n");
        final Map<String, ICommand> commandMap = Commands.getInstance().getCommandMap();
        while (isRunning) {
            System.out.print(prompt);
            command = in.next();
            args = in.nextLine().trim().split(" ");
            if(commandMap.containsKey(command)) {
                commandMap.get(command).execute(args);
            } else {
                System.out.println("evomusic: command not found: " + command);
            }
        }
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

}
