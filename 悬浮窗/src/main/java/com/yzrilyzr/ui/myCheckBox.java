package com.yzrilyzr.ui;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.CheckBox;
import com.yzrilyzr.myclass.util;

public class myCheckBox extends CheckBox
{
	Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
	public myCheckBox(Context c,AttributeSet a)
	{
		super(c,a);
	}
	public myCheckBox(Context c)
	{
		this(c,null);
	}
	@Override
	protected void onDraw(Canvas canvas)
	{
		float w=getWidth();
		float r=util.px(1);
		RectF rf=new RectF(w/4f,w/4f,w*0.75f,w*0.75f);
		paint.setStrokeWidth(util.px(2));
		if(isChecked())
		{
			paint.setColor(isEnabled()?uidata.ACCENT:uidata.getEFColor());
			paint.setStyle(Paint.Style.FILL_AND_STROKE);
			canvas.drawRoundRect(rf,r,r,paint);
			paint.setStyle(Paint.Style.STROKE);
			paint.setColor(uidata.CONTROL);
			Path p=new Path();
			p.moveTo(w*0.33f,w*0.5f);
			p.lineTo(w*0.46f,w*0.63f);
			p.lineTo(w*0.71f,w*0.38f);
			canvas.drawPath(p,paint);
		}
		else
		{
			paint.setStyle(Paint.Style.STROKE);
			paint.setColor(isEnabled()?uidata.CONTROL:uidata.getEFColor());
			canvas.drawRoundRect(rf,r,r,paint);
		}
	}
	/*@Override
	 public boolean onTouchEvent(MotionEvent event)
	 {
	 // TODO: Implement this method
	 //getParent().requestDisallowInterceptTouchEvent(true);
	 switch(event.getAction())
	 {
	 case MotionEvent.ACTION_DOWN:
	 isOn=true;invalidate();
	 break;
	 case MotionEvent.ACTION_MOVE:
	 float x=event.getX(),y=event.getY();
	 if(x<0||x>getWidth()||y<0||y>getHeight())isOn=false;
	 break;
	 case MotionEvent.ACTION_UP:
	 if(isOn)
	 {
	 isOn=false;
	 isChecked=!isChecked;
	 if(occl!=null)occl.onCheckedChange(this,isChecked);
	 if(method!=null)util.call(getContext().getClass(),method,new Class[]{View.class,boolean.class},getContext(),new Object[]{this,isChecked});
	 invalidate();
	 }
	 break;
	 }
	 return true;
	 }
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		setMeasuredDimension(WidgetUtils.measure(widthMeasureSpec,util.px(30)),WidgetUtils.measure(heightMeasureSpec,util.px(30)));
	}

}
