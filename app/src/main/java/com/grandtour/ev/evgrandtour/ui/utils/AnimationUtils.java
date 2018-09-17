package com.grandtour.ev.evgrandtour.ui.utils;

import com.google.android.gms.maps.model.Circle;

import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.support.annotation.NonNull;
import android.view.animation.AccelerateDecelerateInterpolator;

public final class AnimationUtils {

    private AnimationUtils() { }

    public static void addAnimationToCircle(@NonNull final Circle circle) {
        ValueAnimator animator = new ValueAnimator();
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setIntValues(0, 100);
        animator.setDuration(1000);
        animator.setEvaluator(new IntEvaluator());
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedFraction = valueAnimator.getAnimatedFraction();
                circle.setRadius(animatedFraction * 50);
            }
        });
        animator.start();
    }
}
