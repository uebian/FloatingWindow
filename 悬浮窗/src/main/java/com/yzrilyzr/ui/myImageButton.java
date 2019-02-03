package com.yzrilyzr.ui;
import android.widget.*;
import android.content.*;
import android.util.*;
import android.view.*;
import android.graphics.*;
import com.yzrilyzr.myclass.util;

public class myImageButton extends ImageButton implements myTouchProcessor.Event
{
	private myTouchProcessor pr=new myTouchProcessor(this);
    private myRippleDrawable mrd;
	private boolean useRound=false;
    public myImageButton(Context c,AttributeSet a)
    {
        super(c,a);
        setScaleType(ImageView.ScaleType.FIT_CENTER);
        WidgetUtils.setIcon(this,a);
		float radius=util.px(uidata.UI_RADIUS);
		if(a!=null)
		{
			radius=a.getAttributeFloatValue(null,"radius",util.px(uidata.UI_RADIUS));
			useRound=a.getAttributeBooleanValue(null,"round",false);
        }
		mrd=new myRippleDrawable(isEnabled()?uidata.BUTTON:uidata.getBFColor(),radius);
        mrd.setLayer(isEnabled()?this:null);
        setBackgroundDrawable(mrd);
    }
	public myImageButton(Context c){
		this(c,null);
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
