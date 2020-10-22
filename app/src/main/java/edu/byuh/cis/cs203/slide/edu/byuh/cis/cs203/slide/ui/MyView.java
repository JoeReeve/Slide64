package edu.byuh.cis.cs203.slide.edu.byuh.cis.cs203.slide.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import edu.byuh.cis.cs203.slide.R;
import edu.byuh.cis.cs203.slide.edu.byuh.cis.cs203.slide.GuiToken;
import edu.byuh.cis.cs203.slide.edu.byuh.cis.cs203.slide.logic.GameBoard;
import edu.byuh.cis.cs203.slide.edu.byuh.cis.cs203.slide.logic.Player;
import edu.byuh.cis.cs203.slide.edu.byuh.cis.cs203.slide.logic.TickListener;
import edu.byuh.cis.cs203.slide.edu.byuh.cis.cs203.slide.logic.Timer;

import static edu.byuh.cis.cs203.slide.edu.byuh.cis.cs203.slide.logic.Player.BLANK;


public class MyView extends View implements TickListener {

    private Paint p1;
    private Boolean mathDone;
    private float lineWidth;
    private float startX, startY;
    private float gap;
    private GridButton[] buttons;
    private List<GuiToken> tokens;
    private GameBoard engine;
    private Timer tim;
    private int scoreX, scoreO;
    private float w, h;


    /**
     * This contructor initializes the view window with the context
     * and sets up a paint object
     *
     * @param c takes a context
     */
    public MyView(Context c) {
        super(c);
        p1 = new Paint();
        p1.setColor(Color.BLACK);
        mathDone = false;
        scoreO = 0;
        scoreX = 0;

    }


    /**
     * This onDraw method gets the height and width of the screen, creates some variables based
     * on that and then draws the gameboard and buttons on the screen
     *
     * @param c takes in a canvas class as a parameter
     */
    @Override
    public void onDraw(Canvas c) {
        if (!mathDone) {
            //getting screen dimensions to scale everything properly
            w = getWidth();
            h = getHeight();
            p1.setStrokeWidth(w * 0.01f);
            lineWidth = w * 0.8f;

            gap = lineWidth / 5;
            startX = w * 0.17f;
            startY = (h - lineWidth) / 2;
            p1.setTextSize(w/20);

            buttons = new GridButton[10];
            engine = new GameBoard();
            tim = new Timer();
            tim.register(this);


            //instantiating horizontal buttons
            buttons[0] = new GridButton(getResources(),
                    (int) gap, (int) startX, (int) startY - (int) gap, '1');
            buttons[1] = new GridButton(getResources(),
                    (int) gap, (int) startX + (int) gap , (int) startY - (int) gap, '2');
            buttons[2] = new GridButton(getResources(),
                    (int) gap, (int) startX + (int) gap * 2, (int) startY - (int) gap, '3');
            buttons[3] = new GridButton(getResources(),
                    (int) gap, (int) startX + (int) gap * 3, (int) startY - (int) gap, '4');
            buttons[4] = new GridButton(getResources(),
                    (int) gap, (int) startX + (int) gap * 4, (int) startY - (int) gap, '5');

            //instantiating vertical buttons
            buttons[5] = new GridButton(getResources(),
                    (int) gap, (int) startX - (int) gap, (int) startY, 'A');
            buttons[6] = new GridButton(getResources(),
                    (int) gap, (int) startX - (int) gap, (int) startY + (int) gap, 'B');
            buttons[7] = new GridButton(getResources(),
                    (int) gap, (int) startX - (int) gap, (int) startY + (int) gap * 2, 'C');
            buttons[8] = new GridButton(getResources(),
                    (int) gap, (int) startX - (int) gap, (int) startY + (int) gap * 3, 'D');
            buttons[9] = new GridButton(getResources(),
                    (int) gap, (int) startX - (int) gap, (int) startY + (int) gap * 4, 'E');

            // instantiates the array of tokens used to draw the tokens on screen
            tokens = new ArrayList<>();

            //let's not do this math more times than we have to!
            mathDone = true;
        }
        //fill view with white
        c.drawColor(Color.WHITE);

        //drawing the grid
        for (int i = 0; i < 6; i++) {
            //horizontal lines
            c.drawLine(startX, startY + (gap * i),
                    startX + lineWidth, startY + (gap * i), p1);
            //vertical lines
            c.drawLine(startX + (gap * i), startY,
                    startX + (gap * i), startY + lineWidth, p1);
        }

        //draws the scores of each player at the top of the screen
        c.drawText("Player X score: " + scoreX , (float) ((w/10)*1.5), (h/30) * 2, p1);
        c.drawText("Player O score: " + scoreO , (float) ((w/10)*1.5), (h/30) * 3, p1);

        ArrayList<GuiToken> falling = new ArrayList<>();

        //draws the tokens
        for (GuiToken t : tokens) {
            t.draw(c);
            if (t.isInvisible(getHeight())) { //if a token has fallen off the screen we want to
                //remove it
                falling.add(t);
                tim.deregister(t);
            }
        }

        //remove all the tokens that have fallen off the screen
        tokens.removeAll(falling);

        //draw the buttons
        for (GridButton g : buttons) {
            g.draw(c);
        }

    }

