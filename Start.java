/*
 * Pranjal Patni (pxp142030)
 *
 * @author Pranjal Patni
 * @email pxp142030@utdallas.edu
 * @version 1.0
 *
 * This project focuses on the implementation of a distributed system, where
 * there are several nodes who communicate among each other via messages. Each
 * node generates a random value and adds its own value while passing the
 * message to the next node.
 */

import java.util.Random;

/**
 * Start class contains the main method, which is required to execute the
 * program. It generates a new random number before creating a new node and
 * assigns those numbers to the respective node
 *
 * @author Pranjal
 *
 * @variable id is the id of the node to be created
 * @variable config_path is the path of the configuration file from where the
 * data is fetched
 */
public class Start implements Runnable {

    private int id;
    private String config_path;

    public Start(int id, String config_path) {
        this.id = id;
        this.config_path = config_path;
    }

    public void run() {
        int rand;
        Random random = new Random();
        rand = random.nextInt((9999 - 100) + 1) + 100;
        startNode(1, rand, id);
        long start = System.currentTimeMillis();
        long end = start + 2 * 1000; // 0.2 second
        while (System.currentTimeMillis() < end) {

        }
        startNode(2, rand, id);
    }

    private void startNode(int type, int rand, int id) {
        Node n = new Node(type, rand, id, config_path);
        Thread t = new Thread(n);
        t.start();
    }

    public static void main(String... args) {
        int no_of_node = Integer.parseInt(args[0]);
        String config_path = args[1];
        /**
         * To run the program manually without passing command line arguments
         * int no_of_node = 4; String config_path = "configuration.txt";
         */
        Start start = new Start(no_of_node, config_path);
        Thread t = new Thread(start);
        t.start();

    }
}
