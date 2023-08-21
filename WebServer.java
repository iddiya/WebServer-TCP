import java.net.ServerSocket;
import java.net.Socket;

// This class represents a simple web server
public final class WebServer {
    public static void main(String[] args) throws Exception {
        // Set the port number on which the server will listen for connections
        int port = 6789;
        
		ServerSocket serverSocket = new ServerSocket(port); 
		// Print a message indicating that the server is waiting for connections
		System.out.println("Waiting for connections on port 6789...");

		// Process HTTP service requests in an infinite loop
		while (true) {
			// Accept a connection from a client and create a socket for communication
			final Socket socket = serverSocket.accept();

			// Construct an object to process the HTTP request message
			HttpRequest request = new HttpRequest(socket);

			 // Create a new thread to process the request
			 Thread thread = new Thread(request);

			 // Start the thread to handle the request concurrently
			 thread.start();
		}
    }
}