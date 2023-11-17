import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientTest {
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            throw new IllegalArgumentException("wrong number of arguments");
        }
        // create socket to connect to server
        Socket conn = new Socket(args[0], Integer.parseInt(args[1]));

        // get socket I/O stream and perform processing
        // InputStream to receive information from server
        // OutputStream to send information to server
        PrintWriter out = new PrintWriter(conn.getOutputStream());
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

        String fromUser;
        String fromServer;
        while ((fromServer = in.readLine()) != null) {
            System.out.println("Server: " + fromServer);
            if (fromServer.equals("Bye")) {
                break;
            }
            fromUser = stdIn.readLine();
            if (fromUser != null) {
                System.out.println("Client: " + fromUser);
                out.println(fromUser);
                out.flush();
                if (fromUser.equals("Bye")) {
                    break;
                }
            }
        }


//        String str = in.readLine();
//        System.out.println("server: " + str);

        // close connection
        in.close();
        out.close();
        conn.close();
    }

}
