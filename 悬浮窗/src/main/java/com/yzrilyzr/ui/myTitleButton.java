package com.yzrilyzr.ui;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.yzrilyzr.myclass.util;

public class myTitleButton extends ImageButton implements myTouchProcessor.Event
{
	private myTouchProcessor pr;
	private Context ctx;
	private String text=null;
	private myRippleDrawable mrd;
	public myTitleButton(Context c,AttributeSet a)
	{
		super(c,a);
		ctx=c;
		if(a!=null)text=a.getAttributeValue(null,"text");
     	int p=util.px(5);
		setPadding(p,p,p,p);
		mrd=new myRippleDrawable(uidata.MAIN,0);
		setBackgroundDrawable(mrd);
		WidgetUtils.setIcon(this,a);
		setScaleType(ImageView.ScaleType.FIT_XY);
		pr=new myTouchProcessor(this);
	}
	public myTitleButton(Context c)
	{
		this(c,null);
	}
	public void setText(String s)
	{
		text=s;
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		// TODO: Implement this method
		int i=util.px(50);
		setMeasuredDimension(WidgetUtils.measure(widthMeasureSpec,i),WidgetUtils.measure(heightMeasureSpec,i));
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
		if(text!=null)
		{
			myToast t=new myToast(ctx);
			t.setDuration(1000);
			t.setGravity(Gravity.LEFT|Gravity.TOP,(int)(m.getRawX()-m.getX()),(int)(m.getRawY()-m.getY()+getHeight()/2));
			t.setText(text);
			t.show();
		}
		return false;
	}
}
