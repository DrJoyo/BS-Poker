import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class GameServer {
    public static final int CLUB = 0;
    public static final int DIAMOND = 1;
    public static final int HEART = 2;
    public static final int SPADE = 3;
    private Random random = new Random();
    private BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
    private int playerCount;
    private int startingCards;
    private int eliminationCards;
    private int port;
    private int currentTotalCards;
    private ArrayList<PlayerThread> playerList = new ArrayList<>();
    private ArrayList<PlayerThread> inList;
    private int turn = -1;
    private int currentHand;
    private ArrayList<Integer> combinedCards;
    private boolean gameStarted;
    private boolean gameOver;
    private ServerSocket serverSocket;
    private ServerSocket serverSocket2;

    public GameServer(int port, int playerCount, int startingCards, int eliminationCards) throws IOException {
        this.playerCount = playerCount;
        this.startingCards = startingCards;
        this.eliminationCards = eliminationCards;
        this.port = port;
    }

    public void addPlayer() throws IOException {
        System.out.println("Waiting for player to connect...");
        Socket socket = serverSocket.accept();
        System.out.println("Player connected. Waiting for them to enter name.");
        PlayerThread newPlayer = new PlayerThread(this, startingCards, socket);
        playerList.add(newPlayer);
        System.out.println(newPlayer.getMyName() + " joined!");
    }
    public void removePlayer(PlayerThread p) {
        if (inList.remove(p)) {
            playerCount--;
        }
        playerList.remove(p);
        System.out.println(p.getMyName() + " has quit.");

        if (!gameOver && inList.size() == 1) {
            broadcast("m" + inList.get(0).getMyName() + " wins!");
            broadcast("g");
            gameOver = true;
        }
        if (playerCount > 0) {
            turn = turn % playerCount;
        }
    }
    public void init() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            int joined = 0;
            while (!gameStarted) {
                System.out.println("Waiting for player to connect...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected");
                System.out.println("Client address:");
                System.out.println(clientSocket.getInetAddress());
                System.out.println("-----");
                PlayerThread newPlayer = new PlayerThread(this, startingCards, clientSocket);
                playerList.add(newPlayer);
                newPlayer.start();
                joined++;
                if (joined >= playerCount) {
                    random = new Random();
                    currentTotalCards = startingCards * playerCount;
                    inList = (ArrayList<PlayerThread>) playerList.clone();
                    currentHand = 1;
                    dealCards();
                    showEveryoneCards();
                    turn = random.nextInt(playerCount);
                    gameStarted = true;
                }
            }
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            String broadcastMessage;
            while (!gameOver) {
                if (stdIn.ready()) {
                    broadcastMessage = stdIn.readLine();
                    if (broadcastMessage.equals("ENDGAME")) {
                        gameOver = true;
                        broadcast("m" + "Game closed by server.");
                        broadcast("g");
                        for (PlayerThread p : playerList) {
                            p.closeConnection();
                        }
                    } else {
                        broadcast("m" + broadcastMessage);
                    }
                }
            }
            System.out.println("Game over");
        } catch (IOException e) {
            System.out.println("Port already in use!");
            System.out.println(e.toString());
        }

    }

    public void processMove(int response) throws IOException {
        gameStarted = false;
        if (response == 0) {
            if (handExists(currentHand)) {
                broadcast("mThe hand existed. " + inList.get(turn).getMyName() + " gets an extra card.");
            } else {
                turn = Math.floorMod(turn - 1, playerCount);
                broadcast("mThe hand did not exist. " + inList.get(turn).getMyName() + " gets an extra card.");
            }
            handleLoss();
            if (!gameOver) {
                currentHand = 1;
                dealCards();
                showEveryoneCards();
            }
        } else {
            if (response <= currentHand) {
                throw new IllegalStateException("Hand was lower than current hand");
            }
            currentHand = response;
            turn = (turn + 1) % playerCount;
        }
        gameStarted = true;
    }
    /** Adds a card to the player whose turn it is */
    public void handleLoss() throws IOException {
        PlayerThread lost = inList.get(turn);
        lost.incrementCardCount();
        currentTotalCards++;
        if (lost.getCardCount() >= eliminationCards) {
            broadcast("m" + lost.getMyName() + " is eliminated!");
            inList.remove(turn);
            playerCount--;
            turn = turn % playerCount;
            if (inList.size() == 1) {
                broadcast("m" + inList.get(0).getMyName() + " wins!");
                broadcast("g");
                gameOver = true;
                for (PlayerThread p : playerList) {
                    p.closeConnection();
                }
            }
        }
        if (currentTotalCards == 52) {
            gameOver = true;
        }
    }
    public void dealCards() {
        ArrayList<Integer> deck = new ArrayList<>();
        combinedCards = new ArrayList<>();
        for (int i = 0; i < 52; i++) {
            deck.add(i);
        }
        for (PlayerThread p : inList) {
            ArrayList<Integer> hand = new ArrayList<>();
            for (int i = 0; i < p.getCardCount(); i++) {
                hand.add(deck.remove(random.nextInt(deck.size())));
            }
            combinedCards.addAll(hand);
            p.setCards(hand);
        }
    }
    /** Checks if the current hand exists and broadcasts the combined list of cards to everyone */
    public boolean handExists(int handToCheck) {
        Collections.sort(combinedCards);
        String allCards = "";
        for (int card : combinedCards) {
            allCards += PlayerThread.numToCard(card) + " ";
        }
        broadcast("m" + "The combined cards are " + allCards);
        if (handToCheck < 200) {
            throw new IllegalStateException();
        }
        if (handToCheck < 400) {
            return HandChecker.highExists(combinedCards, handToCheck % 200);
        } else if (handToCheck < 600) {
            return HandChecker.pairExists(combinedCards, handToCheck % 200);
        } else if (handToCheck < 800) {
            return HandChecker.twoPairExists(combinedCards, (handToCheck % 200) / 13, (handToCheck % 200) % 13);
        } else if (handToCheck < 1000) {
            return HandChecker.tripsExists(combinedCards, handToCheck % 200);
        } else if (handToCheck < 1200) {
            return HandChecker.straightExists(combinedCards, handToCheck % 200);
        } else if (handToCheck < 1400) {
            return HandChecker.flushExists(combinedCards, (handToCheck % 200) / 13, (handToCheck % 200) % 13);
        } else if (handToCheck < 1600) {
            return HandChecker.fullHouseExists(combinedCards, (handToCheck % 200) / 13, (handToCheck % 200) % 13);
        } else if (handToCheck < 1800) {
            return HandChecker.quadsExists(combinedCards, handToCheck % 200);
        } else if (handToCheck < 2000) {
            return HandChecker.straightFlushExists(combinedCards, (handToCheck % 200) / 13, (handToCheck % 200) % 13);
        } else {
            throw new IllegalStateException();
        }
    }
    public void broadcast(String message) {
        for (PlayerThread p : playerList) {
            p.sendMessage(message);
        }
    }
    public void showEveryoneCards() {
        for (PlayerThread p : inList) {
            p.sendMessage("m" + "Your hand is " + p.stringOfCards());;
        }
    }
    public PlayerThread getTurnPlayer() {
        return inList.get(turn);
    }
    public int getCurrentHand() {
        return this.currentHand;
    }
    public boolean getGameStarted() {
        return this.gameStarted;
    }
    public boolean getGameOver() {
        return this.gameOver;
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 4) {
            throw new IllegalArgumentException("Incorrect number of arguments");
        }

        GameServer game = new GameServer(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
        game.init();
    }
}
