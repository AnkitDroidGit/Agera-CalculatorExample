package com.freeankit.ageracalculator;

import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

/**
 * @author Ankit Kumar (ankitdroiddeveloper@gmail.com) on 14/12/2017 (MM/DD/YYYY )
 */

public class Utils {
    /**
     * Starts an animation that will jank if the UI thread is busy.
     *
     * @param animView
     */
    static void startAnimation(View animView) {
        Animation tx = new TranslateAnimation(-350, 350, 0, 0);
        tx.setDuration(1000);
        tx.setRepeatCount(Animation.INFINITE);
        tx.setInterpolator(new AccelerateDecelerateInterpolator());
        tx.setRepeatMode(Animation.REVERSE);
        animView.startAnimation(tx);
    }
}
