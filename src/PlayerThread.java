import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

/*
0: BS
200: high card
400: pair
600: two pair
800: three of a kind
1000: straight
1200: flush
1400: full house
1600: four of a kind
1800: straight flush
 */
public class PlayerThread extends Thread {
    public static final String[] RANKINGS = {"2", "3", "4", "5", "6", "7", "8", "9", "T", "J", "Q", "K", "A"};
    public static final String[] SUITS = {"\u2663", "\u2666", "\u2665", "\u2660"};
    private String name;
    GameServer game;
    private int cardCount;
    private Socket socket;
    private PrintWriter out;

    private BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
    private ArrayList<Integer> cards;

    public PlayerThread(GameServer game, int cardCount, Socket socket) throws IOException {
        this.game = game;
        this.cardCount = cardCount;
        this.socket = socket;
        //out = new PrintWriter(socket.getOutputStream());
        //in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }
    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));) {
            this.out = new PrintWriter(socket.getOutputStream(), true);
            sendMessage("n");
            System.out.println("Waiting for name...");
            this.name = in.readLine();
            String serverMessage = this.name + " joined!";
            System.out.println(serverMessage);
            String clientMessage;
            while (socket.isConnected() && !socket.isClosed()) {
                if (game.getGameStarted() && !game.getGameOver() && isMyTurn()) {
                    promptMove(game.getCurrentHand());
                    try {
                        clientMessage = in.readLine();
                        int move = Integer.parseInt(clientMessage);
                        game.broadcast("m" + name + " said " + decodeMove(move));
                        game.processMove(move);
                    } catch (Exception e) {
                        System.out.println(name + "'s move was interrupted.");
                        break;
                    }
                } else {
                    Thread.sleep(100);
                }
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            game.removePlayer(this);
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println(e.toString());
            }
            String serverMessage = "m" + name + " has quit.";
            game.broadcast(serverMessage);
        }
    }
    public boolean isMyTurn() {
        return (game.getTurnPlayer() == this);
    }
    /** Prompts player to make a move given the current state of the game */
    public void promptMove(int currentHand) throws IOException {
        out.println("c" + currentHand);
        out.println("m" + "It is your turn. Your hand is " + stringOfCards());
        out.println("t");
        out.flush();
    }
//    public void promptName() throws IOException {
//        sendMessage("n");
//        System.out.println("Name prompt sent");
//        this.name = in.readLine();
//    }
    public void sendMessage(String message) {
        out.println(message);
        out.flush();
    }
    public String stringOfCards() {
        String toReturn = "";
        for (int card : cards) {
            toReturn += numToCard(card) + " ";
        }
        return toReturn;
    }
    public String decodeMove(int input) {
        if (input == 0) {
            return "BS";
        } else if (input < 200) {
            throw new IllegalStateException();
        }
        else if (input < 400) {
            return "h " + RANKINGS[input % 200];
        } else if (input < 600) {
            return "p " + RANKINGS[input % 200];
        } else if (input < 800) {
            return "tp " + RANKINGS[(input % 200) / 13] + " " + RANKINGS[(input % 200) % 13];
        } else if (input < 1000) {
            return "t " + RANKINGS[input % 200];
        } else if (input < 1200) {
            return "s " + RANKINGS[input % 200];
        } else if (input < 1400) {
            return "f " + RANKINGS[(input % 200) / 13] + " " + SUITS[(input % 200) % 13];
        } else if (input < 1600) {
            return "fh " + RANKINGS[(input % 200) / 13] + " " + RANKINGS[(input % 200) % 13];
        } else if (input < 1800) {
            return "q " + RANKINGS[input % 200];
        } else if (input < 2000) {
            return "sf " + RANKINGS[(input % 200) / 13] + " " + SUITS[(input % 200) % 13];
        } else {
            throw new IllegalStateException();
        }
    }
    public void closeConnection() throws IOException {
        out.close();
        socket.close();
    }
    public int getCardCount() {
        return cardCount;
    }
    public String getMyName() {
        return name;
    }
    public ArrayList<Integer> getCards() {
        return cards;
    }
    public Socket getSocket() {
        return socket;
    }
    public void setCards(ArrayList<Integer> newCards) {
        this.cards = newCards;
    }
    public void incrementCardCount() {
        this.cardCount++;
    }
    public static String numToCard(int num) {
        return RANKINGS[num % 13] + SUITS[num / 13];
    }
}
