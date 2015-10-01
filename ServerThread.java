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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ServerThread class implements Runnable and is used because the node which is
 * acting as the server is implementing a multi threaded server
 *
 * @author Pranjal Patni
 * @variable server_t is a socket object which is passed to the thread handling
 * the corresponding client
 * @variable in is used to receive the token that has been sent by the client
 * @variable out is used to send the token to the next server
 * @variable server_token is used to receive the incoming token object
 * @variable value is the random value that has been assigned to each node
 * @variable identifier is the id of the node
 * @variable all_nodes contains the information of every node
 * @variable stop is used to stop the thread since it is associated to that node
 * which receives its token back and needs to log the token information that it
 * has received
 */
public class ServerThread implements Runnable {

    private Socket server_t;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Token server_token;
    private int value;
    private int identifier;
    private String[] all_nodes;
    private int stop = 0;

    /**
     * ServerThread constructor instantiates the ServerThread thread
     *
     * @param client
     * @param value
     * @param id
     * @param all_nodes
     */
    public ServerThread(Socket client, int value, int id, String[] all_nodes) {
        this.server_t = client;
        this.value = value;
        identifier = id;
        this.all_nodes = new String[all_nodes.length];
        this.all_nodes = all_nodes;
    }

    /**
     * run method is called when the ServerThread object's thread's start method
     * is called
     */
    @Override
    public void run() {
        try {
            in = new ObjectInputStream(server_t.getInputStream());
            server_token = (Token) in.readObject();
            processToken();
            if (stop == 1) {
                Thread.currentThread().interrupt();
                return;
            }
            Client next_client = new Client(server_token);
            connect(next_client);
        } catch (IOException e) {
            System.out.println("Error in reading or writing");
            System.exit(-1);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * connect method creates a client object to connect to the next server
     *
     * @param client receives the client object that will connect to the server
     * @throws IOException
     */
    private void connect(Client client) throws IOException {
        int client_identifier = Integer.parseInt(client.getToken().getMap().substring(0, 1));
        String[] client_detail = getNodeDetail(client_identifier).split(" ");
        client.connect(client_detail[1], Integer.parseInt(client_detail[2]));
    }

    /**
     * getNodeDetail method returns the information about a particular node
     *
     * @param id
     * @return a String containing the node's identifier, host-name and port
     */
    private String getNodeDetail(int id) {
        return all_nodes[id];
    }

    /**
     * processToken method process the token that the server thread has received
     * and updates the path and the token value
     *
     * @throws IOException
     */
    private void processToken() throws IOException {
        int path_length = server_token.getMap().length();
        if (path_length == 1 & server_token.getId() == identifier) {
            makeFile(server_token);
            stop = 1;
        } else {
            String newPath = server_token.getMap().substring(2);
            server_token.setMap(newPath);
            server_token.set_total_sum(value);
        }
    }

    /**
     * makeFile method appends the logs to the correct .out file for the
     * corresponding node as soon as the token is received
     *
     * @param t reads the token to create the necessary log
     * @throws IOException
     */
    private void makeFile(Token t) throws IOException {
        File newFile = new File("config-pxp142030-" + t.getId() + ".out");
        PrintWriter writer = new PrintWriter(new FileWriter(newFile, true));
        FileReader reader = new FileReader(newFile);
        BufferedReader br = new BufferedReader(reader);
        String line = null;
        int count_tokens_printed = 1;
        if (newFile.exists() == true) {
            while ((line = br.readLine()) != null) {
                String[] temp_line = line.split(" ");
                if (temp_line[0].equals("Received")) {
                    count_tokens_printed++;
                }
            }
            writer.append("\nReceived token " + t.get_idsNo() + "        Token sum = " + t.get_total_sum());
            if (count_tokens_printed == t.getTotalTokensForId()) {
                writer.append("\nAll tokens received");
            }
        }
        writer.flush();
        writer.close();
    }
}
