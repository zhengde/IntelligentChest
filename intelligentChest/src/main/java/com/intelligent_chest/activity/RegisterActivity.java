package com.intelligent_chest.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.intelligentchest.R;
import com.intelligent_chest.util.ActivityUtil;
import com.intelligent_chest.util.MyApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 实现注册功能
 */
public class RegisterActivity extends Activity implements OnClickListener {
    public final String REGISTER_SUCCESS = "注册成功";
    public final String CHECK_PASSWORD_INFO = "两次密码输入不一致，请重新输入";
    public final String INFO_INCOMPLETE = "您的信息填写尚未完整，请继续填写";
    public final String PASSWORD_TOO_SHORT = "密码应不小于6位";
    public final String ACCOUNT_NAME_EXIST = "该用户名已存在，请更换...";
    public static int PERMISSION = -1;
    public static final int USER_AGREE = 1;
    public static final int USER_DISAGREE = 0;
    public ImageView mImgBack;
    public Button mButFinish;
    public EditText mEtAccountName, mEtPassword, mEtSecPassword;
    public String mAccountName, mPasswrod, mSecondPassword;
    public SharedPreferences mSp;
    public SharedPreferences.Editor mEditor;
    public ActivityUtil mActivityUtil;
    public Boolean mIsExit = false;
    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == USER_AGREE) {
                PERMISSION = USER_AGREE;
                // userAgree();拒绝则不用再写获取数据的代码了，只是模拟~~~~
            } else {
                PERMISSION = USER_DISAGREE;
            }
            rgsiterSuccess();
        }

        ;
    };

    /**
     * 注册成功，跳转到登陆
     */
    private void rgsiterSuccess() {
        Toast.makeText(this, REGISTER_SUCCESS, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);
        mActivityUtil = ActivityUtil.getInstance();
        mActivityUtil.addActivity(this);
        init();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.id_but_register_finish) {
            if (judgeIsReasonable()) {
                return;
            }
            // 弹出dialog，询问用户是否同意智慧衣柜获取数据
            showDialog();
        }
        // 点击了左上角的Back图标
        else {
            finish();
        }
    }

    /**
     * 判断用户要注册的账号和密码是否合理， 不合理返回true，合理返回false
     */
    private Boolean judgeIsReasonable() {
        mAccountName = mEtAccountName.getText().toString().trim();
        mPasswrod = mEtPassword.getText().toString().trim();
        mSecondPassword = mEtSecPassword.getText().toString().trim();
        if (mAccountName.equals("") || mPasswrod.equals("")) {
            Toast.makeText(this, INFO_INCOMPLETE, Toast.LENGTH_SHORT).show();
            return true;
        }
        if (mPasswrod.length() < 6) {
            Toast.makeText(this, PASSWORD_TOO_SHORT, Toast.LENGTH_SHORT).show();
            return true;
        }
        if (!checkPassword()) {
            Toast.makeText(this, CHECK_PASSWORD_INFO, Toast.LENGTH_SHORT)
                    .show();
            return true;
        } else {
            Socket s = null;
            try {
                try {
                    s = new Socket(MyApplication.IP, MyApplication.PORT);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                OutputStream os = s.getOutputStream(); // 发送“a”提醒服务器，app将实现注册功能
                os.write("a".getBytes()); // 发送账号和密码
                os.write(mAccountName.getBytes());
                os.write(mPasswrod.getBytes());
                os.flush();
                //读入流接受服务器，用来接受是否成功注册
                BufferedReader br = new BufferedReader(new
                        InputStreamReader(s.getInputStream())); // 若注册成功，则服务器返回Y，否则返回N
                String feedback = "";
                if ((feedback = br.readLine()).equals("N")) {
                    Toast.makeText(this, ACCOUNT_NAME_EXIST, Toast.LENGTH_SHORT)
                            .show();
                    return true;
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (s != null) {
                        s.shutdownOutput();
                        s.shutdownInput();
                        s.close();
                    }
                } catch
                        (IOException e) {
                    e.printStackTrace();
                }
            }
            // 当用户和密码都合理且不与原有的账号信息冲突时，则返回false，表示注册成功
            return false;
        }
    }

    /**
     * 判断两次输入的密码是否一致，若一致，true，否则返回false
     */
    public boolean checkPassword() {
        return mSecondPassword.equals(mPasswrod);
    }

    /**
     * 弹出dialog，询问用户是否同意智慧衣柜获取数据
     */
    private void showDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("提示").setMessage("为了给您带来更加全面的服务，您是否允许智慧衣柜获取您的衣物信息呢？")
                .setCancelable(false)
                .setPositiveButton("同意", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mHandler.sendEmptyMessage(USER_AGREE);
                    }
                });
        dialog.setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mHandler.sendEmptyMessage(USER_DISAGREE);
            }
        }).show();

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
            //延迟两秒执行此方法————根据schedule方法参数决定如何执行
            //schedule(TimerTask task, long delay);
            tExit.schedule(new TimerTask() {
                public void run() {
                    // 取消退出
                    mIsExit = false;
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
        } else {
            //退出整个Activity
            mActivityUtil.finishAll();
        }
    }

    /**
     * 初始化控件，事件
     */
    private void init() {
        mEtAccountName = (EditText) findViewById(R.id.id_et_register_accountName);
        mEtPassword = (EditText) findViewById(R.id.id_et_register_password);
        mEtSecPassword = (EditText) findViewById(R.id.id_et_secPassword);
        mButFinish = (Button) findViewById(R.id.id_but_register_finish);
        mImgBack = (ImageView) findViewById(R.id.id_img_back);

        mImgBack.setOnClickListener(this);
        mButFinish.setOnClickListener(this);

        mSp = getSharedPreferences("user_data", MODE_PRIVATE);
        mEditor = mSp.edit();

    }

    @Override
    protected void onDestroy() {
        mActivityUtil.remove(this);
        super.onDestroy();
    }
}
