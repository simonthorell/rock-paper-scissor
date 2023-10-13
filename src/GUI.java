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

    private static JButton rockButton;
    private static JButton paperButton;
    private static JButton scissorButton;

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

    public static void window(){

        // Displaying parts consisting of JFrame, JPanels, JLabels, JButtons.
        frame = new JFrame();
            frame.setSize(800, 640);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);
            frame.setVisible(true);

            
            JLayeredPane bottomContainer = new JLayeredPane();

                JPanel menuLayer = new JPanel(new GridLayout(3, 1));
                    menuLayer.setBounds(250, 150, 200, 200);
                    menuLayer.setBackground(Color.GRAY);
                    menuLayer.setVisible(true);

                    JLabel singleMulti = new JLabel("<html><h3>Choose Game Setting!</h3></html>");
                    JButton singlePlayerButton = new JButton("<html><h2>SINGLEPLAYER</h2></html>");
                    JButton multiPlayerButton = new JButton("<html><h2>MULTIPLAYER</h2></html>");

                
                JPanel container = new JPanel(new GridLayout(2, 1));
                    container.setSize(800, 600);

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
                                playerName = new JLabel("<html><h1>PLAYER</h1></html>");

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
                                cpuName = new JLabel("<html><h1>COMPUTER</h1></html>");


        // This is where comopnents is added to eachother.         
        frame.add(bottomContainer);

            bottomContainer.add(container, JLayeredPane.DEFAULT_LAYER);
            bottomContainer.add(menuLayer, JLayeredPane.PALETTE_LAYER);

                menuLayer.add(singleMulti);
                menuLayer.add(singlePlayerButton);
                menuLayer.add(multiPlayerButton);

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
            menuLayer.setVisible(false);
            rockButton.setEnabled(true);
            paperButton.setEnabled(true);
            scissorButton.setEnabled(true);
         });
         multiPlayerButton.addActionListener((ActionEvent e) -> {
            menuLayer.setVisible(false);
         });
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

    // method that nulls/resets all varibles after a game is finnishes 
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
        GameLogic gameLogic = new GameLogic(player1.getPlayerMove() + 1, player2.getPlayerMove() + 1);
        startDisplayAction(player1.getPlayerMove(), player2.getPlayerMove(), gameLogic.getWinner());
    }

}