import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import java.io.File;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.imageio.ImageIO;

public class Wuzi extends JFrame implements MouseListener, Runnable {
    /**
     * Wuzi Game
     */
    private static final long serialVersionUID = 1L;

    // Size of game interface
    int width = Toolkit.getDefaultToolkit().getScreenSize().width;
    int height = Toolkit.getDefaultToolkit().getScreenSize().height;
    // Background Image
    BufferedImage bgImage = null;
    // Save mouse coordinates
    int x, y;
    // Save board,0 refers to no pieces，1 refers to black pieces，2 refers to white pieces
    int[][] allChess = new int[15][15];
    // Save current piece'color，true for black，false for white
    boolean isBlack = true;
    // Check if game is over
    boolean canPlay = true;
    // Save game
    String message = "Black Go First";
    // Save the board
    int[] chessX = new int[255];
    int[] chessY = new int[255];
    int countX, countY;
    // Save maximum time
    int maxTime = 0;
    // insert time(in second)
    int inputTime = 0;
    // Limit game time duration
    String blackMessage = "No limitation";
    String whiteMessage = "No limitation";
    // Save remaining time
    int blackTime = 29;
    int whiteTime = 0;
    // Count down threat
    Thread timer = new Thread(this);

    public Wuzi() {
        this.setTitle("Wuzi Game");
        this.setSize(500, 500);
        this.setLocation((width - 500) / 2, (height - 500) / 2);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.repaint();
        this.addMouseListener(this);
        timer.start();
        timer.suspend();
    }

    
    public void paint(Graphics g) {
    	
         try { 
        	 bgImage = ImageIO.read(new File("/Users/shanzhao/LearnJava/src/wuzibg.jpg")); 
        	 } catch (Exception e) {
        		 e.printStackTrace(); 
         }
         

        // Double buffering
        BufferedImage bi = new BufferedImage(500, 500,
                BufferedImage.TYPE_INT_RGB);
        Graphics g2 = bi.createGraphics();

        g2.drawImage(bgImage, 43, 60, 375, 375, this);
        g2.setColor(new Color(0, 169, 158));

        for (int i = 0; i <= 15; i++) {
            g2.setColor(Color.WHITE);
            g2.drawLine(43, i * 25 + 60, 375 + 43, 60 + i * 25);
            g2.drawLine(i * 25 + 43, 60, 43 + i * 25, 375 + 60);
        }

        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.drawString("Game Message：" + message, 50, 50);
        g2.drawRect(30, 440, 180, 40);
        g2.drawRect(250, 440, 180, 40);
        g2.setFont(new Font("Times New Romans", 0, 12));
        g2.drawString("Time of Black:" + blackMessage, 40, 465);
        g2.drawString("Time of White:" + whiteMessage, 260, 465);
        // Restart
        g2.drawRect(428, 66, 54, 20);
        g2.drawString("Restart", 432, 80);

        // Options
        g2.drawRect(428, 106, 54, 20);
        g2.drawString("Options", 432, 120);

        // States
        g2.drawRect(428, 146, 54, 20);
        g2.drawString("States", 432, 160);

        // Exit
        g2.drawRect(428, 186, 54, 20);
        g2.drawString("Exit", 432, 200);

        // Back to last 
        g2.drawRect(428, 246, 54, 20);
        g2.drawString("Back", 430, 260);

        // Give up
        g2.drawRect(428, 286, 54, 20);
        g2.drawString("Give up", 430, 300);

        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                // Black 
                if (allChess[i][j] == 1) {
                    int tempX = i * 25 + 55;
                    int tempY = j * 25 + 72;
                    g2.setColor(Color.BLACK);
                    g2.fillOval(tempX - 8, tempY - 8, 16, 16);
                }
                // White
                if (allChess[i][j] == 2) {
                    int tempX = i * 25 + 55;
                    int tempY = j * 25 + 72;
                    g2.setColor(Color.WHITE);
                    g2.fillOval(tempX - 8, tempY - 8, 16, 16);
                    g2.setColor(Color.BLACK);
                    g2.drawOval(tempX - 8, tempY - 8, 16, 16);
                }
            }
        }
        g.drawImage(bi, 0, 0, this);

    }

    public void mouseClicked(MouseEvent arg0) {

    }

    public void mouseEntered(MouseEvent arg0) {

    }

    public void mouseExited(MouseEvent arg0) {

    }

    public void mousePressed(MouseEvent e) {
        boolean checkWin = false;

        if (canPlay) {
            x = e.getX();
            y = e.getY();
            /*
             * System.out.println(x); System.out.println(y);
             */
            if (x >= 55 && x <= 405 && y >= 72 && y <= 420) {
                if ((x - 55) % 25 > 12)
                    x = (x - 55) / 25 + 1;
                else
                    x = (x - 55) / 25;
                if ((y - 72) % 25 > 12)
                    y = (y - 72) / 25 + 1;
                else
                    y = (y - 72) / 25;
                if (allChess[x][y] == 0) {

                    chessX[countX++] = x;
                    chessY[countY++] = y;

                    // System.out.println(countX + " " + countY);
                    if (isBlack) {
                        allChess[x][y] = 1;
                        isBlack = false;
                        message = "White please";
                        blackTime = inputTime;
                    } else {
                        allChess[x][y] = 2;
                        isBlack = true;
                        message = "Black please";
                        whiteTime = inputTime;
                    }
                    this.repaint();
                    checkWin = isWin();
                    if (checkWin) {
                        if (allChess[x][y] == 1) {
                            JOptionPane.showMessageDialog(this, "Game over, Black win");
                            restart();
                        }
                        else {
                            JOptionPane.showMessageDialog(this, "Game over, White win");
                            restart();
                        }
//                      canPlay = false;
                    }
                }
            }
        }

        // Restart
        if (e.getX() >= 428 && e.getX() <= 482 && e.getY() >= 66
                && e.getY() <= 86) {
            int result = JOptionPane.showConfirmDialog(this, "What to restart the game?");
            if (result == 0) {
                restart();
            }
        }
        // Count down settings
        if (e.getX() >= 428 && e.getX() <= 482 && e.getY() >= 106
                && e.getY() <= 126) {
            String input = JOptionPane
                    .showInputDialog("Please insert maximum time duration(in seconds),0 refers to no limit:");
            maxTime = Integer.parseInt(input);
            inputTime = Integer.parseInt(input);
            System.out.println(maxTime);
            if (maxTime < 0) {
                JOptionPane.showMessageDialog(this, "Wrong time setting，please try again.");
            } else if (maxTime == 0) {
                int result = JOptionPane.showConfirmDialog(this,
                        "Time setting successful，start game now？");
                if (result == 0) {
                    restart();
                }
            } else if (maxTime > 0) {
                int result = JOptionPane.showConfirmDialog(this,
                        "Time setting successful，restart game now？");
                if (result == 0) {
                    for (int i = 0; i < 15; i++)
                        for (int j = 0; j < 15; j++)
                            allChess[i][j] = 0;
                    for (int i = 0; i < 15; i++) {
                        chessX[i] = -1;
                        chessY[i] = -1;
                    }
                    countX = 0;
                    countY = 0;
                    message = "Black first please";
                    isBlack = true;
                    blackMessage = maxTime / 3600 + ":"
                            + (maxTime / 60 - maxTime / 3600 * 60) + ":"
                            + (maxTime - maxTime / 60 * 60);
                    whiteMessage = maxTime / 3600 + ":"
                            + (maxTime / 60 - maxTime / 3600 * 60) + ":"
                            + (maxTime - maxTime / 60 * 60);

                    blackTime = maxTime;
                    whiteTime = maxTime;
                    System.out.println(blackMessage + " - -" + whiteMessage);
                    timer.resume();
                    this.canPlay = true;
                    this.repaint();
                }
            }
        }
        // Instructions
        if (e.getX() >= 428 && e.getX() <= 482 && e.getY() >= 146
                && e.getY() <= 166) {
            JOptionPane.showMessageDialog(this, "The first five pieces in one line is the winner");
        }
        // Exit
        if (e.getX() >= 428 && e.getX() <= 482 && e.getY() >= 186
                && e.getY() <= 206) {
            int result = JOptionPane.showConfirmDialog(this, "Are you sure to exit?");
            if (result == 0) {
                System.exit(0);
            }
        }
        // Back to last turn
        if (e.getX() >= 428 && e.getX() <= 482 && e.getY() >= 246
                && e.getY() <= 266) {
            int result = JOptionPane.showConfirmDialog(this,
                    (isBlack == true ? "White wants to back to last turn，do you agree？" : "Black wants to back to last turn，do you agree？"));
            if (result == 0) {
                allChess[chessX[--countX]][chessY[--countY]] = 0;
                isBlack = (isBlack == true) ? false : true;
                this.repaint();
            }
        }
        // Give up
        if (e.getX() >= 428 && e.getX() <= 482 && e.getY() >= 286
                && e.getY() <= 306) {
            int result = JOptionPane.showConfirmDialog(this, "Are you sure to give up?");
            if (result == 0) {
                JOptionPane.showMessageDialog(this, "Game over，"
                        + (isBlack == true ? "Black gave up，Whit wins!" : "White gave up, Black wins!"));
                for (int i = 0; i < 15; i++)
                    for (int j = 0; j < 15; j++)
                        allChess[i][j] = 0;
                for (int i = 0; i < 15; i++) {
                    chessX[i] = -1;
                    chessY[i] = -1;
                }
                countX = 0;
                countY = 0;
                message = "Black first please";
                isBlack = true;
                blackMessage = maxTime / 3600 + ":"
                        + (maxTime / 60 - maxTime / 3600 * 60) + ":"
                        + (maxTime - maxTime / 60 * 60);
                whiteMessage = maxTime / 3600 + ":"
                        + (maxTime / 60 - maxTime / 3600 * 60) + ":"
                        + (maxTime - maxTime / 60 * 60);

                blackTime = maxTime;
                whiteTime = maxTime;
                System.out.println(blackMessage + " - -" + whiteMessage);
                timer.resume();
                this.canPlay = true;
                this.repaint();
            }
        }
    }

    public void mouseReleased(MouseEvent arg0) {

    }

    private boolean isWin() {
        boolean flag = false;
        // How many pieces in one color are connected
        int count = 1;
        // if there are 5 pieces connected in one row, which have same column
        int color = allChess[x][y];
        // for row
        count = this.checkCount(1, 0, color);
        if (count >= 5) {
            flag = true;
        } else {
            // for column
            count = this.checkCount(0, 1, color);
            if (count >= 5) {
                flag = true;
            } else {
                // Upper right、Lower left
                count = this.checkCount(1, -1, color);
                if (count >= 5) {
                    flag = true;
                } else {
                    // Upper left, Lower right
                    count = this.checkCount(1, 1, color);
                    if (count >= 5) {
                        flag = true;
                    }
                }
            }
        }

        return flag;
    }

    // Amount of connected pieces
    private int checkCount(int xChange, int yChange, int color) {
        int count = 1;
        int tempX = xChange;
        int tempY = yChange;
        while (x + xChange >= 0 && x + xChange <= 14 && y + yChange >= 0
                && y + yChange <= 14
                && color == allChess[x + xChange][y + yChange]) {
            count++;
            if (xChange != 0)
                xChange++;
            if (yChange != 0) {
                if (yChange > 0)
                    yChange++;
                else {
                    yChange--;
                }
            }
        }
        xChange = tempX;
        yChange = tempY;
        while (x - xChange >= 0 && x - xChange <= 14 && y - yChange >= 0
                && y - yChange <= 14
                && color == allChess[x - xChange][y - yChange]) {
            count++;
            if (xChange != 0)
                xChange++;
            if (yChange != 0) {
                if (yChange > 0)
                    yChange++;
                else {
                    yChange--;
                }
            }
        }
        return count;
    }

    public void run() {
        if (maxTime > 0) {
            while (true) {
                if (isBlack) {
                    blackTime--;
                    if (blackTime == 0) {
                        JOptionPane.showMessageDialog(this, "Black times out,game over!");
                        restart();
                    }
                } else {
                    whiteTime--;
                    if (whiteTime == 0) {
                        JOptionPane.showMessageDialog(this, "White times out,game over!");
                        restart();
                    }
                }
                blackMessage = blackTime / 3600 + ":"
                        + (blackTime / 60 - blackTime / 3600 * 60) + ":"
                        + (blackTime - blackTime / 60 * 60);
                whiteMessage = whiteTime / 3600 + ":"
                        + (whiteTime / 60 - whiteTime / 3600 * 60) + ":"
                        + (whiteTime - whiteTime / 60 * 60);
                this.repaint();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                // System.out.println(blackTime + " -- " + whiteTime);
            }
        }
    }

    public void restart() {
        for (int i = 0; i < 15; i++)
            for (int j = 0; j < 15; j++)
                allChess[i][j] = 0;
        for (int i = 0; i < 15; i++) {
            chessX[i] = 0;
            chessY[i] = 0;
        }
        countX = 0;
        countY = 0;
        message = "Black first please";
        blackMessage = "No limit";
        whiteMessage = "No limit";
        blackTime = maxTime;
        whiteTime = maxTime;
        isBlack = true;
        canPlay = true;
        this.repaint();
    }

    public static void main(String[] args) {
        new Wuzi();
    }

}
