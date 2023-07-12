package de.niecklikescode.turing.api.utils;

import lombok.Getter;

public class Timer {

    @Getter
    private long ms;

    public Timer() {
        reset();
    }

    public void reset() {
        this.ms = System.currentTimeMillis();
    }

    public boolean hasPassed(long time) {
        return ms + time < System.currentTimeMillis();
    }

     // Checks whether a given delay has elapsed and if so fires the passed executable and resets the timer
    public void invokeIfComplete(long delay, Runnable runnable) {
        if(hasPassed(delay)) {
            runnable.run();
            reset();
        }
    }

}
