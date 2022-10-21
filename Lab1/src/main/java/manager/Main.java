package manager;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int input = 0;
        boolean isInt = false;
        while (!isInt) {
            System.out.println("Enter x: ");
            if (sc.hasNextInt()) {
                input = sc.nextInt();
                isInt = true;
            }
            sc.nextLine();
        }

        try {
            long startTime, endTime;
            startTime = System.currentTimeMillis();
            Manager manager = new Manager(input);
            endTime = System.currentTimeMillis();
            System.out.println("Computation finished");
            int fStatus = manager.getStatusF();
            int gStatus = manager.getStatusG();
            Boolean cancelStatus = manager.cancel;

            if(cancelStatus)
                System.out.println("Computation cancelled");

            if(fStatus + gStatus > 0)
                System.out.println("Result: fail");
            else if(fStatus == 0 && gStatus == 0)
                System.out.println("Result: " + manager.getResult());
            else
                System.out.println("Result: undetermined");

            System.out.println("Time of computation: " + (endTime - startTime) + " milliseconds");
            manager.destroyProcesses();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}
