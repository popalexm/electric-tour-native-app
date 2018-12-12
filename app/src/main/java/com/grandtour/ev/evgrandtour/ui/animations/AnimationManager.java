package com.grandtour.ev.evgrandtour.ui.animations;

import com.google.android.gms.maps.model.Circle;

import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.app.Injection;
import com.grandtour.ev.evgrandtour.ui.utils.MapUtils;

import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.TextView;

public final class AnimationManager {

    private static final double BOUNCE_ANIM_AMPLITUDE = 0.2;
    private static final double BOUNCE_ANIM_FREQUENCY = 20;
    private static AnimationManager sInstance;

    private AnimationManager() {
    }

    @NonNull
    public static AnimationManager getInstance() {
        if (AnimationManager.sInstance == null) {
            AnimationManager.sInstance = new AnimationManager();
        }
        return AnimationManager.sInstance;
    }

    public void startUserLocationAnimation(@NonNull final Circle circle) {
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

    public void startBounceAnimation(@NonNull FloatingActionButton button) {
        Animation bounceAnimation = android.view.animation.AnimationUtils.loadAnimation(Injection.provideGlobalContext(), R.anim.bounce_animation);
        Interpolator interpolator = new BounceAnimationInterpolator(AnimationManager.BOUNCE_ANIM_AMPLITUDE, AnimationManager.BOUNCE_ANIM_FREQUENCY);
        bounceAnimation.setInterpolator(interpolator);
        button.startAnimation(bounceAnimation);
    }

    public void shakeTextView(@NonNull TextView textView) {
        Animation animShake = AnimationUtils.loadAnimation(Injection.provideGlobalContext(), R.anim.shake_animation);
        textView.startAnimation(animShake);
    }
}