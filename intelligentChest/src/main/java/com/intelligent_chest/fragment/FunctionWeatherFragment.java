package com.intelligent_chest.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.intelligentchest.R;
import com.mob.mobapi.API;
import com.mob.mobapi.APICallback;
import com.mob.mobapi.MobAPI;
import com.mob.mobapi.apis.Weather;
import com.mob.tools.network.KVPair;
import com.mob.tools.network.NetworkHelper;
import com.mob.tools.utils.Hashon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FunctionWeatherFragment extends Fragment implements APICallback {
    private LinearLayout mTitleLinearLayout, mWeatherLinearLayout, mWeatherView;
    private TextView mTitleWeather, mTitleTemperature, mTitleCity, mDivisionView;
    private ImageView mImg;
    private String mIp = null, mDayInfo = null;
    private Weather mApi;
    private int mFlag = 0;
    // 接口返回的全部天气数据
    private ArrayList<HashMap<String, Object>> mResults;
    // 用于从mResult中取出天气详细数据(result作用域的数据)，详细看接口网址http://api.mob.com/#/mobapi/weather
    private HashMap<String, Object> mWeatherDetails;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mWeatherView = new LinearLayout(getActivity());
        mWeatherLinearLayout = new LinearLayout(getActivity());

        mTitleLinearLayout = new LinearLayout(getActivity());
        mTitleWeather = new TextView(getActivity());
        mTitleTemperature = new TextView(getActivity());
        mTitleCity = new TextView(getActivity());

        MobAPI.initSDK(getActivity(), "ee0efbb5f308");
        // 获取API实例，请求支持预报的城市列表
        mApi = (Weather) MobAPI.getAPI(Weather.NAME);
        mApi.getSupportedCities(this);

        getWeatherData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        initTitleLocation();
        initDivisionView();
        initWeatherDetails();
        mWeatherView.setOrientation(LinearLayout.VERTICAL);

        new Thread() {
            public void run() {
                getWeatherData();
            }
        }.start();

//        return inflater.inflate(R.layout.text_weather,container,false); //写死天气界面
        return mWeatherView;
    }

    /**
     * 该线程的作用是通过第三方工具自动获得app所处城市的ip
     */
    private void getWeatherData() {
        try {
            NetworkHelper network = new NetworkHelper();
            ArrayList<KVPair<String>> values = new ArrayList<KVPair<String>>();
            values.add(new KVPair<String>("ie", "utf-8"));
                    /*
                     * 访问如下链接得到：var returnCitySN =
					 * {"cip":"59.33.247.25","cid":"441300","cname":"广东省惠州市"};
					 */
            String resp = network.httpGet("http://pv.sohu.com/cityjson", values, null, null);
                    /*
                     * 替换后变成：{"cip": "59.33.247.25", "cid": "441300",
					 * "cname":"广东省惠州市"}
					 */
//            System.out.print("===="+values.get(0));
//            System.out.print("===="+resp);
            resp = resp.replace("var returnCitySN = {", "{").replace("};", "}");
            // 根据以上的Json数据来获取"cip"键所对应的城市ip
            mIp = (String) (new Hashon().fromJson(resp).get("cip"));
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * Title布局及控件样式的设置
     */
    private void initTitleLocation() {
        mTitleLinearLayout.setOrientation(LinearLayout.VERTICAL);
        mTitleTemperature.setPadding(2, 2, 2, 5);
        mTitleTemperature.setTextSize(45);
        mTitleTemperature.setGravity(Gravity.CENTER);
        mTitleLinearLayout.addView(mTitleTemperature, 0);

        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        mTitleCity.setTextSize(25);
        mTitleCity.setPadding(2, 2, 5, 5);
        mTitleCity.setGravity(Gravity.CENTER);
        linearLayout.addView(mTitleCity, 0);

        mTitleWeather.setPadding(2, 2, 5, 5);
        mTitleWeather.setTextSize(25);
        mTitleWeather.setGravity(Gravity.CENTER);
        linearLayout.addView(mTitleWeather, 1);

        mTitleLinearLayout.addView(linearLayout, 1);
        mWeatherView.addView(mTitleLinearLayout);
    }

    /**
     * 使mWeatherLinearLayout置于屏幕底部
     */
    private void initDivisionView() {
        mDivisionView = new TextView(getActivity());
        mDivisionView.setVisibility(View.INVISIBLE);
        mDivisionView.setText("" + '\n' + '\n' + '\n' + '\n' + '\n' + '\n' + '\n' + '\n' /* + '\n'
               + '\n' + '\n' + '\n' + '\n'*/);
        mWeatherView.addView(mDivisionView);
    }

    /**
     * 五天内的天气预报布局的设置
     */
    private void initWeatherDetails() {
        mWeatherLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        mWeatherView.addView(mWeatherLinearLayout, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    public void onSuccess(API api, int action, Map<String, Object> result) {
        if (mIp != null) {
            // 防止mWeatherLinearLayout不断重复addView
            if (mFlag >= 2) {
                return;
            } else {
                mFlag++;
                // api.queryByIPAddress()会再去调用onSuccess()
                ((Weather) api).queryByIPAddress(mIp, this);
            }
        }
        switch (action) {
            case Weather.ACTION_IP:
                onWeatherDetailsGot(result);
                break;
        }
    }

    public void onError(API api, int action, Throwable details) {
        details.printStackTrace();
        if (mIp != null) {
            // 防止mWeatherLinearLayout不断重复addView
            if (mFlag >= 2) {
                return;
            } else {
                mFlag++;
                // api.queryByIPAddress()会再去调用onSuccess()
                ((Weather) api).queryByIPAddress(mIp, this);
            }
        }
    }

    /**
     * 显示天气数据
     */
    private void onWeatherDetailsGot(Map<String, Object> result) {
        // 获取接口返回的所有天气数据
        mResults = (ArrayList<HashMap<String, Object>>) result.get("result");
        mWeatherDetails = mResults.get(0);
        // 更新五天内具体的天气情况
        ArrayList<HashMap<String, Object>> weeks = (ArrayList<HashMap<String, Object>>) mWeatherDetails.get("future");
        // 更新Title天气数据(天气情况，城市名)
        mTitleWeather.setText(com.mob.tools.utils.R.toString(mWeatherDetails.get("weather")));
        mTitleTemperature.setText(com.mob.tools.utils.R.toString(mWeatherDetails.get("temperature")));
        mTitleCity.setText(com.mob.tools.utils.R.toString(mWeatherDetails.get("city")));
        // 更新mWeatherLinearLayout天气数据
        for (int i = 0; i < 4; i++) {
            LinearLayout dayWeatherDetails = new LinearLayout(getActivity());
            TextView tvWeek = new TextView(getActivity());
            TextView tvWeekDayTime = new TextView(getActivity());// 白天天气情况（晴）
            TextView tvWeekTemperature = new TextView(getActivity());
            TextView tvdate = new TextView(getActivity());

            tvWeek.setText(com.mob.tools.utils.R.toString(weeks.get(i).get("week")));
            try {
                if ((mDayInfo = weeks.get(i).get("dayTime").toString()) != null) {
                    tvWeekDayTime.setText(com.mob.tools.utils.R.toString(mDayInfo));
                } else {
                    tvWeekDayTime.setText(com.mob.tools.utils.R.toString(weeks.get(i).get("night")));
                }
            } catch (Exception e) {
//             接口不能使用时的测试代码
                tvWeekDayTime.setText("...");
            }
            tvWeekTemperature.setText(com.mob.tools.utils.R.toString(weeks.get(i).get("temperature")));
            String time = com.mob.tools.utils.R.toString(weeks.get(i).get("date"));
            String date = time.substring(5, 7) + "/" + time.substring(8, 10);
            tvdate.setText(date);
            // 接口不能使用时需要注释掉
            try {
                addPicture();
            } catch (Exception e) {
                e.printStackTrace();
            }

            tvWeek.setPadding(0, 10, 0, 10);
            tvWeekDayTime.setPadding(0, 10, 0, 10);
            tvWeekTemperature.setPadding(0, 10, 0, 10);
            tvdate.setPadding(0, 10, 0, 10);

            dayWeatherDetails.addView(tvWeek, 0);
            dayWeatherDetails.addView(tvWeekDayTime, 1);
            try {
                dayWeatherDetails.addView(mImg, 2);
            } catch (Exception e) {
                // 接口不能使用时的测试代码
                dayWeatherDetails.addView(new ImageView(getActivity()), 2);
            }
            dayWeatherDetails.addView(tvWeekTemperature, 3);
            dayWeatherDetails.addView(tvdate, 4);
            dayWeatherDetails.setLayoutParams(
                    new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0.25f));
            dayWeatherDetails.setOrientation(LinearLayout.VERTICAL);
            dayWeatherDetails.setGravity(Gravity.CENTER_HORIZONTAL);

            mWeatherLinearLayout.addView(dayWeatherDetails);
        }
    }

    /**
     * 根据天气情况加载相应的天气图片，19种天气情况，15张背景图片
     */
    private void addPicture() {
        mImg = new ImageView(getActivity());
        Bitmap bitmap;
        if (mDayInfo.contains("晴")) {
            // 得到天气图标的Bitmap对象
            bitmap = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.qing));
            // 获得原图的宽高
            int h = bitmap.getHeight();
            int w = bitmap.getWidth();
            Bitmap resultBitmap = DealBitmapSize(bitmap, h, w);
            mImg.setImageBitmap(resultBitmap);
            return;
        }
        if (mDayInfo.contains("阴")) {
            bitmap = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.yin));
            int h = bitmap.getHeight();
            int w = bitmap.getWidth();
            Bitmap bitmap2 = DealBitmapSize(bitmap, h, w);
            mImg.setImageBitmap(bitmap2);
            mWeatherView.setBackgroundResource(R.drawable.beijingyin);
            return;
        }
        if (mDayInfo.contains("阵雨")) {
            bitmap = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.zhenyu));
            int h = bitmap.getHeight();
            int w = bitmap.getWidth();
            Bitmap bitmap2 = DealBitmapSize(bitmap, h, w);
            mImg.setImageBitmap(bitmap2);
            mWeatherView.setBackgroundResource(R.drawable.beijingzhenyu);
            return;
        }
        if (mDayInfo.contains("小雨")) {
            bitmap = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.xiaoyu));
            int h = bitmap.getHeight();
            int w = bitmap.getWidth();
            Bitmap bitmap2 = DealBitmapSize(bitmap, h, w);
            mImg.setImageBitmap(bitmap2);
            mWeatherView.setBackgroundResource(R.drawable.beijingxiaoyu);
            return;
        }
        if (mDayInfo.contains("中雨")) {
            bitmap = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.zhongyu));
            int h = bitmap.getHeight();
            int w = bitmap.getWidth();
            Bitmap bitmap2 = DealBitmapSize(bitmap, h, w);
            mImg.setImageBitmap(bitmap2);
            mWeatherView.setBackgroundResource(R.drawable.beijingzhongyu);
            return;
        }
        if (mDayInfo.contains("大雨")) {
            bitmap = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.dayu));
            int h = bitmap.getHeight();
            int w = bitmap.getWidth();
            Bitmap bitmap2 = DealBitmapSize(bitmap, h, w);
            mImg.setImageBitmap(bitmap2);
            mWeatherView.setBackgroundResource(R.drawable.beijingdayu);
            return;
        }
        if (mDayInfo.contains("暴雨")) {
            bitmap = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.dayu));
            int h = bitmap.getHeight();
            int w = bitmap.getWidth();
            Bitmap bitmap2 = DealBitmapSize(bitmap, h, w);
            mImg.setImageBitmap(bitmap2);
            mWeatherView.setBackgroundResource(R.drawable.beijingdayu);
            return;
        }
        if (mDayInfo.contains("雨")) {
            bitmap = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.zhenyu));
            int h = bitmap.getHeight();
            int w = bitmap.getWidth();
            Bitmap bitmap2 = DealBitmapSize(bitmap, h, w);
            mImg.setImageBitmap(bitmap2);
            mWeatherView.setBackgroundResource(R.drawable.beijingzhenyu);
            return;
        }
        if (mDayInfo.contains("小雪")) {
            bitmap = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.xiaoxue));
            int h = bitmap.getHeight();
            int w = bitmap.getWidth();
            Bitmap bitmap2 = DealBitmapSize(bitmap, h, w);
            mImg.setImageBitmap(bitmap2);
            mWeatherView.setBackgroundResource(R.drawable.beijingxiaoxue);
            return;
        }
        if (mDayInfo.contains("中雪")) {
            bitmap = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.zhongxue));
            int h = bitmap.getHeight();
            int w = bitmap.getWidth();
            Bitmap bitmap2 = DealBitmapSize(bitmap, h, w);
            mImg.setImageBitmap(bitmap2);
            mWeatherView.setBackgroundResource(R.drawable.beijingzhongxue);
            return;
        }
        if (mDayInfo.contains("大雪")) {
            bitmap = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.daxue));
            int h = bitmap.getHeight();
            int w = bitmap.getWidth();
            Bitmap bitmap2 = DealBitmapSize(bitmap, h, w);
            mImg.setImageBitmap(bitmap2);
            mWeatherView.setBackgroundResource(R.drawable.beijingdaxue);
            return;
        }
        if (mDayInfo.contains("雪")) {
            bitmap = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.xiaoxue));
            int h = bitmap.getHeight();
            int w = bitmap.getWidth();
            Bitmap bitmap2 = DealBitmapSize(bitmap, h, w);
            mImg.setImageBitmap(bitmap2);
            mWeatherView.setBackgroundResource(R.drawable.beijingxiaoxue);
            return;
        }
        if (mDayInfo.contains("霾")) {
            bitmap = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.mai));
            int h = bitmap.getHeight();
            int w = bitmap.getWidth();
            Bitmap bitmap2 = DealBitmapSize(bitmap, h, w);
            mImg.setImageBitmap(bitmap2);
            mWeatherView.setBackgroundResource(R.drawable.beijingmai);
            return;
        }
        if (mDayInfo.contains("雾")) {
            bitmap = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.wu));
            int h = bitmap.getHeight();
            int w = bitmap.getWidth();
            Bitmap bitmap2 = DealBitmapSize(bitmap, h, w);
            mImg.setImageBitmap(bitmap2);
            mWeatherView.setBackgroundResource(R.drawable.beijingwu);
            return;
        }
        if (mDayInfo.contains("多云")) {
            bitmap = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.duoyun));
            int h = bitmap.getHeight();
            int w = bitmap.getWidth();
            Bitmap bitmap2 = DealBitmapSize(bitmap, h, w);
            mImg.setImageBitmap(bitmap2);
            mWeatherView.setBackgroundResource(R.drawable.beijingduoyun);
            return;
        }
        if (mDayInfo.contains("少云")) {
            bitmap = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.shaoyun));
            int h = bitmap.getHeight();
            int w = bitmap.getWidth();
            Bitmap bitmap2 = DealBitmapSize(bitmap, h, w);
            mImg.setImageBitmap(bitmap2);
            mWeatherView.setBackgroundResource(R.drawable.beijingduoyun);
            return;
        }
        if (mDayInfo.contains("云")) {
            bitmap = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.duoyun));
            int h = bitmap.getHeight();
            int w = bitmap.getWidth();
            Bitmap bitmap2 = DealBitmapSize(bitmap, h, w);
            mImg.setImageBitmap(bitmap2);
            mWeatherView.setBackgroundResource(R.drawable.beijingduoyun);
            return;
        }
        if (mDayInfo.contains("雷")) {
            bitmap = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.leizhenyu));
            int h = bitmap.getHeight();
            int w = bitmap.getWidth();
            Bitmap bitmap2 = DealBitmapSize(bitmap, h, w);
            mImg.setImageBitmap(bitmap2);
            mWeatherView.setBackgroundResource(R.drawable.beijingleizhenyu);
            return;
        }
        if (mDayInfo.contains("沙")) {
            bitmap = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.shachenbao));
            int h = bitmap.getHeight();
            int w = bitmap.getWidth();
            Bitmap bitmap2 = DealBitmapSize(bitmap, h, w);
            mImg.setImageBitmap(bitmap2);
            mWeatherView.setBackgroundResource(R.drawable.beijingshachenbao);
            return;
        } // 以防某些天气描述没有考虑而让程序能崩溃
        else {
            bitmap = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.qing));
            int h = bitmap.getHeight();
            int w = bitmap.getWidth();
            Bitmap bitmap2 = DealBitmapSize(bitmap, h, w);
            mImg.setImageBitmap(bitmap2);
            mWeatherView.setBackgroundResource(R.drawable.beijingqing);
        }

        mImg.setPadding(0, 5, 0, 5);
    }

    /**
     * 缩放天气图标,缩小0.5倍
     */
    private Bitmap DealBitmapSize(Bitmap bitmap, int curHeight, int curWidth) {
        double scale = 0.75f;
        float scaleW = 1.0f, scaleH = 1.0f;
        scaleH = (float) (scale * scaleH);
        scaleW = (float) (scale * scaleW);
        Matrix matrix = new Matrix();
        matrix.postScale(scaleW, scaleH);
        Bitmap resultBitmap = Bitmap.createBitmap(bitmap, 0, 0, curWidth, curHeight, matrix, true);
        return resultBitmap;
    }
}
