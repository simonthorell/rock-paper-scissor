public class GameLogic {
    private PlayerStatus player1;
    private PlayerStatus player2;

    public GameLogic(PlayerStatus player1, PlayerStatus player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    public int getWinner() {
        // Return 0 = Tie, Return 1 = player1 wins, Return 2 = player2 wins
        if (player1.getScore() == player2.getScore()) {
            return 0;
        } else if (player1.getPlayerMove() == 0 && player2.getPlayerMove() == 1) {
            player2.setScore(player2.getScore() + 1);
            return 2;
        } else if (player1.getPlayerMove() == 0 && player2.getPlayerMove() == 2) {
            player1.setScore(player1.getScore() + 1);
            return 1;
        } else if (player1.getPlayerMove() == 1 && player2.getPlayerMove() == 0) {
            player1.setScore(player1.getScore() + 1);
            return 1;
        } else if (player1.getPlayerMove() == 1 && player2.getPlayerMove() == 2) {
            player2.setScore(player2.getScore() + 1);
            return 2;
        } else if (player1.getPlayerMove() == 2 && player2.getPlayerMove() == 0) {
            player2.setScore(player2.getScore() + 1);
            return 2;
        } else if (player1.getPlayerMove() == 2 && player2.getPlayerMove() == 1) {
            player1.setScore(player1.getScore() + 1);
            return 1;
        } else {
            return 0;
        }
    }

    public String printMultiplayerWinner(int winner) {
        if (winner == 1) {
            return "Player 1 won!";
        } else if (winner == 2) {
            return "Player 2 won!";
        } else {
            return "Tie!";
        }
    }   

}
