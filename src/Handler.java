import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class Handler {
    private int countPlayerID = 0;
    private List<PlayerStatus> players;

    public Handler() {
        players = new ArrayList<>();
    }
    
    // Additional methods for single player or other game modes...
    public void singlePlayer() {
        // Creating objects for 1 player & 1 computer - passing them to GUI
        PlayerStatus player1 = new PlayerStatus(1, false, false);
        PlayerStatus player2 = new PlayerStatus(2, false, true);
        setPlayersGUI(player1, player2);
    }

    public void multiPlayer() {
        // [Adjustable] MAX_PLAYERS could be dynamic based on a GUI interaction or game setup stage
        final int MAX_PLAYERS = 2; 
        final String displayMessage = "Push any button to play!";
        final String[] countDownMsg = {"3", "2", "1", "Rock, Paper, Scissors!"};

        try {
            waitForMultiPlayers(MAX_PLAYERS, displayMessage);

            PlayerStatus player1 = players.get(0);
            PlayerStatus player2 = players.get(1);
            setPlayersGUI(player1, player2);

            MqttPlayer.countDownAndThrow(countDownMsg);
            
            player1.setPlayerMove(player1.mqttPlayer().getMove());
            player2.setPlayerMove(player2.mqttPlayer().getMove());

            GUI.scenario();
            GameLogic gameLogic = new GameLogic(player1.getPlayerMove(), player2.getPlayerMove());
            MqttPlayer.displayGameResult(gameLogic.printMultiplayerWinner(gameLogic.getWinner()));

            disconnectMultiPlayers();

        } catch (MqttException e) {
            System.out.println("MQTT Error: " + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("Interupted Error: " + e.getMessage());
        }
    }

    private void setPlayersGUI(PlayerStatus player1, PlayerStatus player2) {
        GUI.player1 = player1;
        GUI.player2 = player2;
    }

    private void waitForMultiPlayers(int MAX_PLAYERS, String displayMessage) throws MqttException, InterruptedException {
        // This could be used in order to play tournament. For only 2 multiplayers this can be rewritten.
        while (countPlayerID < MAX_PLAYERS) {
            countPlayerID++;
            PlayerStatus currentPlayer = new PlayerStatus(countPlayerID, true, false);
            players.add(currentPlayer);
            currentPlayer.mqttPlayer().askToPlay(displayMessage, countPlayerID);
            currentPlayer.mqttPlayer().getMove();
        }
    }

    private void disconnectMultiPlayers() {
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

    public void updatedScoreBoard() throws InterruptedException{
       Handler handler = new Handler();
       
        List<PlayerStatus> rankedPlayers = handler.getRankedPlayers();
        HighScore.displayRankOrder(rankedPlayers);
    }

}
