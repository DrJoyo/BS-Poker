import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Game {
    public static final int CLUB = 0;
    public static final int DIAMOND = 1;
    public static final int HEART = 2;
    public static final int SPADE = 3;
    private Random random = new Random();
    private BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
    private int playerCount;
    private int startingCards;
    private int eliminationCards;
    private int currentTotalCards;
    private ArrayList<Player> playerList = new ArrayList<Player>();
    private int turn;
    private boolean gameOver;

    public Game(int playerCount, int startingCards, int eliminationCards) throws IOException {
        this.playerCount = playerCount;
        this.startingCards = startingCards;
        this.eliminationCards = eliminationCards;
        waitForPlayers();
    }

    public void addPlayer() throws IOException {
        playerList.add(new Player(startingCards));
    }
    public void waitForPlayers() throws IOException {
        for (int i = 0; i < playerCount; i++) {
            addPlayer();
        }
    }
    public void gameLoop() throws IOException {
        random = new Random();
        currentTotalCards = startingCards * playerCount;
        turn = random.nextInt(playerCount);
        int currentHand;
        int response;
        boolean bs;
        gameOver = false;
        while (!gameOver) {
            bs = false;
            currentHand = 1;
            dealCards();
            while (!bs) {
                response = playerList.get(turn).promptMove("Move.", currentHand);
                if (response == 0) {
                    bs = true;
                    broadcast(playerList.get(turn).getName() + " called BS!");
                    if (handExists(currentHand)) {
                        broadcast("The hand existed. " + playerList.get(turn).getName() + " gets an extra card.");
                        handleLoss();
                    } else {
                        turn = Math.floorMod(turn - 1, playerCount);
                        broadcast("The hand did not exist. " + playerList.get(turn).getName() + " gets an extra card.");
                        handleLoss();
                    }
                } else {
                    if (response <= currentHand) {
                        throw new IllegalStateException("Hand was lower than current hand");
                    }
                    currentHand = response;
                    turn = (turn + 1) % playerCount;
                }
            }
        }
    }
    /** Adds a card to the player whose turn it is */
    public void handleLoss() {
        Player lost = playerList.get(turn);
        lost.incrementCardCount();
        currentTotalCards++;
        if (lost.getCardCount() >= eliminationCards) {
            broadcast(lost.getName() + " is eliminated!");
            playerList.remove(turn);
            playerCount--;
            turn = turn % playerCount;
            if (playerList.size() == 1) {
                broadcast(playerList.get(0).getName() + " wins!");
                gameOver = true;
            }
        }
        if (currentTotalCards == 52) {
            gameOver = true;
        }
    }
    public void dealCards() {
        ArrayList<Integer> deck = new ArrayList<>();
        for (int i = 0; i < 52; i++) {
            deck.add(i);
        }
        for (Player p : playerList) {
            ArrayList<Integer> hand = new ArrayList<>();
            for (int i = 0; i < p.getCardCount(); i++) {
                hand.add(deck.remove(random.nextInt(deck.size())));
            }
            p.setCards(hand);
        }
    }
    public boolean handExists(int handToCheck) {
        ArrayList<Integer> combined = new ArrayList<>();
        for (Player p : playerList) {
            combined.addAll(p.getCards());
        }
        Collections.sort(combined);
        if (handToCheck < 200) {
            throw new IllegalStateException();
        }
        if (handToCheck < 400) {
            return HandChecker.highExists(combined, handToCheck % 200);
        } else if (handToCheck < 600) {
            return HandChecker.pairExists(combined, handToCheck % 200);
        } else if (handToCheck < 800) {
            return HandChecker.twoPairExists(combined, (handToCheck % 200) / 13, (handToCheck % 200) % 13);
        } else if (handToCheck < 1000) {
            return HandChecker.tripsExists(combined, handToCheck % 200);
        } else if (handToCheck < 1200) {
            return HandChecker.straightExists(combined, handToCheck % 200);
        } else if (handToCheck < 1400) {
            return HandChecker.flushExists(combined, (handToCheck % 200) / 13, (handToCheck % 200) % 13);
        } else if (handToCheck < 1600) {
            return HandChecker.fullHouseExists(combined, (handToCheck % 200) / 13, (handToCheck % 200) % 13);
        } else if (handToCheck < 1800) {
            return HandChecker.quadsExists(combined, handToCheck % 200);
        } else if (handToCheck < 2000) {
            return HandChecker.straightFlushExists(combined, (handToCheck % 200) / 13, (handToCheck % 200) % 13);
        } else {
            throw new IllegalStateException();
        }
    }
    public void broadcast(String message) {
        System.out.println(message);
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            throw new IllegalArgumentException("Incorrect number of arguments");
        }

        Game game = new Game(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
        game.gameLoop();
    }
}
