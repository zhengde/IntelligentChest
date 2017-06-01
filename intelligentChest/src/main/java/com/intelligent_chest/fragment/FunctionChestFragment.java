package com.intelligent_chest.fragment;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.intelligentchest.R;
import com.intelligent_chest.activity.FunctionActivity;
import com.intelligent_chest.util.MyApplication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 衣柜Fragment
 */
public class FunctionChestFragment extends Fragment implements OnClickListener {
    // 下拉刷新布局
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mTvCalClos, mTvCalPants;
    private LinearLayout mLayoutClothes, mLayoutPants, mLayoutSeeAll;
    private View mView;

    // 以下是ViewPager的相关变量
    private ViewPager mViewPager = null;
    private ImageView[] imageViews = null;
    private ImageView imageView = null;
    private AtomicInteger what = new AtomicInteger(0);
    private boolean isContinue = true;

    private final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mViewPager.setCurrentItem(msg.what);
            // 刷新UI
            Bundle bundle = msg.getData();
//            if (bundle != null || bundle.getString("cloNum").equals(null) || bundle.getString("pantsNum").equals(null)) {
//                mTvCalClos.setText("衣服:" + bundle.getString("cloNum") + "件");
//                mTvCalPants.setText("裤子:" + bundle.getString("pantsNum") + "件");
//            } else {
            mTvCalClos.setText("衣服:" + 3 + "件");
            mTvCalPants.setText("裤子:" + 0 + "件");
//            }
//            Toast.makeText(getActivity(), "刷新完成...", Toast.LENGTH_SHORT).show();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_function_chest, container, false);
//        getCloNumFromWeb();//服务器代码加上了再加
        return mView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        getCloNumFromWeb();不加看可不可以
        init();
        initViewPager();
        initSwipeRefreshLayout();
    }

    /**
     * 初始化控件和事件
     */
    private void init() {
        mLayoutClothes = (LinearLayout) mView.findViewById(R.id.id_layout_chest_cal_clo);
        mLayoutPants = (LinearLayout) mView.findViewById(R.id.id_layout_chest_cal_pants);
        mLayoutSeeAll = (LinearLayout) mView.findViewById(R.id.id_layout_chest_see);

        mTvCalClos = (TextView) mLayoutClothes.findViewById(R.id.id_tv_chest_cal_clo);
        mTvCalPants = (TextView) mLayoutPants.findViewById(R.id.id_tv_chest_cal_pants);
        OnClickListener onClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.id_layout_chest_see:
                        ((FunctionActivity) getActivity()).onChestLayoutClick(v);
                        break;
                }
            }
        };
        mLayoutClothes.setOnClickListener(onClickListener);
        mLayoutPants.setOnClickListener(onClickListener);
        mLayoutSeeAll.setOnClickListener(onClickListener);

    }

    public void initViewPager() {
        mViewPager = (ViewPager) mView.findViewById(R.id.id_viewpager_adv);
        // 这里存放的是四张广告背景
        List<View> advPics = new ArrayList<View>();
        ImageView iv_1 = new ImageView(getActivity());
        iv_1.setBackgroundResource(R.drawable.home_a1);
        advPics.add(iv_1);
        ImageView iv_2 = new ImageView(getActivity());
        iv_2.setBackgroundResource(R.drawable.home_a2);
        advPics.add(iv_2);
        ImageView iv_3 = new ImageView(getActivity());
        iv_3.setBackgroundResource(R.drawable.home_a3);
        advPics.add(iv_3);
        ImageView iv_4 = new ImageView(getActivity());
        iv_4.setBackgroundResource(R.drawable.home_a4);
        advPics.add(iv_4);

        // 对imageviews进行填充
        ViewGroup group = (ViewGroup) mView.findViewById(R.id.viewGroup);
        imageViews = new ImageView[advPics.size()];
        // 小图标
        for (int i = 0; i < advPics.size(); i++) {
            imageView = new ImageView(getActivity());
            imageView.setLayoutParams(new LayoutParams(5, 5));
            imageView.setPadding(10, 10, 10, 10);
            imageViews[i] = imageView;
            if (i == 0) {
                imageViews[i].setBackgroundResource(R.drawable.main_dark_dot);
            } else {
                imageViews[i].setBackgroundResource(R.drawable.main_white_dot);
            }
            group.addView(imageViews[i]);
        }

        mViewPager.setAdapter(new com.intelligent_chest.adapter.AdvAdapter(advPics));
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                what.getAndSet(arg0);
                for (int i = 0; i < imageViews.length; i++) {
                    imageViews[arg0].setBackgroundResource(R.drawable.main_dark_dot);
                    if (arg0 != i) {
                        imageViews[i].setBackgroundResource(R.drawable.main_white_dot);
                    }
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }
        });

        mViewPager.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
