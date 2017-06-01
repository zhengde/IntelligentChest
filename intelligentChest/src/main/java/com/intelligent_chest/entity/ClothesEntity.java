package com.intelligent_chest.entity;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * 封装查看所有衣服listView要显示的数据属性。
 * 包括衣服图片，性别，品牌，尺码，衣服类型，清洗方式
 */
public class ClothesEntity  implements Serializable {
    private Bitmap mBitmap;
    private String mId, mSex, mBrand, mSize, mStyle, mWashWay;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.mBitmap = bitmap;
    }

    public String getSex() {
        return mSex;
    }

    public void setSex(String sex) {
        this.mSex = sex;
    }

    public String getBrand() {
        return mBrand;
    }

    public void setBrand(String brand) {
        this.mBrand = brand;
    }

    public String getSize() {
        return mSize;
    }

    public void setSize(String size) {
        this.mSize = size;
    }

    public String getStyle() {
        return mStyle;
    }

    public void setStyle(String style) {
        this.mStyle = style;
    }

    public String getWashWay() {
        return mWashWay;
    }

    public void setWashWay(String washWay) {
        this.mWashWay = washWay;
    }
}
