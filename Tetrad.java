package game;

import java.awt.Point;

/***
 * 
 * @author Andrew Connell
 *
 * This class defines the Tetrad object, which
 * is the main playpiece for the game.
 * 
 * A Tetrad comprises four blocks, and takes the form
 * of a classic Tetronimoes shape.
 * 
 * Therefore, this object must understand where its four
 * blocks are in relative x/y terms to the board.
 *
 */

public class Tetrad extends Entity implements TetradMotion {

    private static int maxOrientations = 4;

    private static int verticalMod = 2;

    private final int numBlocks = 4; // Four blocks in a Tetrad

    /* Entity gives access to positions, which holds details of where each block is */

    /* Each Tetrad can be free moving or lodged.  When lodged, it cannot move */
    private Boolean nextTickLodged_; // Lodge permanently on next tick.

    /* Each Tetrad is a specific shape, and this must be called in the constructor */
    private Shape shape_;

    /* 
     * Each Tetrad has an initial orientation in degrees (0) -> and can turn through 90 degrees
     * at a time.
     */
    private int orientation_;

    /* Each Tetrad has a pre-defined basePos_, from which other positions are calculated */
    private BoardPosition basePos_; // This is only set when the piece is placed on the board(!)

    /* Boolean to store whether the Tetrad was last turned clockwise -> default True */
    private Boolean turningClockwise_ = true;

    /* Flag if on edge */
    Boolean flagToMoveRight_ = false;

    /* Flag to move left */
    Boolean flagToMoveLeft_ = false;

    /* Boolean to capture whether we have turned on right edge */
    Boolean onRightEdge_ = false;

    /* Boolean to capture whether we have turned on left edge */
    Boolean onLeftEdge_ = false;


    /** CONSTRUCTOR **/
    public Tetrad(BoardPosition basePosition, Shape thisShape, int numberOfPositions, Board onBoard) {

        /* Invoke the super constructor to set array for positions */
        super(numberOfPositions, onBoard);

        /* Set the shape of the Tetrad */
        shape_ = thisShape;

        /* Set Tetrad name to shape of Tetrad */
        name_ = thisShape.name();

        /* Set the basePosition */
        basePos_ = basePosition;

        /* Set base orientation to 0 */
        orientation_ = 0;

        /* Give one tick for movement before locking the lodge */
        nextTickLodged_ = false;

        /* Set the Tetrad to free moving */
        lodged_ = false;

        /* Calculate the locations of the blocks of the Tetrad based on the basePosition */
        calculatePositionsForDrop(basePos_, 0, 0);

        /* Place the Tetrad */
        placeTetrad();

    }


    /** Methods for Tetrad follow... */

    /* Implemented from TetradMotion */
    @Override
    public Boolean turn(Boolean clockWise) {

        /* Set flags and edges to false */
        setFlagsAndEdgesFalse();

        /* If the piece is lodged, do not permit a turn */
        if (lodged_) return false;

        /* Amend turningClockwise_ to mirror current direction */
        turningClockwise_ = clockWise;

        /* Get the current orientation of this Tetrad */
        int currentOrientation = orientation_;

        /* This is the orientation we are attempting to look at */
        int newOrientation = currentOrientation;

        /* Increase orientation by 1 */
        newOrientation++;
        if (newOrientation > maxOrientations) newOrientation = 1;

        // System.out.format("Current orientation is %d.\n", orientation_);
        // System.out.format("New orientation is %d.\n",  newOrientation);


        if (!performTurnCalculationsAndValidation(currentOrientation, newOrientation, clockWise)) return false;

        return true;

    }

    
    /* Run the necessary calculations and validations for whether a turn can be completed */
    private Boolean performTurnCalculationsAndValidation(int currentOrientation, int newOrientation, Boolean clockWise) {

        /* Arrays to hold new X and Y values */
        int[] newX = new int[numBlocks];
        int[] newY = new int[numBlocks];

        /* Set the new base position */
        BoardPosition newBase = null;
        
        /* Run exceptions for straightLine */
        straightLineExceptionHandler(currentOrientation);

        /* Calculate and set the new base position */
        newBase = calculateNewBasePosition(clockWise, newOrientation);

        /* Check each block to see if it can be set to new position */
        if (!checkBlocksCanTurn(newX, newY, newBase, currentOrientation, newOrientation)) return false;

        /* If we get to this point, then we know that the piece can move! */
        commitTurn(newOrientation, currentOrientation, newBase);
        
        return true;
        
    }
    