    /**
     * this is where we control what happens when the user interacts with the screen
     * @param m needs a motionevent passed in
     * @return returns true when the motion event has been processed
     */
    @Override
    public boolean onTouchEvent(MotionEvent m) {



        //changes the pressed button image back to the regular image after it was released
        if (m.getAction() == MotionEvent.ACTION_UP) {
            for (GridButton g : buttons) {
                g.release();
            }
        }

        //checking to make sure no animation is happening. If it is we wait until it stops
        if (GuiToken.isMoving()) return true;

        if (m.getAction() == MotionEvent.ACTION_DOWN) {
            float touchX = m.getX();
            float touchY = m.getY();
            boolean buttonPressed = false;
            Player player = engine.getCurrentPlayer();

            //checks to see if a button was pressed
            for (GridButton g : buttons) {
                if (g.contains(touchX, touchY)) {
                    g.press();
                    buttonPressed = true;
                    int tokenImage;
                    boolean isTop = g.isTop(); //true if top button was pressed, false if side
                    char ch = g.getChar(); //the label of the pressed button
                    char r; // the row the token is in
                    char c; // the column the token is in

                    //this array is used to keep track of which tokens to move when a button is
                    // pressed
                    List<GuiToken> neighbors = new ArrayList<>();


                    //chooses which picture to display based on whose turn it is
                    if (player == Player.X) {
                        tokenImage = R.drawable.player_x;
                    } else {
                        tokenImage = R.drawable.player_o;
                    }

                    //checks which row and column to instantiate the token with
                    if (isTop) {
                        r = 'A' - 1;
                        c = ch;
                    } else {
                        r = ch;
                        c = '1' - 1;
                    }

                    //instantiates the new token with the proper image and velocity, adds it to the
                    //token array and neighbors array
                    GuiToken t = new GuiToken(getResources(), g, tokenImage, r, c);
                    tokens.add(t);
                    neighbors.add(t);

                    //adds the token to timer's list of subscriber so it knows when
                    //  the timers status changes
                    tim.register(t);

                    //submits move to game engine
                    engine.submitMove(ch, player);

                    //this logic moves the game pieces around depending on which button was pressed
                    if (isTop) { // top buttons
                        boolean found = true;
                        // if a matching token hasn't been found after looping through
                        // the tokens array it will break out of the entire loop
                        for (char row = 'A'; row < 'F'; row++) {
                            if (found) {
                                for (GuiToken token : tokens) {
                                    found = false;
                                    if ((token.pos.column == ch) & (token.pos.row == row)) {
                                        neighbors.add(token);
                                        found = true;
                                        break;
                                    }
                                }
                            } else {
                                break;
                            }
                        }
                        //slide all the tokens in the neighbors array
                        for (GuiToken token : neighbors) {
                            token.changeDestination(isTop);
                            token.pos.row += 1;
                        }
                    } else { //side buttons
                        boolean found = true;
                        for (char col = '1'; col < '6'; col++) {
                            if (found) {
                                for (GuiToken token : tokens) {
                                    found = false;
                                    if (token.pos.row == ch & token.pos.column == col) {
                                        neighbors.add(token);
                                        found = true;
                                        break;
                                    }
                                }
                            } else {
                                break;
                            }
                        }
                        for (GuiToken token : neighbors) {
                            token.changeDestination(isTop);
                            token.pos.column += 1;
                        }
                    }

                    //breaks out of the loop after the first button it finds
                    break;
                }
            }

            //displays a message if the user pressed anything but a button
            if (!buttonPressed) {
                Toast t = Toast.makeText(getContext(),
                        "Tap the buttons to play!", Toast.LENGTH_SHORT);
                t.show();
            }
        }

        //invalidating the screen so it redraws everything to where it should be
        invalidate();
        return true;
    }

    /**
     * This is what happens every time our timer (handler) notifies us of a tick event.
     * Here we simply invalidate the screen again and then check to see if anyone has won the game.
     * If someone has won the game we pause the timer and display an alert dialogue asking if the
     * user wants to play again or quit.
     */
    @Override
    public void onTick() {
        invalidate();
        Player winner = engine.checkForWin();
        if (!GuiToken.isMoving()) {
            if (winner != BLANK) {
                tim.flipPaused();
                AlertDialog.Builder ab = new AlertDialog.Builder(getContext());
                ab.setTitle("Congratulations " + winner + "! You win!");
                ab.setMessage("Would you like to play again?");
                ab.setCancelable(false);
                ab.setPositiveButton("Yes", (dialog, which) -> {
                    for (GuiToken t : tokens) {
                        tim.deregister(t);
                    }
                    tokens.removeAll(tokens);
                    engine = new GameBoard();
                    tim.flipPaused();
                    if (winner == Player.X) {
                        scoreX++;
                    } else {
                        scoreO++;
                    }
                });
                ab.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((Activity) getContext()).finish();
                    }
                });
                    AlertDialog box = ab.create();
                    box.show();

            }
        }
    }

}



