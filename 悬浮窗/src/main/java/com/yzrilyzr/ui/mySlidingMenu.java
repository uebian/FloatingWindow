package com.yzrilyzr.ui;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.nineoldandroids.view.ViewHelper;
import com.yzrilyzr.myclass.util;
import java.util.ArrayList;
/*import com.yzrilyzr.FAQ.Main.ClientService;
import com.yzrilyzr.FAQ.Main.C;
import com.yzrilyzr.FAQ.BG.Shapes;
*/
public class mySlidingMenu extends LinearLayout
{
	private int mMenuWidth;
	private View mMenu,mContent;
	private float dX=0,cxx=0;
	private boolean bool=false;
	//Shapes sha;
	public mySlidingMenu(Context context, AttributeSet attrs)
	{
		super(context,attrs);
		mMenuWidth=util.px(260);
		cxx=mMenuWidth;//sha=new Shapes();
	}
	public mySlidingMenu(Context context)
	{
		this(context,null);
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mMenu = (ViewGroup)getChildAt(0);
		mContent = (ViewGroup)getChildAt(1);
		mMenu.getLayoutParams().width=mMenuWidth;
		mContent.getLayoutParams().width=util.getScreenWidth();
	}
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		super.onLayout(changed,l,t,r,b);
		if (changed)this.scrollTo(mMenuWidth, 0);
	}
	@Override
	public boolean onTouchEvent(MotionEvent ev)
	{
		int act=ev.getAction();
		//ClientService.sendMsg(C.LOG,act+"");
		if(act==MotionEvent.ACTION_UP)
		{
			bool=false;
			if(getIsOpen())cxx=0;
			else cxx=mMenuWidth;
		}
		if(act==MotionEvent.ACTION_DOWN)
		{
			dX=ev.getX();
			bool=false;
		}
		else if(act==MotionEvent.ACTION_MOVE)
		{
			cxx+=dX-ev.getX();
			if(cxx<0)cxx=0;
			else if(cxx>mMenuWidth)cxx=mMenuWidth;
			this.scrollTo((int)cxx,0);
			dX=ev.getX();
			bool=true;
		}
		return true;
	}
	@Override
	public boolean onInterceptTouchEvent(MotionEvent e)
	{
		if(e.getAction()==MotionEvent.ACTION_MOVE)return bool;
		return super.onInterceptTouchEvent(e);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		// TODO: Implement this method
		super.onDraw(canvas);
		if(!bool)
		{
			int sx=getScrollX();
			if(sx!=cxx)scrollBy((int)Math.ceil((cxx-sx)*0.5),0);
			if(sx<0)scrollTo((int)(cxx=0),0);
			else if(sx>mMenuWidth)scrollTo((int)(cxx=mMenuWidth),0);
		}
		//sha.onCompute();
		//sha.onDraw(canvas);
		invalidate();
	}

	public void openMenu()
	{
		cxx=0;
	}
	public void closeMenu()
	{
		cxx=mMenuWidth;
	}
	public boolean getIsOpen()
	{return getScrollX()<mMenuWidth/2;}
	public void toggle()
	{
		if (getIsOpen())closeMenu();
		else openMenu();
	}
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt)
	{
		super.onScrollChanged(l, t, oldl, oldt);
		float scale = l * 1.0f / mMenuWidth;
		float leftScale = 1 - 0.3f * scale;
		float rightScale = 0.8f + scale * 0.2f;
		ViewHelper.setScaleX(mMenu, leftScale);
		ViewHelper.setScaleY(mMenu, leftScale);
		ViewHelper.setAlpha(mMenu, 0.6f + 0.4f * (1 - scale));
		ViewHelper.setTranslationX(mMenu, mMenuWidth * scale * 0.6f);
		ViewHelper.setPivotX(mContent, 0);
		ViewHelper.setPivotY(mContent, mContent.getHeight() / 2);
		ViewHelper.setScaleX(mContent, rightScale);
		ViewHelper.setScaleY(mContent, rightScale);
		ViewHelper.setAlpha(mContent,1);
	}
}
