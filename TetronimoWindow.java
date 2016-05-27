package game;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.JFrame;

/***
 * 
 * @author Andrew Connell
 *
 * This class defines the window object within which the 
 * Tetronimoes Display is contained
 *
 */

public class TetronimoWindow extends JFrame {

    /* Reference variable to the game run in this window */
    Game owner_;

    /* Reference to the display for the game being run in this window */
    TetronimoDisplay gameDisplay_;
    

    /** FOR THE KEY LISTENER **/
    /* Boolean to confirm whether the keypress has already taken place / been released */
    Boolean moveLeftKeyPressedAlready_ = false;
    Boolean moveRightKeyPressedAlready_ = false;
    Boolean turnKeyPressedAlready_ = false;
    Boolean speedDropKeyPressedAlready_ = false;
    Boolean pauseKeyPressedAlready_ = false;
    
    /* Timer reference for auto moves left and right, as well as speedDrop */
    Timer autoLeft_;
    Timer autoRight_;
    Timer speedDrop_;
    
    
    
    /** CONSTRUCTOR **/
    public TetronimoWindow(Game thisGame) {

        initialiseWindow(thisGame);

    }

    
    public void initialiseWindow(Game thisGame) {
        owner_ = thisGame;

        /* Variable for dimension size of window -> can change later */
        Dimension winSize = new Dimension(600, 800);

        /** Define the size and display properties of this window **/
        this.setSize(winSize);
        this.setMinimumSize(winSize);
        this.setMaximumSize(winSize);

        /* Produce the display */
        gameDisplay_ = new TetronimoDisplay(this);

        this.setResizable(false);
        this.setTitle("Twintris");

        this.add(gameDisplay_);

        /* Add Key Listeners */
        this.addKeyListeners();

        this.setVisible(true);
        
        /* Set to close on exit from the frame */
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
    

    /*** KEY LISTENERS FOR PIECE CONTROL ***/

    public void addKeyListeners() {
        this.addKeyListener(new KeyListener() {


            @Override
            public void keyPressed(KeyEvent e) {

                /* If the game is not in play, return */
                if (!owner_.inPlay()) {
                    
                    /* Spacebar will access whatever is selected */
                    if (e.getExtendedKeyCode() == KeyEvent.VK_SPACE) {

                        switch(gameDisplay_.getHighlightedWord()) {
                        
                        /* START */
                        case 0:
                            /* If on Start Game */
                            if (gameDisplay_.showEnd_ == true) {

                                System.exit(0);
                                
                            } else {

                            owner_.setInPlay(true);
                            
                            }
                            
                            break;
                        
                        /* QUIT GAME */
                        case 1:
                            System.exit(0);
                            break;
                        }
                        
                        return;
                    }

                    /* Scroll down */
                    if (e.getKeyChar() == 's' || e.getKeyChar() == 'S') {
                        gameDisplay_.scrollDownHighlightedWord();
                    }
                    
                    /* Scroll up */
                    if (e.getKeyChar() == 'w' || e.getKeyChar() == 'W') {
                        gameDisplay_.scrollUpHighlightedWord();
                    }

                    
                    return;
                    
                }
                
                char keyChar = e.getKeyChar();

                
                /* ActionListener for moving left */
                ActionListener autoMoveLeft = new ActionListener() {

                    public void actionPerformed(ActionEvent onStart) {
                        
                        owner_.getCurrentPiece().moveLeft();
                        gameDisplay_.repaint();
                        
                    }
                    
                };
                
                /* If we are not registered as going left and game isn't paused */
                if (!moveLeftKeyPressedAlready_) {
                    /* And the user is pressing the key to go left */
                    if (keyChar == 'a' || keyChar == 'A') {
                        /* Force moveRight to deactivate */
                        moveRightKeyPressedAlready_ = false;
                        if (autoRight_ != null) autoRight_.stop();
                        
                        /* And we are not registered as going right */
                        if (!moveRightKeyPressedAlready_) {
                            moveLeftKeyPressedAlready_ = true;
                            System.out.println("Pressed 'a'");
                            /* If the game isn't paused */
                            if (!pauseKeyPressedAlready_) {
                            owner_.getCurrentPiece().moveLeft();
                            gameDisplay_.repaint();

                            /* 
                             * While the key is pressed, auto refresh the move and 
                             * repaint the panel every 60ms 
                             */
                            
                            /** DROP SPEED / INCREASE SPEED -> KEY PRESS **/
                            autoLeft_ = new Timer(120, autoMoveLeft);
                            autoLeft_.start();
                            }
                        }
                    }
                }

                
                /* ActionListener for moving right */
                ActionListener autoMoveRight = new ActionListener() {

                    public void actionPerformed(ActionEvent onStart) {
                        
                        owner_.getCurrentPiece().moveRight();
                        gameDisplay_.repaint();
                        
                    }
                    
                };

                
                /* If we are not registered as going right and game isn't paused */
                if (!moveRightKeyPressedAlready_) {
                    /* And the user is pressing the key to go right */
                    if (keyChar == 'd' || keyChar == 'D') {
                        /* Force moveLeft to deactivate */
                        moveLeftKeyPressedAlready_ = false;
                        if (autoLeft_ != null) autoLeft_.stop();
                        
                        /* And we are not registered as going left */
                        if (!moveLeftKeyPressedAlready_) {
                            System.out.println("Pressed 'd'");
                            moveRightKeyPressedAlready_ = true;
                            /* If not paused, move the piece */
                            if (!pauseKeyPressedAlready_) {
                            owner_.getCurrentPiece().moveRight();
                            gameDisplay_.repaint();

                            /* 
                             * While the key is pressed, auto refresh the move and 
                             * repaint the panel every 60ms 
                             */
                            
                            /** DROP SPEED / INCREASE SPEED -> KEY PRESS **/
                            autoRight_ = new Timer(120, autoMoveRight);
                            autoRight_.start();
                            }
                        }
                    }
                }

                
                /* Turn if not paused */
                if (!turnKeyPressedAlready_ && !pauseKeyPressedAlready_)
                    if (keyChar == 'w' || keyChar == 'W') {
                        turnKeyPressedAlready_ = true;
                        System.out.println("Pressed 'w'");
                        owner_.getCurrentPiece().turnRight();
                        gameDisplay_.repaint();

                    }

                
                /* ActionListener for speedDrop */
                ActionListener speedDrop = new ActionListener() {

                    public void actionPerformed(ActionEvent onStart) {
                        
                        owner_.getCurrentPiece().dropRow();
                        gameDisplay_.repaint();
                        
                    }
                    
                };
                
                /* If game isn't paused, speed drop */
                if (!speedDropKeyPressedAlready_ && !pauseKeyPressedAlready_)
                    if (keyChar == 's' || keyChar == 'S') {
                        speedDropKeyPressedAlready_ = true;
                        System.out.println("Pressed 's'");
                        
                        if (!pauseKeyPressedAlready_) {
                        if (owner_.getCurrentPiece() != null) owner_.getCurrentPiece().dropRow();
                        gameDisplay_.repaint();
                        /* 
                         * While the key is pressed, auto refresh the move and 
                         * repaint the panel every 60ms 
                         */
                        
                        /** DROP SPEED / INCREASE SPEED -> KEY PRESS **/
                        speedDrop_ = new Timer(owner_.getCurrentSpeed() / 5, speedDrop);
                        speedDrop_.start();
                        }
                    }
                
                
                /* PAUSE EVERYTHING */
                if (!pauseKeyPressedAlready_) {
                    if (keyChar == 'p' || keyChar == 'P') {
                        
                        /* Set the pauseKeyPressedAlready_ boolean true */
                        pauseKeyPressedAlready_ = true;
                       
                        System.out.println("Pressed 'p'");
                        
                        /* Stop the game tick */
                        owner_.tick.stop();
                        
                        /* If speed drop running, stop the tick */
                        if (speedDrop_ != null) { 
                            if (speedDrop_.isRunning()) speedDrop_.stop();
                        }
                        
                        /* If moving left, stop the tick */
                        if (autoLeft_ != null) autoLeft_.stop();
                        if (autoRight_ != null) autoRight_.stop();
                        
                    }
                } else {
                    
                    /* In this case, the key has been pressed */
                    if (keyChar == 'p' || keyChar == 'P') {
                        
                        /* Set the pauseKeyPressedAlready_ boolean false */
                        pauseKeyPressedAlready_ = false;
                        
                        System.out.println("Pressed 'p'");
                        
                        /* Start the game tick again */
                        owner_.tick.start();
                        //owner_.getCurrentPiece().dropRow();
                        
                        /* Check what is being pressed */
                        if (moveLeftKeyPressedAlready_ && !moveRightKeyPressedAlready_) autoLeft_.start();
                        if (moveRightKeyPressedAlready_ && !moveLeftKeyPressedAlready_) autoRight_.start();
                        
                    }
                    
                }
                
            }
            

            @Override
            public void keyReleased(KeyEvent e) {

                /* If the game is not in play, return */
                if (!owner_.inPlay()) return; 
                
                if (e.getKeyChar() == 'a' || e.getKeyChar() == 'A') 
                    if (moveLeftKeyPressedAlready_) {
                        moveLeftKeyPressedAlready_ = false;
                        if (autoLeft_ != null) {
                            autoLeft_.stop();
                            gameDisplay_.repaint();
                        }
                        /* Check to see if the right move key has been pressed! */
                        if (moveRightKeyPressedAlready_) {
                            autoRight_.start();
                        }
                    }

                if (e.getKeyChar() == 'd' || e.getKeyChar() == 'D')  
                    if (moveRightKeyPressedAlready_) {
                        moveRightKeyPressedAlready_ = false;
                        if (autoRight_ != null) {
                            autoRight_.stop();
                            gameDisplay_.repaint();
                        }
                        /* Check to see if the right move key has been pressed! */
                        if (moveLeftKeyPressedAlready_) {
                            autoRight_.start();
                        }
                    }

                if (e.getKeyChar() == 'w' || e.getKeyChar() == 'W')
                    if (turnKeyPressedAlready_) turnKeyPressedAlready_ = false;

                if (e.getKeyChar() == 's' || e.getKeyChar() == 'S') 
                    if (speedDropKeyPressedAlready_) {
                        speedDropKeyPressedAlready_ = false;
                        if (speedDrop_ != null) {
                            speedDrop_.stop();
                        }
                    }

            }

            @Override
            public void keyTyped(KeyEvent e) {
                // TODO Auto-generated method stub

            }

        });
    }

    /** Check KeyListener status **/

    /* Checks the status of key presses and permits repeated turns on each tick, automatically -> movement only */
    public void checkKeyPressedStatus() {

        /* If there is a KeyListener, then the one we want is [0] */
        if (this.getKeyListeners() != null) {
            
            /* If pause is registered: pause */
            if (pauseKeyPressedAlready_) return;
            
            /* 
             * If both moveLeft and moveRight are somehow registered as pressed,
             * return without doing anything.
             */
            if (moveLeftKeyPressedAlready_ && moveRightKeyPressedAlready_) {
                return;
            }
            
            /* If moveLeft key is pressed */
            if (moveLeftKeyPressedAlready_) {
                /* Move the piece left and redraw board */
                owner_.getCurrentPiece().moveLeft();
                this.gameDisplay_.repaint();
                return;
            }
            
            /* If moveRight key is pressed */
            if (moveRightKeyPressedAlready_) {
                /* Move the piece right and redraw board */
                owner_.getCurrentPiece().moveRight();
                this.gameDisplay_.repaint();
                return;
            }
            
        }

    }

    
    /* Send true if game paused, false if running */
    public Boolean isPaused() {
        
        if (pauseKeyPressedAlready_) return true;
        return false;
        
    }
    
    /** Access the display directly **/
    public TetronimoDisplay directAccessToDisplay() {
        
        return gameDisplay_;
        
    }
    
}