    /* Check the blocks for this shape can turn before committing to a move */
    private Boolean checkBlocksCanTurn(int[] newX, int[] newY, BoardPosition newBase, int currentOrientation, int newOrientation) {
        
        for (int eachPos = 0; eachPos < numBlocks; eachPos++) {

            // System.out.format("Looking at block %d\n", eachPos);

            /* Set newX, set newY for each position for checking */
            newX[eachPos] = newBase.getThisPosition().x + shape_.accessRelativePoints()[newOrientation][eachPos].x;
            newY[eachPos] = newBase.getThisPosition().y + shape_.accessRelativePoints()[newOrientation][eachPos].y;

            if (!checkValidityOfTurn(newX, newY, eachPos)) return false;

            /* Set a reference variable to the position we're looking at */
            BoardPosition checkHere = owner_.accessPosition(newX[eachPos], newY[eachPos]);

            /* If there is something on this position, and it isn't the current block, return false */
            if(checkIfPositionAlreadyOccupied(checkHere, newX, newY, eachPos, currentOrientation)) return false;

            /* If this is a straight line and we flagged it at the edge, move right or left */
            if (flagToMoveRight_) moveRight();
            if (flagToMoveLeft_) moveLeft();

        }

        return true;
        
    }
    

    /* Commit to the Turn */
    private void commitTurn(int newOrientation, int currentOrientation, BoardPosition newBase) {

        /* Set orientation_ to newOrientation! */
        orientation_ = newOrientation;

        placeTetradInNewPosition(currentOrientation, newBase);

        /* Check if on the bottom, if not, check if lodged */
        for (BoardPosition p: positions_) {
            if (p.getThisPosition().y != owner_.getHeight() - 1) checkIfLodged();
        }

        System.out.println("Tetrad turned.");

    }


    /* If all checks complete and valid, place Tetrad in new position */
    private void placeTetradInNewPosition(int currentOrientation, BoardPosition newBase) {

        /* First remove the Tetrad from the board */
        removeTetrad();

        /* Set each block to its new position */
        calculatePositionsForDrop(newBase, 0, 0);

        /* Place the Tetrad */
        placeTetrad();

        straightLineExceptionHandler_MoveBack(currentOrientation);

    }


    /* Check if a BoardPosition is already occupied by another entity */
    private Boolean checkIfPositionAlreadyOccupied(BoardPosition checkHere, int[] xArray, int[] yArray, int posNum, int currentOrientation) {

        if (checkHere.hasEntity() && checkHere.returnEntityHere().getEntityID() != id_) {
            System.out.format("Already something on this block [%d, %d].  Aborted.\n", xArray[posNum], yArray[posNum]);

            /* In which case, if this is a straight line, we need to move it back to the right or left*/
            if (this.shape_ == Shape.STRAIGHTLINE && currentOrientation % 2 == 0 && onRightEdge_) moveRight();
            if (this.shape_ == Shape.STRAIGHTLINE && currentOrientation % 2 == 0 && onLeftEdge_) moveLeft();

            return true;
        }

        return false;

    }


    /* Check if we are trying to conduct a valid turn.  If not, return false */
    private Boolean checkValidityOfTurn(int[] newX, int[] newY, int posNum) {

        /* First check that we want to access a valid position: if not, return false */
        if (newX[posNum] < 0 || newX[posNum] >= owner_.getWidth() || newY[posNum] < 0 || newY[posNum] >= owner_.getHeight()) {
            System.out.println("Attempting to turn out of bounds.  Aborted.");
            return false;
        }

        return true;

    }


    /* If the piece is a straightLine, move back if within specific regions */
    private void straightLineExceptionHandler_MoveBack(int currentOrientation) {

        /* Now that the Tetrad has been turned, we move it back to where it should be */
        if (this.shape_ == Shape.STRAIGHTLINE && currentOrientation % 2 == 0 && onRightEdge_) moveRight();
        if (this.shape_ == Shape.STRAIGHTLINE && currentOrientation % 2 == 0 && onLeftEdge_) {
            moveLeft(); moveLeft();
        }

    }


    /* Set the flags for moves and on edges to false */
    private void setFlagsAndEdgesFalse() {

        flagToMoveRight_ = false;
        flagToMoveLeft_ = false;
        onRightEdge_ = false;
        onLeftEdge_ = false;

    }


    /* Calculate new base position for the Tetrad */
    private BoardPosition calculateNewBasePosition(Boolean clockWise, int newOrientation) {

        BoardPosition newBase = null;
        if (clockWise) {
            newBase = new BoardPosition(
                    positions_[0].getThisPosition().x + shape_.accessRelativePoints()[newOrientation][0].x,
                    positions_[0].getThisPosition().y + shape_.accessRelativePoints()[newOrientation][0].y);

            if (flagToMoveRight_) {
                newBase = new BoardPosition(
                        positions_[0].getThisPosition().x + shape_.accessRelativePoints()[newOrientation][0].x + 1,
                        positions_[0].getThisPosition().y + shape_.accessRelativePoints()[newOrientation][0].y);
            }
        }

        return newBase;

    }


