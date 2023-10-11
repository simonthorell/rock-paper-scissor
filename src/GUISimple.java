import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GUISimple{

    private static JFrame frame;
    private static JPanel container;
    private static JPanel top;
    private static JPanel topLeft;
    private static JLabel playerHand;
    private static JPanel topMiddle;
    private static JPanel topMiddleTop;
    private static JPanel topMiddleLeft;
    private static JLabel playerScore;
    private static JPanel topMiddleMiddle;
    private static JLabel scoreName;
    private static JPanel topMiddleRight;
    private static JLabel cpuScore;
    private static JPanel topMiddleBottom;
    private static JLabel draw;
    private static JPanel topRight;
    private static JLabel cpuHand;
    private static JPanel bottom;
    private static JPanel bottomLeft;
    private static JPanel bottomLeftTop;
    private static JLabel playerWin;
    private static JPanel bottomLeftBottom;
    private static JLabel playerName;
    private static JPanel bottomMiddle;
    private static JButton rockButton;
    private static JButton paperButton;
    private static JButton scissorButton;
    private static JPanel bottomRight;
    private static JPanel bottomRightTop;
    private static JLabel cpuWin;
    private static JPanel bottomRightBottom;
    private static JLabel cpuName;

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

        frame = new JFrame();
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);

        container = new JPanel(new GridLayout(2, 1));
        container.setSize(800, 600);

        top = new JPanel(new GridLayout(1,3));
        top.setBackground(Color.WHITE);
        top.setSize(800, 300);

        topLeft = new JPanel(new BorderLayout());
        topLeft.setSize(800/3, 300);
        topLeft.setAlignmentX(0);
        topLeft.setBackground(Color.WHITE);
        topLeft.setVisible(true);

        playerHand = new JLabel();


        topMiddle = new JPanel(new GridLayout(2, 1));
        topMiddle.setSize(800/3, 300);
        topMiddle.setAlignmentX(800/3);
        topMiddle.setBackground(Color.WHITE);
        topMiddle.setVisible(true);

        topMiddleTop = new JPanel(new GridLayout(1, 3));
        topMiddleTop.setBackground(Color.WHITE);

        topMiddleLeft = new JPanel();
        topMiddleLeft.setBackground(Color.WHITE);
        playerScore = new JLabel("<html><h1>"+pScore+"</h1></html>");

        topMiddleMiddle = new JPanel();
        topMiddleMiddle.setBackground(Color.WHITE);
        scoreName = new JLabel("<html><h2><u>SCORES</u></h2></html>");

        topMiddleRight = new JPanel();
        topMiddleRight.setBackground(Color.WHITE);
        cpuScore = new JLabel("<html><h1>"+cScore+"</h1></html>");

        topMiddleBottom = new JPanel();
        topMiddleBottom.setBackground(Color.WHITE);
        draw = new JLabel("");
        
        topRight = new JPanel(new BorderLayout());
        topRight.setSize(800/3, 300);
        topRight.setAlignmentX(800/3*2);
        topRight.setBackground(Color.WHITE);
        topRight.setVisible(true);

        cpuHand = new JLabel();

        bottom = new JPanel(new GridLayout(1, 3));
        bottom.setSize(800,300);
        bottom.setVisible(true);

        bottomLeft = new JPanel(new GridLayout(2, 1));
        bottomLeft.setSize(300, 300);
        bottomLeft.setBackground(Color.GRAY);
        bottomLeft.setVisible(true);

        bottomLeftTop = new JPanel();
        playerWin = new JLabel(" "); 
        

        bottomLeftBottom = new JPanel();
        playerName = new JLabel("<html><h1>PLAYER</h1></html>");

        bottomMiddle = new JPanel(new GridLayout(3, 1));
        rockButton = new JButton("<html><h1>ROCK</h1></html>");
        paperButton = new JButton("<html><h1>PAPER</h1></html>");
        scissorButton = new JButton("<html><h1>SCISSOR</h1></html>");

        bottomRight = new JPanel(new GridLayout(2, 1));
        bottomRight.setSize(300, 300);
        bottomRight.setBackground(Color.GRAY);
        bottomRight.setVisible(true);

        bottomRightTop = new JPanel();
        cpuWin = new JLabel(""); 

        bottomRightBottom = new JPanel();
        cpuName = new JLabel("<html><h1>COMPUTER</h1></html>");

        frame.add(container);
        container.add(top);
        container.add(bottom);
        top.add(topLeft);

        topLeft.add(bottomLeftTop);
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

        rockButton.addActionListener((ActionEvent e) -> {
            // choosenButton(1);
            draw.setText(null);
            playerWin.setText(null);
            cpuWin.setText(null);

            player1.setPlayerMove(1);
            System.out.println(player1.getPlayerMove());
            scenario();
            // Handler.pressedButton(1);
            // Handler.scenario();
        });

        paperButton.addActionListener((ActionEvent e) -> {
            // choosenButton(2);
            draw.setText(null);
            playerWin.setText(null);
            cpuWin.setText(null);

            player1.setPlayerMove(2);
            System.out.println(player1.getPlayerMove());
            scenario();
            // Handler.pressedButton(2);
            // Handler.scenario();
        });

        scissorButton.addActionListener((ActionEvent e) -> {
            // choosenButton(3);
            draw.setText(null);
            playerWin.setText(null);
            cpuWin.setText(null);

            player1.setPlayerMove(3);
            System.out.println(player1.getPlayerMove());
            scenario();
            // Handler.pressedButton(3);
            // Handler.scenario();
        });
    }


    private static BufferedImage loadImage(String sheetPath) {
        try {
            return ImageIO.read(new File(sheetPath));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

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

    public static void startDisplayAction(int player, int cpu, int wld){
        
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

    private static void winLooseDraw(int wld){
        if(wld == 1){
            draw.setText("<html><h1>DRAW!</h1></html>");  
        }else if(wld == 2){
            playerWin.setText("<html><h1>WIN!</h1></html>");
            pScore++;
            playerScore.setText("<html><h1>"+pScore+"</h1></html>");
            gameFinnished();
        }else if(wld == 3){
            cpuWin.setText("<html><h1>WIN!</h1></html>");
            cScore++;
            cpuScore.setText("<html><h1>"+cScore+"</h1></html>");
            gameFinnished();
        }
    }

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
            }else if(choice == JOptionPane.NO_OPTION){
                System.exit(0);
            }
        }
    }

    public static void scenario() {
        GameLogic gameLogic = new GameLogic(player1.getPlayerMove(), player2.getPlayerMove());
        startDisplayAction(player1.getPlayerMove(), player2.getPlayerMove(), gameLogic.getWinner());

    }

    // public static void scenario(int option){

    //     if (option == 1){ // 1 - 1
    //         rockButton.setEnabled(false);
    //         startDisplayAction(0, 3, 1);     
    //     }

    //     if (option == 2){ // 1 - 2
    //         startDisplayAction(0, 4, 3);
    //     }
        
    //     if (option == 3){ // 1 - 3
    //         startDisplayAction(0, 5, 2);
    //     }
        
    //     if (option == 4){ // 2 - 1
    //         startDisplayAction(1, 3, 2);
    //     }
        
    //     if (option == 5){ // 2 - 2
    //         startDisplayAction(1, 4, 1);
    //     }
        
    //     if (option == 6){ // 2 - 3
    //         startDisplayAction(1, 5, 3);
    //     }
        
    //     if (option == 7){ // 3 - 1
    //         startDisplayAction(2, 3, 3);
    //     }
        
    //     if (option == 8){ // 3 - 2
    //         startDisplayAction(2, 4, 2);
    //     }

    //     if (option == 9){ // 3 - 3
    //         startDisplayAction(2, 5, 1);
    //     }
    
    // }
}