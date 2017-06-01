package com.intelligent_chest.util;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
/**
 * 随时随地退出整个程序，通过使用ActivityUtil对象的finishAll()方法即可，
 * 要实现此功能必须要在每个Activity中add()和remove()【重写onDestroy()方法】
 * 
 * */
public class ActivityUtil {
	// 定义一个activity列表
	private List<Activity> activities = new ArrayList<Activity>();

	// 定义一个类的实例
	private static ActivityUtil instance;
	// 私有构造方法不允许创建类的实例
	private ActivityUtil() {
	}
	public static ActivityUtil getInstance() {
		if (null == instance) {
			instance = new ActivityUtil();
		}
		return instance;
	}

	/**
	 * 如果activity已经 destroy了就移除
	 * 
	 * @param activity
	 */
	public void remove(Activity activity) {
		activities.remove(activity);
	}

	/**
	 * 添加activity
	 * 
	 * @param activity
	 */
	public void addActivity(Activity activity) {
		activities.add(activity);
	}

	/**
	 * 遍历 结束activity 并且退出
	 */
	public void finishAll() {
			for(Activity activity:activities){
				if(!activity.isFinishing()){
					activity.finish();
				}
			}
		}
}
