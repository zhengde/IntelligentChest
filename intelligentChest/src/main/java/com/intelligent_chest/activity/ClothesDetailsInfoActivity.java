package com.intelligent_chest.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.intelligentchest.R;
import com.intelligent_chest.entity.ClothesEntity;
import com.intelligent_chest.util.ActivityUtil;
import com.intelligent_chest.util.MyApplication;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class ClothesDetailsInfoActivity extends Activity implements View.OnClickListener {
    private ImageView mImgClo, mImgBack;
    private ClothesEntity mClothesEntity;
    private Button mButTake;
    private TextView mTvSex, mTvStyle, mTvWashWay, mTvBrand, mTvSize;
    private String mId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_clothes_details_info);
        ActivityUtil.getInstance().addActivity(this);

        init();

        mClothesEntity = (ClothesEntity) getIntent().getSerializableExtra("clo_info");
        if (mClothesEntity != null) {
            mTvSex.setText(mClothesEntity.getSex());
            mTvStyle.setText(mClothesEntity.getStyle());
            mTvBrand.setText(mClothesEntity.getBrand());
            mTvSize.setText(mClothesEntity.getSize());
            mTvWashWay.setText(mClothesEntity.getWashWay());
            mImgClo.setImageBitmap(mClothesEntity.getBitmap());
            mId = mClothesEntity.getId();
        }else{
            // 显示测试数据
            mTvSex.setText("性别：男");
            mTvStyle.setText("服装类型：衬衫");
            mTvBrand.setText("品牌：阿迪达斯");
            mTvSize.setText("尺码：L");
            mTvWashWay.setText("清洗方式：手洗最佳");
            mId = "";
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.clo1);
            mImgClo.setImageBitmap(bitmap);
        }
        //图片放大处理
//        DealBitmapSize();

    }


    /**
     * 放大图片
     */

    private Bitmap DealBitmapSize(Bitmap bitmap, int curHeight, int curWidth) {
        double scale = 2.5f;
        float scaleW = 1.0f, scaleH = 1.0f;
        scaleH = (float) (scale * scaleH);
        scaleW = (float) (scale * scaleW);
        Matrix matrix = new Matrix();
        matrix.postScale(scaleW, scaleH);
        Bitmap resultBitmap = Bitmap.createBitmap(bitmap, 0, 0, curWidth, curHeight, matrix, true);
        return resultBitmap;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_img_back:
                finish();
                break;
            case R.id.id_but_take:
                Toast.makeText(this, "服装区域内已亮灯，请取出服装", Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SendIdToWeb();
                    }
                }).start();
                break;
        }
    }

    /**
     * 当用户点击取出衣服按钮时会向服务器发送该衣服的id
     */
    public void SendIdToWeb() {
        try {
            Socket socket = new Socket(MyApplication.IP, MyApplication.PORT);
            OutputStream os = socket.getOutputStream();
            os.write(mId.getBytes());
            os.flush();
            socket.shutdownOutput();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void init() {
        mImgClo = (ImageView) findViewById(R.id.id_img_clothes);
        mImgBack = (ImageView) findViewById(R.id.id_img_back);
        mButTake = (Button) findViewById(R.id.id_but_take);
        mTvBrand = (TextView) findViewById(R.id.id_tv_brand);
        mTvSex = (TextView) findViewById(R.id.id_tv_sex);
        mTvSize = (TextView) findViewById(R.id.id_tv_size);
        mTvStyle = (TextView) findViewById(R.id.id_tv_style);
        mTvWashWay = (TextView) findViewById(R.id.id_tv_wash_method);

        mButTake.setOnClickListener(this);
        mImgBack.setOnClickListener(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityUtil.getInstance().remove(this);
    }
}
