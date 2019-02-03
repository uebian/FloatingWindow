package com.yzrilyzr.ui;
import android.widget.*;
import android.util.*;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;

public class myRoundDrawable extends Drawable
{
	private Paint mPaint;  
	private int mWidth;  
	private Bitmap mBitmap ;   

	public myRoundDrawable(Bitmap bitmap)  
	{  
		mBitmap = bitmap ;   
		init();
	}  

	public myRoundDrawable(Context ctx,int id)
	{
		mBitmap=BitmapFactory.decodeResource(ctx.getResources(),id);
		init();
	}
	private void init()
	{
		BitmapShader bitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP,  
													 Shader.TileMode.CLAMP);  
		mPaint = new Paint();  
		mPaint.setAntiAlias(true);  
		mPaint.setShader(bitmapShader);  
		mWidth = Math.min(mBitmap.getWidth(), mBitmap.getHeight());  

	}
	@Override  
	public void draw(Canvas canvas)  
	{  
		canvas.drawCircle(mWidth / 2, mWidth / 2, mWidth / 2, mPaint);  

	}  

	@Override  
	public int getIntrinsicWidth()  
	{  
		return mWidth;  
	}  

	@Override  
	public int getIntrinsicHeight()  
	{  
		return mWidth;  
	}  

	@Override  
	public void setAlpha(int alpha)  
	{  
		mPaint.setAlpha(alpha);  
	}  

	@Override  
	public void setColorFilter(ColorFilter cf)  
	{  
		mPaint.setColorFilter(cf);  
	}  

	@Override  
	public int getOpacity()  
	{  
		return PixelFormat.TRANSLUCENT;  
	}  


}
