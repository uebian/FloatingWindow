package com.yzrilyzr.ui;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;
import com.yzrilyzr.myclass.util;

public class mySeekBar extends SeekBar
{
	private Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
	private boolean ondown=false;
	public mySeekBar(Context c, AttributeSet a)
	{
		super(c, a);	
	}
	public mySeekBar(Context c)
	{
		this(c, null);
	}
	@Override
	protected void onDraw(Canvas canvas)
	{
		float hei=getHeight(),wid=getWidth();
		float r=hei/2f,r3=hei*0.4f,r2=hei*0.05f;
		float drawx=r+(wid-2f*r)*(float)getProgress()/(float)getMax();
		float drawx2=r+(wid-2f*r)*(float)getSecondaryProgress()/(float)getMax();
		paint.setStyle(Paint.Style.FILL);
		if(isEnabled())
		{
			paint.setColor(uidata.getASColor());
			canvas.drawRoundRect(new RectF(r,r3,wid-r,hei-r3),r2,r2,paint);
			paint.setColor(uidata.getAFColor());
			canvas.drawRoundRect(new RectF(r,r3,drawx2,hei-r3),r2,r2,paint);
			paint.setColor(uidata.ACCENT);
			canvas.drawRoundRect(new RectF(r,r3,drawx,hei-r3),r2,r2,paint);
			paint.setColor(uidata.ACCENT);
			if(ondown)canvas.drawCircle(drawx,hei/2,hei*0.3f,paint);
			else canvas.drawCircle(drawx,hei/2,hei*0.2f,paint);
		}
		else
		{
			paint.setColor(uidata.getESColor());
			canvas.drawRoundRect(new RectF(r,r3,drawx-hei*0.2f,hei-r3),r2,r2,paint);
			canvas.drawRoundRect(new RectF(hei*0.2f+drawx,r3,wid-r,hei-r3),r2,r2,paint);
			paint.setColor(uidata.getEFColor());
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(util.px(2));
			canvas.drawCircle(drawx,hei/2,hei*0.12f,paint);
		}

	}
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		getParent().requestDisallowInterceptTouchEvent(true);
		int a=event.getAction();
		if(a==MotionEvent.ACTION_DOWN||a==MotionEvent.ACTION_MOVE)ondown=true;
		else ondown=false;
		return super.onTouchEvent(event);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		setMeasuredDimension(WidgetUtils.measure(widthMeasureSpec,-2), WidgetUtils.measure(heightMeasureSpec, util.px(30)));
	}
}
