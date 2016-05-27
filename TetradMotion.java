package game;

/***
 * 
 * @author Andrew Connell
 *
 * This interface defines the common set of 
 * motions that blocks can undertake.
 *
 */

public interface TetradMotion {

    /* 
     * turn(boolean clockWise) receives true for clockwise, false for
     * counter-clockwise.
     * 
     * Attempt to turn left or right: return boolean value
     * to confirm whether move was successful or not.
     */
    
    public Boolean turn(Boolean clockWise);
    public Boolean turnLeft();
    public Boolean turnRight();

    
    /*
     * Move the Tetrad along the x-axis by the amount specified in alongX 
     */
    public Boolean move(int alongX);
    
    /* Short hand to move left or move right */
    public Boolean moveLeft();
    public Boolean moveRight();
    
    /*
     * Descend one block depth
     */
    public Boolean dropRow();
    
    /*
     * Force the block to quickly drop to the nearest lodge point.
     */
    
    public Boolean speedDrop();

}
