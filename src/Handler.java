import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Handler {
    private boolean runGame = true;
    private int countPlayerID = 0;
    private List<PlayerStatus> players;

    public Handler() throws MqttException, InterruptedException {
        while (runGame) {
            GUISimple.window();
            // Add GUI interactions and other pre-game setup here...

            singlePlayer();
            multiPlayer();
            Thread.sleep(5000); // SLEEP FOR 5 SECONDS BEFORE RUNNING GAME AGAIN
        }
    }
    
    // Additional methods for single player or other game modes...
    public void singlePlayer() throws MqttException {
        // Implement single player game mode
        PlayerStatus player1 = new PlayerStatus(1, false, false);
        PlayerStatus player2 = new PlayerStatus(2, false, true);

        GUISimple.player1 = player1;
        GUISimple.player2 = player2;
    }

    public void multiPlayer() throws MqttException, InterruptedException {
        // [Adjustable] MAX_PLAYERS could be dynamic based on a GUI interaction or game setup stage
        final int MAX_PLAYERS = 4; 
        String displayMessage = "Push any button to play!";
        String[] countDownMsg = {"3", "2", "1", "Rock, Paper, Scissors!"};
        boolean waitForPlayers = true;
        players = new ArrayList<>();

        while (waitForPlayers && countPlayerID < MAX_PLAYERS) {
            countPlayerID++;
            PlayerStatus currentPlayer = new PlayerStatus(countPlayerID, true, false);
            players.add(currentPlayer);
            currentPlayer.mqttPlayer().askToPlay(displayMessage, countPlayerID);
            currentPlayer.mqttPlayer().getMove();
        }

        // TOURNAMENT LOGIC HERE...
        TournamentTree tournamentTree = new TournamentTree(players);
        
        while (true) {
            TournamentTree.Node nextMatch = tournamentTree.nextGame();
            MqttPlayer.displayNextMatch(nextMatch);

            if (nextMatch == null) {
                System.out.println("Tournament over!");
                break;
            }

            PlayerStatus player1 = nextMatch.player1;
            PlayerStatus player2 = nextMatch.player2;

            MqttPlayer.countDownAndThrow(countDownMsg);

            player1.setPlayerMove(player1.mqttPlayer().getMove());
            player2.setPlayerMove(player2.mqttPlayer().getMove());

            GameLogic gameLogic = new GameLogic(player1.getPlayerMove(), player2.getPlayerMove());
            int winner = gameLogic.getWinner();

            PlayerStatus winningPlayer = null;
            if (winner == 1) {
                MqttPlayer.displayGameResult("Player 1 won!");
                winningPlayer = player1;
            } else if (winner == 2) {
                MqttPlayer.displayGameResult("Player 2 won!");
                winningPlayer = player2;
            } else {
                MqttPlayer.displayGameResult("Tie!");
                continue;
            }

            tournamentTree.reportMatchResult(nextMatch, winningPlayer);
        }

        for (PlayerStatus currentPlayer : players) {
            currentPlayer.mqttPlayer().disconnect();
        }

            
    }
/* 
    public List<PlayerStatus> getPlayers(){
        return players;
    }
*/

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

    
       



