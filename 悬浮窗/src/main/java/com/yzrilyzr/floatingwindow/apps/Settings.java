package com.yzrilyzr.floatingwindow.apps;
import com.yzrilyzr.ui.*;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import com.yzrilyzr.floatingwindow.ANRThread;
import com.yzrilyzr.floatingwindow.API;
import com.yzrilyzr.floatingwindow.PluginService;
import com.yzrilyzr.floatingwindow.R;
import com.yzrilyzr.floatingwindow.Window;
import com.yzrilyzr.floatingwindow.apps.cls;
import com.yzrilyzr.myclass.JSEnv;
import com.yzrilyzr.myclass.util;

public class Settings implements Window.OnButtonDown,myViewPager.OnPageChangeListener
{
	Context ctx;
	Window w;
	View pq,pw,pe,pr;
	public Settings(Context c,Intent e)
	{
		ctx=c;
		w=new Window(ctx,util.px(320),util.px(400))
		.setTitle("设置/关于")
		//.setBar(0,0,0,8)
		.setIcon("settings")
		.setOnButtonDown(this)
		.show();
		myTabLayout t=new myTabLayout(c);
		final myViewPager v=new myViewPager(c);
		t.setItems("设置","界面设置","帮助","关于");
		t.setViewPager(v);
		v.setTabLayout(t);
		w.addView(t);
		w.addView(v);
		pq=LayoutInflater.from(c).inflate(R.layout.window_settings,null);
		pw=LayoutInflater.from(c).inflate(R.layout.window_uisettings,null);
		LinearLayout lw=new LinearLayout(ctx);
		lw.setOrientation(1);
		pe=new myTextView(c);
		pr=new myTextView(c);
		((myTextView)pr).setText(R.string.about);
		int y=util.px(5);
		pr.setPadding(y,y,y,y);
		v.setPages(pq,pw,lw,pr);
		v.setOnPageChangedListener(this);
		try
		{
			initUi();
			initSet();
		}
		catch(Throwable ep)
		{
			ep.printStackTrace();
			util.toast("加载设置出错");
		}
		lw.addView(LayoutInflater.from(ctx).inflate(R.layout.window_help,null));
		if(util.getSPRead().getBoolean("first",true))
		{
			util.getSPWrite().putBoolean("first",false).commit();
			new Handler().postDelayed(new Runnable(){
				@Override
				public void run()
				{
					v.setCurrentItem(2,false);
				}
			},500);
		}
	}

