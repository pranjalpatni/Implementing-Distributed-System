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
import java.io.*;
import java.net.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

/**
 * Node class implements Runnable method and creates two threads, one for client
 * and the other for server. Server listens at the designated port while the
 * client is responsible for creating the token and connecting to the server via
 * socket connection
 *
 * @author Pranjal Patni
 * @variable type indicates that whether the node is acting as a server or as a
 * client
 * @variable value is the random value of the token that is assigned to each
 * node
 * @variable server_socket is used to create socket for the server at which it
 * listens for a client
 * @variable identifier tells the node about its own identity
 * @variable port is the port number which the server_socket uses
 * @variable hostname is the host name of the node where it is running
 * @variable all_nodes is an array that stores the information of the whole
 * topology
 * @variable number_of_nodes is the total number of nodes in the distributed
 * system
 * @variable all_circuits contains all the circuit paths for which each node has
 * to generate a corresponding token
 * @variable config_file_path stores the address of the configuration file
 * @variable token is the object that contains all the required information that
 * is needed to be sent from one node to another
 */
public class Node implements Runnable {

    private final int type;
    private final int value;
    private ServerSocket server_socket = null;
    private int identifier;
    private int port;
    private String hostname;
    private String[] all_nodes;
    private int number_of_nodes;
    private Queue<String> all_circuits = new LinkedList<>();
    //private final String config_file_path = "C:\\Users\\Pranjal\\Documents\\NetBeansProjects\\AdvancedOperatingSystem\\src\\Project1\\configuration.txt";
    private final String config_file_path;
    private Token[] token;

    /**
     * Node constructor initiates the node with the values that are passed as
     * command line arguments to the main class
     *
     * @param type indicates that whether the node is acting as a server or as a
     * client
     * @param rand contains the value of the random number that is generated for
     * each node
     * @param id is the identifier for each node
     * @param config_path is the path of the configuration file
     */
    public Node(int type, int rand, int id, String config_path) {
        this.type = type;
        this.value = rand;
        this.identifier = id;
        config_file_path = config_path;
    }

    /**
     *
     * runAsServer method is used when the node is working as a server
     *
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void runAsServer() throws FileNotFoundException, IOException {
        find(config_file_path, identifier);
        listen(identifier, hostname, port);
    }

    /**
     *
     * runAsClient method is used when the node is working as a client
     *
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void runAsClient() throws FileNotFoundException, IOException {
        find(config_file_path, identifier);
        if (all_circuits.isEmpty()) {
            createFile(false);
            Thread.currentThread().interrupt();
            return;
        }
        token = new Token[all_circuits.size()];
        createToken();
        createFile(true);
        Client[] client = new Client[token.length];
        for (int i = 0; i < client.length; i++) {
            client[i] = new Client(token[i]);
        }
        connect(client);
    }

    /**
     *
     * createFile method creates a file that stores the log events for each node
     *
     * @param initiate is used to distinguish two clients that whether they are
     * @param inititate
     * @throws IOException
     */
    private void createFile(boolean inititate) throws IOException {
        PrintWriter writer = new PrintWriter("config-pxp142030-" + identifier + ".out", "UTF-8");
        writer.print("Net ID: pxp142030");
        writer.print("\nNode ID: " + identifier);
        String[] nodeDetail = all_nodes[identifier].split(" ");
        writer.print("\nListening on " + nodeDetail[1] + ":" + nodeDetail[2]);
        writer.print("\nRandom number: " + value);
        if (inititate == true) {
            for (int i = 0; i < token.length; i++) {
                writer.print("\nEmitting token " + token[i].get_idsNo() + " with path " + token[i].getOriginalMap());
            }
        }
        if (inititate == false) {
            writer.print("\nAll tokens received");
        }
        writer.close();
    }

    /**
     *
     * createToken method creates an object of Token class and initiates it with
     * the required values
     *
     */
    private void createToken() {
        for (int i = 0; i < token.length; i++) {
            String checkPath = all_circuits.remove();
            String newPath = "";
            String originalPath = "";
            if (checkPath.length() == 1) {
                newPath = checkPath;
                originalPath = checkPath + " -> " + checkPath;
            } else {
                String[] temp = (checkPath + " " + identifier).split(" ");
                newPath = checkPath.substring(2) + " " + identifier;
                for (String j : temp) {
                    originalPath = originalPath + j + " -> ";
                }
                originalPath = originalPath.substring(0, originalPath.length() - 4);
            }
            if (i < token.length - 1) {
                token[i] = new Token(identifier, newPath, value, i + 1, originalPath, token.length);
            } else {
                token[i] = new Token(identifier, newPath, value, i + 1, originalPath, token.length);
            }
        }
    }

