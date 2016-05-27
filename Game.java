package game;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.Timer;

/***
 * 
 * @author Andrew Connell
 *
 * This is the Game class for Tetronimoes.  It will instantiate and
 * run a new Game.  This could do with being refactored and abstracted a bit more
 * in the future -> generic game start up system for anything using the same board / positioning
 * approach
 *
 */

public class Game {

    /* ID Number generator for each piece */
    private int nextID_;

    /* Each game required a board! */
    private Board gameBoard_;

    /* The display for this game */
    private TetronimoWindow gameWindow_;

    /* Boolean to confirm if game in play */
    private Boolean inPlay_;

    /* The piece currently controlled by the player */
    private Tetrad currentPiece_;

    /* The shape of the next piece to be delivered to the player */
    private Shape nextPiece_;

    /* ScoringMetrics for this Game */
    private ScoringMetrics gameScore_;

    /* Base speed for the Game */
    private int baseSpeed_;

    /* Row that is to be flashed on line completion */
    private int flashRow_ = 0;

    /* Boolean to state whether this row shows blocks or not */
    private boolean flash_ = false;

    /* Set the current speed for the Game */
    private int currentSpeed_;

    /* Timer object to automatically drop the row and repaint */
    Timer tick;

    /* ResetGame switch */
    Boolean resetGame_;


    /** CONSTRUCTOR **/
    public Game(TetronimoWindow passWindow) {

        /* Create a game window */
        if (passWindow == null) {
            gameWindow_ =  new TetronimoWindow(this);
        } else {
            gameWindow_ = passWindow;
        }

        initiateGame();

    }


    public static void main(String[] args) {

        // Do whatever we need to, in here.

        while(true) {

            /* TEST */
            Game testGame = new Game(null);

        }

    }

    
    /* Testing Placements */
    /* ================== */
    
    public Tetrad placeHorizontalLine(int xPos) {

        Tetrad placeLine = new Tetrad(new BoardPosition(xPos, 1), Shape.STRAIGHTLINE, 4, getBoard());
        placeLine.turnRight();

        return placeLine;

    }


    public Tetrad placeBox(int xPos) {

        Tetrad placeLine = new Tetrad(new BoardPosition(xPos, 1), Shape.BOX, 4, getBoard());

        return placeLine;

    }


    public Tetrad testL(int xPos) {

        Tetrad placeLine = new Tetrad(new BoardPosition(xPos, 1), Shape.L, 4, getBoard());

        return placeLine;

    }

    
    /* Build an ActionListener for the Tick */
    public ActionListener buildTickPerform(Game testGame) {
        
        /** CREATE THE ACTION LISTENER FOR TICK **/
        ActionListener tickPerform = new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {

                testGame.gameWindow_.gameDisplay_.repaint();
                if (!testGame.gameWindow_.speedDropKeyPressedAlready_) testGame.currentPiece_.dropRow();

            }

        };

