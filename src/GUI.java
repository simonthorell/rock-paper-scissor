import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GUI{

    private static JFrame frame;

    private static JLabel playerHand;
    private static JLabel cpuHand;
    private static JLabel playerScore;
    private static JLabel cpuScore;
    private static JLabel draw;
    private static JLabel playerWin;
    private static JLabel cpuWin;
    private static JLabel playerName;
    private static JLabel cpuName;
    private static JPanel waiting4players;
    private static JLabel player1Connected;
    private static JLabel player2Connected;
    //private static JLabel highscoreLabel;
    

    private static JButton rockButton;
    private static JButton paperButton;
    private static JButton scissorButton;
    //private static JButton highscoreButton;

    private static int pScore = 0;
    private static int cScore = 0;

    private static BufferedImage playerSheet;
    private static BufferedImage cpuSheet;
    private static int frameWidth;
    private static int frameHeight; 
    private static int pictureIndex = 0;
    private static int totalFrames = 6;
    private static Timer slideTimer;

    private static String[] sheetPaths = {
        "simple-pics/spritesheet-pRock.png",
        "simple-pics/spritesheet-pPaper.png",
        "simple-pics/spritesheet-pScissor.png",
        "simple-pics/spritesheet-cRock.png",
        "simple-pics/spritesheet-cPaper.png",
        "simple-pics/spritesheet-cScissor.png"
    };

    public static PlayerStatus player1;
    public static PlayerStatus player2;

    private static boolean singleplayer = true;

    private static void gameOption() {
        Handler gameOption = new Handler();

        if(singleplayer){
            // Start single player game
            gameOption.singlePlayer();
        } else if(!singleplayer){
            // Start multi player game
            gameOption.multiPlayer();
        }
    }

    public static void window(){

        // Displaying parts consisting of JFrame, JPanels, JLabels, JButtons.
        frame = new JFrame();
            frame.setSize(800, 640);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);
            frame.setVisible(true);

            
            JLayeredPane bottomContainer = new JLayeredPane();
            //final int CUSTOM_LAYER_1 = JLayeredPane.POPUP_LAYER + 2;
            final int CUSTOM_LAYER_2 = JLayeredPane.DEFAULT_LAYER + 2;
            //final int CUSTOM_LAYER_3 = JLayeredPane.PALETTE_LAYER + 4;

                JPanel singleMulti = new JPanel(new GridLayout(3, 3));
                    singleMulti.setBounds(200, 80, 400, 200);
                    singleMulti.setBackground(new Color(82,78,80,90));
                    singleMulti.setVisible(true);

                    JLabel gameopt = new JLabel("<html><h1>Choose Game Option!</h1></html>");
                    gameopt.setHorizontalAlignment(SwingConstants.CENTER);
                    JButton singlePlayerButton = new JButton("<html><h2>SINGLEPLAYER</h2></html>");
                    JButton multiPlayerButton = new JButton("<html><h2>MULTIPLAYER</h2></html>");
                  
                JPanel multiMenu = new JPanel(new GridLayout(4, 1));
                    multiMenu.setBounds(200, 80, 400, 200);
                    multiMenu.setBackground(new Color(82,78,80,90));
                    multiMenu.setVisible(false);

                    JLabel spectHeader = new JLabel("<html><h1>ARE YOU</h1></html>");
                    spectHeader.setHorizontalAlignment(SwingConstants.CENTER);
                    JButton playing = new JButton("<html><h2>PLAYING</h2></html>");
                    JLabel spectMid = new JLabel("<html><h1>OR</h1></html>");
                    spectMid.setHorizontalAlignment(SwingConstants.CENTER);
                    JButton spectating = new JButton("<html><h2>SPECTATING</h2></html>");

                JPanel menuButton = new JPanel();
                    menuButton.setBounds(0, 0, 100, 50);
                    menuButton.setOpaque(true);
                    Font customFont = new Font("Arial", Font.BOLD, 24);

                    JButton menu = new JButton("MENU");
                    menu.setSize(100, 50);
                    menu.setFont(customFont);

                JPanel menuLayer = new JPanel(new GridLayout(4, 1));
                    menuLayer.setBounds(200, 80, 400, 200);
                    menuLayer.setBackground(new Color(82,78,80,90));
                    menuLayer.setVisible(false);

                    JLabel menuText = new JLabel("<html><h1>MENU</h1></html>");
                    menuText.setHorizontalAlignment(SwingConstants.CENTER);
                    gameopt.setHorizontalAlignment(SwingConstants.CENTER);
                    JButton gameOptions = new JButton("<html><h2>GAME OPTIONS</h2></html>");
                    JButton scoreBoard = new JButton("<html><h2>HIGHSCORES</h2></html>");
                    JButton exit = new JButton("<html><h2>EXIT</h2></html>");

                waiting4players = new JPanel(new GridLayout(2, 1));
                    waiting4players.setBounds(0,0 , 800, 600);
                    waiting4players.setVisible(false);

                    JPanel waiting4playersTop = new JPanel(new GridLayout(1, 1));
                        waiting4playersTop.setSize(800,300);
                        waiting4playersTop.setBackground(Color.WHITE);

                        JLabel waitingGif = new JLabel();
                        waitingGif.setHorizontalAlignment(SwingConstants.CENTER);
                        ImageIcon loadGif = new ImageIcon("simple-pics/loading.gif");
                        waitingGif.setIcon(loadGif);

                    JPanel waiting4playersBottom = new JPanel(new GridLayout(1, 3));
                        waiting4playersBottom.setSize(800,300);

                        JLabel waitingText = new JLabel("<html><h2>WAITING FOR PLAYERS...</h2></html>");
                        waitingText.setHorizontalAlignment(SwingConstants.CENTER);
                        player1Connected = new JLabel("test1"); // player1.getName()+"\n CONNECTED!
                        player1Connected.setHorizontalAlignment(SwingConstants.CENTER);
                        player2Connected = new JLabel("test2"); // player2.getName()+"\n CONNECTED!"
                        player2Connected.setHorizontalAlignment(SwingConstants.CENTER);
                /* 
                JPanel highscore = new JPanel(new GridLayout());
                    highscore.setBounds(0, 0, 800, 300);
                    highscore.setBackground(Color.GRAY);
                    highscore.setVisible(false);

                    highscoreButton.setEnabled(true);
                    highscoreButton.setSize(400, 150);

                    */

                JPanel container = new JPanel(new GridLayout(2, 1));
                    container.setSize(800, 600);
                    container.setVisible(false);

                    JPanel top = new JPanel(new GridLayout(1,3));
                        top.setBackground(Color.WHITE);
                        top.setSize(800, 300);

                        JPanel topLeft = new JPanel(new BorderLayout());
                            topLeft.setSize(800/3, 300);
                            topLeft.setAlignmentX(0);
                            topLeft.setBackground(Color.WHITE);
                            topLeft.setVisible(true);

                            playerHand = new JLabel();

                        JPanel topMiddle = new JPanel(new GridLayout(2, 1));
                            topMiddle.setSize(800/3, 300);
                            topMiddle.setAlignmentX(800/3);
                            topMiddle.setBackground(Color.WHITE);
                            topMiddle.setVisible(true);

                            JPanel topMiddleTop = new JPanel(new GridLayout(1, 3));
                                topMiddleTop.setBackground(Color.WHITE);

                            JPanel topMiddleLeft = new JPanel();
                                topMiddleLeft.setBackground(Color.WHITE);
                                playerScore = new JLabel("<html><h1>"+pScore+"</h1></html>");

                            JPanel topMiddleMiddle = new JPanel();
                                topMiddleMiddle.setBackground(Color.WHITE);
                                JLabel scoreName = new JLabel("<html><h2><u>SCORES</u></h2></html>");

                            JPanel topMiddleRight = new JPanel();
                                topMiddleRight.setBackground(Color.WHITE);
                                cpuScore = new JLabel("<html><h1>"+cScore+"</h1></html>");

                            JPanel topMiddleBottom = new JPanel();
                                topMiddleBottom.setBackground(Color.WHITE);
                                draw = new JLabel("");
        
                        JPanel topRight = new JPanel(new BorderLayout());
                            topRight.setSize(800/3, 300);
                            topRight.setAlignmentX(800/3*2);
                            topRight.setBackground(Color.WHITE);
                            topRight.setVisible(true);
                            cpuHand = new JLabel();

                    JPanel bottom = new JPanel(new GridLayout(1, 3));
                        bottom.setSize(800,300);
                        bottom.setVisible(true);

                        JPanel bottomLeft = new JPanel(new GridLayout(2, 1));
                            bottomLeft.setSize(300, 300);
                            bottomLeft.setBackground(Color.GRAY);
                            bottomLeft.setVisible(true);

                            JPanel bottomLeftTop = new JPanel();
                                playerWin = new JLabel(" "); 
        
                            JPanel bottomLeftBottom = new JPanel();
                                playerName = new JLabel();

                        JPanel bottomMiddle = new JPanel(new GridLayout(3, 1));
                            rockButton = new JButton("<html><h1>ROCK</h1></html>");
                                rockButton.setEnabled(false);
                            paperButton = new JButton("<html><h1>PAPER</h1></html>");
                                paperButton.setEnabled(false);
                            scissorButton = new JButton("<html><h1>SCISSOR</h1></html>");
                                scissorButton.setEnabled(false);

                        JPanel bottomRight = new JPanel(new GridLayout(2, 1));
                            bottomRight.setSize(300, 300);
                            bottomRight.setBackground(Color.GRAY);
                            bottomRight.setVisible(true);

                            JPanel bottomRightTop = new JPanel();
                                cpuWin = new JLabel(""); 

                            JPanel bottomRightBottom = new JPanel();
                                cpuName = new JLabel();


        // This is where comoponents are added to eachother.         
        frame.add(bottomContainer);

            bottomContainer.add(container, JLayeredPane.DEFAULT_LAYER);
            bottomContainer.add(waiting4players, CUSTOM_LAYER_2);
            bottomContainer.add(singleMulti, JLayeredPane.PALETTE_LAYER);
            bottomContainer.add(menuLayer, JLayeredPane.DRAG_LAYER);
            bottomContainer.add(multiMenu, JLayeredPane.MODAL_LAYER);
            bottomContainer.add(menuButton, JLayeredPane.POPUP_LAYER);

                    singleMulti.add(gameopt);
                    singleMulti.add(singlePlayerButton);
                    singleMulti.add(multiPlayerButton);

                    multiMenu.add(spectHeader);
                    multiMenu.add(playing);
                    multiMenu.add(spectMid);
                    multiMenu.add(spectating);

                    menuButton.add(menu);

                    menuLayer.add(menuText);
                    menuLayer.add(gameOptions);
                    menuLayer.add(scoreBoard);
                    menuLayer.add(exit);

                    waiting4players.add(waiting4playersTop);
                        waiting4playersTop.add(waitingGif);

                    waiting4players.add(waiting4playersBottom);
                        waiting4playersBottom.add(player1Connected);
                        waiting4playersBottom.add(waitingText);
                        waiting4playersBottom.add(player2Connected);
                    

                container.add(top);
                    top.add(topLeft);
                        topLeft.add(playerHand, BorderLayout.WEST);

                    top.add(topMiddle);
                        topMiddle.add(topMiddleTop);
                            topMiddleTop.add(topMiddleLeft);
                                topMiddleLeft.add(playerScore);
                            topMiddleTop.add(topMiddleMiddle);
                                topMiddleMiddle.add(scoreName);
                            topMiddleTop.add(topMiddleRight);
                                topMiddleRight.add(cpuScore);
                        topMiddle.add(topMiddleBottom);
                            topMiddleBottom.add(draw);

                    top.add(topRight);
                        topRight.add(cpuHand, BorderLayout.EAST);

                container.add(bottom);
                    bottom.add(bottomLeft);
                        bottomLeft.add(bottomLeftTop);
                            bottomLeftTop.add(playerWin);
                        bottomLeft.add(bottomLeftBottom);
                            bottomLeftBottom.add(playerName);

                    bottom.add(bottomMiddle);
                        bottomMiddle.add(rockButton);
                        bottomMiddle.add(paperButton);
                        bottomMiddle.add(scissorButton);

                    bottom.add(bottomRight);
                        bottomRight.add(bottomRightTop);
                            bottomRightTop.add(cpuWin);
                        bottomRight.add(bottomRightBottom);
                            bottomRightBottom.add(cpuName);

        // actionlisteners for the Buttons
        rockButton.addActionListener((ActionEvent e) -> {
            // choosenButton(1);
            draw.setText(null);
            playerWin.setText(null);
            cpuWin.setText(null);
            player1.setPlayerMove(0);
            player2.setComputerMove();
            scenario();
        });

        paperButton.addActionListener((ActionEvent e) -> {
            // choosenButton(2);
            draw.setText(null);
            playerWin.setText(null);
            cpuWin.setText(null);
            player1.setPlayerMove(1);
            player2.setComputerMove();
            scenario();
        });

        scissorButton.addActionListener((ActionEvent e) -> {
            // choosenButton(3);
            draw.setText(null);
            playerWin.setText(null);
            cpuWin.setText(null);
            player1.setPlayerMove(2);
            player2.setComputerMove();
            scenario();
        });

        singlePlayerButton.addActionListener((ActionEvent e) -> {
            singleMulti.setVisible(false);
            container.setVisible(true);
            rockButton.setEnabled(true);
            paperButton.setEnabled(true);
            scissorButton.setEnabled(true);
            gameOption();
            currentPlayers();
         });

        multiPlayerButton.addActionListener((ActionEvent e) -> {
            singleMulti.setVisible(false);
            multiMenu.setVisible(true);
         });

        playing.addActionListener((ActionEvent e) -> {
            multiMenu.setVisible(false);
            waiting4players.setVisible(true);
            int x = JOptionPane.showConfirmDialog(frame, "Do you want to play from your Arduino?", "Arduino or not!", JOptionPane.YES_NO_OPTION);
            if(x == JOptionPane.YES_OPTION){
                gameOption();
                currentPlayers(); 
            }else{
                player1.setName("Robban"); // something to implement?
                singleplayer = false;
                gameOption();
                currentPlayers();
                //player1Connected.setText("KOMIGEN!");
            }
            
            
        });

        spectating.addActionListener((ActionEvent e) -> {
            multiMenu.setVisible(false);
            menuLayer.setVisible(false);
            waiting4players.setVisible(true);
            currentPlayers();
        });

        menu.addActionListener((ActionEvent e) -> {
            if(menuLayer.isVisible()){
               menuLayer.setVisible(false); 
            } else {
                menuLayer.setVisible(true); 
            }
            
        });

        gameOptions.addActionListener((ActionEvent e) -> {
            menuLayer.setVisible(false);
            singleMulti.setVisible(true);
        });

        scoreBoard.addActionListener((ActionEvent e) -> {
            waiting4players.setVisible(false);
            container.setVisible(true);
            menuLayer.setVisible(false);
        });

        exit.addActionListener((ActionEvent e) -> {
            System.exit(0); 
        });

         /*  scoreBoard button and the button below is for the same thing!
         highscoreButton.addActionListener(new ActionListener() {
            HighScore hs = new HighScore();

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    hs.displayRankOrder();
                } catch
            }
        }); */
    }

    // method that checks if the image file exists
    private static BufferedImage loadImage(String sheetPath) {
        try {
            return ImageIO.read(new File(sheetPath));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // method that devides the spritesheets into 6 parts and displays a choosen part.
    private static void displayPictures() {

        if (pictureIndex < totalFrames) {
            // Calculate the x-coordinate to select the current frame from the sprite sheet
            int x = pictureIndex * frameWidth;

            // Select the current frame from the sprite sheet
            BufferedImage currentPlayerFrame = playerSheet.getSubimage(x, 0, frameWidth, frameHeight);
            BufferedImage currentCpuFrame = cpuSheet.getSubimage(x, 0, frameWidth, frameHeight);

            // Update the pictureLabel with the current frame
            ImageIcon iconPlayer = new ImageIcon(currentPlayerFrame);
            ImageIcon iconCpu = new ImageIcon(currentCpuFrame);
            playerHand.setIcon(iconPlayer);
            cpuHand.setIcon(iconCpu);
        }
    }

    // method that tells displayPictures() what to display and how fast/slow it should be diplayed
    public static void startDisplayAction(int player, int cpu, int wld){
        cpu = cpu + 3;

        rockButton.setEnabled(false);
        paperButton.setEnabled(false);
        scissorButton.setEnabled(false);

        playerSheet = loadImage(sheetPaths[player]);
        cpuSheet = loadImage(sheetPaths[cpu]);

        frameWidth = playerSheet.getWidth() / totalFrames;
        frameHeight = playerSheet.getHeight();

        slideTimer = new Timer(250, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (pictureIndex < totalFrames) {
                    displayPictures();
                    pictureIndex++;
                } else {
                    slideTimer.stop();
                    pictureIndex = 0;
                    winLooseDraw(wld);
                    rockButton.setEnabled(true);
                    paperButton.setEnabled(true);
                    scissorButton.setEnabled(true);
                }
            }
        });

            slideTimer.start();
    }

    //method that decides what text to send in win/loose/draw scenarios
    private static void winLooseDraw(int wld){
        if(wld == 0){
            draw.setText("<html><h1>DRAW!</h1></html>");  
        }else if(wld == 1){
            playerWin.setText("<html><h1>WIN!</h1></html>");
            pScore++;
            playerScore.setText("<html><h1>"+pScore+"</h1></html>");
            gameFinnished();
        }else if(wld == 2){
            cpuWin.setText("<html><h1>WIN!</h1></html>");
            cScore++;
            cpuScore.setText("<html><h1>"+cScore+"</h1></html>");
            gameFinnished();
        }
    }

    // method that nulls/resets all varibles after a game is finnished
    private static void gameFinnished(){

        int choice;

        if(cScore == 3){
            choice = JOptionPane.showConfirmDialog(frame, "You just lost to the Computer, buhu! \n Do you want to play again?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if(choice == JOptionPane.YES_OPTION){
                pScore = 0;
                cScore = 0;
                playerScore.setText("<html><h1>"+pScore+"</h1></html>");
                cpuScore.setText("<html><h1>"+cScore+"</h1></html>");
                playerHand.setIcon(null);
                cpuHand.setIcon(null);
                playerWin.setText(null);
                cpuWin.setText(null);
            }else if(choice == JOptionPane.NO_OPTION){
                System.exit(0);
            }
        }else if(pScore == 3){
            choice = JOptionPane.showConfirmDialog(frame, "You just Won the Game, jippi! \n Do you want to play again?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if(choice == JOptionPane.YES_OPTION){
                pScore = 0;
                cScore = 0;
                playerScore.setText("<html><h1>"+pScore+"</h1></html>");
                cpuScore.setText("<html><h1>"+cScore+"</h1></html>");
                playerHand.setIcon(null);
                cpuHand.setIcon(null);
                playerWin.setText(null);
                cpuWin.setText(null);
            }else if(choice == JOptionPane.NO_OPTION){
                System.exit(0);
            }
        }
    }

    public static void scenario() {
        GameLogic gameLogic = new GameLogic(player1.getPlayerMove(), player2.getPlayerMove());
        startDisplayAction(player1.getPlayerMove(), player2.getPlayerMove(), gameLogic.getWinner());
    }

    public static void currentPlayers(){
        if(singleplayer){
            playerName.setText("<html><h1>"+player1.getName()+"</h1></html>");
            cpuName.setText("<html><h1>"+player2.getName()+"</h1></html>");
        }else if(!singleplayer){
            
                if(player1.getName() != null){
                    player1Connected.setText("<html><h2>"+ player1.getName()+"<br>IS CONNECTED!</h2></html>");
                    playerName.setText("<html><h1>"+player1.getName()+"</h1></html>");
                } else if(player2.getName() != null){
                    player2Connected.setText("<html><h2>" + player2.getName() + "<br>IS CONNECTED!</h2></html>");
                    cpuName.setText("<html><h1>"+player2.getName()+"</h1></html>");
                } else if(player1.getName() != null && player2.getName() != null){
                    waiting4players.setVisible(false);
                    
                }
            
        }
        
        
    }

}