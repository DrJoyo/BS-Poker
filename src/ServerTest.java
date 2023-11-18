import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerTest {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            throw new IllegalArgumentException("wrong number of arguments");
        }
        // create server socket
        ServerSocket server = new ServerSocket(Integer.parseInt(args[0]));

        // wait for the connection
        System.out.println("Waiting for the client...");
        Socket conn = server.accept();
        System.out.println("Client connected");

        // get socket I/O stream and perform processing
        // InputStream to receive information from client
        // OutputStream to send information to client
        PrintWriter out = new PrintWriter(conn.getOutputStream());
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

        out.println("Hi!");
        out.flush();

        String fromUser;
        String fromClient;
        while ((fromClient = in.readLine()) != null) {
            System.out.println("Client: " + fromClient);
            if (fromClient.equals("Bye")) {
                break;
            }
            fromUser = stdIn.readLine();
            if (fromUser != null) {
                System.out.println("Server: " + fromUser);
                out.println(fromUser);
                out.flush();
                if (fromUser.equals("Bye")) {
                    break;
                }
            }
        }

        // close connection
        in.close();
        out.close();
        conn.close();
    }
}
