package com.intelligent_chest.util;

import android.app.Application;
import android.content.Context;
/**
 * 全局获取context
 * 1.原理：重写父类的onCreate()方法，通过调用getApplicationContext()方法得到一个
 * 应用程序级别的Context对象，然后又提供了一个静态的getContext()方法，
 * 在这里将刚才获取到的Context对象进行返回
 * 
 * 2.使用示例：
 * MyTextDemo{
 * 	textDemo(){
 * 		Toast.makeText(Myapplication.getContext(),"演示代码",Toast.LENGTH_SHORT).show();
 * 	}
 * }
 * 
 * 3.【注意:】要使用此类还必须要在AndroidManifest.xml文件的<application>标签下进行指定
 * 代码如右：android:name="com.intelligent_chest.util.MyApplication"。要有完整包名
 * 
 * */

public class MyApplication extends Application {
    public final static String IP = "192.168.1.100";
//    public final static String IP = "192.168.145.128";
    public final static int PORT = 8888;
	private static Context context;
	
	@Override
	public void onCreate() {
		super.onCreate();
		context = getApplicationContext();
	}
	
	public static Context getContext() {
		return context;
	}
}
