package com.yzrilyzr.floatingwindow.apps;

import android.content.Context;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import com.yzrilyzr.floatingwindow.API;
import com.yzrilyzr.floatingwindow.PluginService;
import com.yzrilyzr.floatingwindow.Window;
import com.yzrilyzr.floatingwindow.apps.cls;
import com.yzrilyzr.floatingwindow.view.StarterView;
import com.yzrilyzr.icondesigner.VecView;
import com.yzrilyzr.myclass.util;

public class StartButton implements StarterView.Listener,View.OnTouchListener,View.OnClickListener,View.OnLongClickListener
{
	Window button,menu;
	Context ctx;
	int code=-1;
	StarterView cv;
	private static boolean once=false;
	boolean moved=false;
	public StartButton(Context ctx,Intent e)
	{
		if(once)return;
		once=true;
		this.ctx=ctx;
		//button
		int dd=util.px(25);
        button=new Window(ctx,dd,dd);
		button.getLayoutParams().type=WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
		button.setPosition((util.getScreenWidth()-dd)/2,(util.getScreenHeight()-dd)/2)
			.setCanResize(false)
			.setCanFocus(false)
			.show();
		Window.windowList.remove(button);
        LinearLayout a =(LinearLayout)button.getMainView();
        a.setBackgroundDrawable(null);
        a.removeAllViews();
        VecView i=new VecView(ctx);
        i.setImageVec("floatingwindow");
		i.setLayoutParams(new LinearLayout.LayoutParams(-1,-1));
		a.addView(i);
		//menu
		menu=new Window(ctx,-1,-1)
		.setCanFocus(false);
		menu.getLayoutParams().type=WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
		menu.getLayoutParams().windowAnimations=android.R.style.Animation;
		cv=new StarterView(ctx);
        LinearLayout aa=(LinearLayout)menu.getMainView();
        aa.setOnTouchListener(null);
        aa.setBackgroundDrawable(null);
        aa.removeAllViews();
        aa.addView(cv);
        cv.setListener(this);
		i.setOnTouchListener(this);
        i.setOnClickListener(this);
		i.setOnLongClickListener(this);
	}

	@Override
	public boolean onLongClick(View p1)
	{
		API.startService(ctx,new Intent().putExtra("url","https://github.com/yzrilyzr/FloatingWindow"),cls.WEBVIEWER);
		return true;
	}

	
	@Override
	public void onClick(View p1)
	{
		if(!moved)
		{
			menu.show(false);
			cv.toggle();
		}
	}
	@Override
	public boolean onTouch(View p1, MotionEvent p2)
	{
		cv.setPosition(p2.getRawX(),p2.getRawY());
		moved=button.moveableView(p1,p2);
		return false;
	}
	@Override
	public void onItemClick(int which)
	{
		code=which;
		
	}
	@Override
	public void onAnimEnd()
	{
		menu.dismiss();
		if(code==4)
		{
			PluginService.fstop(ctx);
		}
		if(code>=0&&code<=3)
		{
			if(cv.getPkg(code)==null)
				API.startService(ctx,new Intent()
				.putExtra("mode",1).putExtra("which",code)
				,cls.PLUGINPICKER);
			else
				PluginService.loadPlugin(ctx,new Intent()
				.putExtra("pkg",cv.getPkg(code))
				.putExtra("class",cv.getClass(code)));
		}
		if(code==5)
		{
			API.startService(ctx,cls.PLUGINPICKER);
		}
	}
	@Override
	public void onAnimStart()
	{
		code=-1;
	}
}
