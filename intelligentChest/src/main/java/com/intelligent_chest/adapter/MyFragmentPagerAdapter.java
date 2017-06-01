package com.intelligent_chest.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.ViewGroup;

import java.util.List;
/**
 * 作为functionActivity中ViewPager的数据适配器
 * */
public class MyFragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {
	private List<Fragment> mFragmentList;
	public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> wFragmentList) {
		super(fm);
		this.mFragmentList = wFragmentList;
	}

	@Override
	public Fragment getItem(int arg0) {
		return mFragmentList.get(arg0);
	}

	@Override
	public int getCount() {
		return mFragmentList.size();
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		super.destroyItem(container, position, object);
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		return super.instantiateItem(container, position);
	}

}
