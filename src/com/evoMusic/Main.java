package com.evoMusic;

import com.evoMusic.controller.InputController;

public class Main {  
    
    public final static String VERSION = "0.1";

    public static void main(String[] args) {
        InputController.INSTANCE.run(args);
    }
}
