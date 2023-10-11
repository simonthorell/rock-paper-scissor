public class GameLogic {
    private int player1;
    private int player2;

    public GameLogic(int player1, int player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    public int getWinnerSinglePlayer() {
        // Return 0 = Tie, Return 1 = player1 wins, Return 2 = player2 wins
        if (player1 == player2) {
            return 0;
        } else if (player1 == 0 && player2 == 1) {
            return 2;
        } else if (player1 == 0 && player2 == 2) {
            return 1;
        } else if (player1 == 1 && player2 == 0) {
            return 1;
        } else if (player1 == 1 && player2 == 2) {
            return 2;
        } else if (player1 == 2 && player2 == 0) {
            return 2;
        } else if (player1 == 2 && player2 == 1) {
            return 1;
        } else {
            return 0;
        }
    }

    public int getWinner() {
        // Return 0 = Tie, Return 1 = player1 wins, Return 2 = player2 wins
        if (player1 == player2) {
            return 0;
        } else if (player1 == 1 && player2 == 2) {
            return 2;
        } else if (player1 == 1 && player2 == 3) {
            return 1;
        } else if (player1 == 2 && player2 == 1) {
            return 1;
        } else if (player1 == 2 && player2 == 3) {
            return 2;
        } else if (player1 == 3 && player2 == 1) {
            return 2;
        } else if (player1 == 3 && player2 == 2) {
            return 1;
        } else {
            return 0;
        }
    }

}
