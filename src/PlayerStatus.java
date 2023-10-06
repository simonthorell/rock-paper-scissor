public class PlayerStatus {
    private int playerID;
    private int rockPaperScissor;
    private int score;

    public PlayerStatus(int playerID) {
        this.playerID = playerID;
    }

    public void setRockPaperScissor(int rockPaperScissor) {
        // generate random number between 1 and 3
        // 1 = rock, 2 = paper, 3 = scissor
        // set this.rockPaperScissor to the random number
        this.rockPaperScissor = rockPaperScissor;
    }

}