    /* Exception handler for straight line so that it can turn evenly along the edges of the board (left && right) */
    private void straightLineExceptionHandler(int currentOrientation) {

        verticalExceptionHandler(currentOrientation);
        horizontalExceptionHandler(currentOrientation);        

    }


    /* Vertical position exception handler for straight line */
    private void verticalExceptionHandler(int currentOrientation) {

        /* If this is a straight line, stood upright, and it is placed 1 from the edge on the right: giving appearance it could potentially turn */
        if (this.shape_ == Shape.STRAIGHTLINE && currentOrientation % verticalMod == 0) {

            if (positions_[0].getThisPosition().x == owner_.getWidth() - 2) {
                /* Move the line one to the left so that we can try turning */
                this.moveLeft();
                onRightEdge_ = true;
            }

            if (positions_[0].getThisPosition().x == 1) {
                /* Move the line two to the right so that we can try turning */
                this.moveRight();
                onLeftEdge_ = true;
            }

        }

    }


    /* Horizontal position exception handler for straight line */
    private void horizontalExceptionHandler(int currentOrientation) {

        /* If this is a straight line, lain flat, and it is placed on the right edge */
        if (this.shape_ == Shape.STRAIGHTLINE && currentOrientation % verticalMod == 1) {

            /* Check each position and find if one of the boxes has x of width - 1 */
            for (BoardPosition p: positions_)
                if (p.getThisPosition().x == owner_.getWidth() - 1) 
                    flagToMoveRight_ = true;

            /* Are we flagged to move right? */
            if (flagToMoveRight_)
                onRightEdge_ = true;

            /* Check for x = 0 */
            for (BoardPosition p: positions_)
                if (p.getThisPosition().x == 0)
                    flagToMoveLeft_ = true;

            /* Are we flagged to move left? */
            if (flagToMoveLeft_) onLeftEdge_ = true;

        }

    }

    @Override
    public Boolean turnLeft() {

        /* Invoke turn counter-clockwise */
        turn(false);

        return null;

    }


    @Override
    public Boolean turnRight() {

        /* Invoke turn clockwise */
        turn(true);

        return null;

    }


    @Override
    public Boolean dropRow() {

        /* 
         * For each block in the Tetrad, check first if nextTickLodged_ is true
         * if it is, then lodge the Tetrad.
         * If it isn't, then drop down a row.
         */

        if (nextTickLodged_ || onBottomRow()) {

            /* Permit a shift to left or right */

            lodged_ = true;
            // System.out.format("Next Tick Lodged.  Cannot drop.\n");

            return false;

        } else {

            /* Check if lodged */
            if (!checkIfLodged()) {

                dropTetradOneLine();

            } else {

                // System.out.println("Tetrad is lodged.  Cannot place lower.\n");

            }

        }

        return true;
    }


    /* Drop Tetrad one line */
    private void dropTetradOneLine() {
        
        /* Remove the Tetrad */
        removeTetrad();

        /* Calculate the positions for one row lower */
        calculatePositionsForDrop(positions_[0], 0, 1);

        /* Place the Tetrad */
        placeTetrad();

        /* Console display */
        System.out.println("Tetrad placed.");
        
    }
    
    
    @Override
    public Boolean speedDrop() {

        /* Drop the Tetrad row by row until lodged_ */
        while (!lodged_)
            dropRow();

        return null;
    }


    /* Calculate the positions of the Tetrad relative to its basePos ONLY WHEN BEING PLACED FIRST TIME */
    /*                                                               --------------------------------- */

    private void calculatePositionsForDrop(BoardPosition basePosition, int alongX, int alongY) {

        /* 
         * First of all, we have to look at each block in this shape and see if there is a valid space
         * for it to move into.
         */

        /* If not valid, return */
        if (!checkSideEdges(basePosition, alongX)) return;

        /* For initial block */
        positions_[0] = owner_.accessPosition(
                basePosition.getThisPosition().x + alongX,
                basePosition.getThisPosition().y + alongY);

        for (int blockPiece = 1; blockPiece < numBlocks; blockPiece++) {

            /* If blockPiece is < 0, display console message */
            if (blockPiece < 0) System.out.format("blockPiece argument < 0\n");

            /* For relative blocks */

            /* Clockwise */
            if (turningClockwise_) {
                positions_[blockPiece] = owner_.accessPosition(
                        basePosition.getThisPosition().x + shape_.accessRelativePoints()[orientation_][blockPiece].x + alongX,
                        basePosition.getThisPosition().y + shape_.accessRelativePoints()[orientation_][blockPiece].y + alongY);
            } 
        }

    }


