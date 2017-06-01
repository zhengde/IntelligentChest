package com.intelligent_chest.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.example.intelligentchest.R;
import com.intelligent_chest.adapter.MyFragmentPagerAdapter;
import com.intelligent_chest.fragment.FunctionChestFragment;
import com.intelligent_chest.fragment.FunctionMineFragment;
import com.intelligent_chest.fragment.FunctionWeatherFragment;
import com.intelligent_chest.util.ActivityUtil;
import com.intelligent_chest.view.FunctionView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 含有三个Fragment【衣柜，搭配，我的】 【衣柜】：显示放，取，增（新买的），删（不在穿了）查衣服的功能按钮(5个) 【搭配】：天气和搭配结合
 * 【我的】：个人信息，衣柜内衣物数量，类型等
 */
public class FunctionActivity extends FragmentActivity implements OnClickListener, OnPageChangeListener {
    private ActivityUtil mActivityUtil;
    private Boolean mIsExit = false;
    private ViewPager mViewPager;
    private FunctionChestFragment mChestFragment;
    private FunctionWeatherFragment mMatchFragment;
    private FunctionMineFragment mMineFragment;
    private android.support.v4.app.FragmentPagerAdapter mAdapter;
    /**
     * 管理3个Fragment，因此拥有Fragment对应的数据（Fragment中的所有控件），
     * 最后作为参数（数据源）传递给MyFragmentPagerAdapter
     */
    private List<Fragment> mTabs = new ArrayList<Fragment>();
    /**
     * 管理底部栏中的3个FunctionView，因此拥有FunctionView对应的数据（文字，图片，字体大小等），
     * 应用于点击，滑动事件中的页卡切换，颜色渐变
     */
    private List<FunctionView> mTabIndicators = new ArrayList<FunctionView>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_function);
        mActivityUtil = ActivityUtil.getInstance();
        mActivityUtil.addActivity(this);
        initView();
        initDatas();
    }

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.id_viewPager);

        FunctionView chest = (FunctionView) findViewById(R.id.id_indicator_chest);
        mTabIndicators.add(chest);
        FunctionView match = (FunctionView) findViewById(R.id.id_indicator_match);
        mTabIndicators.add(match);
        FunctionView mine = (FunctionView) findViewById(R.id.id_indicator_mine);
        mTabIndicators.add(mine);

        chest.setOnClickListener(this);
        match.setOnClickListener(this);
        mine.setOnClickListener(this);

        chest.setIconAlpha(1.0f);
    }

    /**
     * 初始化Fragment的内容，并将Fragment添加到List集合中
     * <p>
     * 【思考】：每次初始化FunctionActivity就要初始化Fragment，那么原先在Fragment中的数据是否会丢失~~。
     * 知识整理中有解决思路
     */
    @SuppressWarnings("deprecation")
    private void initDatas() {
        // 初始化Fragment的内容，并将Fragment添加到List集合中
        if (mChestFragment == null) {
            mChestFragment = new FunctionChestFragment();
            mTabs.add(mChestFragment);
        }
        if (mMatchFragment == null) {
            mMatchFragment = new FunctionWeatherFragment();
            mTabs.add(mMatchFragment);
        }
        if (mMineFragment == null) {
            mMineFragment = new FunctionMineFragment();
            mTabs.add(mMineFragment);
        }
        if (mAdapter == null)
            mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), mTabs);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(this);
    }


    @Override
    public void onClick(View v) {
        clickTab(v);
    }

    /**
     * 点击底部Tab时进行页卡切换，事先在xml中设置好的颜色(绿色)瞬间显示出来 bottom:bgColor="#ff45c01a"
     */
    private void clickTab(View v) {
        resetOtherTabs();
        switch (v.getId()) {
            case R.id.id_indicator_chest:
                mTabIndicators.get(0).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(0, false);
                break;
            case R.id.id_indicator_match:
                mTabIndicators.get(1).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(1, false);
                break;
            case R.id.id_indicator_mine:
                mTabIndicators.get(2).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(2, false);
                break;
        }
    }

    /**
     * 通过透明度来恢复底部栏的文字和图片的颜色(灰色)
     */
    private void resetOtherTabs() {
        for (int i = 0; i < mTabIndicators.size(); i++) {
            mTabIndicators.get(i).setIconAlpha(0);
        }
    }

    /**
     * 实现滑动过程中，底部栏文字和图片的颜色渐变
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (positionOffset > 0) {
            FunctionView left = mTabIndicators.get(position);
            FunctionView right = mTabIndicators.get(position + 1);
            left.setIconAlpha(1 - positionOffset);
            right.setIconAlpha(positionOffset);
        }

    }

    /**
     * Chest界面中的按钮被点击后执行
     */
    public void onChestLayoutClick(View v) {
        switch (v.getId()) {
            case R.id.id_layout_chest_see:
                Intent intent = new Intent(FunctionActivity.this, ClothesListViewActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    /**
     * MineFragment中的Layout被点击后执行
     */
    public void onMineLayoutClick(View v) {
        switch (v.getId()) {
            case R.id.id_layout_fashionistas_mall:
                Intent intent = new Intent(this, FashionistasMallActivity.class);
                startActivity(intent);
                break;
            case R.id.id_layout_check_version:
                Toast.makeText(FunctionActivity.this, "已经是最新版本了", Toast.LENGTH_SHORT).show();
                break;
            case R.id.id_layout_feedback:
                showFeedbackDialog();
                break;
            case R.id.id_layout_about_us:
                showAboutDialog();
                break;
            case R.id.id_layout_permission:
                showModifyPermission();
                break;
//            case R.id.id_img_portrait:
//                Toast.makeText(FunctionActivity.this, "img_portrait", Toast.LENGTH_SHORT).show();
//                break;
        }
    }


    /**
     * 显示"意见反馈"dialog
     */
    private void showFeedbackDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("意见反馈");
        View view = getLayoutInflater().inflate(R.layout.dialog_feedback, null);
        alertDialog.setView(view);
        final EditText et = (EditText) view.findViewById(R.id.id_layout_dialog).findViewById(R.id.id_et_feedback);
        alertDialog.setPositiveButton("确定提交", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (et.getText().toString().length() <= 0) {
                    Toast.makeText(FunctionActivity.this, "您的意见还未填写", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(FunctionActivity.this, "感谢您的意见，我们会尽快改善", Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog.show();
    }


    /**
     * 显示"关于我们"dialog
     */
    private void showAboutDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("关于我们").setMessage("惠州学院嵌入式实验室团队").show();
    }

    /**
     * 显示“修改权限”dialog
     */
    private void showModifyPermission() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("修改权限").setMessage("   为了给您带来更加全面的服务，您是否允许智慧衣柜获取您的衣物信息？");
        alertDialog.setPositiveButton("同意", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(FunctionActivity.this, "您已同意", Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog.setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(FunctionActivity.this, "您已拒绝", Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog.show();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 调用双击退出函数
            exitByTwoClick();
        }
        return false;
    }

    /**
     * 连续双击退出程序
     */
    public void exitByTwoClick() {
        Timer tExit = null;
        if (mIsExit == false) {
            // 准备退出
            mIsExit = true;
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            // 延迟两秒执行此方法————根据schedule方法参数决定如何执行
            tExit.schedule(new TimerTask() {
                public void run() {
                    // 取消退出
                    mIsExit = false;
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
        } else {
            // 退出整个Activity
            mActivityUtil.finishAll();
        }
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    protected void onDestroy() {
        mActivityUtil.remove(this);
        super.onDestroy();
    }


}
