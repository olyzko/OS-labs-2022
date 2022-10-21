package clients;

import sun.misc.Signal;

public class ProcessG {
    public static void main(String[] args) {
        initSignalHandler();
        Client g = new Client("G");
    }

    private static void initSignalHandler(){
        Signal.handle(new Signal("INT"), signal -> initSignalHandler());
    }
}
