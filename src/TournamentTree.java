import java.util.List;

public class TournamentTree {
    private List<PlayerStatus> players;

    public TournamentTree(List<PlayerStatus> players) {
        this.players = players;
    }

    public PlayerStatus[] nextGame() {
        // THIS LOGICS SHOULD ONLY RUN 2 PLAYERS AGAINST EACH OTHER AT A TIME - BELOW IS ONLY FOR TESTING USING FIRST 2 PLAYERS IN LIST.
        PlayerStatus player1 = players.get(0);
        PlayerStatus player2 = players.get(1);
        return new PlayerStatus[] { player1, player2 };
    }

}

