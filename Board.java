package game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Timer;

/***
 * 
 * @author Andrew Connell
 *
 * This class creates, in memory, a board for Tetronimoes.
 * 
 * The space is [m] blocks wide by [n] blocks high.
 * These values can, therefore, be amended later on, if required.
 *
 */

public class Board {

    /* Reference variable for the Game for which this Board is used */
    Game owner_;

    /* Width and height of the board */
    private int width_;
    private int height_;
    
    /* 
     * Hard wire Board.checkN as the value which we will use to identify
     * the bottom invisible row on the board for the purposes of 
     * checking when a game is completed.
     */
    public static int checkN = 5;

    /* Array for all available positions on the board */
    private BoardPosition[][] positions_;

    /**
     * Constructor for a standard board of m by n.
     */

    public Board(Game thisGame, int width, int height) {

        owner_ = thisGame;

        width_ = width;
        height_ = height;

        positions_ = new BoardPosition[width_][height_];

        generatePositions(positions_);

        /* Test commentary */
        // System.out.println("Board constructed.");

    }


    /**
     * Methods for the Board
     **/

    /* Get the width of the board */
    public int getWidth() {

        return width_;

    }

    /* Get the height of the board */
    public int getHeight() {

        return height_;

    }

    /* Access the position at (xPos, yPos) on the board */
    public BoardPosition accessPosition(int xPos, int yPos) {

        return positions_[xPos][yPos];

    }


    /* Pass all positions on this board */
    public BoardPosition[][] getPositions() {

        return positions_;

    }


    /* Generate positions on the board once the array has been established */
    public void generatePositions(BoardPosition[][] thisBoard) {

        for (int xPos = 0; xPos < getWidth(); xPos++) {
            for (int yPos = 0; yPos < getHeight(); yPos++) {
                thisBoard[xPos][yPos] = new BoardPosition(xPos, yPos);
            }
        }

    }


    /* Access the ID number generator from the Game */
    public int getNextIDNumber() {

        return owner_.getNextPieceID();

    }


    /* Review the Nth line of the board and exit if a block has lodged on it */
    public Boolean isGameEnded(int checkN) {

        for (int alongX = 0; alongX < width_; alongX++) {
            if (positions_[alongX][Board.checkN].hasEntity()) {
                if (positions_[alongX][Board.checkN].returnEntityHere() instanceof Tetrad) {
                    Tetrad thisTet = (Tetrad) positions_[alongX][Board.checkN].returnEntityHere();
                    if (checkIfLodgedBlock(thisTet)) return true;
                }
            }
        }
        
        return false;

    }


    /* Check if a specific Tetrad is lodged above the top visible line on screen -> if so, flash all rows */
    private boolean checkIfLodgedBlock(Tetrad thisTet) {

        if (thisTet.isLodged()) {

            // System.out.println("Game over -> There's something breaking the bounds!");
            owner_.stopTicking();

            for (int rowNum = Board.checkN + 1; rowNum < height_; rowNum++) {
                /* Flash each line on the board */
                flashBlocks(rowNum, 4);
            }

            endTheGame();

            return true;

        }

        return false;

    }


    /* End the game and display Game Over screen */
    private void endTheGame() {

        owner_.setFlash(false);
        owner_.accessDisplay().directAccessToDisplay().signalEnded();
        owner_.accessDisplay().directAccessToDisplay().repaint();
        owner_.setInPlay(false);

    }


