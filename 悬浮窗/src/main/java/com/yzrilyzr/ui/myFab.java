package com.yzrilyzr.ui;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import com.yzrilyzr.myclass.util;

public class myFab extends ImageView implements myTouchProcessor.Event
{
	private myTouchProcessor pr=new myTouchProcessor(this);
	private myRippleDrawable mrd;
	public myFab(Context c,AttributeSet a)
	{
		super(c,a);
		WidgetUtils.setIcon(this,a);
		setClickable(true);
		mrd=new myRippleDrawable(uidata.MAIN);
		mrd.setLayer(this);
		setBackgroundDrawable(mrd);
	}
	public myFab(Context c)
	{
		this(c,null);
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		// TODO: Implement this method
		//super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int i=util.px(60);
		setMeasuredDimension(WidgetUtils.measure(widthMeasureSpec,i),WidgetUtils.measure(heightMeasureSpec,i));
		int a=i/6;
		setPadding(a,a,a,a);
		mrd.setRadius(i/2);
	}
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if(pr.process(this,event))return true;
		return super.onTouchEvent(event);
	}
	@Override
	public void onDown(View v, MotionEvent m)
	{
		mrd.shortRipple(m.getX(),m.getY());
	}
	@Override
	public void onUp(View v, MotionEvent m)
	{
	}
	@Override
	public boolean onView(View v, MotionEvent m)
	{
		return false;
	}
	@Override
	public void onClick(View v)
	{
	}
	@Override
	public boolean onLongClick(View v, MotionEvent m)
	{
		mrd.longRipple(m.getX(),m.getY());
		return false;
	}
}
