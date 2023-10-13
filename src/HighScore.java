import java.util.ArrayList;
import java.util.List;

public class HighScore {
    
    public static List displayRankOrder(List<PlayerStatus> rankedPlayers){
        
        int rank = 1;
        List<String> ranked = new ArrayList<>();
        
        
        for(PlayerStatus player : rankedPlayers){
            ranked.add ("Rank " + rank + ": " + player.getPlayerID() + " - Score: " + player.getScore());
            System.out.println("Rank " + rank + ": " + player.getPlayerID() + " - Score: " + player.getScore());
        }


        return ranked;




        

        
    }
}
