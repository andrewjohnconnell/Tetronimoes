package game;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JPanel;

/***
 * 
 * @author Andrew Connell
 *
 * This class defines the display panel object
 * on which we will show the Tetronimoes game in play.
 *
 */

public class TetronimoDisplay extends JPanel {

    /* Reference variable for the TetronimoWindow that holds this display */
    TetronimoWindow holder_;

    /* Reference variable to the game being played */
    Game thisGame_;

    /* Reference to display width and height for board */
    int width_;
    int height_;

    /* Booleans to control whether to show start / end screen */
    Boolean showStart_ = true; // Show the start screen by default
    Boolean needToLoadStartScreen_ = true; // Define whether to refresh start screen
    Boolean showEnd_ = false;

    /* Variables to support drawing */
    Boolean showGrid_ = true;
    Boolean showEntities_ = true;
    Boolean showScore_ = true;

    /* Arraylist of integers for colors for the start screen to stop it flashing */
    ArrayList<Integer> startBlocks_;

    /* Holder for colour array */
    ArrayList<Color> colourArray_;

    /* Font to draw on the screen */
    Font screenFont_;

    /* Edges for the board window */
    int leftEdge_, rightEdge_, topEdge_, bottomEdge_;

    /* Other dimensions for the board window */
    int boardWidth_, boardHeight_, boxWidth_, boxHeight_;


    /** CONSTRUCTOR **/
    public TetronimoDisplay(TetronimoWindow holder) {

        holder_ = holder;

        /* Set the game we are accessing */
        thisGame_ = holder_.owner_;

        /* Set the size of this panel to match the window */
        this.setSize(holder_.getSize());

        /* Quick and easy refs */
        width_ = this.getSize().width;
        height_ = this.getSize().height;

        /* Set background to red -> definitively know it's there */
        this.setBackground(Color.WHITE);

        repaint();

    }


    /** METHODS **/

    /* 
     * Generate a grid to show the Tetronimoes board on (m x n)
     */
    private Boolean displayGrid(Graphics2D g, int x, int y) {

        /* Amend y to hide the top Board.checkN rows */
        y = y - (Board.checkN + 1);

        //showGrid_ = true;

        //System.out.format("Width %d\n", width_);
        setEdges(x, y);

        fillBoard(g, Color.WHITE);

        drawGrid(g, x, y);

        return true;

    }

    
    /* Draw the actual grid */
    private void drawGrid(Graphics2D g, int x, int y) {
        /* Set colour to black */
        g.setColor(Color.BLACK);

        /* Draw a box bounding the edges */
        g.drawRect(leftEdge_, topEdge_, boardWidth_, boardHeight_);

        /* If the game is in play, check if the grid needs to be drawn */
        if (thisGame_.inPlay()) {
            showBoxGrid(g, x, y);
        }
    }
    
    
    /* Draw the internal box grid */
    private void showBoxGrid(Graphics2D g, int x, int y) {
        
        /* If game isn't paused, display the internal box grid */
        if (!thisGame_.accessDisplay().isPaused()) {

            /* Now boxes inside the boundary :: NOTE -> THE FIRST TWO ROWS ARE INVISIBLE! */
            for (int row = 0; row < y; row++) {
                for (int column = 0; column < x; column++) {

                    g.drawRect(leftEdge_ + (column * boxWidth_), topEdge_ + (row * boxHeight_), boxWidth_, boxHeight_);

                }
            }

            g.drawRect(leftEdge_,  topEdge_,  boxWidth_ * x, boxHeight_ * y);

        }
        
    }
    
    
    /* Fill the board with a colour */
    private void fillBoard(Graphics2D g, Color thisCol) {
        
        /* Fill the board with white, if in play, grey if not */
        if (thisGame_.inPlay()) {
            g.setColor(thisCol);
            if (thisGame_.accessDisplay().isPaused())
                g.setColor(Color.GRAY);

            g.fillRect(leftEdge_, topEdge_, boardWidth_, boardHeight_);

        }
        
    }

    
    /* Set edges and dimensions for the grid */
    private void setEdges(int x, int y) {
        
        leftEdge_ = (int)(width_ * 0.2);
        rightEdge_ = (int)(width_ * 0.8);

        topEdge_ = (int) (height_ * 0.15);
        bottomEdge_ = (int) (height_ * 0.85);

        boardWidth_ = rightEdge_ - leftEdge_;
        boardHeight_ = bottomEdge_ - topEdge_;

        boxWidth_ = (rightEdge_ - leftEdge_) / x;
        boxHeight_ = (bottomEdge_ - topEdge_) / y;
        
    }
    
    
    /*
     * Display entities
     */
    private Boolean displayEntities(Graphics2D g, int x, int y) {

        /* 
         * Go through each BoardPosition.
         * If there is an entity on it, colour the board position (initially, black)
         */

        /* Start at the sixth row! */
        for (int row = 6; row < y; row++) {
            for (int column = 0; column < x; column++) {

                if (thisGame_.getBoard().accessPosition(column, row).hasEntity()) {

                    setColourForEntity(g, thisGame_.getBoard().accessPosition(column, row));

                    /* If the game is paused, change the colour to black */
                    if (thisGame_.accessDisplay().isPaused()) g.setColor(Color.BLACK);

                    /* If this is the flashRow_ number and flash is on, set paint to white */
                    if (thisGame_.getFlash() && (row == thisGame_.getFlashRow()))  
                        g.setColor(Color.WHITE);


                    drawBlock(g, column, row);

                }

            }
        }

        return true;

    }
    
    
    /* Set the colour to draw an entity with, based on its name */
    private void setColourForEntity(Graphics2D g, BoardPosition thisPoint) {
       
        /* Switch colour of fill based on entity */
        switch(thisPoint.returnEntityHere().getName()) {
        case "STRAIGHTLINE":
            g.setColor(Color.BLUE);
            break;
        case "S":
            g.setColor(Color.GREEN);
            break;
        case "Z":
            g.setColor(Color.RED);
            break;
        case "BOX":
            g.setColor(Color.YELLOW);
            break;
        case "J":
            g.setColor(Color.PINK);
            break;
        case "L":
            g.setColor(Color.CYAN);
            break;
        case "T":
            g.setColor(Color.ORANGE);
            break;
        default:
            g.setColor(Color.BLACK);
        }
        
    }
    

