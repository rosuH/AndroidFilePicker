package me.rosuh.filepicker.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import me.rosuh.filepicker.R;

/**
 * created by wuzhiming on 2018/10/24
 *
 * 设置字体大小时，直接使用px，会自动转换
 */
public class PercentTextView extends AppCompatTextView {

    private float mHRatio = 1f;

    public PercentTextView(Context context) {
        super(context);
        init(context);
    }

    public PercentTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PercentTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context) {
        float screenHeight = ScreenUtils.getScreenHeightInPixel(context);
        mHRatio = screenHeight / PercentLayoutUtils.DESIGN_SCREEN_HEIGHT;
        setTextSize(getTextSize());
    }

    private void init(Context context, AttributeSet attrs) {
        init(context);
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.PercentTextView);
        int paddingStart = arr.getDimensionPixelOffset(R.styleable.PercentTextView_android_paddingStart, 0);
        int paddingTop = arr.getDimensionPixelOffset(R.styleable.PercentTextView_android_paddingTop, 0);
        int paddingBottom = arr.getDimensionPixelOffset(R.styleable.PercentTextView_android_paddingBottom, 0);
        int paddingEnd = arr.getDimensionPixelOffset(R.styleable.PercentTextView_android_paddingEnd, 0);
        arr.recycle();
        setPadding(paddingStart, paddingTop, paddingEnd, paddingBottom);
    }

    @Override
    public void setTextSize(int unit, float size) {
        size = (int)(size * mHRatio);
        super.setTextSize(unit, size);
    }

    @Override
    public void setTextSize(float size) {
        setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        left = (int) (left * mHRatio);
        top = (int) (top * mHRatio);
        right = (int) (right * mHRatio);
        bottom = (int) (bottom * mHRatio);
        super.setPadding(left, top, right, bottom);
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        if (params instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) params;
            layoutParams.leftMargin = (int) (layoutParams.leftMargin * mHRatio);
            layoutParams.topMargin = (int) (layoutParams.topMargin * mHRatio);
            layoutParams.rightMargin = (int) (layoutParams.rightMargin * mHRatio);
            layoutParams.bottomMargin = (int) (layoutParams.bottomMargin * mHRatio);
            layoutParams.setMarginStart((int) (layoutParams.getMarginStart() * mHRatio));
            layoutParams.setMarginEnd((int) (layoutParams.getMarginEnd() * mHRatio));
            super.setLayoutParams(layoutParams);
            return;
        }
        super.setLayoutParams(params);
    }

}
