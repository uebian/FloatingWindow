package com.yzrilyzr.ui;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.yzrilyzr.myclass.util;
import android.content.res.Resources;

public class myTabLayout extends View
{
	String[] items;
	myViewPager page;
	private int screenWidth;
	private int cur=0;
	private float xx=0,dx=0,vx=0,lxx,ew=1,drawx;
	private boolean touch=false,isScroll=false,touchable=true;
	private Paint pa=new Paint(Paint.ANTI_ALIAS_FLAG);
	public myTabLayout(Context c,AttributeSet a)
	{
		super(c,a);
		if(a!=null)
		{
			items=a.getAttributeValue(null,"items").split(",");
		}
	}
	public void setTouch(boolean touch)
	{
		this.touch = touch;
	}

	public boolean isTouch()
	{
		return touch;
	}

	public void setIsScroll(boolean isScroll)
	{
		this.isScroll = isScroll;
	}

	public boolean isScroll()
	{
		return isScroll;
	}

	public void setTouchable(boolean touchable)
	{
		this.touchable = touchable;
	}

	public boolean isTouchable()
	{
		return touchable;
	}
	public void setItems(String... item){
		items=item;
	}
	public myTabLayout(Context c)
	{
		this(c,null);
	}
	public void setCurrent(int i){
		cur=(int) util.limit(i,0,items.length);
		if(xx+cur*ew<0||xx+(cur+1)*ew>screenWidth)
		{
			xx=-cur*ew+screenWidth/2-ew/2;
		}
		invalidate();
	}
	public void setDrawX(int i){
		drawx=i;
		invalidate();
	}
	@Override
	protected void onDraw(Canvas canvas)
	{
		float k=Math.min(items.length,3.5f);
		if(k==0)return;
		ew=screenWidth/k;
		pa.setColor(uidata.TEXTBACK);
		pa.setTextAlign(Paint.Align.CENTER);
		if(uidata.UI_USETYPEFACE)pa.setTypeface(uidata.UI_TYPEFACE);
		pa.setTextSize(util.px(uidata.TEXTSIZE));
		if(vx!=0&&!touch)
		{
			xx+=(vx*=0.95)*3f;
			if(vx>-0.1&&vx<0.1)
			{vx=0;}
		}
		xx=util.limit(xx,-ew*items.length+screenWidth,0);
		float f=xx;
		int u=0;
		for(String s:items)
		{
			if(u++==cur)pa.setColor(uidata.ACCENT);
			else pa.setColor(uidata.TEXTMAIN);
			canvas.drawText(s,ew/2+f,pa.getTextSize()*1.2f,pa);
			f+=ew;
		}
		pa.setColor(uidata.ACCENT);
		float x=xx+drawx*ew/page.getWidth();
		canvas.drawRect(x,getHeight()-util.px(3),x+ew,getHeight(),pa);
		if(vx!=0)invalidate();
	}
	public void setViewPager(myViewPager p){
		page=p;
	}
	@Override
	public boolean onTouchEvent(MotionEvent e)
	{
		if(!touchable)return true;
		float x=e.getX();
		int a=e.getAction();
		if(a==MotionEvent.ACTION_DOWN)
		{
			touch=true;
			lxx=xx;
			dx=x;
		}
		else if(a==MotionEvent.ACTION_MOVE)
		{
			vx=x-dx;
			xx+=vx;
			dx=x;
			if(lxx>xx+util.px(5)||lxx<xx-util.px(5))isScroll=true;
		}
		else if(a==MotionEvent.ACTION_UP)
		{
			if(!isScroll)
			{
				vx=0;
				cur=(int) Math.floor((x-xx)/ew);
				if(page!=null)page.setCurrentItem(cur,true);
				if(xx+cur*ew<0||xx+(cur+1)*ew>screenWidth)
				{
					xx=-cur*ew+screenWidth/2-ew/2;
				}
			}
			touch=false;
			isScroll=false;
		}
		invalidate();
		return true;
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		setMeasuredDimension(WidgetUtils.measure(widthMeasureSpec,-1),WidgetUtils.measure(heightMeasureSpec,util.px(30)));
		screenWidth = MeasureSpec.getSize(widthMeasureSpec);
	}
}