    /** Draw a tetronimo block **/
    public void drawBlock(Graphics2D g, int column, int row) {
        /* Colour fill for boxes */
        g.fillRect(leftEdge_ + (column * boxWidth_), topEdge_ + ((row - 6) * boxHeight_), boxWidth_, boxHeight_);

        /* Six shades for border of block */

        /* Boundary rectangle around box */

        Color boundaryCol = g.getColor();
        int bCR = boundaryCol.getRed();
        int bCG = boundaryCol.getGreen();
        int bCB = boundaryCol.getBlue();

        /* Darken boundary colour */
        bCR -= 130;
        if (bCR < 0) bCR = 0;

        bCG -= 130;
        if (bCG < 0) bCG = 0;

        bCB -= 130;
        if (bCB < 0) bCB = 0;

        /* Draw the lines around the edges */

        /** MAX VARIATION IN SHADES **/
        int shadeMax = 14;

        for (int shadeNum = 0; shadeNum < shadeMax; shadeNum++) {


            g.setColor(new Color(bCR, bCG, bCB));
            g.drawRect(leftEdge_ + (column * boxWidth_) + (shadeNum), topEdge_ + ((row - 6) * boxHeight_) + (shadeNum), 
                    boxWidth_ - (2 * shadeNum), boxHeight_ - (2 * shadeNum));
            if (shadeNum == shadeMax - 1) 
                g.fillRect(leftEdge_ + (column * boxWidth_) + (shadeNum), topEdge_ + ((row - 6) * boxHeight_) + (shadeNum), 
                        boxWidth_ - (2 * shadeNum), boxHeight_ - (2 * shadeNum));

            bCR += 10;
            if (bCR > 255) bCR = 255;
            bCG += 10;
            if (bCR > 255) bCR = 255;
            bCB += 10;
            if (bCR > 255) bCR = 255;

        }
    }


    /* Draw miniaturised block for title / end screen effects */
    private void drawMiniBlock(Graphics2D g, Color drawIn, int column, int row) {

        /* First of all, capture boxWidth_ and boxHeight_ as they were */
        int oldBW = boxWidth_;
        int oldBH = boxHeight_;

        /* Store old colour for g to revert to */
        Color origCol = g.getColor();

        /* Switch to drawIn colour */
        g.setColor(drawIn);

        /* Mini boxes 1 / 5 of main */
        boxWidth_ = boxWidth_ / 3;
        boxHeight_ = boxHeight_ / 3;

        drawBlock(g, column, row);

        boxWidth_ = oldBW;
        boxHeight_ = oldBH;

        /* Revert colour */
        g.setColor(origCol);

    }

