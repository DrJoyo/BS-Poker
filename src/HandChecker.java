import java.util.ArrayList;

public class HandChecker {
    // Cards 0 to 51
    // Ranks 2-A represented by 0-12, calculated by card % 13
    // Suits clubs-spades represented by 0-3, calculated by card / 13

    /** Checks if a single card of the rank exists */
    public static boolean highExists(ArrayList<Integer> cards, int rank) {
        for (int card : cards) {
            if (card % 13 == rank) {
                return true;
            }
        }
        return false;
    }

    /** Checks if a pair of the rank exists */
    public static boolean pairExists(ArrayList<Integer> cards, int rank) {
        int count = 0;
        for (int card : cards) {
            if (card % 13 == rank) {
                count++;
            }
        }
        return (count >= 2);
    }

    /** Checks if two pairs of the given ranks exist */
    public static boolean twoPairExists(ArrayList<Integer> cards, int rank1, int rank2) {
        int count1 = 0;
        int count2 = 0;
        for (int card : cards) {
            if (card % 13 == rank1) {
                count1++;
            } else if (card % 13 == rank2) {
                count2++;
            }
        }
        return (count1 >= 2 && count2 >= 2);
    }

    /** Checks if a 3-of-a-kind of the rank exists */
    public static boolean tripsExists(ArrayList<Integer> cards, int rank) {
        int count = 0;
        for (int card : cards) {
            if (card % 13 == rank) {
                count++;
            }
        }
        return (count >= 3);
    }

    /** Checks if a straight with high rank exists */
    public static boolean straightExists(ArrayList<Integer> cards, int rank) {
        boolean[] ranks = new boolean[13];
        for (int card : cards) {
            ranks[card % 13] = true;
        }
        return ranks[rank] && ranks[rank - 1] && ranks[rank - 2] && ranks[rank - 3] && ranks[(rank + 9) % 13];
    }

    /** Checks if a flush of the suit and rank exists */
    public static boolean flushExists(ArrayList<Integer> cards, int rank, int suit) {
        int suitCount = 0;
        boolean ranked = false;
        for (int card : cards) {
            if (card / 13 == suit) {
                suitCount++;
                if (card % 13 == rank) {
                    ranked = true;
                }
            }
        }
        return (suitCount >= 5 && ranked);
    }

    /** Checks if a full house of the given ranks exists */
    public static boolean fullHouseExists(ArrayList<Integer> cards, int rank1, int rank2) {
        int count1 = 0;
        int count2 = 0;
        for (int card : cards) {
            if (card % 13 == rank1) {
                count1++;
            } else if (card % 13 == rank2) {
                count2++;
            }
        }
        return (count1 >= 3 && count2 >= 2);
    }

    /** Checks if a 4-of-a-kind of the rank exists */
    public static boolean quadsExists(ArrayList<Integer> cards, int rank) {
        int count = 0;
        for (int card : cards) {
            if (card % 13 == rank) {
                count++;
            }
        }
        return (count >= 4);
    }

    /** Checks if a straight flush or royal flush of the suit and rank exists */
    public static boolean straightFlushExists(ArrayList<Integer> cards, int rank, int suit) {
        int highCard = suit * 13 + rank;
        boolean[] required = new boolean[5];
        for (int card : cards) {
            if (highCard - 5 < card && card <= highCard) {
                required[highCard - card] = true;
            }
        }
        return required[0] && required[1] && required[2] && required[3] && required[4];
    }
}
