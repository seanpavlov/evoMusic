package com.evoMusic;

import java.util.logging.Level;

import com.evoMusic.controller.InputController;
import com.evoMusic.parameters.P;
import com.evoMusic.util.Helpers;

public class Main {  
    
    public final static String VERSION = "0.1";

    public static void main(String[] args) {
        Helpers.LOGGER.setLevel(P.IN_DEBUG_MODE ? Level.ALL : Level.SEVERE);
        InputController.INSTANCE.run(args);
    }
}