    /** Display the Score for this Game **/
    private Boolean displayScore(Graphics2D g, ScoringMetrics gameScore) {

        /* Set the font for drawing score details */

        screenFont_ = new Font("Arial", Font.BOLD, 14);
        g.setFont(screenFont_);
        g.setColor(Color.WHITE);

        String levelString = "Level:  " + gameScore.getLevel();
        String scoreString = "Score:  " + gameScore.getScore();
        String completedLines = "Lines:  " + gameScore.getCompletedLines();


        g.drawString(levelString, 120, 30);
        g.drawString(scoreString, 400, 30);

        g.drawString(completedLines, 250, 50);

        return true;

    }


    /*
     * Override PaintComponent()
     */
    public void paintComponent(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

        super.paintComponent(g2d);

        /* Paint the background */
        displayBackground(g2d);

        if (thisGame_.inPlay()) showStart_ = false;

        /* If flagged to show the start screen */
        if (showStart_) {
            displayGrid(g2d, 12,26);
            startScreen(g2d);
        }

        /* If flagged to show the end screen */
        if (showEnd_) {
            endScreen(g2d);
        }


        /* If not showing start and not showing end, then game is playing */
        if (thisGame_.inPlay()) {

            /* Paint the score details and board */
            if (showScore_) displayScore(g2d, thisGame_.getGameScore());

            /* Show grid if game is running, hide if paused */
            if (showGrid_) 
                displayGrid(g2d, thisGame_.getBoard().getWidth(), thisGame_.getBoard().getHeight());

            /* Paint the entities, if permitted */
            if (showEntities_) displayEntities(g2d, thisGame_.getBoard().getWidth(), thisGame_.getBoard().getHeight()); 


            /* If the game is paused, display paused message */
            if (thisGame_.accessDisplay().isPaused()) {
                showPauseMessage(g2d);
            }


            /* If in play, show name of next piece */
            if (thisGame_.getNextPiece() != null) {
                showNextPiece(g2d);
                nextShapeArea(g2d, Shape.valueOf(thisGame_.getNextPieceName()));
            }
        }

    }


    /* Display "Paused" on screen */
    public void showPauseMessage(Graphics2D g) {

        screenFont_ = new Font("Arial", Font.BOLD, 26);

        FontMetrics fm = g.getFontMetrics(screenFont_);
        int stringWidth = fm.stringWidth("Paused");
        g.setFont(screenFont_);
        g.setColor(Color.YELLOW);
        g.drawString("Paused", (thisGame_.accessDisplay().getWidth() + stringWidth ) / 2 - stringWidth, 
                thisGame_.accessDisplay().getHeight() / 2 );

    }

