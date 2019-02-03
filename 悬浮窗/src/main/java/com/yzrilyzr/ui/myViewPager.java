package com.yzrilyzr.ui;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

public class myViewPager extends HorizontalScrollView
{
	private int screenWidth=0,curScreen=0;
	private float lastx;
	private LinearLayout ll;
	private OnPageChangeListener lis=null;
	myTabLayout tab;
	private boolean touchable=true;
	public myViewPager(Context context)
	{
		super(context);
		init();
	}
	public myViewPager(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	public void setTouchable(boolean touchable)
	{
		this.touchable = touchable;
	}

	public boolean isTouchable()
	{
		return touchable;
	}
	public void setTabLayout(myTabLayout r)
	{
		tab=r;
	}
	private void init()
	{
		ll=new LinearLayout(getContext());
		addView(ll);
		setHorizontalScrollBarEnabled(false);
	}
	public void addPage(View v){
		ll.addView(v);
		LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(screenWidth,-1);
		p.weight=1;
		v.setLayoutParams(p);
	}
	public void setPages(View... views)
	{
		ll.removeAllViews();
		for(View v:views)addPage(v);
	}
	private void meas()
	{
		for(int i = 0; i < ll.getChildCount(); i++)
		{
			LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(screenWidth,-1);
			p.weight=1;
			ll.getChildAt(i).setLayoutParams(p);
		}
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		// TODO: Implement this method
		super.onDraw(canvas);
		if(tab!=null)tab.setDrawX(getScrollX());
	}
	public void setCurrentItem(int i,boolean b)
	{
		//setFocusable(true);
		//setFocusableInTouchMode(true);
		//requestFocus();
		if(lis!=null)lis.onPageChanged(curScreen,i);
		curScreen=i;
		if(b)smoothScrollTo(screenWidth*i,0);
		else scrollTo(screenWidth*i,0);
		lastx=i*screenWidth;
		if(tab!=null)tab.setCurrent(i);
		
	}
	public int getCurrentItem()
	{
		return curScreen;
	}
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		// TODO: Implement this method
		super.onLayout(changed, l, t, r, b);
		if(changed){
			screenWidth=getWidth();
			meas();
			/*new Handler(getContext().getMainLooper()).postDelayed(new Runnable(){
					@Override
					public void run()
					{
						scrollTo(curScreen*screenWidth,0);
						lastx=curScreen*screenWidth;
					}
				},200);*/
		}
	}

	/*@Override
	public void addView(View child, int index, android.view.ViewGroup.LayoutParams params)
	{
		if(child!=this)addPage(child);
	}

	@Override
	public void addView(View child)
	{
		if(child!=this)addPage(child);
	}

	@Override
	public void addView(View child, int width, int height)
	{
		if(child!=this)addPage(child);
	}

	@Override
	public void addView(View child, android.view.ViewGroup.LayoutParams params)
	{
		if(child!=this)addPage(child);
	}

	@Override
	public void addView(View child, int index)
	{
		if(child!=this)addPage(child);
	}*/
	@Override
	public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate)
	{
		// TODO: Implement this method
		return false;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev)
	{
		if(!touchable)return true;
		// TODO: Implement this method
		switch(ev.getAction())
		{
			case MotionEvent.ACTION_UP:
				screenWidth=getWidth();
				//setFocusable(true);
				//setFocusableInTouchMode(true);
				//requestFocus();
				int l=curScreen;
				float dx=lastx-getScrollX();
				if(Math.abs(dx)>screenWidth/4)
				{
					if(dx>0)l--;
					else l++;
					if(lis!=null)lis.onPageChanged(curScreen,l);
					if(tab!=null)tab.setCurrent(l);
					curScreen=l;
				}
				smoothScrollTo(l*screenWidth,0);
				lastx=l*screenWidth;
				return true;
				//smoothScrollTo(l*screenWidth,0);
				//lastx=l*screenWidth;

		}
		return super.onTouchEvent(ev);
	}
	public void setOnPageChangedListener(OnPageChangeListener l)
	{
		lis=l;
	}
	public interface OnPageChangeListener
	{
		public abstract void onPageChanged(int last,int newone);
	}
}
