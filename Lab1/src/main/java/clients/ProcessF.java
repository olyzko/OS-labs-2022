package clients;

import sun.misc.Signal;

public class ProcessF {
    public static void main(String[] args) {
        initSignalHandler();
        Client f = new Client("F");
    }

    private static void initSignalHandler(){
        Signal.handle(new Signal("INT"), signal -> initSignalHandler());
    }
}
