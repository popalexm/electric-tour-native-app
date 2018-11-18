package com.grandtour.ev.evgrandtour.ui.animations;

import android.view.animation.Interpolator;

public class BounceAnimationInterpolator implements Interpolator {

    private final double amplitude;
    private final double frequency;

    BounceAnimationInterpolator(double amplitude, double frequency) {
        this.amplitude = amplitude;
        this.frequency = frequency;
    }

    @Override
    public float getInterpolation(float time) {
        return (float) (-1 * Math.pow(Math.E, -time / amplitude) * Math.cos(frequency * time) + 1);
    }
}