    /**
     *
     * run method is called when a thread is created for an object of the Node
     * class and start method is called on that thread
     */
    @Override
    public void run() {
        if (type == 1) {
            try {
                runAsServer();
            } catch (IOException ex) {
                System.out.println("File not found");
                System.exit(-1);
            }
        } else {
            try {
                /**
                 * If this part of code is implemented instead of the part given
                 * below, then the client will only start once we press '1'
                 *
                 * System.out.println("Press 1 to run client"); Scanner hold =
                 * new Scanner(System.in); if (hold.nextInt() == 1) {
                 * runAsClient(); }
                 */
                long start = System.currentTimeMillis();
                long end = start + 2 * 1000; //         2 seconds
                while (System.currentTimeMillis() < end) {
                }
                runAsClient();
            } catch (IOException ex) {
                System.out.println("File not found");
                System.exit(-1);
            }
        }
    }

    /**
     * find method scans the configuration file to find out about all the
     * necessary information about the topology
     *
     * @param path
     * @param id
     * @throws FileNotFoundException
     */
    private void find(String path, int id) throws FileNotFoundException {
        Scanner scan_path = new Scanner(new File(path));
        identifier = id;
        String nextLine = scan_path.nextLine().trim();
        while (nextLine.equals("") || nextLine.charAt(0) == '#') {
            nextLine = scan_path.nextLine().trim();
        }
        if (nextLine.contains("#")) {
            number_of_nodes = Integer.parseInt(nextLine.split("#")[0].trim().split("\\s+")[0]);
        } else {
            number_of_nodes = Integer.parseInt(nextLine.trim().split("\\s+")[0]);
        }
        all_nodes = new String[number_of_nodes];
        nextLine = scan_path.nextLine().trim();
        while (nextLine.equals("") || nextLine.charAt(0) == '#') {
            nextLine = scan_path.nextLine().trim();
        }
        for (int i = 0; i < number_of_nodes; i++) {
            if (nextLine.contains("#")) {
                all_nodes[i] = nextLine.split("#")[0].trim();
            } else {
                all_nodes[i] = nextLine;
            }
            if (identifier == i) {
                String[] info = all_nodes[i].split(" ");
                hostname = info[1];
                port = Integer.parseInt(info[2]);
            }
            nextLine = scan_path.nextLine();
        }
        while (scan_path.hasNextLine()) {
            nextLine = scan_path.nextLine();
            while (nextLine.equals("") || nextLine.charAt(0) == '#') {
                nextLine = scan_path.nextLine();
            }
            String circuit;
            if (nextLine.contains("#")) {
                circuit = nextLine.split("#")[0].trim();
            } else {
                circuit = nextLine;
            }
            if (Integer.parseInt(circuit.substring(0, 1)) == identifier) {
                all_circuits.add(circuit);
            }
        }
    }

    /**
     * getNodeDetail method returns the information about a particular node
     *
     * @param id
     * @return a String containing the node's identifier, host-name and port
     * number
     */
    private String getNodeDetail(int id) {
        return all_nodes[id];
    }

    /**
     * connect method calls the connect method present in the Client class to
     * establish connection with the server
     *
     * @param client receives all the client objects on which the connect method
     * is needed to be called
     * @throws IOException
     */
    private void connect(Client[] client) throws IOException {
        for (int i = 0; i < client.length; i++) {
            int client_identifier = Integer.parseInt(client[i].getToken().getMap().substring(0, 1));
            String[] client_detail = getNodeDetail(client_identifier).split(" ");
            client[i].connect(client_detail[1], Integer.parseInt(client_detail[2]));
        }
    }

    /**
     * listen method is used by the server to listen to the client's requests
     * and creates another thread to handle the corresponding client, thereby
     * making the server multi-threaded
     *
     * @param identifier id of the node
     * @param hostname the host name where the server is running
     * @param port the port number at which the server is listening
     * @throws IOException
     */
    public void listen(int identifier, String hostname, int port) throws IOException {
        server_socket = new ServerSocket(port);
        System.out.println("Node " + identifier + " running on " + hostname + " at port " + port + " with value: " + value);
        while (true) {
            ServerThread newClient;
            newClient = new ServerThread(server_socket.accept(), value, this.identifier, all_nodes);
            Thread t = new Thread(newClient);
            t.start();
        }
    }
}
