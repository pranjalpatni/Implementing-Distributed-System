# Implementing-Distributed-System

# Description
-	This project implements a distributed system consisting of n nodes

-	The value of n and the location of each of the n node have been specified in a configuration file

-	 Every node selects a label value (basically an integer) uniformly at random in the beginning

-	 Every node then circulates a token through the system that visits each node in the system once and computes the sum of all the label values along the way

-	The path taken by the token of each node is again specified in the configuration file

-	This path is piggybacked on the token by the node that generated the token

-	At the end, each node prints its label value and the sum of all the label values computed by its token

-	The path may contain cycles and/or may not contain all the nodes
-	The token should simply traverse the nodes in other order specified in the path even if it means visiting a node (and adding its number) more than once

# Prerequisites:
1-	Socket programming and knowledge about multithreading:

o	I implemented my code in Java and used TCP socket programming

o	Each node creates 2 threads. One of them works as a server and the other one acts as a client

o	Each node generates a log file where it mentions all the relevant information like its node ID, token number, random value assigned to it, path of the token and so on

2-	Bash scripting:

o	Opening up terminals at different machines is time consuming, so a script is run which SSH into the machines and runs the program remotely

o	Another script called cleanup script is required to kill all the processes that were opened in the remote machine, so that the port number which was assigned to the server socket gets reset. Otherwise, we won’t be able to re establish the connection for the same port number again, the next time we run our program


# Challenges Faced:

-	Working with multiple threads:

It was very challenging to work with threads. Because the execution of the program may change as different threads may start randomly every time we start the program.
Before the server thread goes into listening mode, if the client thread tries to establish a connection with the server, then we will receive an Exception. So it becomes very important that the client threads start working only after all the server threads have reached the listening mode. One possible way to do this is to make all the client threads sleep for a few seconds. This will provide enough time to the server threads to go to the listening mode.

# How to Run the Project:

Please read the "How to Execute" file
