package game;

import java.awt.Point;

/***
 * 
 * @author Andrew Connell
 *
 * This class defines the BoardPosition object.
 * 
 * The BoardPosition has:
 *      relative x/y positioning;
 *      a link to the Tetrad that is currently on that position;
 *      
 */

public class BoardPosition {

    /*
     * The location_ Point provides the relative x/y position
     * for this BoardPosition in relation to its Board.
     */
    private Point location_;
    
    /* Each location also notes whether it has an entity */
    private Boolean hasEntity_;
    
    /* If there is an entity, the location provides a link to it */
    private Entity entityHere_;
    
    
    /** Constructor for BoardPosition **/
    public BoardPosition(int xPos, int yPos) {
        
        /* Default values: point(x,y), no entity */
        location_ = new Point(xPos, yPos);
        hasEntity_ = false;
        entityHere_ = null;
        
    }
    
    
    /** Methods for BoardPosition follow... **/
    
    /* Get position of this BoardPosition on the board */
    public Point getThisPosition() {
        
        return location_;
        
    }
    
    
    /* Get value for hasEntity_ */
    public Boolean hasEntity() {
        
        return hasEntity_;
        
    }
    
    
    /* If there is an entity here, get name of entity */
    public String getNameOfEntityHere() {
        
        return entityHere_.getName();
        
    }
    
    
    /* Return the actual entity that is at this position */
    public Entity returnEntityHere() {
        
        return entityHere_;
        
    }
    
    
    /* Set the value for hasEntity_ */ 
    public void setHasEntity(Boolean setTo) {
        
        hasEntity_ = setTo;
        
    }
    
    
    /* Set the entity on this location */
    public void setEntityHere(Entity setThis) {
        
        entityHere_ = setThis;
        
    }
    
}