    /* Display NEXT PIECE NAME on screen */
    public void showNextPiece(Graphics2D g) {

        screenFont_ = new Font("Arial", Font.BOLD, 14);

        FontMetrics fm = g.getFontMetrics(screenFont_);
        int stringWidth = fm.stringWidth("NEXT");
        g.setFont(screenFont_);
        g.setColor(Color.WHITE);
        String s = "NEXT";
        g.drawString(s, ((rightNEdge_ + leftNEdge_) + stringWidth ) / 2 - stringWidth, 
                topNEdge_ - fm.getHeight() / 2 );

    }

    
    /* 
     * Paint the background for the game
     */
    public void displayBackground(Graphics2D g) {

        /* Lighten the background from edge to centre: black to white */
        /* Use rectangles to show the shading */

        /* Going from black to white is 0 -> 255 */
        /* Difference in size is height / 255 and width / 255 */
        /* Adding 1 each time */

        int baseColNum = 0;

        int baseX = 0;
        int maxX = width_ - 1;

        int baseY = 0;
        int maxY = height_ - 1;

        int xDifferentialPerRun = width_ / 255; // Based on 255 being max for each colour value
        int yDifferentialPerRun = height_ / 255; // Based on 255 being max for each colour value

        while (baseColNum < 255) {

            g.setColor(new Color(baseColNum, baseColNum, baseColNum)); // Create shade for the rectangle
            //            /* If paused, leave the blue at 150, constantly */
            //            if (thisGame_.accessDisplay().isPaused()) g.setColor(new Color(baseColNum, baseColNum, 150));

            g.fillRect(baseX, baseY, maxX, maxY);

            baseColNum += width_ / 255;

            baseX += xDifferentialPerRun;
            baseY += yDifferentialPerRun;
            maxX -= 2 * xDifferentialPerRun;
            maxY -= 2 * yDifferentialPerRun;

        }

    }

    
    /* Draw a group of mini blocks, 3x3 in different colours */
    public void drawMiniBlockCluster(Graphics2D g, int startCol, int startX, int startY) {

        Color colorArray[] = {Color.RED, Color.BLUE, Color.YELLOW, Color.GREEN, Color.ORANGE, Color.PINK, Color.MAGENTA};

        int arrayCycle = startCol;

        if (startCol > 6) arrayCycle = startCol % 2;

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                drawMiniBlock(g, colorArray[arrayCycle], startX + x, startY + y);
                /* Switch colour */
                arrayCycle++;
                if (arrayCycle > 6) arrayCycle = 0;
            }
        }

    }

    
    /* Fill board with blocks */
    public void blockFill(Graphics2D g) {

        /* If numbers list is null, generate, else return */
        if (startBlocks_ == null) {

            startBlocks_ = new ArrayList<Integer>();

            /* Generate numbers for colours for blocks */

            /* Generate array list of colours for shuffling */
            colourArray_ = new ArrayList<Color>();

            Color cols[] = {
                    Color.BLUE, Color.MAGENTA, Color.PINK, 
                    Color.RED, Color.GREEN, Color.YELLOW 
            };

            colourArray_.addAll(Arrays.asList(cols));

            for (int y = 6; y < 26; y++) {
                for (int x = 0; x < 12; x++) {
                    colourArray_.add(cols[new Random().nextInt((5 - 0) + 1) + 0]);
                }
            }
        } 

        /* Capture initial colour */
        Color origCol = g.getColor();

        /* Fill board with mini blocks */

        /* Position in arrayList */
        int arrPos = 0;

        for (int y = 6; y < 26; y++) {
            for (int x = 0; x < 12; x++) {
                g.setColor(colourArray_.get(arrPos++));
                if (!(x >= 3 && x <= 8 && y >= 13 && y <= 19))
                    drawBlock(g, x, y);
            }
        }

        g.setColor(origCol);

    }


    int leftNEdge_, rightNEdge_, topNEdge_;
    int nextBoxWidth_, nextBoxHeight_;

    /* Area for next shape to be used */
    public void nextShapeArea(Graphics2D g, Shape nextShape) {

        int split = 4; // Split the box into this many sections


        /* We have an area of 0.02 to 0.18 of the screen to use */
        leftNEdge_ = (int) (0.02 * width_);
        rightNEdge_ = (int) (0.18 * width_);
        topNEdge_ = (int) (topEdge_ + (0.05 * height_));

        nextBoxWidth_ = (rightNEdge_ - leftNEdge_) / split;
        nextBoxHeight_ = nextBoxWidth_;

        /* Create a box */
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(leftNEdge_, topNEdge_, rightNEdge_ - leftNEdge_, rightNEdge_ - leftNEdge_);

        /* Create border */
        g.setColor(Color.BLACK);
        g.drawRect(leftNEdge_, topNEdge_, rightNEdge_ - leftNEdge_, rightNEdge_ - leftNEdge_);

        /* Draw shape based on next shape in */
        String nextPiece = thisGame_.getNextPieceName();
        switch(nextPiece){ 
        case "Z":
        case "S":
        case "L":
        case "J":
            /* Set topNEdge - 0.5 box width */
            int oldTopNEdge = topNEdge_;
            topNEdge_ = (int) (topNEdge_ - (0.5 * nextBoxHeight_));

            switch(nextPiece) {
            case "Z":
                drawNextShape(g, 1,2, 2,2, 2,1, 3,1, Color.RED);
                break;
            case "S":
                drawNextShape(g, 1,1, 2,1, 2,2, 3,2, Color.GREEN);
                break;
            case "L":
                drawNextShape(g, 1,1, 2,1, 3,1, 3,2, Color.CYAN);
                break;
            case "J":
                drawNextShape(g, 1,2, 2,2, 3,2, 3,1, Color.PINK);
                break;
            }
            topNEdge_ = oldTopNEdge;
            break;
        case "BOX":
            drawNextShape(g, 1,1, 1,2, 2,1, 2,2, Color.YELLOW);
            break;
        case "T":
        case "STRAIGHTLINE":
            /* Set leftNEdge + 0.5 box width */
            int oldLeftNEdge = leftNEdge_;
            leftNEdge_ = (int) (leftNEdge_ + (0.5 * nextBoxWidth_));
            switch(nextPiece) {
            case "T":
                drawNextShape(g, 2,0, 1,1, 2,1, 2,2, Color.ORANGE);
                break;
            case "STRAIGHTLINE":
                drawNextShape(g, 0,1, 1,1, 2,1, 3,1, Color.BLUE);
                break;
            }

            /* Reset leftNEdge */
            leftNEdge_ = oldLeftNEdge;

            break;
        }
    }

    /* Draw the next shape to be displayed */
    public void drawNextShape(Graphics2D g,
            int r1, int c1, int r2, int c2, 
            int r3, int c3, int r4, int c4, Color c) {

        drawNextBlock(g, r1, c1, c);
        drawNextBlock(g, r2, c2, c);
        drawNextBlock(g, r3, c3, c);
        drawNextBlock(g, r4, c4, c);

    }


    public void drawNextBlock(Graphics2D g, int row, int column, Color colour) {

        g.setColor(colour);

        /* Boundary rectangle around box */

        Color boundaryCol = g.getColor();
        int bCR = boundaryCol.getRed();
        int bCG = boundaryCol.getGreen();
        int bCB = boundaryCol.getBlue();

        /* Darken boundary colour */
        bCR -= 130;
        if (bCR < 0) bCR = 0;

        bCG -= 130;
        if (bCG < 0) bCG = 0;

        bCB -= 130;
        if (bCB < 0) bCB = 0;


        /** MAX VARIATION IN SHADES **/
        int shadeMax = 14;

        for (int shadeNum = 0; shadeNum < shadeMax; shadeNum++) {


            g.setColor(new Color(bCR, bCG, bCB));
            g.drawRect(leftNEdge_ + (column * nextBoxWidth_) + (shadeNum), topNEdge_ + ((row) * nextBoxHeight_) + (shadeNum), 
                    nextBoxWidth_ - (2 * shadeNum), nextBoxHeight_ - (2 * shadeNum));
            if (shadeNum == shadeMax - 1) 
                g.fillRect(leftNEdge_ + (column * nextBoxWidth_) + (shadeNum), topNEdge_ + ((row) * nextBoxHeight_) + (shadeNum), 
                        nextBoxWidth_ - (2 * shadeNum), nextBoxHeight_ - (2 * shadeNum));

            bCR += 10;
            if (bCR > 255) bCR = 255;
            bCG += 10;
            if (bCR > 255) bCR = 255;
            bCB += 10;
            if (bCR > 255) bCR = 255;

        }

    }

    /* Int reference for the word on startScreen that should be in blue */
    private int highlightedWord_ = 0;

    /* Start Screen */
    public void startScreen(Graphics2D g) {

        /* Draw blocks in different colours to spell Tetronimoes on Screen */


        blockFill(g);

        screenFont_ = new Font("Arial", Font.BOLD, 14);

        FontMetrics fm = g.getFontMetrics(screenFont_);
        int stringWidth = fm.stringWidth("PRESS SPACE TO BEGIN");
        g.setFont(screenFont_);

        toHighlight(g, 0);
        g.drawString("PRESS SPACE TO BEGIN", (thisGame_.accessDisplay().getWidth() + stringWidth ) / 2 - stringWidth, 
                (int) (thisGame_.accessDisplay().getHeight() / 2));

    }


    /* Game Over Screen */
    private void endScreen(Graphics2D g) {
        blockFill(g);
        screenFont_ = new Font("Arial", Font.BOLD, 26);

        FontMetrics fm = g.getFontMetrics(screenFont_);
        int stringWidth = fm.stringWidth("GAME OVER");
        g.setFont(screenFont_);

        g.setColor(Color.BLACK);
        g.drawString("GAME OVER", (thisGame_.accessDisplay().getWidth() + stringWidth ) / 2 - stringWidth, 
                (int) (thisGame_.accessDisplay().getHeight() / 2) );

    }


    private void toHighlight(Graphics2D g, int thisNum) {
        if (highlightedWord_ == thisNum) {
            g.setColor(Color.BLUE);
        } else {
            g.setColor(Color.BLACK);
        }
    }

    public void scrollDownHighlightedWord() {

        highlightedWord_++;
        if (highlightedWord_ > 0) highlightedWord_ = 0;

    }

    public void scrollUpHighlightedWord() {

        highlightedWord_--;
        if (highlightedWord_ < 0) highlightedWord_ = 0;

    }

    public int getHighlightedWord() {

        return highlightedWord_;

    }

    public void signalEnded() {
        /* Stop ticks */
        thisGame_.stopTicking();
        showEnd_ = true;
    }

    public ArrayList<Integer> getStartBlockColourPlacements() {

        return startBlocks_;

    }

    public ArrayList<Color> getColourList() {

        return colourArray_;

    }

}
