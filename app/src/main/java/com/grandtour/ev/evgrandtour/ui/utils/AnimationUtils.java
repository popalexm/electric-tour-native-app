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
        animator.setIntValues(0, MapUtils.LOCATION_CIRCLE_RADIUS);
        animator.setDuration(1000);
        animator.setEvaluator(new IntEvaluator());
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(valueAnimator -> {
            float animatedFraction = valueAnimator.getAnimatedFraction();
            circle.setRadius(animatedFraction * MapUtils.LOCATION_CIRCLE_RADIUS);
        });
        animator.start();
    }
}
