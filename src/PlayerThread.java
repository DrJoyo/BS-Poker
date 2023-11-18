import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class PlayerThread extends Thread {
    private Socket socket;
    private boolean isMyTurn = false;
    private int currentHand;
    public PlayerThread(Socket socket) {
        super("PlayerThread");
        this.socket = socket;
    }
    @Override
    public void run() {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
