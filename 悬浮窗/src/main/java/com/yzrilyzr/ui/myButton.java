package com.yzrilyzr.ui;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.ui.uidata;

public class myButton extends Button implements myTouchProcessor.Event
{
	private myTouchProcessor pr=new myTouchProcessor(this);
	private myRippleDrawable mrd;
    private boolean useRound=false;
    public myButton(Context c,AttributeSet a)
    {
        super(c,a);
		float radius=util.px(uidata.UI_RADIUS);
		if(a!=null)
		{
			radius=a.getAttributeFloatValue(null,"radius",util.px(uidata.UI_RADIUS));
			useRound=a.getAttributeBooleanValue(null,"round",false);
        }
		setTextColor(isEnabled()?uidata.TEXTMAIN:uidata.getEFColor());
        if(uidata.UI_USETYPEFACE)setTypeface(uidata.UI_TYPEFACE);
        getPaint().setTextSize(util.px(uidata.TEXTSIZE));
        mrd=new myRippleDrawable(isEnabled()?uidata.BUTTON:uidata.getBFColor(),radius);
        mrd.setLayer(isEnabled()?this:null);
        setBackgroundDrawable(mrd);
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        // TODO: Implement this method
        super.onSizeChanged(w, h, oldw, oldh);
        if(useRound)mrd.setRadius(h/2f);
    }
	@Override
	public void setEnabled(boolean enabled)
	{
		// TODO: Implement this method
		super.setEnabled(enabled);
		mrd.setLayer(enabled?this:null);
		mrd.setColor(enabled?uidata.BUTTON:uidata.getBFColor());
		setTextColor(enabled?uidata.TEXTMAIN:uidata.getEFColor());
	}
	public myButton(Context c)
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
		if(isEnabled())mrd.shortRipple(m.getX(),m.getY());
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
		if(isEnabled())mrd.longRipple(m.getX(),m.getY());
		return false;
	}
}
