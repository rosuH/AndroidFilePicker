package me.rosuh.filepicker.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * created by wuzhiming on 2018/10/24
 */
public class ScreenUtils {
        /**
     * dp 转 px
     * @param context
     * @param dipValue
     * @return
     */
    public static int dipToPx(Context context, float dipValue) {
        float m = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * m + 0.5f);
    }


    /**
     * px 转 dp
     * @param context
     * @param px
     * @return
     */
    public static float pxToDip(Context context, float px) {
        if (context == null) {
            return -1;
        }
        return px / context.getResources().getDisplayMetrics().density;
    }


    /**
     * sp 转 px
     * @param context
     * @param spValue
     * @return
     */
    public static int spToPx(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }


    /**
     * 获得屏幕宽度像素
     * @param context
     * @return
     */
    public static int getScreenWidthInPixel(Context context)
    {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        int screenW = outMetrics.widthPixels;
        wm = null;
        return screenW;
    }

    /**
     * 获得屏幕高度像素
     * @param context
     * @return
     */
    public static int getScreenHeightInPixel(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        int screenH = outMetrics.heightPixels;
        wm = null;
        return screenH;
    }

    /**
     * 是否横屏
     */
    public static boolean isLandScape(Context context) {
        return getScreenWidthInPixel(context) > getScreenHeightInPixel(context);
    }

    /**
     * 将像素转为 dp
     * @param context
     * @param pxValue 像素
     * @return
     */
    public static int px2dp(Context context, float pxValue){
        return (int)(pxValue / context.getResources().getDisplayMetrics().density);
    }

}
