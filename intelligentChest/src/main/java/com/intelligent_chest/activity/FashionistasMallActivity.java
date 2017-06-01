package com.intelligent_chest.activity;

import com.intelligent_chest.util.ActivityUtil;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

/**
 * 加上颜色渐变效果
 */
public class FashionistasMallActivity extends Activity {
	private ActivityUtil mActivityUtil;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivityUtil = ActivityUtil.getInstance();
		mActivityUtil.addActivity(this);
		
		TextView tv = new TextView(this);
		tv.setText("时尚达人商城正在准备，敬请期待...");
		tv.setGravity(Gravity.CENTER);
		tv.setTextSize(15.0f);

		setContentView(tv);
	}
	@Override
	protected void onDestroy() {
		mActivityUtil.remove(this);
		super.onDestroy();
	}

}
