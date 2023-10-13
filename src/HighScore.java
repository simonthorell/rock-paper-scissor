import java.util.List;

public class HighScore {
    
    public static void displayRankOrder(List<PlayerStatus> rankedPlayers){
        int rank = 1;
        for(PlayerStatus player : rankedPlayers){
            System.out.println("Rank " + rank + ": " + player.getPlayerID() + " - Score: " + player.getScore());
        }
    }
}
