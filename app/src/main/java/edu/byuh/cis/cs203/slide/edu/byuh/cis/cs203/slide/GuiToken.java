package edu.byuh.cis.cs203.slide.edu.byuh.cis.cs203.slide;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

import edu.byuh.cis.cs203.slide.edu.byuh.cis.cs203.slide.logic.TickListener;
import edu.byuh.cis.cs203.slide.edu.byuh.cis.cs203.slide.ui.GridButton;

public class GuiToken implements TickListener {

    private RectF bounds;
    private Bitmap image;
    private PointF velocity; //the speed at which the token moves
    private PointF destination; //where we want the token to go
    private int v; //the speed of the tokens sliding
    public GridPosition pos; //the position of the token on our 1-5,A-E grid
    private static int movers = 0; //how many tokens are currently moving?
    private boolean falling; //whether or not the token is falling into oblivion (slid off the grid)
    private boolean happy; // a token is happy if it contains it's destination and is not moving


    /**
     * By instantiating the token you take an image and scale it, then give the token some bounds
     * in which it will draw itself later on. Also sets up the velocity and destination for later
     * use
     *
     * @param res needs a resources object when instantiated in order to draw and scale the image
     * @param picture the actual image file you want to show as a token
     */
    public GuiToken(Resources res, GridButton parent, int picture, char r, char c) {
        this.bounds = new RectF(parent.getBounds());
        image = BitmapFactory.decodeResource(res, picture);
        image = Bitmap.createScaledBitmap(image, (int) bounds.width(), (int) bounds.height(), true);
        velocity = new PointF();
        destination = new PointF();
        pos = new GridPosition(r, c);
        falling = false;
        happy = false;
        //v is the speed at which the tokens will slide across the board
        v = 10;
    }

    /**
     * allows the token to draw itself
     * @param c takes the canvas object from where it is going to be drawn
     */
    public void draw(Canvas c) {
        c.drawBitmap(image, bounds.left, bounds.top, null);
    }

    /**
     * This method can be called to offset the bounds of the token image by the velocity
     */
    public void move() {
        if (!falling) { //if something is not falling and it should be do this
            if (pos.row > 'E' || pos.column > '5') {
                //were changing the velocity here to make the token fall off the screen when it gets
                //pushed off
                velocity.x = 0;
                velocity.y = 1;
                //changing the destination here to make sure the tokens won't reach it ever again
                destination.x = 0;
                destination.y = 0;
                //the token is now falling off the grid
                falling = true;
            }
        }
        if (!happy) {
            if (bounds.contains(destination.x, destination.y)) {
                decreaseVelocity();
                movers--;
                happy = true;
            }
        }


        if (falling) { // if token should be falling
            // this will give the appearance of increasing speed as the token falls
            velocity.y *= 2;
        }
        bounds.offset(velocity.x, velocity.y);
    }

    /**
     * This method checks if a top row button or a side column button was pressed and changes it's
     * velocity to allow it to move in the right direction
     * @param isTop will tell the token whether a side or top button was pressed
     */
    public void increaseVelocity(boolean isTop) {
        if (isTop) {
            velocity.x = 0;
            velocity.y = v;
        } else {
            velocity.x = v;
            velocity.y = 0;
        }
    }

    /**
     * this simply sets the objects velocity to 0 so the image will stop moving
     */
    public void decreaseVelocity() {
        velocity.x = 0;
        velocity.y = 0;
    }

    /**
     * This method updates the target destination which will make the token start animating until
     * it gets there
     * @param isTop true if it is a top button false if it's a side button
     */
    public void changeDestination(boolean isTop) {
        increaseVelocity(isTop);
        if (isTop) { //top button
            destination.y = bounds.top + bounds.height() * 2;
            destination.x = bounds.left;
        } else { // side button
            destination.y = bounds.top;
            destination.x = bounds.left + bounds.width() * 2;
        }
        movers++;
        happy = false;
    }

    /**
     * this will check to see if a token has moved off the screen
     * @param screenHeight we need to pass in the screen height to check if the token exceeds it
     * @return if we can no longer see the token this will return true, else false
     */
    public boolean isInvisible(float screenHeight) {
        if (bounds.top > screenHeight) {
            movers--;
            return true;
        } else {
            return false;
        }
    }

    /**
     * determines if animation is currently happening or not
     * @return true if yes false if no
     */
    public static boolean isMoving() {
        return (movers > 0);
    }

    @Override
    public void onTick() {
        Log.d("CS203", "movers="+movers);
        move();
    }

    public class GridPosition {

        public char row, column;

        /**
         * This mini-class is used as a field in the GuiToken class to keep track of where the token
         * is on the grid
         * @param r what row the token is in
         * @param c what column the token is in
         */
        public GridPosition(char r, char c) {
            row = r;
            column = c;
        }

    }

}
