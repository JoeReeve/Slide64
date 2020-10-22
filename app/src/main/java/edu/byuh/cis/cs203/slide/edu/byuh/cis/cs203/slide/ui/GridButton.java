package edu.byuh.cis.cs203.slide.edu.byuh.cis.cs203.slide.ui;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;

import edu.byuh.cis.cs203.slide.R;

public class GridButton {

    protected RectF bounds;
    private Bitmap button, pressedButton;
    private boolean pressed;
    private char ch;

    /**
     * This GridButton constructor allows the caller to specify
     * what size they want the image and where they want it to
     * appear on screen
     *
     * @param res needs to get a resources object when it is instantiated
     * @param size how big do you want the button?
     * @param x starting x position for the image
     * @param y starting y position for the image
     * @param character a character label that we want to display along with the button
     */

    public GridButton(Resources res, float size, float x, float y, char character){
        button = BitmapFactory.decodeResource(res, R.drawable.button);
        button = Bitmap.createScaledBitmap(button, (int) size, (int) size, true);
        pressedButton = BitmapFactory.decodeResource(res, R.drawable.pressed_button);
        pressedButton = Bitmap.createScaledBitmap(pressedButton, (int) size, (int) size, true);
        bounds = new RectF(x, y, x + size, y + size);
        ch = character;
        pressed = false;

    }

    /**
     * This method allows the button to draw itself given a canvas object
     *
     * @param c simply takes the canvas object from the class using the button
     */

    public void draw(Canvas c) {
        if (pressed == true) {
            c.drawBitmap(pressedButton, bounds.left, bounds.top, null);
        } else {
            c.drawBitmap(button, bounds.left, bounds.top, null);
        }
    }


    /**
     * This method takes an x and y coordinate and checks to see if it is within the bounds of the
     * button and returns either true of false
     *
     * @param x the x coordinate of the input
     * @param y the y coordinate of the input
     * @return
     */
    public boolean contains(float x, float y) {
        return (bounds.contains(x,y));
    }

    /**
     * This method should get called during an onPressed event and change the pressed field to true
     */
    public void press() {
        pressed = true;
    }

    /**
     * This method should get called after lifting your finger from a
     * button and change the pressed field to false
     */
    public void release() {
        pressed = false;
    }

    /**
     * This method gets the Char field of the object
     * @return a char that helps identify which button it is
     */
    public char getChar() {
        return ch;
    }

    public boolean isTop() {
        return (ch >= '1' && ch <= '5');
    }

    /**
     * Get the current dimensions of the button
     * @return a RectF object
     */
    public RectF getBounds() {
        return bounds;
    }


}