    /* Check valid bounds to move left or right: return true if can move */
    private Boolean checkSideEdges(BoardPosition basePosition, int alongX) {

        //System.out.println("Checking side edges.");

        /* Check base isn't out of bounds */
        if (basePosition.getThisPosition().x + alongX < 0 ||
                basePosition.getThisPosition().x + alongX > owner_.getWidth() - 1) return false;

        /* Check that we are not attempting to move any block out of bounds */
        if (turningClockwise_) {
            if (!checkBoundValidity(basePosition, alongX)) return false;
        }

        /* Check for entities already on any of the positions to be used */
        if (existingEntityCheck(basePosition, alongX)) return false;

        //System.out.println("Check Side Edges returning True.");
        return true;

    }

    
    /* 
     * Check if there are any entities already on positions that we would like to move a Tetrad to:
     * return true if already occupied  
     */
    private Boolean existingEntityCheck(BoardPosition basePosition, int alongX) {
        
        /* Check for an entity on the basePosition */
        if (owner_.accessPosition(basePosition.getThisPosition().x + alongX, basePosition.getThisPosition().y).hasEntity()) {
            System.out.println("Entity already on spot trying to move to.  Aborting.");
            return true;
        }

        /* Check along each other block in this position */
        for (int thisBlock = 1; thisBlock < numBlocks; thisBlock++) {

            /* Clockwise */
            if (turningClockwise_) {
                if (owner_.accessPosition(
                        basePosition.getThisPosition().x + shape_.accessRelativePoints()[orientation_][thisBlock].x + alongX,
                        basePosition.getThisPosition().y + shape_.accessRelativePoints()[orientation_][thisBlock].y).hasEntity()) 
                    return true;
            } 

            //System.out.println("Successfully checked along blocks.");

        }
        
        return false;
        
    }
    
    
    /* Check that a side edge is not out of bounds for the game board */
    private Boolean checkBoundValidity(BoardPosition basePosition, int alongX) {
    
        for (int thisBlock = 1; thisBlock < numBlocks; thisBlock++) {
            if (basePosition.getThisPosition().x + shape_.accessRelativePoints()[orientation_][thisBlock].x  + alongX < 0 ||
                    basePosition.getThisPosition().x + shape_.accessRelativePoints()[orientation_][thisBlock].x + alongX > owner_.getWidth() - 1) {
                return false;
            }
        }
        
        return true;
    
    }

    /* Check if the Tetrad is either on top of another Tetrad or has reached the bottom of the Board */
    private Boolean checkIfLodged() {

        BoardPosition currentPos;
        BoardPosition belowPos;

        /* Cycle each blockPiece.  If the blockPiece is above another Tetrad, then we lodge on next tick */
        for (int blockPiece = 0; blockPiece < numBlocks; blockPiece++) {

            currentPos = positions_[blockPiece];

            //System.out.format("Currently on [%d, %d]\n", currentPos.getThisPosition().x, currentPos.getThisPosition().y);

            /* If this position x or y is < 0, return true */
            if (currentPos.getThisPosition().x < 0 || currentPos.getThisPosition().y < 0) {
                System.out.println("INVALID POSITION! ABORTING.");
                System.exit(99);
            }

            belowPos = owner_.accessPosition(currentPos.getThisPosition().x, currentPos.getThisPosition().y + 1);

            // If there is an entity on the tile below and it is NOT the same entity as this... Or on bottom row
            // (subtract 1 from getHeight, as we are accessing an array)
            if ((belowPos.hasEntity() && this.getEntityID() != belowPos.returnEntityHere().getEntityID()
                    && belowPos.returnEntityHere().entityLodgeCheck()) ||
                    currentPos.getThisPosition().y == owner_.getHeight() - 1) {   

                if (lodgeCheck(currentPos)) return true;

            } else {

                // We have moved to a position that won't lodge, so reset nextTickLodged_
                // System.out.println("Can't lodge in this position.  Continue dropping.");
                nextTickLodged_ = false;

            }

        }

        return false; 

    }


