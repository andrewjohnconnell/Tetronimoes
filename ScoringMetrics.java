package game;

/***
 * 
 * @author Andrew Connell
 *
 * This class creates a Score object, which captures and records the player's score 
 * based on lines completed and blocks of lines completed. (Record of lines completed
 * is also stored, so that we can advance levels.)
 *
 */

public class ScoringMetrics {

    /* Player's current score */
    private int currentScore_;
    
    /* Player's total completed lines */
    private int totalLines_;
    
    /* Player's current level */
    private int currentLevel_;
    
    /* Speed amendment */
    private int speedAmend_;
    
    /* Total lines completed */
   
    /* Reference to the Game being played */
    private Game owner_;
    
    /* Amend speed by this value */
    private int amendSpeedVal_;
    
    /** CONSTRUCTOR **/
    public ScoringMetrics(Game thisGame) {
        
        /* Set thisGame to owner_ */
        owner_ = thisGame;
        
        /* Initialise current score to 0 */
        currentScore_ = 0;
        
        /* Initialise level to 1 */
        currentLevel_ = 1;
        
        /* Initialise completed lines to 0 */
        totalLines_ = 0;
        
        /* Initialise speed amend to 0, so we run at the base speed */
        speedAmend_ = 0;
        
        /* Initialise the amendSpeedVal_ value */
        amendSpeedVal_ = 35;
        
        
    }
    
    
    /** METHODS **/
    
    /* Amend the current score by a received int */
    public void amendScore(int increaseBy) {
        
        currentScore_ += increaseBy;
        
    }
    
    
    /* Complete a single line */
    public void singleLineCompleted() {
        
        amendScore(10);
        totalLines_++;
        
        /* For multiples of 15 lines, increase level and speed */
        if (totalLines_ % 10 == 0) { 
            currentLevel_++;
            if (speedAmend_ < (owner_.getBaseSpeed() - amendSpeedVal_)) {
                speedAmend_ += amendSpeedVal_;
                owner_.setCurrentSpeed(owner_.getBaseSpeed() - speedAmend_);
                owner_.changeTickSpeed();
            }
        }
        
    }
    
    
    /* Complete multiple lines: receive number of lines */
    public void multipleLinesCompleted(int numLines) {
        
        /* Add points for each line */
        for (int lines = 0; lines < numLines; lines++) {
            
            singleLineCompleted();
            
        }
        
        /* Add multiples of 25 for each additional line */
        amendScore(25 * (numLines - 1));
        
    }
    
    
    /* Reset Score */
    public void resetScore() {
        
        currentScore_ = 0;
        
    }
    
    
    /* Return the current score */
    public int getScore() {
        
        return currentScore_;
        
    }
    
    
    /* Return the current level */
    public int getLevel() {
        
        return currentLevel_;
        
    }
    
    
    /* Reset total lines */
    public void resetLines() {
        
        totalLines_ = 0;
        
    }
    
    
    /* Get number of completed lines */
    public int getCompletedLines() {
        
        return totalLines_;
        
    }
    
}
