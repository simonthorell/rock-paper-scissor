import java.util.ArrayList;
import java.util.List;
import org.eclipse.paho.client.mqttv3.MqttException;

public class Handler {
    private boolean runGame = true;
    private int countPlayerID = 0;
    private List<PlayerStatus> players;

    public Handler() throws MqttException, InterruptedException {
        while(runGame) {
            // run gameMenuGUIjavaClass();
            // this GUI class should return a boolean value = false when pushing a button to exit the game.     
        
            // FOR TESTING OF ARDUINO MULTIPLAYER
            multiPlayer();
            Thread.sleep(5000); // SLEEP FOR 5 SECONDS BEFORE RUNNING GAME AGAIN
        }
    }

    public void singlePlayer() {
        // run singlePlayerGUIjavaClass();
        // create one new instance of PlayerStatus and one new instance of ComputerStatus.
        // CountPlayerID should be incremented for each player in order to have unique object ID's. 
    }

    public void multiPlayer() throws MqttException, InterruptedException {
        // run multiPlayerGUIjavaClass();
        final int MAX_PLAYERS = 2;     // Should be set to higher amount for tournament option. 
        String displayMessage = "Push any button to play!";
        String[] countDownMsg = {"3", "2", "1", "Rock, Paper, Scissors!"};
        boolean waitForPlayers = true; // Should be set to false from GUI when all players are ready to play.
        players = new ArrayList<>();

        while (waitForPlayers == true && countPlayerID < MAX_PLAYERS) {
            countPlayerID++;
            PlayerStatus currentPlayer = new PlayerStatus(countPlayerID, true);
            players.add(currentPlayer);
            currentPlayer.arduino().askToPlay(displayMessage); // Send msg as object with msg and next available player ID/topic
            currentPlayer.arduino().getMove(); // Get move from player
        }

        // TOURNAMENT TREE GAME LOGIC HERE

        // THIS LOGICS SHOULD ONLY RUN 2 PLAYERS AGAINST EACH OTHER AT A TIME - BELOW IS ONLY FOR TESTING USING FIRST 2 PLAYERS IN LIST. 
        PlayerStatus player1 = players.get(0);
        PlayerStatus player2 = players.get(1);

        ArduinoMQTT.countDownAndThrow(countDownMsg);

        player1.setRockPaperScissor(player1.arduino().getMove());
        player2.setRockPaperScissor(player2.arduino().getMove());

        GameLogic gameLogic = new GameLogic(player1.getRockPaperScissor(), player2.getRockPaperScissor());
        int winner = gameLogic.getWinner();

        if (winner == 0) {
            ArduinoMQTT.displayGameResult("Tie!");
        } else if (winner == 1) {
            ArduinoMQTT.displayGameResult("Player 1 won!");
        } else if (winner == 2) {
            ArduinoMQTT.displayGameResult("Player 2 won!");
        } else {
            ArduinoMQTT.displayGameResult("Error!");
        }

        for (PlayerStatus currentPlayer : players) {
            currentPlayer.arduino().disconnect();
        }
    }

}