        return tickPerform;
    
    }

    /** 
     * METHODS 
     **/
    
    /* Start the game by launching the tick */
    public void startGame(Game testGame) {
    
        testGame.tick.start();
    
    }

    
    /* Reset the game, if we build in a loop cycle to go back to start */
    public void resetGame() {
        /* Switch resetGame_ to false */
        resetGame_ = false;

        /* Set inPlay to false */
        inPlay_ = false;

        /* Set baseSpeed_ to 500 */
        baseSpeed_ = 500;

        /* Set flashRow_ to 0 and flash_ to false */
        flashRow_ = 0;
        flash_ = false;

        /* Initialise the currentSpeed_ to baseSpeed_ */
        currentSpeed_ = baseSpeed_;


        if (this.tick == null) {
            this.tick = new Timer(this.currentSpeed_, buildTickPerform(this));
        }

        /* Set ID Number Generator to start at 1 */
        nextID_ = 1;

        this.gameScore_ = new ScoringMetrics(this);

        this.createBoard(12, 26); // Top six rows are hidden

        this.consoleDisplay(this.getBoard());

        int placeNum = 1;
        this.currentPiece_ = null;
        
    }
    
    
    /* Initiate a new Game */
    public void initiateGame() {

        /* Reset the system */
        resetGame();
        
        while (true) {

            /* The row to check for blocks above the line in this case is Board.checkN */
            int brokenLine = Board.checkN;
            
            while (!this.getBoard().isGameEnded(brokenLine)) {

                /* If game is ended, stop all ticking */

                buildInitialTetrad();

                buildReplacementTetrad();

                Random newRand = new Random();
                //if (newRand.nextInt(((3 - 1) + 1) + 1) == 1) leftTetrad.moveLeft();
                //if (newRand.nextInt(((3 - 1) + 1) + 1) == 2) leftTetrad.moveRight();
                //if (newRand.nextInt(((3 - 1) + 1) + 1) == 3) leftTetrad.turnRight();



                if (this.getBoard().isGameEnded(brokenLine)) {
                    this.stopTicking();
                    this.gameWindow_.gameDisplay_.repaint();
                }

                this.gameWindow_.gameDisplay_.repaint();

            }

            
        }

    }
    
    
    /* Create Tetrad at start of game */
    private void buildInitialTetrad() {
        
        /* When piece null at start, do this */
        if (this.currentPiece_ == null) {
            this.gameBoard_.checkForCompletedLines();
            this.currentPiece_ = this.generateNewTetrad(true, null);
            /* This command will set the nextPiece_ */
            this.generateNewTetrad(false, null);

        }
        
    }


    /* Replace the currentPiece_ with a new piece when Tetrad is lodged */
    private void buildReplacementTetrad() {
        
        /* When piece has been lodged */
        if (this.currentPiece_.isLodged()) {
            this.gameBoard_.checkForCompletedLines();
            this.currentPiece_ = this.generateNewTetrad(true, this.nextPiece_);

            /* This command will set the nextPiece_ */
            this.generateNewTetrad(false, null);
        }
        
    }
    
    
    /* Output for console just to check everything is initialising */
    public void consoleDisplay(Board show) {

        BoardPosition currentPos = null;

        for (int y = 0; y < show.getHeight(); y++) {

            System.out.println();

            for (int x = 0; x < show.getWidth(); x++) {
                currentPos = show.accessPosition(x, y);
                if (currentPos.hasEntity()) {
                    System.out.print("E");
                } else {
                    System.out.print("S");
                }
            }

        }

    }
    

    /* Get the next piece ID for an entity being placed in the game */
    public int getNextPieceID() {

        return nextID_++;

    }


    /* Create a new board for playing on (m by n) */
    public void createBoard(int x, int y) {

        gameBoard_ = new Board(this, x, y);

    }


    /*
     * Gain access to the board used in this game
     */
    public Board getBoard() {

        return gameBoard_;

    }


    /* Generate a new, random Tetrad */
    public Tetrad generateNewTetrad(boolean placeOnBoard, Shape predefShape) {

        // BoardPosition basePosition, Shape thisShape, int numberOfPositions, Board onBoard

        /* Variables for use in calculating new position */
        int min, max;

        /* Generate a random basePosition: 3 -> width - 5 */
        min = 3; max = gameBoard_.getWidth() - 5;

        /* Create a new RNG */
        Random randomNum = new Random();

        /* xPosition for new Tetrad */
        int newXPos = randomNum.nextInt((max - min) + 1) + min;

        /* Now that we have the xPos, we need to pick a random shape */
        int shapeNum = randomNum.nextInt((Shape.values().length - 0)) + 0;

        /* Check the 'hidden' rows to ensure that nothing is lodged above them */
        checkHiddenRows();

        /* Giving: */
        Tetrad returnThis = identifyNextPiece(placeOnBoard, predefShape, newXPos, shapeNum);

        return returnThis;

    }

    
    /* Identify what nextPiece_ should be passed to currentPiece_ */
    private Tetrad identifyNextPiece(Boolean placeOnBoard, Shape predefShape, int xPos, int shapeNum) {
        
        Tetrad returnThis = null;
        
        if (placeOnBoard) {
            if (predefShape == null) {
                returnThis = new Tetrad(new BoardPosition(xPos, 2), Shape.values()[shapeNum], 4, getBoard());
            } else {
                returnThis = new Tetrad(new BoardPosition(xPos, 2), predefShape, 4, getBoard());
            }
        } else {
            nextPiece_ = Shape.values()[shapeNum];
            return null;
        }
        
        return returnThis;
        
    }
    
    
    /* Check the 'hidden' rows for any lodged pieces */
    private void checkHiddenRows() {
        
        /* Check each and every position in rows 0 -> Board.checkN.  If there's anything in them, it's game over! */
        for (int y = 0; y <= Board.checkN; y++) 
            for (int x = 0; x < this.getBoard().getWidth(); x++) 

                if (this.getBoard().accessPosition(x, y).hasEntity() && this.getBoard().accessPosition(x,y).returnEntityHere().entityLodgeCheck()) {
                    System.out.println("Sorry, there's already a piece above the line... Game over!");
                    /* Stop ticking */
                    stopTicking();
                    gameWindow_.directAccessToDisplay().signalEnded();
                    //System.exit(99);
                }
        
    }
    

    /* Set the current piece being controlled by the player */
    public void setCurrentPiece(Tetrad thisPiece) {

        currentPiece_ = thisPiece;

    }


    /* Return the piece currently being controlled by the player */
    public Tetrad getCurrentPiece() {

        return currentPiece_;

    }


    /* Get the ScoringMetrics for this Game */
    public ScoringMetrics getGameScore() {

        return gameScore_;

    }


    /* Stop the drop tick */
    public void stopTicking() {

        if (tick != null) {
            tick.stop();
        }

    }


    /* Change tick speed */
    public void changeTickSpeed() {

        tick.setDelay(this.currentSpeed_);;

    }


    /* Get base speed for the Game */
    public int getBaseSpeed() {

        return baseSpeed_;

    }


    /* Set base speed for the Game */
    public void setCurrentSpeed(int setTo) {

        currentSpeed_ = setTo;

    }

    /* Get current speed for the Game */
    public int getCurrentSpeed() {

        return currentSpeed_;

    }

    /* Return a reference to the TetronimoWindow */
    public TetronimoWindow accessDisplay() {

        return gameWindow_;

    }

    /* Access the current piece in play */
    public Tetrad accessCurrentPiece() {

        return currentPiece_;

    }

    /* Is the game being played */
    public Boolean inPlay() {

        if (inPlay_) return true;
        return false;

    }


    /* Set inPlay to boolean value */
    public void setInPlay(Boolean setTo) {

        inPlay_ = setTo;
        if (inPlay_) startGame(this);

    }


    /* Get the name of the next piece */
    public String getNextPieceName() {

        return nextPiece_.name();

    }


    /* Set the row to be flashed */
    public void setFlashRow(int setTo) {

        flashRow_ = setTo;

    }

    /* Get the row to be flashed */
    public int getFlashRow() {

        return flashRow_;

    }

    /* Switch the boolean for flashing */
    public void setFlash(Boolean setTo) {

        flash_ = setTo;

    }

    /* Get the boolean for flashing */
    public Boolean getFlash() {

        return flash_;

    }

    /* Nullify Current and Next */
    public void nullifyCurrentAndNext() {
        currentPiece_ = null;
        nextPiece_ = null;
    }

    /* Get shape of next piece */
    public Shape getNextPiece() {

        return nextPiece_;

    }


}
