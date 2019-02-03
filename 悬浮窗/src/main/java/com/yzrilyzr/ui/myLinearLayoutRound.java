package com.yzrilyzr.ui;
import android.content.Context;
import android.util.AttributeSet;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.ui.uidata;

public class myLinearLayoutRound extends myLinearLayout
{
	private boolean useRound=false,color2Back=false;
	private myRippleDrawable mrd;
	public myLinearLayoutRound(Context c,AttributeSet a)
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
	}
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		// TODO: Implement this method
		super.onSizeChanged(w, h, oldw, oldh);
		if(useRound)mrd.setRadius(h/2f);
	}
	public myLinearLayoutRound(Context c)
	{
		this(c,null);
	}
}
