package com.intelligent_chest.util;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

/*
 * 关于屏幕宽高度的计算：
 * width = widthPixels * density;   height = heightPixels * density  
 * (ps: widthPixels  和  heightPixels  ，density   都是从DisplayMetrics中获取的)。
 *
 */
/**
 * 获取屏幕宽高
 */
public class WindowUtil {
	private static DisplayMetrics dm;
	private static int mScreenWidth, mScreenHeight;

	public static int getScreenWidth(Context context) {
		dm = new DisplayMetrics();
		Activity activity = (Activity) context;
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		mScreenWidth = (int) (dm.widthPixels * dm.density);
		return mScreenWidth;
	}

	public static int getScreenHeight(Context context) {
		dm = new DisplayMetrics();
		Activity activity = (Activity) context;
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		mScreenHeight = (int) (dm.heightPixels * dm.density);
		return mScreenHeight;
	}
}
