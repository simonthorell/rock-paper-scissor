import java.util.Random;

import org.eclipse.paho.client.mqttv3.MqttException;

public class ArduinoPlayerStatus {

    //Dirty hack because I cant use #define in java, costs us 3 bytes of memory
    private final byte iscomputer = 1;
    private final byte ismqttPlayer = 2;
    private final byte isarduinoPlayer = 3;


    private int playerID;
    private int playerMove;
    private String name;
    private int score;
    private int playerType;
    private MqttPlayer mqttPlayer;
    private ArduinoMQTT arduinoPlayer;
    private Random rnd;

    public ArduinoPlayerStatus(int playerID, int playerType) throws MqttException {
        this.playerID = playerID;
        switch(playerType)
        {
            case iscomputer:
                this.playerType = playerType;
                this.name = "Computer";
                rnd = new Random();
                this.playerType = playerType;
                break;

            case ismqttPlayer:
                mqttPlayer = new MqttPlayer(playerID);
                this.playerType = playerType;
                break;
            
            case isarduinoPlayer:
                arduinoPlayer = new ArduinoMQTT(playerID, "ArduinoPlayer");
                this.playerType = playerType;
                break;
        }       
    }

    public int getPlayerID(){
        return this.playerID;
    }

    // 1 = rock, 2 = paper, 3 = scissor
    public void setPlayerMove(int playerMove){
        this.playerMove = playerMove;
    }

    // 1 = rock, 2 = paper, 3 = scissor
    public int getPlayerMove(){
        return this.playerMove;
    }

    public int setComputerMove(){
        this.playerMove = rnd.nextInt(3);
        return this.playerMove;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return this.name != null ? this.name : "";
    }

    public void setScore(int score){
        this.score = score;
    }

    public int getScore(){
        return this.score;
    }

    /* 
     * 1 = computer
     * 2 = MqttPlayer
     * 3 = ArduinoMQTT
     */
    public int getPlayerType(){
        return this.playerType;
    }

    public MqttPlayer mqttPlayer(){
        return this.mqttPlayer;
    }

    public ArduinoMQTT arduinoMQTT(){
        return this.arduinoPlayer;
    }
}
