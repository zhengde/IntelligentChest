package com.intelligent_chest.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.intelligentchest.R;
import com.gc.materialdesign.views.CheckBox;
import com.intelligent_chest.activity.FunctionActivity;

//import com.intelligent_chest.fragment.FunctionMineFragment.onLayoutClickListener;

/**
 * 1.版本更新 2.关于我们 3.意见反馈 4.时尚达人商城 layout点击时的效果用drawable实现，不用水波纹
 */
public class FunctionMineFragment extends Fragment {
	private View mView;
//	private ImageView mImg;
	private LinearLayout mLayoutFashionistasMall, mLayoutCheckVersion, mLayoutPermission, mLayoutFeedback, mLayoutAboutUs;
    private CheckBox mCheckBox;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_function_mine, container, false);
		return mView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		init();
//		setImgAttribute();
	}

	/**
	 * 初始化
	 */
	private void init() {
		mLayoutFashionistasMall = (LinearLayout) mView.findViewById(R.id.id_layout_fashionistas_mall);
		mLayoutCheckVersion = (LinearLayout) mView.findViewById(R.id.id_layout_check_version);
		mLayoutFeedback = (LinearLayout) mView.findViewById(R.id.id_layout_feedback);
		mLayoutAboutUs = (LinearLayout) mView.findViewById(R.id.id_layout_about_us);
        mLayoutPermission = (LinearLayout) mView.findViewById(R.id.id_layout_permission);
//        mImg = (ImageView) mView.findViewById(R.id.id_img_portrait);
        click();
	}

	/**
	 * 各布局及控件的点击效果
	 */
	private void click() {
		/*mImg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((FunctionActivity) getActivity()).onMineLayoutClick(v);
			}
		});*/
		mLayoutFashionistasMall.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((FunctionActivity) getActivity()).onMineLayoutClick(v);
			}
		});
		mLayoutCheckVersion.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((FunctionActivity) getActivity()).onMineLayoutClick(v);
			}
		});
		mLayoutFeedback.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((FunctionActivity) getActivity()).onMineLayoutClick(v);
			}
		});
		mLayoutAboutUs.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((FunctionActivity) getActivity()).onMineLayoutClick(v);
			}
		});
        mLayoutPermission.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FunctionActivity) getActivity()).onMineLayoutClick(v);
            }
        });
	}

	/**
	 * 设置图片属性
	 */
	/*private void setImgAttribute() {
		int screenHeight = WindowUtil.getScreenHeight(getActivity());
		int screenWidth = WindowUtil.getScreenWidth(getActivity());
		LayoutParams params = mImg.getLayoutParams();
		params.width = screenWidth / 5;
		params.height = screenHeight / 6;
		mImg.setLayoutParams(params);
		mImg.setPadding(0, screenHeight / 15, 0, 45);
		// 后期完善再添加从本地获取照片功能
		// mImg.setImageResource(R.drawable.baoyu);
	}*/

}
