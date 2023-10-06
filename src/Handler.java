import org.eclipse.paho.client.mqttv3.MqttException;

public class Handler {
    boolean runGame = true;
    int CountPlayerID = 0;

    public Handler() throws MqttException, InterruptedException {
        while(runGame) {
            // run gameMenuGUIjavaClass();
            // this GUI class should return a boolean value = false when pushing a button to exit the game.
            
            // FOR TESTING OF ARDUINO MULTIPLAYER ONLY
            testMQTT();
        }
    }

    public void singlePlayer() {
        // run singlePlayerGUIjavaClass();
        // create one new instance of PlayerStatus and one new instance of ComputerStatus.
        // CountPlayerID should be incremented for each player in order to have unique object ID's. 
    }

    public void multiPlayer() {
        // run multiPlayerGUIjavaClass();
        // create 2 new instances of PlayerStatus. For tournament option, unlimited amount of players could be created.
        // CountPlayerID should be incremented for each player in order to have unique object ID's. 
    }

    public void highScores() {
        // Should we maybe add a high score option to the menu ? 
        // run highScoresrGUIjavaClass();
    }

    private void testMQTT() throws MqttException, InterruptedException{
        // ONLY FOR TESTING ARDUINO MQTT SCRIPT
        // MQTT Host: 1c87c890092b4b9aaa4e1ca5a02dfc9e.s1.eu.hivemq.cloud
        // MQTT Port: 8883
        // MQTT Username: W-bot
        // MQTT Password: W-bot123!        

        int playerID = 2;
        String displayMessage = "Push any button to play!";
        String[] countDownMsg = {"3", "2", "1", "Rock, Paper, Scissors!"};

        ArduinoMQTT onlinePlayer1 = new ArduinoMQTT(playerID);
        
        onlinePlayer1.askToPlay(displayMessage);
        onlinePlayer1.countDownAndThrow(countDownMsg);
        int arduinoMove = onlinePlayer1.getArduinoMove();
        System.out.println("Arduino move: " + arduinoMove);
    }


}
