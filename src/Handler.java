import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class Handler {
    private int countPlayerID = 0;
    private List<PlayerStatus> players;
    private GUI gui;

    public Handler(){
        players = new ArrayList<>();
        this.gui = new GUI();
        this.gui.setHandler(this);
        this.gui.window();
        mutliPlayerWait();  
    }
    
    // Additional methods for single player or other game modes...
    public void singlePlayer() {
        // Creating objects for 1 player & 1 computer - passing them to GUI
        PlayerStatus player1 = new PlayerStatus(1, false, false);
        PlayerStatus player2 = new PlayerStatus(2, false, true);
        setPlayersGUI(player1, player2);
    }

    private void mutliPlayerWait(){
        /* 
         * This throws errors that you dont own the thread
         * if you dont do this sync thingies
         * Legit no clue what it does but it removes the error
         */
        synchronized (this){
            try{
                do{
                    wait(100);
                }while(gui.gameHandlerTesting == 0);
            } catch (InterruptedException e){
                System.out.println(e);
            }   
        }

        if (gui.gameHandlerTesting == 1){
            singlePlayer();
        }
        else if(gui.gameHandlerTesting == 2){
            multiPlayer();
        }

    }

    public void multiPlayer(){
        // [Adjustable] MAX_PLAYERS could be dynamic based on a GUI interaction or game setup stage
        final int MAX_PLAYERS = 2; 
        final String displayMessage = "Push any button to play!";
        final String[] countDownMsg = {"3", "2", "1", "Rock, Paper, Scissors!"};
        System.out.println("Multiplayer func running");

        boolean playing = true;
        
        while (playing) {
            try {
                // Will only be run first iteration as contion of Max players will be met.
                waitForMultiPlayers(MAX_PLAYERS, displayMessage); 

                PlayerStatus player1 = players.get(0);
                PlayerStatus player2 = players.get(1);

                gui.gotBothPlayersRobbanFix();

                while ((player1.getScore() < 3) || (player2.getScore() < 3)) {
                    MqttPlayer.countDownAndThrow(countDownMsg);
                
                    player1.setPlayerMove(player1.mqttPlayer().getMove());
                    player2.setPlayerMove(player2.mqttPlayer().getMove());

                    gui.scenario();
                    GameLogic gameLogic = new GameLogic(player1, player2);
                    MqttPlayer.displayGameResult(gameLogic.printMultiplayerWinner(gameLogic.getWinner()));

                    // ADD SLEEP HERE? 
                }

                playing = (playAgain());

            } catch (MqttException e) {
                System.out.println("MQTT Error: " + e.getMessage());
            } catch (InterruptedException e) {
                System.out.println("Interupted Error: " + e.getMessage());
            }
        }

        disconnectMultiPlayers();
        
    }

    private void setPlayersGUI(PlayerStatus player1, PlayerStatus player2) {
        gui.player1 = player1;
        gui.player2 = player2;
    }

    private void waitForMultiPlayers(int MAX_PLAYERS, String displayMessage) throws MqttException, InterruptedException {
        // This could be used in order to play tournament. For only 2 multiplayers this can be rewritten.
        while (countPlayerID < MAX_PLAYERS) {
            countPlayerID++;
            PlayerStatus currentPlayer = new PlayerStatus(countPlayerID, true, false);
            currentPlayer.mqttPlayer().askToPlay(displayMessage, countPlayerID);
            currentPlayer.mqttPlayer().getMove();
            players.add(currentPlayer);
            gui.setPlayer(currentPlayer);
            gui.currentPlayers();
        }
    }

    private boolean playAgain() throws MqttException, InterruptedException {
        for (PlayerStatus player : players) {
            String playAgainMsg = "Enter 1 to play again!";
            player.mqttPlayer().askToPlayAgain(playAgainMsg);
            player.setPlayerMove(player.mqttPlayer().getMove());
        }

        if ((players.get(0).getPlayerMove() == 1) && (players.get(0).getPlayerMove() == 1)) {
            return true;
        } else {
            return false;
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
