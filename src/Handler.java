import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.ArrayList;
import java.util.Collections;

import java.util.List;

public class Handler {
    private int countPlayerID = 0;
    private List<PlayerStatus> players;

    public Handler() throws MqttException, InterruptedException {
        GUI.window();
        singlePlayer();
        multiPlayer();
    }
    
    // Additional methods for single player or other game modes...
    public void singlePlayer() throws MqttException {
        // Implement single player game mode
        players = new ArrayList<>();
        PlayerStatus player1 = new PlayerStatus(1, false, false);
        PlayerStatus player2 = new PlayerStatus(2, false, true);
        GUI.player1 = player1; // Passing player 1 object to GUI
        GUI.player2 = player2; // Passing player 2 object to GUI
    }

    public void multiPlayer() throws MqttException, InterruptedException {
        // [Adjustable] MAX_PLAYERS could be dynamic based on a GUI interaction or game setup stage
        final int MAX_PLAYERS = 2; 
        final String displayMessage = "Push any button to play!";
        final String[] countDownMsg = {"3", "2", "1", "Rock, Paper, Scissors!"};
        players = new ArrayList<>();

        // This could be used in order to play tournament. For only 2 multiplayers this can be rewritten.
        while (countPlayerID < MAX_PLAYERS) {
            countPlayerID++;
            PlayerStatus currentPlayer = new PlayerStatus(countPlayerID, true, false);
            players.add(currentPlayer);
            currentPlayer.mqttPlayer().askToPlay(displayMessage, countPlayerID);
            currentPlayer.mqttPlayer().getMove();
        }

        PlayerStatus player1 = players.get(0);
        PlayerStatus player2 = players.get(1);
        GUI.player1 = player1; // Passing player 1 object to GUI
        GUI.player2 = player2; // Passing player 2 object to GUI

        MqttPlayer.countDownAndThrow(countDownMsg);
        player1.setPlayerMove(player1.mqttPlayer().getMove());
        player2.setPlayerMove(player2.mqttPlayer().getMove());

        GUI.scenario();

        GameLogic gameLogic = new GameLogic(player1.getPlayerMove(), player2.getPlayerMove());
        MqttPlayer.displayGameResult(gameLogic.printMultiplayerWinner(gameLogic.getWinner()));

        for (PlayerStatus currentPlayer : players) {
            currentPlayer.mqttPlayer().disconnect();
        }
    }

    public List<PlayerStatus> getRankedPlayers() {
        Collections.sort(players, (player1, player2) -> {
            return Integer.compare(player2.getScore(), player1.getScore());
         });
        return players;
    }

    public void updatedScoreBoard() throws MqttException, InterruptedException{
       Handler handler = new Handler();
       
        List<PlayerStatus> rankedPlayers = handler.getRankedPlayers();
        HighScore.displayRankOrder(rankedPlayers);
    }

}
