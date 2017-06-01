package com.intelligent_chest.view;

import com.example.intelligentchest.R;
import com.intelligent_chest.util.WindowUtil;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

public class FunctionView extends View {
	private int mColor = 0xFF45C01A;
	private Bitmap mIconBitmap;
	private String mText = "";
	private int mTextSize = (int) TypedValue.applyDimension(
			TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics());

	private Canvas mCanvas;
	private Bitmap mBitmap;
	private Paint mPaint;

	private float mAlpha;

	private Rect mIconRect;
	private Rect mTextBound= new Rect();
	private Paint mTextPaint= new Paint();

	public FunctionView(Context context) {
		this(context, null);
	}

	public FunctionView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	/**
	 * 获取自定义属性的值
	 */
	public FunctionView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.BottomBar);

		int n = a.getIndexCount();

		for (int i = 0; i < n; i++) {
			int attr = a.getIndex(i);
			switch (attr) {
			case R.styleable.BottomBar_icon:
				BitmapDrawable drawable = (BitmapDrawable) a.getDrawable(attr);
				mIconBitmap = drawable.getBitmap();
				break;
			case R.styleable.BottomBar_color:
				mColor = a.getColor(attr, 0xFF45C01A);
				break;
			case R.styleable.BottomBar_text:
				mText = a.getString(attr);
				break;
			case R.styleable.BottomBar_text_size:
				mTextSize = (int) a.getDimension(attr, TypedValue
						.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12,
								getResources().getDisplayMetrics()));
				break;
			}

		}

		a.recycle();

		mTextPaint.setTextSize(mTextSize);
		mTextPaint.setColor(0Xff555555);
		mTextPaint.getTextBounds(mText, 0, mText.length(), mTextBound);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int iconWidth = Math.min(getMeasuredWidth() - getPaddingLeft()
				- getPaddingRight(), getMeasuredHeight() - getPaddingTop()
				- getPaddingBottom() - mTextBound.height());

		int left = getMeasuredWidth() / 2 - iconWidth / 2;
		int top = getMeasuredHeight() / 2 - (mTextBound.height() + iconWidth)
				/ 2;
		mIconRect = new Rect(left, top, left + iconWidth, top + iconWidth);
		
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(mIconBitmap, null, mIconRect, null);

		int alpha = (int) Math.ceil(255 * mAlpha);

		setupTargetBitmap(alpha);
		drawSourceText(canvas, alpha);
		drawTargetText(canvas, alpha);

		canvas.drawBitmap(mBitmap, 0, 0, null);
	}
	
	/**
	 * 在内存中绘制可变色的Icon
	 */
	private void setupTargetBitmap(int alpha) {
		mBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(),
				Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
		mPaint = new Paint();
		mPaint.setColor(mColor);
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setAlpha(alpha);
		mCanvas.drawRect(mIconRect, mPaint);
		mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		mPaint.setAlpha(255);
		mCanvas.drawBitmap(mIconBitmap, null, mIconRect, mPaint);
	}
	
	/**
	 * 绘制原文本
	 */
	private void drawSourceText(Canvas canvas, int alpha) {
		mTextPaint.setColor(0xff333333);
		mTextPaint.setAlpha(255 - alpha);
		int x = getMeasuredWidth() / 2 - mTextBound.width() / 2;
		int y = mIconRect.bottom + mTextBound.height();
		canvas.drawText(mText, x, y, mTextPaint);
	}
	
	/**
	 * 绘制变色的文本
	 */
	private void drawTargetText(Canvas canvas, int alpha) {
		mTextPaint.setColor(mColor);
		mTextPaint.setAlpha(alpha);
		int x = getMeasuredWidth() / 2 - mTextBound.width() / 2;
		int y = mIconRect.bottom + mTextBound.height();
		canvas.drawText(mText, x, y, mTextPaint);
	}

	private static final String INSTANCE_STATUS = "instance_status";
	private static final String STATUS_ALPHA = "status_alpha";

	@Override
	protected Parcelable onSaveInstanceState() {
		Bundle bundle = new Bundle();
		bundle.putParcelable(INSTANCE_STATUS, super.onSaveInstanceState());
		bundle.putFloat(STATUS_ALPHA, mAlpha);
		return bundle;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		if (state instanceof Bundle) {
			Bundle bundle = (Bundle) state;
			mAlpha = bundle.getFloat(STATUS_ALPHA);
			super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATUS));
			return;
		}
		super.onRestoreInstanceState(state);
	}

	public void setIconAlpha(float alpha) {
		this.mAlpha = alpha;
		invalidateView();
	}

	/**
	 * 在UI线程或是子线程都能重绘
	 */
	private void invalidateView() {
		if (Looper.getMainLooper() == Looper.myLooper()) {
			invalidate();
		} else {
			postInvalidate();
		}
	}

}
