package game;

import java.awt.Point;

/***
 * 
 * @author Andrew Connell
 *
 * This is the Shapes enum.  It is used to define the relative positions of 
 * four blocks for Tetrads.
 * 
 * The values read in are numOfTurns, x/y, x1/y1, x2/y2, x3/y3
 * and represent the relative positions of the starting points of each block
 *
 * This could have been implemented using a standard shape class
 * and a host of methods to create each one at outset.  Really just wanted to 
 * experiment with using an enum system for specific shape values to see how
 * it worked in practice.
 *
 */

public enum Shape {

    /** Defines a STRAIGHTLINE from 0 to 0 degrees with relative positions to the first block **/
    STRAIGHTLINE(
            // Clockwise
            0,0,  0,1,  0,2,  0,3,       // 0 Degrees {Applies to clockwise and counter-clockwise}
            -1,1,  1,0,  2,0,  3,0,       // 90 Degrees
            1,-1,  0,1,  0,2,  0,3,     // 180 Degrees
            -1,1,  1,0,  2,0,  3,0,      // 270 Degrees
            1,-1,  0,1,  0,2,  0,3     // Back to 0
            
            ),
    
    S(
            // Clockwise
            0,0,  0,-1,  1,-1,  -1,0,        // 0
            0,0,  0,-1,  1,0,  1,1,         // 90
            0,0,  0,-1,  1,-1,  -1,0,      // 180
            0,0,  0,-1,  1,0,  1,1,    // 270
            0,0,  0,-1,  1,-1,  -1,0      // Back to 0
            
            ),
    
    Z(
            // Clockwise
            0,0,  -1,0,  0,1,  1,1,          // 0
            0,0,  0,-1,  -1,0,  -1,1,     // 90
            0,0,  -1,0,  0,1,  1,1,     // 180
            0,0,  0,-1,  -1,0,  -1,1,     // 270
            0,0,  -1,0,  0,1,  1,1     // Back to 0
            
            ),
    
    BOX(
            // Clockwise
            0,0,  1,0,  0,1,  1,1,          // 0
            0,0,  1,0,  0,1,  1,1,      // 90
            0,0,  1,0,  0,1,  1,1,     // 180
            0,0,  1,0,  0,1,  1,1,     // 270
            0,0,  1,0,  0,1,  1,1      // Back to 0

            ),
    
    L(
            // Clockwise
            0,0,  0,-1,  0,1,  1,1,      //0
            0,0,  -1,0,  -1,1,  1,0,      // 90
            0,0,  0,-1,  -1,-1,  0,1,     // 180
            0,0,  -1,0,  1,0,  1,-1,     // 270
            0,0,  0,-1,  0,1,  1,1      // Back to 0

            ),
    
    /* J <SHOULD> be the inverse of L */
    J(
            // Clockwise
            0,0,  0,-1,  0,1,  -1,1,      //0
            0,0,  -1,-1,  -1,0,  1,0,      // 90
            0,0,  0,1,  1,-1,  0,-1,     // 180
            0,0,  -1,0,  1,0,  1,1,     // 270
            0,0,  0,-1,  0,1,  -1,1      // Back to 0

            ),
    
    /* T */
    T(
            // Clockwise
            0,0,  -1,1,  0,1,  1,1,      //0
            0,0,  -1,0,  -1,-1,  -1,1,      // 90
            0,0,  -1,-1,  0,-1,  1,-1,     // 180
            0,0,  1,0,  1,-1,  1,1,     // 270
            0,0,  0,1,  -1,1,  1,1      // Back to 0

            );
    
    

    
    private Point[][] points_;

    
    
    /** Constructor for the enum Shape **/
    private Shape(int x, int y, int x1, 
            int y1, int x2, int y2, int x3, int y3,
            int aX, int aY, int aX1,
            int aY1, int aX2, int aY2, int aX3, 
            int aY3, int bX, int bY,
            int bX1, int bY1, int bX2, int bY2, 
            int bX3, int bY3, int cX,
            int cY, int cX1, int cY1, int cX2,
            int cY2, int cX3, int cY3,
            int dX, int dY, int dX1, int dY1,
            int dX2, int dY2, int dX3, int dY3) {
        
        points_ = new Point[5][4];

        
        int[] firstX = {x, x1, x2, x3};
        int[] firstY = {y, y1, y2, y3};
        int[] secondX = {aX, aX1, aX2, aX3};
        int[] secondY = {aY, aY1, aY2, aY3};
        int[] thirdX = {bX, bX1, bX2, bX3};
        int[] thirdY = {bY, bY1, bY2, bY3};
        int[] fourthX = {cX, cX1, cX2, cX3};
        int[] fourthY = {cY, cY1, cY2, cY3};
        int[] fifthX = {dX, dX1, dX2, dX3};
        int[] fifthY = {dY, dY1, dY2, dY3};
        
        int[][] xPoints = new int[5][4];
        xPoints[0] = firstX;
        xPoints[1] = secondX;
        xPoints[2] = thirdX;
        xPoints[3] = fourthX;
        xPoints[4] = fifthX;
        
        int[][] yPoints = new int[5][4];
        yPoints[0] = firstY;
        yPoints[1] = secondY;
        yPoints[2] = thirdY;
        yPoints[3] = fourthY;
        yPoints[4] = fifthY;
        
        for (int eachTurn = 0; eachTurn < 5; eachTurn++) {
            for (int eachPoint = 0; eachPoint < 4; eachPoint++) {
            
                /* Clockwise Points */
                points_[eachTurn][eachPoint] = new Point(xPoints[eachTurn][eachPoint], 
                        yPoints[eachTurn][eachPoint]);
                
            }    
        }
        
    }
    
    
    /** Methods for Shape follow... **/

    /* Access the points for this shape */
    public Point[][] accessRelativePoints() {
        
        return points_;
        
    }

    
    
    
}
