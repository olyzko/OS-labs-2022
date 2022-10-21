package manager;

import org.newsclub.net.unix.AFUNIXServerSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import sun.misc.Signal;

public class Manager {
    private final Integer inputValue;
    private final AFUNIXServerSocket server;
    private final Executor executor;
    private final ProcessBuilder processBuilderF;
    private final ProcessBuilder processBuilderG;
    private Process processF;
    private Process processG;
    private Boolean failF;
    private Boolean failG;
    public Boolean cancel;
    private final ArrayList<Integer> resultsArr;

    Manager(Integer inputValue) throws IOException {
        System.out.println("Manager has been launched");
        this.inputValue = inputValue;
        initSignalHandler();

        File socketFile = new File(new File(System.getProperty("java.io.tmpdir")), "junixsocket-test.sock");
        server = AFUNIXServerSocket.newInstance();
        server.bind(AFUNIXSocketAddress.of(socketFile));

        processBuilderF = new ProcessBuilder("java", "-jar", "./out/artifacts/Lab1F/Lab1.jar");
        processBuilderG = new ProcessBuilder("java", "-jar", "./out/artifacts/Lab1G/Lab1.jar");
        executor = Executors.newFixedThreadPool(2);

        failF = false;
        failG = false;
        cancel = false;
        resultsArr = new ArrayList<>();

        run();
    }

    public int getStatusF(){
        return failF ? 1 : processF.isAlive() ? -1 : 0;
    }
    public int getStatusG(){
        return failG ? 1 : processG.isAlive() ? -1 : 0;
    }

    public int getResult(){
        return resultsArr.get(0) * resultsArr.get(1);
    }

    private void initSignalHandler(){
        Signal.handle(new Signal("INT"), signal -> cancellingAdvanced());
    }

    public void run(){
        Runnable runnableF = () -> {
            try {
                processF = processBuilderF.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        Runnable runnableG = () -> {
            try {
                processG = processBuilderG.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        executor.execute(runnableF);
        executor.execute(runnableG);

        sendValues();
        checkProcesses();
    }

    private void sendValues(){
        for(int i=0; i<2; i++) {
            try (Socket socket = server.accept()) {
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.writeObject(inputValue);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkProcesses() {
        AtomicBoolean fAlive = new AtomicBoolean(true);
        AtomicBoolean gAlive = new AtomicBoolean(true);
        int counter = 0;
        long startTime = System.currentTimeMillis();

        while (true){
            if(!processF.isAlive() && fAlive.get()){
                readParsedData();
                counter++;
                fAlive.set(false);
                if(counter == 2)
                    break;
            } else if(cancel)
                break;

            if(!processG.isAlive() && gAlive.get()){
                readParsedData();
                counter++;
                gAlive.set(false);
                if(counter == 2)
                    break;
            } else if(cancel)
                break;

            if(System.currentTimeMillis() - startTime > 20000) {
                System.out.println("Function G hangs, computation is impossible.");
                break;
            }
        }
    }

    private void readParsedData(){
        try (Socket socket = server.accept()) {
            if(cancel)
                return;
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            String result = (String) in.readObject();
            String funcType = result.substring(0, 1);
            result = result.substring(1);

            if(result.equals("hard fail")){
                if(funcType.equals("F"))
                    failF = true;
                else
                    failG = true;

                System.out.println("Function " + funcType + " hard failed");
            } else {
                resultsArr.add(Integer.valueOf(result));
                if(funcType.equals("F"))
                    System.out.println("Function F: " + result);
                else
                    System.out.println("Function G: " + result);
            }
        } catch (IOException | ClassNotFoundException e ) {
            e.printStackTrace();
        }
    }

    private void cancellingAdvanced(){
        System.out.println("Please confirm that computation should be stopped y(es, stop)/n(ot yet)");
        long startTime = System.currentTimeMillis();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true){
            if(System.currentTimeMillis() - startTime > 5000) {
                System.out.println("Action is not confirmed within 5 seconds. Proceeding...");
                initSignalHandler();
                return;
            }
            else if(resultsArr.size() == 2) {
                System.out.println("Overriden by system");
                initSignalHandler();
                return;
            }
            else {
                try {
                    if (reader.ready()){
                        String answer = reader.readLine();
                        if(!answer.isEmpty()){
                            if(answer.equals("y")){
                                System.out.println("Cancelling computation...");
                                cancel = true;
                                return;
                            } else if (answer.equals("n")) {
                                initSignalHandler();
                                System.out.println("Proceeding...");
                                return;
                            } else {
                                System.out.println("Please enter valid response 'y' or 'n'");
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void destroyProcesses() {
        processF.destroy();
        processG.destroy();
    }
}
