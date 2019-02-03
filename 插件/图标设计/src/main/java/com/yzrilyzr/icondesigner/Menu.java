package com.yzrilyzr.icondesigner;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import java.util.concurrent.CopyOnWriteArrayList;

public class Menu extends MView
{
	Paint paint;
	public CopyOnWriteArrayList<MView> views=new CopyOnWriteArrayList<MView>();
	public boolean show=false;
	private MView cur;
	public Menu(float x,float y,float w,float h,MView... m)
	{
		super(x-1,y-1,w+2,h+2);
		paint=new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(menucolor);
		addView(m);
	}
	public void addView(MView... m){
		if(m!=null)
			for(MView v:m)
				if(v!=null)
				{
					views.add(v);
					v.parent=this;
				}
	}
	@Override
	public void onDraw(Canvas c)
	{
		if(!show)return;
		//paint.setShadowLayer(px(2),0,px(2),shadowcolor);
		c.drawRect(this,paint);
		for(MView v:views)v.onDraw(c);
	}

	@Override
	public void onTouchEvent(MotionEvent e)
	{
		if(!show)return;
		int a=e.getAction();
		float x=e.getX(),y=e.getY();
		if(a==MotionEvent.ACTION_DOWN)
		{
			cur=null;
			for(int i=views.size()-1;i!=-1;i--)
			{
				MView b=views.get(i);
				if(b.contains(x,y))
				{
					if(b instanceof Menu)if(((Menu)b).show==false)continue;
					cur=b;
					break;
				}
			}
		}
		if(cur!=null)cur.onTouchEvent(e);
	}
}
