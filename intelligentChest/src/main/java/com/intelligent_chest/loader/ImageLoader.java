package com.intelligent_chest.loader;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.LruCache;
import android.widget.ImageView;
import android.widget.ListView;

import com.intelligent_chest.entity.ClothesEntity;
import com.intelligent_chest.util.MyApplication;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Created by Czd on 2016/5/30.
 * 使用LruCache缓存机制来缓存从服务器中获取到的图片
 */
public class ImageLoader {
    private ImageView mImageView;
    // 类似于Map集合，K-V类型
    private LruCache<String, Bitmap> mLruCache;
    private List<ClothesEntity> mList;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            // 将衣服图片显示在界面上
            if (mImageView == null) {
                System.out.println("Handler null");
            } else
                mImageView.setImageBitmap((Bitmap) msg.obj);
        }
    };

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    public ImageLoader(ListView listView) {
        // 获得当前运行环境的最大内存空间
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        // 设置LruCache所拥有的内存空间大小
        int cacheSize = maxMemory / 4;
        mLruCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                // 该方法在每次Bitmap加入缓存时调用
                // 固定写法，返回作为参数传入的Bitmap内存大小。默认是返回的是元素的个数super.sizeOf(key, value);
                return value.getByteCount();
            }
        };
    }

    /**
     * 将Bitmap增加到缓存
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    public void addBitmapToCache(String id, Bitmap bitmap) {
        if (getBitmapFromCache(id) == null) {
            System.out.println("addBitmapToCache");
            mLruCache.put(id, bitmap);
        }
    }

    /**
     * 从缓存中获得Bitmap
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    public Bitmap getBitmapFromCache(String id) {
        Bitmap bitmap = mLruCache.get(id);
        if (bitmap == null) {
            System.out.println("Bitmap为null");
        }
        return bitmap;
    }

    /**
     * 显示图片
     */
    public void showImage(String id, ImageView imageView) {
        System.out.println("showImage");
        // 从缓存中取出对应的图片
        Bitmap bitmap = getBitmapFromCache(id);
        if (bitmap == null) {
            // 缓存没有图片(为null)，说明从没下载该bitmap，所以开启线程与服务器进行通信获取图片
            new Thread(new GetBitmapThread(id, imageView)).start();
        } else {
            // 缓存中有该图片，直接set
            System.out.println("图片已有缓存，直接显示");
            imageView.setImageBitmap(bitmap);
        }
    }

    /**
     * 功能:1.与服务器通信获得衣服对应的图片
     * 2.判断图片是否在缓存中，根据不同结果进行图片加载
     */
    private class GetBitmapThread implements Runnable {
        //        private ImageView mImageView;
        private String mId;

        public GetBitmapThread(String id, ImageView imageView) {
            ImageLoader.this.mImageView = imageView;
            mId = id;
        }

        @Override
        public void run() {
            // 根据传递过来的id值先判断之前是否已经接收过该图片，有则直接显示该图片，没有则接收并将其添加到缓存中
            Bitmap bitmap = getFromWeb(mId);
//            addBitmapToCache(mId, bitmap);
            Message message = Message.obtain();
            message.obj = bitmap;
            mHandler.sendMessage(message);
        }
    }

    /**
     * 服务器通信获得衣服对应的图片
     *
     * @param id 衣服id
     */
    public Bitmap getFromWeb(String id) {
        Bitmap bitmap = null;
        try {
            Socket socket = new Socket(MyApplication.IP, MyApplication.PORT);
            System.out.println("getFromWeb");
            ByteArrayOutputStream bos;
            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();
            // 向服务器发送接收图片的标记符
            os.write("Clothes".getBytes());
            os.flush();
            // 接收服务器发送的"ok"，将2次输出流断开
            byte[] b = new byte[1024];
            is.read(b);
            System.out.println(new String(b).trim());
            // 向服务器发送衣服id，希望服务器发送该对应该id号衣服的图片给客户端
            os.write(id.getBytes());
            os.flush();
            b = new byte[1024];
            is.read(b, 0, b.length);
            os.write("ok".getBytes());
            os.flush();
            // 获取图片总的字节长度大小
            String sLen = new String(b).trim();
            System.out.println("图片大小为：" + sLen);
            int totalLen = 0;
            if (!sLen.equals("")) {
                totalLen = Integer.parseInt(sLen);
            }
            int picSum = totalLen;
            // read()方法之间用一个write隔开，避免两个输入流的数据粘合在一起。
            // 服务器是没有read这个ok的。具体如何加看现象。。。
            bos = new ByteArrayOutputStream(totalLen);
            // len表示一次循环接收到的图片字节长度大小
            int len = 0;
            b = new byte[1024 * 2];
            int i = 0;
            while ((len = is.read(b, 0, b.length)) != -1) {
                bos.write(b, 0, len);
                // 循环过程中用总长度-每次循环得到的长度，当totalLen<=0时说明图片已经完全接收，此时需要退出循环，不再接收图片数据
                totalLen -= len;
                if (totalLen <= 0) {
                    break;
                }
            }
            System.out.println("即将解析字节流成bitmap");
            if (getBitmapFromCache(id) == null) {
                bitmap = BitmapFactory.decodeByteArray(bos.toByteArray(), 0, bos.size());
                bos.close();
                if (bitmap != null) {
                    addBitmapToCache(id, bitmap);
                    System.out.println("已解析字节流成bitmap");
                } else {
                    System.out.println("decodeByteArray时bitmap为null");
                }
            } else {
                System.out.println("图片已在缓存中，与服务器结束通信");
                is.close();
                os.close();
                socket.close();
            }
            is.close();
            os.close();
            socket.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

}