    /* Try to flash blocks on a given row on the screen */
    private void flashBlocks(int rowNum, int numFlashes) {
        /* Flash all blocks on screen */
        try {

            /* Switch twice for each flash (i.e. 2 times on, 2 times off == 4 switches == 2 flashes) */
            for (int x = 0; x < numFlashes; x++) {
                Thread.sleep(50); // 50ms wait time on thread
                owner_.setFlashRow(rowNum); // Flash on this row
                owner_.setFlash(!owner_.getFlash()); // Flip the value of flash for the blocks
                owner_.accessDisplay().directAccessToDisplay().repaint();
            }

        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    /* Check all lines below bound to see if completed */
    public void checkForCompletedLines() {

        /* Count the number of completed lines */
        int completedLines = 0; /** Start with -1, as will always return true on first line */

        /* Start at the bottom and work up */
        for (int rows = height_ - 1; rows > 6; rows--)
            completedLines = checkEachLine(completedLines, rows);

        /* Update the score */
        if (completedLines > 0) 
            owner_.getGameScore().multipleLinesCompleted(completedLines);

        /** Used to test whether or not row removal count is correct **/
        //        System.out.format("Removed %d rows.\n", completedLines);
        //        if (completedLines != 0 && completedLines > 0) System.exit(99);

    }


    /* Check each line to see if it has been completed */
    private int checkEachLine(int currentCompletedCount, int rows) {

        int checkAgain = rows;
        int origRow = checkAgain;

        /* 
         * While checking for completed lines continues to return this row number,
         * keep checking it for completions.
         */
        while (checkAgain == rows) {

            /* Set original row to what we're checking again */
            origRow = checkAgain;

            /* Set checkAgain to be the output of lineCompleted(int) */
            checkAgain = lineCompleted(rows);

            /* If checkAgain returns the same number, increase number of removed lines */
            if (checkAgain == origRow) currentCompletedCount++;

        }

        return currentCompletedCount;

    }


    /* Check if a line has been completed - if completed, clear the line and move all other blocks ABOVE this line down a line */
    public int lineCompleted(int rowNumber) {  // Board.checkN represents the bottom invisible row on the board

        /* Set the row number for next run */
        int nextRowCheck = rowNumber;

        /* 
         * Check all the board positions in a row.  If there are no gaps, and each position
         * holds a block, the line is completed 
         */

        int gapCnt = checkForGapsInLine(rowNumber);
        
        /* If the gapCnt == 0, remove the line */
        if (gapCnt == 0) {

            removeLineSequence(nextRowCheck, rowNumber);

        } else {

            /* Go to the next row up */
            return nextRowCheck - 1;

        }


        /* We have deleted this line, so we need to check it again */
        return nextRowCheck;

    }

    
    /* Check for gaps in a specific line, in order to work out if it is completed */
    private int checkForGapsInLine(int rowNumber) {
        
        int gapCnt = 0;

        for (int eachCol = 0; eachCol < width_; eachCol++) {
            if (!this.accessPosition(eachCol, rowNumber).hasEntity() &&
                    !(this.accessPosition(eachCol, rowNumber).returnEntityHere() instanceof Tetrad)) {
                gapCnt++;
            }
        }

        return gapCnt;
        
    }
    
    
    /* Consolidated sequence for removing a line from the board */
    private void removeLineSequence(int nextRowCheck, int rowNumber) {
        
        animateLineRemoval(nextRowCheck);
        clearLineFromBoard(rowNumber);
        shiftRemainingBlocksDown(rowNumber);
    
    }
    

    /* Animate the flash for removing a line from the board */
    private void animateLineRemoval(int nextRowCheck) {

        /* First of all, stop the timers and fire the animation to flash the line */
        owner_.tick.stop();
        flashBlocks(nextRowCheck, 10);

        /* Reset the flash values */
        owner_.setFlashRow(0);
        owner_.setFlash(false);

        /* Restart the timer */
        owner_.tick.start();

    }


    /* Clear the blocks in this line from memory */
    private void clearLineFromBoard(int rowNumber) {

        for (int eachCol = 0; eachCol < width_; eachCol++) {

            this.accessPosition(eachCol, rowNumber).setEntityHere(null);
            this.accessPosition(eachCol, rowNumber).setHasEntity(false);

        }

    }


    /* Shift remaining blocks on the board down one place */
    private void shiftRemainingBlocksDown(int rowNumber) {

        /* Now, move all other blocks down 1 */
        while (rowNumber > Board.checkN) { /** NEED TO ABSTRACT TOP LEVEL VALUE **/

            /* Go to the row above this one */
            rowNumber--;

            /* Shift each value down a row */
            for (int eachCol = 0; eachCol < width_; eachCol++) {

                /* Current position */
                BoardPosition currentPos = this.accessPosition(eachCol,  rowNumber);

                /* Position below */
                BoardPosition belowPos = this.accessPosition(eachCol, rowNumber + 1);

                /* Set entity to position below */
                belowPos.setEntityHere(currentPos.returnEntityHere());
                belowPos.setHasEntity(currentPos.hasEntity());

                /* Null out the current row */
                currentPos.setEntityHere(null);
                currentPos.setHasEntity(false);

            }
        }
    }

}
