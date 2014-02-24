package main;

import controller.GAHandler;
import controller.InputController;

public class Main {

    public static void main(String[] args) {
        
        GAHandler gaHandler = new GAHandler();
        Thread gaHandlerThread = new Thread(gaHandler);
        Thread inputControllerThread = new Thread(new InputController(gaHandler));
        gaHandlerThread.start();
        inputControllerThread.start();
    }
}
