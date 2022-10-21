package clients;

import org.newsclub.net.unix.AFUNIXSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;
import os.lab1.compfunc.basic.IntOps;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.SecureRandom;
import java.util.Optional;

public class Client {
    private final File socketFile = new File(new File(System.getProperty("java.io.tmpdir")), "junixsocket-test.sock");
    private final String funcType;
    private Integer value;

    public Client(String funcType) {
        this.funcType = funcType;
        compute();
    }

    public void readMessage() {
        try (AFUNIXSocket socket = AFUNIXSocket.newInstance()) {
            socket.connect(AFUNIXSocketAddress.of(socketFile));
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            value = (Integer) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String result){
        try (AFUNIXSocket socket = AFUNIXSocket.newInstance()) {
            socket.connect(AFUNIXSocketAddress.of(socketFile));
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(result);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void compute() {
        readMessage();

        Optional<Integer> computedResult = Optional.empty();

        SecureRandom r = new SecureRandom();
        int randomF = r.nextInt(15);
        int randomG = r.nextInt(15);
        try {
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (randomF < 10) {
            if (funcType.equals("F"))
                computedResult = IntOps.trialF(value);
        }

        if (randomG < 10) {
            if (funcType.equals("G"))
                computedResult = IntOps.trialG(value);
        }
        else if (randomG < 12)
            while(true) {
                try {
                    Thread.sleep(8000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


        String result = computedResult.map(integer -> funcType + integer).orElseGet(() -> funcType + "hard fail");
        sendMessage(result);
    }
}
