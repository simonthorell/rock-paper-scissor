public class Handler {
    boolean runGame = true;
    int CountPlayerID = 0;

    public Handler() {
        while(runGame) {
            // run gameMenuGUIjavaClass();
            // this GUI class should return a boolean value = false when pushing a button to exit the game. 
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


}
