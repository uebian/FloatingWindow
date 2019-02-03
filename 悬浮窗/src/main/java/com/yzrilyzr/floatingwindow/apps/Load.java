package com.yzrilyzr.floatingwindow.apps;
import com.yzrilyzr.floatingwindow.*;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.Gravity;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.LinearLayout;
import com.yzrilyzr.floatingwindow.apps.cls;
import com.yzrilyzr.floatingwindow.view.StarterView;
import com.yzrilyzr.icondesigner.VecView;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.ui.myLoadingView;
import com.yzrilyzr.ui.myRippleDrawable;
import com.yzrilyzr.ui.myTextViewTitle;
import com.yzrilyzr.ui.uidata;
import android.view.View;
import android.content.res.Resources;
import android.widget.LinearLayout.LayoutParams;
import com.yzrilyzr.icondesigner.VECfile;
import java.io.IOException;
import com.yzrilyzr.icondesigner.Shape;

public class Load implements Runnable,OnClickListener
{
	Window w;
	Context c;
	private static boolean once=false,sa=false;
	public Load(Context ct,Intent e)
	{
		if(once)return;
		once=true;
		this.c=ct;
        w=new Window(c,util.px(210),util.px(275))
			.show()
			.setCanFocus(false)
			.setCanResize(false);
		w.getLayoutParams().type=WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
		w.dismiss().show().setColor(uidata.BACK);
        LinearLayout v=(LinearLayout) w.getMainView();
        v.removeAllViews();
        v.setOnTouchListener(null);
        v.setOrientation(1);
        v.setGravity(Gravity.CENTER);
         VecView iv=new VecView(c);
        int ii=util.px(180);
        iv.setImageVec("octocat");//floatingwindow");
        iv.setLayoutParams(new LinearLayout.LayoutParams(ii,ii));
		iv.setOnClickListener(this);
        v.addView(iv);
        myTextViewTitle tv=new myTextViewTitle(c);
        tv.setText("悬浮窗");
        tv.setGravity(Gravity.CENTER);
        v.addView(tv);
		VecView iv2=new VecView(c);
		try
		{
			VECfile vf=VECfile.readFileFromIs(ct.getAssets().open("yzrilyzr.vec"));
			for(Shape l:vf.getShapes())l.setStrokeColor(uidata.TEXTMAIN);
			iv2.setImageBitmap(VECfile.createBitmap(vf,util.px(80),util.px(37)));
		}
		catch (IOException ep)
		{ep.printStackTrace();}
        v.addView(iv2);
		LinearLayout.LayoutParams k=new LinearLayout.LayoutParams(util.px(80),util.px(37));
		k.gravity=Gravity.RIGHT;
		iv2.setLayoutParams(k);
		myLoadingView ml=new myLoadingView(c);
        ii=util.px(40);
        ml.setLayoutParams(new LinearLayout.LayoutParams(ii,ii));
        v.addView(ml);
        new Thread(){
			@Override public void run()
			{
				long m=System.currentTimeMillis();
				Window.readData();
				uidata.readData();
				GetServer.get();
				if(Window.usejs)PluginService.jsenv=new JSEnv("",this);
				ANRThread.startMain();
				StarterView.load(c);
				PluginPicker.load();
				m=System.currentTimeMillis()-m;
				new Handler(c.getMainLooper()).postDelayed(Load.this,m<1000l&&m>0?1000l-m:0);
			}
		}.start();
    }
	@Override
	public void onClick(View p1)
	{
		if(sa)return;
		sa=true;
		Window w=new Window(c,util.px(60),util.px(25))
			.show()
			.setCanFocus(false)
			.setColor(0x80000000)
			.setBColor(0)
			.setPosition(0,util.getScreenHeight()-util.px(30))
			.setCanResize(false);
		w.getLayoutParams().type=WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
		w.getLayoutParams().flags=WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		w.dismiss().show();
		Window.windowList.remove(w);
		LinearLayout v=(LinearLayout) w.getMainView();
        v.removeAllViews();
        v.setOnTouchListener(null);
        v.setGravity(Gravity.CENTER);
        myTextViewTitle tv=new myTextViewTitle(c);
        tv.setText("安全模式");
        tv.setGravity(Gravity.CENTER);
        v.addView(tv);
		new Handler().postDelayed(new Runnable(){
				@Override
				public void run()
				{
					uidata.UI_DENSITY=(float)util.getScreenWidth()/360f;
					uidata.TEXTSIZE=12f;
					uidata.TEXTMAIN=-1447447;
					uidata.TEXTBACK=-1;
					uidata.CONTROL=-1118482;
					uidata.MAIN=-13070228;
					uidata.BACK=-12895429;
					uidata.ACCENT=-27859;
					uidata.BUTTON=-9539986;
					uidata.UNENABLED=-2236963;
					Window.startonboot=false;
					Window.anr=true;
					Window.crashdialog=false;
					Window.nolimit=true;
					Window.notify=true;
					Window.usejs=false;
					Window.xposed=false;
					Window.saveData();
					uidata.saveData();
				}
			},500);

	}
	@Override
	public void run()
	{
		// TODO: Implement this method
		w.dismiss();
		API.startService(c,cls.STARTBUTTON);
	}
}
