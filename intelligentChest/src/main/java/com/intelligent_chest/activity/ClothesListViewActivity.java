package com.intelligent_chest.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.intelligentchest.R;
import com.intelligent_chest.adapter.ClothesListViewAdapter;
import com.intelligent_chest.entity.ClothesEntity;
import com.intelligent_chest.loader.ImageLoader;
import com.intelligent_chest.util.ActivityUtil;
import com.intelligent_chest.util.MyApplication;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClothesListViewActivity extends Activity {
    private ImageLoader mImageLoader;
    private List<ClothesEntity> mList;
    private ListView mListView;
    private ClothesListViewAdapter mMyAdapter;
    private ClothesEntity mClothesDetailsEntity;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mMyAdapter = new ClothesListViewAdapter(ClothesListViewActivity.this, mList, mListView);
            mListView.setAdapter(mMyAdapter);

            // 要保存起来，在lv跳转到新界面时要取出
            String[] cloStrings = (String[]) msg.obj;
            Intent intent = new Intent();
            intent.putExtra("clo_details", cloStrings);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lv_clothes);
        ActivityUtil.getInstance().addActivity(this);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
                .penaltyLog().penaltyDeath().build());

        mList = new ArrayList<ClothesEntity>();
        mListView = (ListView) findViewById(R.id.id_lv_clothes_info);
        // 通信代码
        mImageLoader = new ImageLoader(mListView);
        new Thread(new CommunicationThread()).start();

        //添加监听，点击listView中的子列表会进行界面跳转，进入该衣服细节介绍界面
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ClothesListViewActivity.this, ClothesDetailsInfoActivity.class);
                // 获得用户点击的是哪个item，将该item的实体数据传给mClothesDetailsEntity
                mClothesDetailsEntity = mList.get(position);
                // mClothesDetailsEntity中的属性数据通过Intent传给ClothesDetailsActivity
                intent.putExtra("clo_info", mClothesDetailsEntity);
                startActivity(intent);
            }
        });
    }

    class CommunicationThread implements Runnable {
        @Override
        public void run() {
            getAllClothesData();
        }

        /**
         * 查看衣物
         */
        private void getAllClothesData() {
            try {
                Socket socket = new Socket(MyApplication.IP, MyApplication.PORT);
                // 获取输出流，向服务器发送消息
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
                bufferedOutputStream.write("See".getBytes());
                bufferedOutputStream.flush();
                // 第一次接收:根据服务器发送过来的衣服信息数组个数，即有多少件衣服信息。
                // 对应去new相应个数的字节数组
                byte[] bs = new byte[1024];
                InputStream is = socket.getInputStream();
                is.read(bs);
                String clo_sum = new String(bs, "utf-8").trim();
                System.out.println("服务器传来【" + clo_sum + "】件衣物的数据");
                String clo_all_infos = new String(bs, "utf-8").trim();
                String temp_clo = clo_all_infos.replace("Over", "").trim();
                String[] clo_all_info = temp_clo.split("o");
                int i = 0;
                //接收从服务器获取到的第一件衣服的属性数据
                String[] clo_info = new String[18];
                // 切割字符串，将各属性抽取出来用于显示在不同控件上
                for (int j = 0; j < (clo_all_info.length); j++) {
                    System.out.println("第" + (i) + "件衣服的数据分别是：" + clo_all_info[j].trim());
                    // 判断clo_info数组是否已经放满一件衣服的数据，是则setLvUI();
                    clo_info[i] = clo_all_info[j];
                    i++;
                    if (i == 6 + (j / 6) * 6) {
                        setLvUI(clo_info, i);
                    }
                    continue;
                }
                socket.shutdownInput();
                socket.shutdownOutput();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * @param clo_info 一件衣服实体类的所有属性数据，一共有6个属性
         * @param index    String[]数组的下标，下标对应相应的属性数据。第一次传过来的下标是6
         *                 设置衣服的实体数据，提供ListView item的数据
         */
        private void setLvUI(String[] clo_info, int index) {
            mClothesDetailsEntity = new ClothesEntity();
            mClothesDetailsEntity.setId(clo_info[index - 6]);
            mClothesDetailsEntity.setSex(clo_info[index - 5]);
            mClothesDetailsEntity.setStyle(clo_info[index - 4]);
            mClothesDetailsEntity.setSize(clo_info[index - 3]);
            mClothesDetailsEntity.setBrand(clo_info[index - 2]);
            mClothesDetailsEntity.setWashWay(clo_info[index - 1]);

            mList.add(mClothesDetailsEntity);
            Message message = Message.obtain();
            message.obj = clo_info;
            mHandler.sendMessage(message);
        }
    }

    @Override
    protected void onDestroy() {
        ActivityUtil.getInstance().remove(this);
        super.onDestroy();

    }
}
