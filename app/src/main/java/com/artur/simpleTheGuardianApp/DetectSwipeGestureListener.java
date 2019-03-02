package com.artur.simpleTheGuardianApp;

import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by artur on 02-Mar-19.
 */

public class DetectSwipeGestureListener extends GestureDetector.SimpleOnGestureListener {

    public static final int SWIPE_DIRECTION_LEFT = 1;
    public static final int SWIPE_DIRECTION_RIGHT = 2;
    public static final int SWIPE_DIRECTION_UP = 3;
    public static final int SWIPE_DIRECTION_DOWN = 4;
    // Minimal x and y axis swipe distance.
    private final int MIN_SWIPE_DISTANCE_X = 100;
    private final int MIN_SWIPE_DISTANCE_Y = 100;
    // Maximal x and y axis swipe distance.
    private final int MAX_SWIPE_DISTANCE_X = 1000;
    private final int MAX_SWIPE_DISTANCE_Y = 1000;
    private Swipe swipe;

    public void registerForSwipeDetection(Swipe swipe) {
        this.swipe = swipe;
    }

    /* This method is invoked when a swipe gesture happened. */
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        // Get swipe delta value in x axis.
        float deltaX = e1.getX() - e2.getX();

        // Get swipe delta value in y axis.
        float deltaY = e1.getY() - e2.getY();

        // Get absolute value.
        float deltaXAbs = Math.abs(deltaX);
        float deltaYAbs = Math.abs(deltaY);

        // Only when swipe distance between minimal and maximal distance value then we treat it as effective swipe
        if ((deltaXAbs >= MIN_SWIPE_DISTANCE_X) && (deltaXAbs <= MAX_SWIPE_DISTANCE_X)) {
            if (deltaX > 0) {
                swipe.onSwipeDetected(SWIPE_DIRECTION_LEFT);
            } else {
                swipe.onSwipeDetected(SWIPE_DIRECTION_RIGHT);
            }
        }

        if ((deltaYAbs >= MIN_SWIPE_DISTANCE_Y) && (deltaYAbs <= MAX_SWIPE_DISTANCE_Y)) {
            if (deltaY > 0) {
                swipe.onSwipeDetected(SWIPE_DIRECTION_UP);
            } else {
                swipe.onSwipeDetected(SWIPE_DIRECTION_DOWN);
            }
        }


        return true;
    }

    public interface Swipe {
        void onSwipeDetected(int direction);
    }
}
