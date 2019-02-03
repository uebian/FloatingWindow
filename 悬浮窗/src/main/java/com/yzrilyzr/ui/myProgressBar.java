package com.yzrilyzr.ui;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;
import com.yzrilyzr.myclass.util;

public class myProgressBar extends SeekBar
{
	private Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);;
    public myProgressBar(Context c,AttributeSet a)
    {
        super(c,a);
    }
    public myProgressBar(Context c)
    {
		this(c,null);
	}
    @Override
    protected void onDraw(Canvas canvas)
    {
		float hei=getHeight(),wid=getWidth();
		float r=hei/2f,r3=hei*0.133f,r2=hei*0.25f;
		float drawx=r+(wid-2f*r)*(float)getProgress()/(float)getMax();
		float drawx2=r+(wid-2f*r)*(float)getSecondaryProgress()/(float)getMax();
		paint.setColor(isEnabled()?uidata.getASColor():uidata.getESColor());
		canvas.drawRoundRect(new RectF(r,r3,wid-r,hei-r3),r2,r2,paint);
		paint.setColor(uidata.getAFColor());
		canvas.drawRoundRect(new RectF(r,r3,drawx2,hei-r3),r2,r2,paint);
		paint.setColor(uidata.ACCENT);
		canvas.drawRoundRect(new RectF(r,r3,drawx,hei-r3),r2,r2,paint);
	}
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        setMeasuredDimension(WidgetUtils.measure(widthMeasureSpec,-1),WidgetUtils.measure(heightMeasureSpec,util.px(10)));
    }

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		return false;
	}
}
