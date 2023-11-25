# BS-Poker

This mini application lets you host games of BS Poker with friends over the internet. I created this project because I enjoy playing the game with my friends in person, but I couldn't find a program to play it online since it's not a very well known game.

The rules:
- Everyone starts with the same number of cards (typically 2 or 3).
- One player starts by naming a poker hand that they think exists among everyone's cards combined.
- The next player must either name a higher poker hand, or call BS if they don't think the previously named hand exists.
- The play rotates until BS is called, at which point everyone reveals their cards to determine whether the last called hand exists. If the hand exists, the player who called BS loses the round. Otherwise, the player who got called BS on loses the round.
- The player who loses a round gets an extra card for subsequent rounds. 
- Once a player reaches a predetermined number of cards, they are eliminated.
- The game continues until there is one player remaining.

How to run the program as the host:
- Download and compile GameServer.java, PlayerThread.java, and HandChecker.java
- Run GameServer with arguments (port #, # of players, starting cards, elimination card count)
- After game starts, you can type messages to broadcast to all players. Enter "ENDGAME" to end the game early.

How to run the program as a player:
- Download and compile Client.java
- Run Client with arguments (IP address, port #)
- When the program says it's your turn, enter your move by the following code

Move code:
- BS: bs
- High card: h (rank)
- Pair: p (rank)
- Two pair: tp (rank1) (rank2)
- Three of a kind: t (rank)
- Straight: s (rank)
- Flush: f (rank) (suit)
- Full house: fh (rank1) (rank2)
- Four of a kind: q (rank)
- Straight flush: sf (rank) (suit)
- Royal flush: sf a (suit)

Rank/Suit code:
2 to 9: 2 to 9
10: T
Jack: J
Queen: Q
King: K
Ace: A
Clubs: C
Diamond: D
Hearts: H
Spades: S