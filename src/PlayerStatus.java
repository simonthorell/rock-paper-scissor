public class PlayerStatus {
    private int playerID;
    private int rockPaperScissor;
    private String name;
    private int score;

    public PlayerStatus(int playerID) {
        this.playerID = playerID;
    }

    public int getPlayerID() {
        return playerID;
    }

    public void setRockPaperScissor(int rockPaperScissor) {
        // 1 = rock, 2 = paper, 3 = scissor
        this.rockPaperScissor = rockPaperScissor;
    }

    public int getRockPaperScissor() {
        // 1 = rock, 2 = paper, 3 = scissor
        return rockPaperScissor;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setScore(String score) {
        this.name = score;
    }

    public int getScore() {
        return score;
    }

}
