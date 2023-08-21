import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;

// This class represents an HTTP request handler
final class HttpRequest implements Runnable {
   final static String CRLF = "\r\n ";
   Socket socket;

   // Default constructor
   public HttpRequest() {
   }

   // Constructor that takes a Socket object
   public HttpRequest(Socket socket) throws Exception {
      this.socket = socket;
   }

   // Runnable interface implementation
   @Override
   public void run() {
      try {
         // Process the request
         processRequest();
      } catch (Exception e) {
         // Print any exception that occurs
         System.out.println(e);
      }
   }

   // Method to process the HTTP request
   void processRequest() throws Exception {
      // Get the input stream from the socket
      InputStream is = socket.getInputStream();

      // Get the output stream from the socket
      DataOutputStream os = new DataOutputStream(socket.getOutputStream());

      // Create a BufferedReader to read the input stream
      BufferedReader br = new BufferedReader(new InputStreamReader(is));

      // Read the request line from the client
      String requestLine = br.readLine();

      // Display the request line
      System.out.println();
      System.out.println(requestLine);
      
      String headerLine = null;
      while ((headerLine = br.readLine()).length() != 0) {
         // Print each header line
         System.out.println(headerLine);
      }

      // Extract the filename from the request line
      // Extract the filename from the request line
      StringTokenizer tokens = new StringTokenizer(requestLine);
      tokens.nextToken(); // Skip the first token (the HTTP method)
      String fileName = tokens.nextToken(); // Get the second token (the file name)

      // Prepend a "." so that file request is within the current director
      fileName = "." + fileName;
      
      // Open the requested file
      FileInputStream fis = null;
      boolean fileExists = true;
      try {
         fis = new FileInputStream(fileName);
      } catch (FileNotFoundException e) {
         // If the file is not found, set the flag to false
         System.out.println("FILE NOT FOUND");
         fileExists = false;
      }

      // Construct the response message
      String statusLine = null;
      String contentTypeLine = null;
      String entityBody = null;
      if (fileExists) {
         // If the file exists, set the status line and content type
         statusLine = "HTTP/1.0 200 OK" + CRLF;
         contentTypeLine = "Content-type: " + contentType(fileName) + CRLF;
      } else {
         // If the file doesn't exist, set the status line, content type, and entity body
         statusLine = "HTTP/1.0 404 Not Found" + CRLF;
         contentTypeLine = "Content-type: text/html" + CRLF;
         entityBody = "<HTML>" +
            "<HEAD><TITLE>Not Found</TITLE></HEAD>" +
            "<BODY>Not Found</BODY></HTML>";
      }

      // Send the status line
      os.writeBytes(statusLine);
      // Send the content type line
      os.writeBytes(contentTypeLine);
      // Send a blank line to indicate the end of the header lines
      os.writeBytes(CRLF);

      if (fileExists) {
         // If the file exists, send its contents to the client
    	 System.out.println("Sending file: " + fileName);
         sendBytes(fis, os);
         fis.close();
      } else {
    	  // If the file doesn't exist, send the entity body (error message) to the client
         os.writeBytes(entityBody);
      }

      // Close the streams and socket
      br.close();
      os.close();
      socket.close();
   }

   // Method to send the bytes of a file to the output stream
   private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception {
	   // Construct a 1K buffer to hold bytes on their way to the socket
	   byte[] buffer = new byte[1024];
	   int bytes = 0;

	   // Read bytes from the file and write them to the output stream
       while((bytes = fis.read(buffer)) != -1 ){
           os.write(buffer, 0, bytes);
       }
	}	
   // Method to determine the content type based on the file extension
   private static String contentType(String fileName) {
      if (fileName.endsWith(".htm") || fileName.endsWith(".html")) {
         return "text/html";
      } else if (fileName.endsWith(".gif")) {
         return "image/gif";
      } else if (fileName.endsWith(".jpeg") || fileName.endsWith(".jpg")) {
         return "image/jpeg";
      } else {
         return "application/octet-stream";
      }
   }
}