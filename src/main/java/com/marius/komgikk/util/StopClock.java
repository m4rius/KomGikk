package com.marius.komgikk.util;

public class StopClock {
    private long startTime = 0;
    private long stopTime = 0;
    private boolean running = false;


    public StopClock start() {
        this.startTime = System.currentTimeMillis();
        this.running = true;
        return this;
    }


    public StopClock stop() {
        this.stopTime = System.currentTimeMillis();
        this.running = false;
        return this;
    }


    //elaspsed time in milliseconds
    public long getElapsedTime() {
        long elapsed;
        if (running) {
            elapsed = (System.currentTimeMillis() - startTime);
        }
        else {
            elapsed = (stopTime - startTime);
        }
        return elapsed;
    }


    //elaspsed time in seconds
    public long getElapsedTimeSecs() {
        long elapsed;
        if (running) {
            elapsed = ((System.currentTimeMillis() - startTime) / 1000);
        }
        else {
            elapsed = ((stopTime - startTime) / 1000);
        }
        return elapsed;
    }
}
