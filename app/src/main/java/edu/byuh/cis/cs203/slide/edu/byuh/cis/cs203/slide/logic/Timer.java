package edu.byuh.cis.cs203.slide.edu.byuh.cis.cs203.slide.logic;

import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;
import java.util.List;


public class Timer extends Handler {

    private List<TickListener> listeners;
    private boolean paused;

    public Timer() {
        sendMessageDelayed(obtainMessage(), 0);
        listeners = new ArrayList<>();
        paused = false;
    }

    public void flipPaused() {
        if (paused) {
            paused = false;
        } else {
            paused = true;
        }
    }

    public void register(TickListener t) {
        listeners.add(t);
    }

    public void deregister(TickListener t) {
        listeners.remove(t);
    }

    public void notifyTickListeners() {
        if (!paused) {
            for (TickListener t : listeners) {
                t.onTick();
            }
        }
    }

    @Override
    public void handleMessage(Message m) {
        notifyTickListeners();
        sendMessageDelayed(obtainMessage(), 30);
    }
}
