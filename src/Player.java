import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
public class Player {
    public static final String[] rankings = {"2", "3", "4", "5", "6", "7", "8", "9", "T", "J", "Q", "K", "A"};
    public static final String[] suits = {"\u2663", "\u2666", "\u2665", "\u2660"};
    private String name;
    private int cardCount;
    private ArrayList<Integer> cards;
    private BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

    public Player(int cardCount) throws IOException {
        this.cardCount = cardCount;
        System.out.println("Enter your name.");
        this.name = stdIn.readLine();
    }
    /** Prompts player to make a move given the current state of the game */
    public int promptMove(String prompt, int currentHand) throws IOException {
        String response;
        System.out.println("It is your turn, " + name + ". Your cards are:");
        showCards();
        int encoded = -1;
        response = stdIn.readLine();
        encoded = encodeMove(response);
        if (encoded != 0 && encoded <= currentHand) {
            encoded = -1;
        }
        if (currentHand == 1 && encoded == 0) {
            encoded = -1;
        }
        while (encoded == -1) {
             System.out.println("Invalid move. Please try again.");
             response = stdIn.readLine();
             encoded = encodeMove(response);
             if (encoded != 0 && encoded <= currentHand) {
                 encoded = -1;
             }
             if (currentHand == 1 && encoded == 0) {
                 encoded = -1;
             }
        }
        System.out.println(name + " said " + response);
        return encoded;
    }
    public void showCards() {
        for (int card : cards) {
            System.out.print(numToCard(card) + " ");
        }
        System.out.println();
    }

    /** Takes input string and turns it into an int for Game to process */
    public int encodeMove(String input) {
        String[] splitInput = input.split(" ");
        if (splitInput.length == 0) {
            return -1;
        }
        int input1;
        int input2;
        switch(splitInput[0]) {
            case "bs":
                return 0;
            case "h", "H", "p", "P", "t", "T", "s", "S", "q", "Q":
                if (splitInput.length != 2) {
                    return -1;
                }
                input1 = rankToInt(splitInput[1]);
                if (input1 == -1) {
                    return -1;
                }
                if (verifyRank(input1)) {
                    return encodeOneInputMove(splitInput[0], input1);
                } else {
                    return -1;
                }
            case "tp", "TP", "f", "F", "fh", "FH", "sf", "SF":
                if (splitInput.length != 3) {
                    return -1;
                }
                try {
                    input1 = rankToInt(splitInput[1]);
                    if (input1 == -1) {
                        return -1;
                    }
                    if (verifyRank(input1)) {
                        return encodeTwoInputMove(splitInput[0], input1, splitInput[2]);
                    } else {
                        return -1;
                    }
                } catch (Exception e) {
                    return -1;
                }
            default:
                return -1;
        }
    }
    public int rankToInt(String s) {
        if (s.equals("2")) {
            return 0;
        } else if (s.equals("3")) {
            return 1;
        } else if (s.equals("4")) {
            return 2;
        } else if (s.equals("5")) {
            return 3;
        } else if (s.equals("6")) {
            return 4;
        } else if (s.equals("7")) {
            return 5;
        } else if (s.equals("8")) {
            return 6;
        } else if (s.equals("9")) {
            return 7;
        } else if (s.equalsIgnoreCase("t")) {
            return 8;
        } else if (s.equalsIgnoreCase("j")) {
            return 9;
        } else if (s.equalsIgnoreCase("q")) {
            return 10;
        } else if (s.equalsIgnoreCase("k")) {
            return 11;
        } else if (s.equalsIgnoreCase("a")) {
            return 12;
        } else {
            return -1;
        }
    }
    public int suitToInt(String s) {
        if (s.equalsIgnoreCase("c") || s.equalsIgnoreCase("clubs") || s.equalsIgnoreCase("club")) {
            return 0;
        } else if (s.equalsIgnoreCase("d") || s.equalsIgnoreCase("diamonds") || s.equalsIgnoreCase("diamond")) {
            return 1;
        } else if (s.equalsIgnoreCase("h") || s.equalsIgnoreCase("hearts") || s.equalsIgnoreCase("heart")) {
            return 2;
        } else if (s.equalsIgnoreCase("s") || s.equalsIgnoreCase("spades") || s.equalsIgnoreCase("spade")) {
            return 3;
        } else {
            return -1;
        }
    }
    public int encodeOneInputMove(String type, int input1) {
        switch(type) {
            case "h", "H":
                return 200 + input1;
            case "p", "P":
                return 400 + input1;
            case "t", "T":
                return 800 + input1;
            case "s", "S":
                if (input1 < 3) {
                    return -1;
                }
                return 1000 + input1;
            case "q", "Q":
                return 1600 + input1;
            default:
                return -1;
        }
    }
    public int encodeTwoInputMove(String type, int input1, String strInput2) {
        int input2;
        switch(type) {
            case "tp", "TP":
                input2 = rankToInt(strInput2);
                if (!verifyRank(input2)) {
                    return -1;
                }
                if (input1 > input2) {
                    return 600 + 13 * input1 + input2;
                } else if (input1 < input2) {
                    return 600 + 13 * input2 + input1;
                } else {
                    return -1;
                }
            case "f", "F":
                input2 = suitToInt(strInput2);
                if (!verifyRank(input2)) {
                    return -1;
                }
                if (input2 > 3) {
                    return -1;
                }
                return 1200 + 13 * input1 + input2;
            case "fh", "FH":
                input2 = rankToInt(strInput2);
                if (!verifyRank(input2)) {
                    return -1;
                }
                if (input1 == input2) {
                    return -1;
                }
                return 1400 + 13 * input1 + input2;
            case "sf", "SF":
                input2 = suitToInt(strInput2);
                if (!verifyRank(input2)) {
                    return -1;
                }
                if (input1 < 3 || input2 > 3) {
                    return -1;
                }
                return 1800 + 13 * input1 + input2;
            default:
                return -1;
        }
    }
    public boolean verifyRank(int rank) {
        return (rank < 13 && rank >= 0);
    }
    public int getCardCount() {
        return cardCount;
    }
    public String getName() {
        return name;
    }
    public ArrayList<Integer> getCards() {
        return cards;
    }
    public void setCards(ArrayList<Integer> newCards) {
        this.cards = newCards;
    }
    public void incrementCardCount() {
        this.cardCount++;
    }
    public static String numToCard(int num) {
        return rankings[num % 13] + suits[num / 13];
    }
}
