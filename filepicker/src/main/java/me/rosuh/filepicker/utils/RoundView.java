package me.rosuh.filepicker.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author rosuh
 * @date 2019/1/6
 */
public class RoundView extends View {

    public RoundView(final Context context) {
        super(context);
    }

    public RoundView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private RectF rectF = new RectF();
    private float[] radii = {4f, 4f, 4f, 4f, 4f, 4f, 4f, 4f};
    private Path mPath = new Path();

    @Override
    protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        rectF.left = getPaddingLeft();
        rectF.top = getPaddingTop();
        rectF.right = w - getPaddingRight();
        rectF.bottom = h - getPaddingBottom();
        mPath.addRoundRect(rectF, radii, Path.Direction.CW);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        canvas.clipPath(mPath);
        super.onDraw(canvas);
    }
}
