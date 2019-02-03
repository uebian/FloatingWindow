package com.yzrilyzr.ui;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ToggleButton;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.ui.uidata;

public class myToggleButton extends ToggleButton implements myTouchProcessor.Event
{
	private myTouchProcessor pr=new myTouchProcessor(this);
    private boolean useRound=false;
    private mDrawable mrd;
    public myToggleButton(Context c,AttributeSet a)
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
        mrd=new mDrawable(isEnabled()?uidata.BUTTON:uidata.getBFColor(),radius);
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
    public myToggleButton(Context c)
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
	class mDrawable extends myRippleDrawable{
		RectF bar;
		public mDrawable(int color,float radius){
			super(color,radius);
		}
		@Override
		public void setBounds(int left, int top, int right, int bottom)
		{
			// TODO: Implement this method
			super.setBounds(left, top, right, bottom);
			bar=new RectF(rectF.left,rectF.bottom-rectF.height()/20f,rectF.right,rectF.bottom);
		}
		@Override
		public void draw(Canvas canvas)
		{
			// TODO: Implement this method
			super.draw(canvas);
			paint2.setColor(isEnabled()?(isChecked()?uidata.ACCENT:uidata.CONTROL):uidata.getESColor());
			canvas.drawRect(bar,paint2);
		}
	}
}