    /* Check if a piece is lodged */
    private Boolean lodgeCheck(BoardPosition currentPos) {
     
        // If we have not set to lodge on next tick...
        if (!nextTickLodged_) {
            // Set to lodge permanently on the next tick
            System.out.println("Setting nextTickLodged_ to true.");
            nextTickLodged_ = true;
            // UNLESS:: WE ARE ABOVE Y = 4, IN WHICH CASE, EXIT!
            if (currentPos.getThisPosition().y < 4) {
                // System.out.println("Game Over -> Lodged Above Row 4");
            }
            return true;
        } else {
            // Lodge permanently
            lodged_ = true;
            return true;
        }
        
    }
    
    
    /* Set Tetrad on BoardPositions: switches values of BoardPosition to hasEntity_ and name of entity */
    private void placeTetrad() {

        for (BoardPosition p: positions_) {

            p.setHasEntity(true);
            p.setEntityHere(this);

        }

    }


    /* Remove Tetrad from BoardPositions */
    private void removeTetrad() {

        for (BoardPosition p: positions_) {

            p.setHasEntity(false);
            p.setEntityHere(null);

        }
    }


    /* If the Tetrad is on the bottom row, return true */
    private Boolean onBottomRow() {

        for (BoardPosition p: positions_) {

            if (p.getThisPosition().y == owner_.getHeight() - 1) {
                return true;
            }

        }

        return false;

    }


    /* Get the current orientation of the Tetrad */
    public int getOrientation() {

        return orientation_;

    }


    /* Set the orientation to a specific value: return true if complete */
    private Boolean setOrientation(int setTo) {

        orientation_ = setTo;
        return true;

    }


    /* Move the Tetrad along the x-axis */
    @Override
    public Boolean move(int alongX) {

        /* 
         * alongX is the number of points along the axis we are going to move.
         * +ive == right, -ive == left.
         */

        /* If this Tetrad is not displayed, do not permit a move */

        /* If this piece is lodged, then return without doing anything */
        if (lodged_) return false;

        //if (!isDisplayed()) return false;

        BoardPosition checkHere = new BoardPosition(positions_[0].getThisPosition().x, positions_[0].getThisPosition().y);

        relocateTetradHorizontally(checkHere, alongX);
        
        /* Now that the Tetrad is placed, do a check to see if we need to reset lodged_, if not on bottom row */
        boolean onBottom = false;

        for (BoardPosition p: positions_) 
            if (p.getThisPosition().y == owner_.getHeight() - 1) onBottom = true;

        stackingTetradLodgeCheck(onBottom);
        
        return true;

    }

    
    /* If Tetrad on top of another Tetrad, check to ensure lodged_ marker is set appropriately */
    private void stackingTetradLodgeCheck(Boolean onBottom) {
        
        if (!onBottom) {

            /* 
             * Check each piece to see if there's one immediately below it. 
             * If there is, mark nextTickLodged_ true, else false.
             */

            for (BoardPosition p: positions_) {

                BoardPosition next = new BoardPosition(p.getThisPosition().x, p.getThisPosition().y);
                next.getThisPosition().y += 1;

                if (next.hasEntity() && next.returnEntityHere().getEntityID() != this.id_) {
                    if (nextTickLodged_) lodged_ = true;
                }

            }

            /* If all spaces are clear, remove nextTickLodged marker */
            nextTickLodged_ = false;

        }

    }
    
    
    /* Relocate a Tetrad on the board horizontally (i.e. after move left / right) */
    private void relocateTetradHorizontally(BoardPosition checkHere, int alongX) {
        
        removeTetrad();
        calculatePositionsForDrop(checkHere, alongX, 0);
        placeTetrad();

    }

    /* Move one block to the left */
    @Override
    public Boolean moveLeft() {

        /* If this move is valid, return true */
        if (move(-1)) return true;

        return false;

    }


    /* Move one block to the right */
    @Override
    public Boolean moveRight() {

        /* If this move is valid, return true */
        if (move(1)) return true;

        return false;

    }


    /* Return a Boolean value confirming whether this piece is lodged or not */
    public Boolean isLodged() {

        /* Check whether it is lodged above line 4 -> if so, exit */
        for (BoardPosition p: positions_) {
            if (lodged_ && p.getThisPosition().y < 4) {
                System.out.println("Busted!");
                owner_.owner_.accessDisplay().directAccessToDisplay().signalEnded();
            }
        }

        return lodged_;

    }


    /* Get the shape of this Tetrad */
    public Shape getShape() {

        return shape_;

    }


    /* Check that the Tetrad is in display before it can be moved */
    public Boolean isDisplayed() {

        /* For each board position */
        for (BoardPosition p : positions_) {

            /* If the y value is <= 5 (top six rows not shown), return false */
            if (p.getThisPosition().y <= 5) return false;

        }

        return true;

    }


    /* Get the position of the Tetrad */
    public BoardPosition getBasePosition() {

        return basePos_;

    }

}