	@Override
	public void onPageChanged(int last, int newone)
	{
		if(newone<2)w.setIcon("settings");
		else w.setIcon("info");
	}
	public void initSet()
	{
		final View v=pq.findViewById(R.id.windowsettingsLinearLayout1);
		final View v2=pq.findViewById(R.id.windowsettingsLinearLayout2);
		OnClickListener o=new OnClickListener(){
			@Override
			public void onClick(final View p1)
			{
				myDialog.Builder b=new myDialog.Builder(ctx);
				final int[] t=new int[]{2002,2003,2005,2007,2010};
				DialogInterface.OnClickListener doc=new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface pp1, int p2)
					{
						if(p1==v)Window.type=t[p2];
						else if(p1==v2)Window.crashdialog=p2==1;
					}
				};
				if(p1==v)
				{
					int k=0;
					for(;k<t.length;k++)if(Window.type==t[k])break;
					b.setTitle("设置窗口层级")
					.setSingleChoiceItems("来电界面(2002),系统警告(2003),Toast提示(2005),优先来电(2007),系统错误(2010)".split(","),k,doc)
					.setNegativeButton("取消",null)
					.show();
				}
				else if(p1==v2)
				{
					b.setTitle("崩溃处理方式")
					.setSingleChoiceItems("直接退出,显示崩溃日志".split(","),Window.crashdialog?1:0,doc)
					.setNegativeButton("取消",null)
					.show();
				}
			}
		};
		v.setOnClickListener(o);
		v2.setOnClickListener(o);
		final mySwitch r=(mySwitch) pq.findViewById(R.id.windowsettingsmySwitch1);
		final mySwitch r2=(mySwitch) pq.findViewById(R.id.windowsettingsmySwitch3);
		final mySwitch r3=(mySwitch) pq.findViewById(R.id.windowsettingsmySwitch4);
		final mySwitch r4=(mySwitch) pq.findViewById(R.id.windowsettingsmySwitch5);
		final mySwitch r5=(mySwitch) pq.findViewById(R.id.windowsettingsmySwitch6);
		final mySwitch r6=(mySwitch) pq.findViewById(R.id.windowsettingsmySwitch2);
		final mySwitch r7=(mySwitch) pq.findViewById(R.id.windowsettingsmySwitch7);
		final mySwitch r8=(mySwitch) pq.findViewById(R.id.windowsettingsmySwitch8);
		r.setChecked(Window.nolimit);
		r2.setChecked(Window.startonboot);
		r3.setChecked(android.provider.Settings.Secure.getInt(ctx.getContentResolver(),android.provider.Settings.Secure.ADB_ENABLED, 0) > 0);
		r4.setChecked(Window.anr);
		r5.setChecked(Window.notify);
		r6.setChecked(Window.usejs);
		r7.setChecked(Window.xposed);
		r8.setChecked(Window.devmode);
		mySwitch.OnCheckedChangeListener ccl=new mySwitch.OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton p1, boolean p2)
			{
				if(p1==r)
				{
					if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M&&p2)util.toast("当前设备不支持该设置");
					Window.nolimit=p2;
				}
				else if(p1==r2)Window.startonboot=p2;
				else if(p1==r3)android.provider.Settings.Secure.putInt(ctx.getContentResolver(), android.provider.Settings.Secure.ADB_ENABLED,p2?1:0);
				else if(p1==r4)
				{
					Window.anr=p2;
					if(p2)ANRThread.startMain();
				}
				else if(p1==r5)
				{
					Window.notify=p2;
					if(!p2)util.toast("保存后重新启动程序以完全应用更改");
				}
				else if(p1==r6)
				{
					Window.usejs=p2;
					if(p2)PluginService.jsenv=new JSEnv("print(\"JS辅助已开启\")",this);
				}
				else if(p1==r7)Window.xposed=p2;
				else if(p1==r8)
				{
					Window.devmode=p2;
					if(p2)util.toast("保存后重新启动程序以完全应用更改");
				}
			}
		};
		r.setOnCheckedChangeListener(ccl);
		r2.setOnCheckedChangeListener(ccl);
		r3.setOnCheckedChangeListener(ccl);
		r4.setOnCheckedChangeListener(ccl);
		r5.setOnCheckedChangeListener(ccl);
		r6.setOnCheckedChangeListener(ccl);
		r7.setOnCheckedChangeListener(ccl);
		r8.setOnCheckedChangeListener(ccl);
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
		{
			r3.setEnabled(false);
			((ViewGroup)r3.getParent()).getChildAt(0).setEnabled(false);
		}
		if(!util.sup)
		{
			v2.setOnClickListener(null);
			v2.setEnabled(false);
			((ViewGroup)v2).getChildAt(0).setEnabled(false);
			r4.setChecked(true);
			r4.setEnabled(false);
			((ViewGroup)r4.getParent()).getChildAt(0).setEnabled(false);


		}
	}
	public void initUi()
	{
		final myColorView a=(myColorView)pw.findViewById(R.id.windowuisettingsmyColorView1);
		final myColorView b=(myColorView)pw.findViewById(R.id.windowuisettingsmyColorView2);
		final myColorView c=(myColorView)pw.findViewById(R.id.windowuisettingsmyColorView3);
		final myColorView d=(myColorView)pw.findViewById(R.id.windowuisettingsmyColorView4);
		final myColorView e=(myColorView)pw.findViewById(R.id.windowuisettingsmyColorView5);
		final myColorView f=(myColorView)pw.findViewById(R.id.windowuisettingsmyColorView6);
		final myColorView g=(myColorView)pw.findViewById(R.id.windowuisettingsmyColorView7);
		final myColorView h=(myColorView)pw.findViewById(R.id.windowuisettingsmyColorView8);
		final View hh=pw.findViewById(R.id.windowuisettingsLinearLayout1);
		a.setColorView(uidata.MAIN);
		b.setColorView(uidata.BACK);
		c.setColorView(uidata.ACCENT);
		d.setColorView(uidata.UNENABLED);
		e.setColorView(uidata.CONTROL);
		f.setColorView(uidata.BUTTON);
		g.setColorView(uidata.TEXTMAIN);
		h.setColorView(uidata.TEXTBACK);
		OnClickListener o=new OnClickListener(){
			@Override
			public void onClick(final View p1)
			{
				if(p1==hh)
				{
					LinearLayout l=new LinearLayout(ctx);
					l.setOrientation(1);
					final myTextView t=new myTextView(ctx);
					final mySeekBar s=new mySeekBar(ctx);
					s.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
						@Override
						public void onProgressChanged(SeekBar p1, int p2, boolean p3)
						{
							t.getPaint().setTextSize(uidata.TEXTSIZE*((float)p2/160f+1f));
							t.setText(Integer.toString(p2+160));
						}

						@Override
						public void onStartTrackingTouch(SeekBar p1)
						{
						}

						@Override
						public void onStopTrackingTouch(SeekBar p1)
						{
							// TODO: Implement this method
						}
					});
					s.setMax(960);
					s.setProgress((int)(uidata.UI_DENSITY*160f)-160);
					l.addView(t);
					l.addView(s);
					new myDialog.Builder(ctx)
					.setTitle("设置DPI")
					.setView(l)
					.setNegativeButton("取消",null)
					.setPositiveButton("确定",new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface pp1, int p2)
						{
							uidata.UI_DENSITY=(float)s.getProgress()/160f+1f;
							util.toast("保存后重新启动程序以完全应用更改");
						}})
					.show();
					return;
				}
				int color=0;
				if(p1==a)color=uidata.MAIN;
				if(p1==b)color=uidata.BACK;
				if(p1==c)color=uidata.ACCENT;
				if(p1==d)color=uidata.UNENABLED;
				if(p1==e)color=uidata.CONTROL;
				if(p1==f)color=uidata.BUTTON;
				if(p1==g)color=uidata.TEXTMAIN;
				if(p1==h)color=uidata.TEXTBACK;
				API.startServiceForResult(ctx,new Intent().putExtra("color",color),w
				,new BroadcastReceiver(){
					@Override
					public void onReceive(Context ct,Intent ee)
					{
						int y=ee.getIntExtra("color",0);
						if(p1==a)uidata.MAIN=y;
						if(p1==b)uidata.BACK=y;
						if(p1==c)uidata.ACCENT=y;
						if(p1==d)uidata.UNENABLED=y;
						if(p1==e)uidata.CONTROL=y;
						if(p1==f)uidata.BUTTON=y;
						if(p1==g)uidata.TEXTMAIN=y;
						if(p1==h)uidata.TEXTBACK=y;
						a.setColorView(uidata.MAIN);
						b.setColorView(uidata.BACK);
						c.setColorView(uidata.ACCENT);
						d.setColorView(uidata.UNENABLED);
						e.setColorView(uidata.CONTROL);
						f.setColorView(uidata.BUTTON);
						g.setColorView(uidata.TEXTMAIN);
						h.setColorView(uidata.TEXTBACK);
					}
				},cls.COLORPICKER);
			}
		};
		a.setOnClickListener(o);
		b.setOnClickListener(o);
		c.setOnClickListener(o);
		d.setOnClickListener(o);
		e.setOnClickListener(o);
		f.setOnClickListener(o);
		g.setOnClickListener(o);
		h.setOnClickListener(o);
		hh.setOnClickListener(o);
		mySwitch r=(mySwitch) pw.findViewById(R.id.windowuisettingsmySwitch1);
		r.setChecked(uidata.UI_USETYPEFACE);
		r.setOnCheckedChangeListener(new mySwitch.OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton p1, boolean p2)
			{
				uidata.UI_USETYPEFACE=p2;
			}
		});
		myEditText y=(myEditText) pw.findViewById(R.id.windowuisettingsmyEditText1),
		u=(myEditText) pw.findViewById(R.id.windowuisettingsmyEditText4);
		y.setText(uidata.TEXTSIZE+"");
		u.setText(uidata.UI_RADIUS+"");
	}
	@Override
	public void onButtonDown(int code)
	{
		if(code==Window.ButtonCode.CLOSE)
		{
			myEditText y=(myEditText) pw.findViewById(R.id.windowuisettingsmyEditText1),
			u=(myEditText) pw.findViewById(R.id.windowuisettingsmyEditText4);
			try
			{
				uidata.TEXTSIZE=Float.parseFloat(y.getText().toString());
				uidata.UI_RADIUS=Float.parseFloat(u.getText().toString());
			}
			catch(Throwable e)
			{}
			uidata.saveData();
			Window.saveData();
		}
	}
}
