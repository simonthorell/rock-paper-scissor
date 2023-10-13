import java.util.Random;

import org.eclipse.paho.client.mqttv3.MqttException;

public class PlayerStatus {
    private int playerID;
    private int playerMove;
    private String name;
    private int score;
    private MqttPlayer mqttPlayer;

    public PlayerStatus(int playerID, boolean isMqttPlayer, boolean isComputer) throws MqttException {
        this.playerID = playerID;

        if (isMqttPlayer) {
            mqttPlayer = new MqttPlayer(playerID);
        }

        if (isComputer) {
            setComputerMove();
            this.name = "Computer";
        } else {
            this.name = "Player " + playerID;
        }
    }

    public PlayerStatus (int playerID){
        this.playerID = playerID;
    }

    public int getPlayerID() {
        return playerID;
    }

    public void setPlayerMove(int playerMove) {
        // 1 = rock, 2 = paper, 3 = scissor
        this.playerMove = playerMove;
    }

    public int getPlayerMove() {
        // 1 = rock, 2 = paper, 3 = scissor
        return playerMove;
    }

    public int setComputerMove() {
        Random random = new Random();
        this.playerMove = random.nextInt(3);
        // this.playerMove = random.nextInt(3) + 3;
        return playerMove;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        if (name != null) {
            return name;
        } else {
            return "player1";
        }
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public MqttPlayer mqttPlayer() {
        return mqttPlayer;
    }

}
