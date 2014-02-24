package controller;


public class InputController implements Runnable {

    private GAHandler gaHandler;

    public InputController(GAHandler gaHandler) {
        this.gaHandler = gaHandler;

    }

    @Override
    public void run() {
        // Temporary run method.
        while (true) {
            System.out.println("Running InputController");
        }

    }

}
