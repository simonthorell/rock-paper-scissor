import java.util.Random;

public class ComputerStatus {
    private int playerID;
    private int playerMove;
    private String name;
    private int score;

    public ComputerStatus(int playerID) {
        this.playerID = playerID;
        this.playerMove = setPlayerMove();
        this.name = "Computer";
    }

    public int getPlayerID() {
        return playerID;
    }

    private int setPlayerMove() {
        // generate random number between 1 and 3
        // 1 = rock, 2 = paper, 3 = scissor
        // set this.rockPaperScissor to the random number
        Random random = new Random();
        return random.nextInt(3) + 1;
    }

    public int getPlayerMove() {
        // 1 = rock, 2 = paper, 3 = scissor
        return playerMove;
    }

    public String getName() {
        return name;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }

}
