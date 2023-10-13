import java.util.Random;

import org.eclipse.paho.client.mqttv3.MqttException;

public class ArduinoPlayerStatus {

    //Dirty hack because I cant use #define in java, costs us 3 bytes of memory
    private final static byte isComputer = 1;
    private final static byte isMqttPlayer = 2;
    private final static byte isArduinoPlayer = 3;
    //Lets us do this
    //ArduinoPlayerStatus player1 = new PlayerStatus(1, ArduinoPlayerStatus.isComputer);


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
            case isComputer:
                this.playerType = playerType;
                this.name = "Computer";
                rnd = new Random();
                this.playerType = playerType;
                break;

            case isMqttPlayer:
                mqttPlayer = new MqttPlayer(playerID);
                this.playerType = playerType;
                break;
            
            case isArduinoPlayer:
                arduinoPlayer = new ArduinoMQTT(playerID, "ArduinoPlayer");
                this.playerType = playerType;
                break;

            default:
                this.name = "Player " + playerID;
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