//                        mSwipeRefreshLayout.setRefreshing(false);
                        isContinue = false;
                        break;
                    case MotionEvent.ACTION_UP:
//                        mSwipeRefreshLayout.setRefreshing(true);
                        isContinue = true;
                        break;
                    default:
                        isContinue = true;
                        break;
                }
                return false;
            }
        });
        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    if (isContinue) {
                        mHandler.sendEmptyMessage(what.get());
                        whatOption();
                    }
                }
            }
        }).start();
    }

    private void whatOption() {
        what.incrementAndGet();
        if (what.get() > imageViews.length - 1) {
            what.getAndAdd(-4);
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {

        }
    }

    /**
     * 初始化下拉布局
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void initSwipeRefreshLayout() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.id_layout_swipe_refresh);
        // 设置刷新时进度动画的颜色变化
        mSwipeRefreshLayout.setColorScheme(R.color.swipe_color_1,
                R.color.swipe_color_2,
                R.color.swipe_color_3,
                R.color.swipe_color_4);

        // 设置进度圈大小
        mSwipeRefreshLayout.setScrollBarSize(SwipeRefreshLayout.LAYER_TYPE_NONE);
        // 设置进度圈背景色
        mSwipeRefreshLayout.setBackgroundColor(getResources().getColor(R.color.swipe_background_color));
        /**
         *  @param scale 进度圈是否在下拉过程中进行缩放
         *  @param end 设置进度圈的最大下拉高度
         * */
        mSwipeRefreshLayout.setPressed(true);
        // 设置手势滑动监听器
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCloNumFromWeb();
            }
        });
    }

    @Override
    public void onClick(View v) {
        ((FunctionActivity) getActivity()).onChestLayoutClick(v);
    }

    /**
     * 第二行的所有生命回调方法是一个执行回路，当且仅当一个Fragment从后台回到前台执行且未被销毁时执行
     * Fragment生命周期：onAttach，onCreate，
     * onCreateView，onActivityCreate，onStart，onResume，onPause，onStop，onDestroyView
     * onDestroy，onDetach
     * <p/>
     * <p/>
     * 在不同生命周期调用有不同的作用：
     * 在onCreateView：刚进入界面时从服务器获取衣柜现有衣服数据
     * 在onActivityCreated中的initSwipeRefreshLayout：
     * 作用1：下拉也可以刷新衣柜中衣物件数的数据
     * 作用2：当进入新的Activity(如ClothesDetailsInfoActivity)后又返回该Activity时,
     * Fragment会重新获取衣物件数数据，确保数据不会丢失，实时显示
     */
    public void getCloNumFromWeb() {
        Toast.makeText(getActivity(), "刷新完成...", Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket(MyApplication.IP, MyApplication.PORT);
                    // 发送标记符给服务器，刷新UI
                    OutputStream os = socket.getOutputStream();
                    os.write("SeeNum".getBytes());
                    os.flush();
                    InputStream is = socket.getInputStream();
                    byte[] bytes = new byte[10];
                    is.read(bytes);
                    String sum = new String(bytes, "utf-8").trim();
                    // 切割之后，第一个是衣服件数，第二个是裤子件数
                    String[] sums = sum.split("o");
                    // 通知UI线程更新UI
                    Message message = Message.obtain();
                    Bundle bundle = new Bundle();
                    bundle.putString("cloNum", sums[0]);
                    bundle.putString("pantsNum", sums[1]);
                    message.setData(bundle);
                    mHandler.sendMessage(message);

                    socket.shutdownInput();
                    socket.shutdownOutput();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
