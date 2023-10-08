import org.eclipse.paho.client.mqttv3.MqttException;

public class PlayerStatus {
    private int playerID;
    private int rockPaperScissor;
    private String name;
    private int score;
    private mqttPlayer mqttPlayer;

    public PlayerStatus(int playerID, boolean isMqttPlayer) throws MqttException {
        this.playerID = playerID;

        if (isMqttPlayer) {
            mqttPlayer = new mqttPlayer(playerID);
        }
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

    public void setScore(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public mqttPlayer mqttPlayer() {
        return mqttPlayer;
    }

}
