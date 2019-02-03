package com.yzrilyzr.icondesigner;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;

public class Edit extends MView
{
	public String txt;
	public boolean touch=false;
	public Paint pa;
	public int color=editcolor;
	public boolean selected=false;
	public Edit(float x,float y,float w,float h,String txt)
	{
		super(x+3,y+3,w-6,h-6);
		this.txt=txt;
		pa=new Paint(Paint.ANTI_ALIAS_FLAG);
		pa.setTextSize(px(12));
		pa.setStrokeWidth(px(2));
	}
	public void onDraw(Canvas c)
	{
		pa.setColor(color);
		pa.setStyle(Paint.Style.FILL);
		c.drawRoundRect(this,5,5,pa);
		pa.setColor(0xff000000);
		c.drawText(txt,left+px(3),(bottom+top)/2+pa.getTextSize()/2,pa);
	}
	@Override
	public void onTouchEvent(MotionEvent k)
	{
		// TODO: Implement this method
		int a=k.getAction();float x=k.getX(),y=k.getY();
		if(a==MotionEvent.ACTION_DOWN&&contains(x,y))touch=true;
		if(a==MotionEvent.ACTION_UP){
			if(contains(x,y)&&touch){
				render.curView=this;
				render.showIME();
			}
			touch=false;
		}
	}
	public interface Event
	{
		public abstract void e(Button b);
	}
}
