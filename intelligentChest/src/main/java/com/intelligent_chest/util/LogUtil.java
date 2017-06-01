package com.intelligent_chest.util;

import android.util.Log;

/**
 * Log工具类
 * 实际运行机制：当我们还在开发阶段，需要对程序进行测试。
 * 我们在每一个方法里面都增加了if判断，只有当LEVEL常量的值小于或等于对应日志级别的时候，才会将日志打印出来。
 * 我们只需要修改LEVEL的值就能自由地控制日志的打印行为。
 * 比如：让LEVEL=VERBOSE就可以把所有日志都打印出来；让LEVEL等于WARN就可以只打印WARN及以上级别的日志
 * 而让LEVEL=NOTHING就可以把所有日志都屏蔽掉
 * 
 * 使用这种方法后，我们只需要在开发阶段将LEVEL指定成VERBOSE，
 * 当项目正式上线的时候讲LEVEL指定成NOTHING就不会打印出测试信息了
 * */

public class LogUtil {

	public static final int VERBOSE = 1;
	public static final int DEBUG = 2;
	public static final int INFO = 3;
	public static final int WARN = 4;
	public static final int ERROR = 5;
	public static final int NOTHING = 6;
	public static final int LEVEL = VERBOSE;

	public static void v(String tag,String msg){
		if(LEVEL <= VERBOSE){
			Log.v(tag, msg);
		}
	}
	public static void d(String tag,String msg){
		if(LEVEL <= DEBUG){
			Log.d(tag, msg);
		}
	}
	public static void i(String tag,String msg){
		if(LEVEL <= INFO){
			Log.i(tag, msg);
		}
	}
	public static void w(String tag,String msg){
		if(LEVEL <= WARN){
			Log.w(tag, msg);
		}
	}
	public static void e(String tag,String msg){
		if(LEVEL <= ERROR){
			Log.e(tag, msg);
		}
	}
}
