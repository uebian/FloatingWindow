package com.yzrilyzr.connecttogether;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import com.yzrilyzr.background.Shapes;
import android.view.MotionEvent;

public class BackgroundView extends View
{
	Shapes bg=new Shapes(50);
	public BackgroundView(Context c,AttributeSet a)
	{
		super(c,a);
		if(a!=null)
		{
			String t=a.getAttributeValue("type","null");
			if(t!=null)
				switch(t)
				{
					case "shape":
						bg=new Shapes(50);
						break;
					default:
						bg=null;
						break;
				}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if(bg!=null)bg.onTouchEvent(event);
		return true;
	}
	@Override
	protected void onDraw(Canvas canvas)
	{
		if(bg!=null)
		{
			bg.onCompute();
			bg.onDraw(canvas);
			invalidate();
		}
	}
}
