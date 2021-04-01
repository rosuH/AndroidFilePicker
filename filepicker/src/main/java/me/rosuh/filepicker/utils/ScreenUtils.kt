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

}
