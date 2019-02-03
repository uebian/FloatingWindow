package com.yzrilyzr.ui;
import android.content.Context;
import android.util.AttributeSet;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.ui.uidata;

public class myTextView extends android.widget.TextView
{
	protected final static int DEF=0,BACK=1,TITLE=2,TITLEBACK=3;
	int type;
	public myTextView(Context c,AttributeSet a)
	{
		super(c,a);
		init(DEF);
	}
	public myTextView(Context c)
	{
		this(c,null);
	}
	protected void init(int type)
	{
		this.type=type;
		setTextColor(isEnabled()?(type==DEF||type==TITLE?uidata.TEXTMAIN:uidata.TEXTBACK):uidata.getEFColor());
		if(uidata.UI_USETYPEFACE)setTypeface(uidata.UI_TYPEFACE);
		getPaint().setTextSize(util.px(type==DEF||type==BACK?uidata.TEXTSIZE:uidata.TEXTSIZE*1.2f));
	}

	@Override
	public void setEnabled(boolean enabled)
	{
		// TODO: Implement this method
		super.setEnabled(enabled);
		setTextColor(enabled?(type==DEF||type==TITLE?uidata.TEXTMAIN:uidata.TEXTBACK):uidata.getEFColor());
	}
	
}
