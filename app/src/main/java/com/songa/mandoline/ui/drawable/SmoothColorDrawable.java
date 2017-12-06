package com.songa.mandoline.ui.drawable;

import android.animation.ArgbEvaluator;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;

/**
 * A {@link ColorDrawable} that smoothly transitions from one color to another, even when its target
 * color is changed in the middle of a transition.
 */
public class SmoothColorDrawable extends ColorDrawable
{
    private final ArgbEvaluator evaluator = new ArgbEvaluator();

    private final int defaultColor;

    private int startColor;
    private int currentColor;
    private int nextColor;

    private final int durationInMs;
    private long startingPoint;
    private boolean done;

    /**
     * @param startColor The starting color of this drawable
     * @param defaultColor A default fallback color
     * @param durationInMs The duration of transitions
     */
    public SmoothColorDrawable(int startColor, int defaultColor, int durationInMs)
    {
        super(startColor);
        this.defaultColor = defaultColor;
        this.startColor = startColor;
        this.currentColor = startColor;
        this.nextColor = startColor;
        this.durationInMs = durationInMs;
        this.startingPoint = System.currentTimeMillis();
        this.done = true;
    }

    /**
     * Transitions the drawable to the default color.
     */
    public void setDefaultColor()
    {
        setNewColor(defaultColor);
    }

    /**
     * Transitions the drawable to the new color.
     *
     * @param newColor
     */
    public void setNewColor(int newColor)
    {
        this.startColor = currentColor;
        this.nextColor = newColor;
        this.startingPoint = System.currentTimeMillis();
        this.done = false;
        invalidateSelf();
    }

    @Override
    public void draw(Canvas canvas)
    {
        if (done) {
            canvas.drawColor(nextColor);
            return;
        }

        if (durationInMs<=1) {
            canvas.drawColor(nextColor);
            done = true;
            return;
        }

        float elapsed = System.currentTimeMillis()-startingPoint;

        if (elapsed>durationInMs) {
            canvas.drawColor(nextColor);
            currentColor = nextColor;
            done = true;
            return;
        }

        float ratio = elapsed/durationInMs;
        int color = (int)evaluator.evaluate(ratio, startColor, nextColor);
        currentColor = color;
        canvas.drawColor(currentColor);
        invalidateSelf();
    }
}
