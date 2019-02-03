package com.yzrilyzr.ui;
import android.view.*;

public class myTouchProcessor
{
	private Event u;
    private boolean onView=false,LC=false,CC=false,VC=false,PE=true;
    private long millis=0;
	public interface Event
	{
		public void onDown(View v, MotionEvent m);
		public void onUp(View v, MotionEvent m);
		public boolean onView(View v, MotionEvent m);
		public void onClick(View v);
		public boolean onLongClick(View v, MotionEvent m);
	}
	public myTouchProcessor(Event e){
		u=e;
	}
	public void setParentEvent(boolean b)
    {
		PE = b;
	}
    public boolean process(View v, MotionEvent e)
    {
        ViewParent vp=v.getParent();
        if (!PE)vp.requestDisallowInterceptTouchEvent(true);
        int x=(int)e.getX(),y=(int)e.getY(),a=e.getAction(),w=v.getWidth(),h=v.getHeight();
        if (a == MotionEvent.ACTION_DOWN)
        {
            u.onDown(v, e);
            millis = System.currentTimeMillis();
            onView = true;
            LC = false;
            CC = false;
            VC = false;
        }
        if (onView)
        {
            if (a == MotionEvent.ACTION_UP)
            {
                u.onUp(v, e);
                if (!CC && !VC)u.onClick(v);
            }
            else
            {
                if (x > 0 && x < w && y > 0 && y < h)
                {
                    VC = u.onView(v, e);
                    onView = true;
                    if (System.currentTimeMillis() - millis > 500 && !VC && !LC)
                    {
                        CC = u.onLongClick(v, e);
                        LC = true;
                    }
                }
                else
                {
                    onView = false;
                    u.onUp(v, e);
                }
            }
        }
        return VC || CC;
    }

}
