package com.yzrilyzr.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.ui.uidata;

public class myLinearLayoutRipple extends myLinearLayout implements myTouchProcessor.Event
{
	private boolean useRound=false,color2Back=false;
    private myRippleDrawable mrd;
    private myTouchProcessor pr=new myTouchProcessor(this);;
    public myLinearLayoutRipple(Context c,AttributeSet a)
    {
        super(c,a);
		float radius=util.px(uidata.UI_RADIUS);
    	if(a!=null)
		{
			radius=a.getAttributeFloatValue(null,"radius",util.px(uidata.UI_RADIUS));
			useRound=a.getAttributeBooleanValue(null,"round",false);
			color2Back=a.getAttributeBooleanValue(null,"color2Back",false);
		}
		mrd=new myRippleDrawable(!color2Back?uidata.MAIN:uidata.BACK,radius);
        setBackgroundDrawable(mrd);
        mrd.setLayer(this);
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        // TODO: Implement this method
        super.onSizeChanged(w, h, oldw, oldh);
        if(useRound)mrd.setRadius(h/2f);
    }
    public myLinearLayoutRipple(Context c)
    {
        this(c,null);
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

