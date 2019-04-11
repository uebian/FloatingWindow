package com.yzrilyzr.longtexteditor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import com.yzrilyzr.floatingwindow.API;
import com.yzrilyzr.floatingwindow.R;
import com.yzrilyzr.floatingwindow.Window;
import com.yzrilyzr.floatingwindow.apps.cls;
import com.yzrilyzr.icondesigner.VecView;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.ui.myDialog;
import com.yzrilyzr.ui.myTextView;
import com.yzrilyzr.ui.myToast;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import android.os.Build;
public class Main implements Window.OnButtonDown,Window.OnCrash,OnClickListener,TextWatcher
{
	private LongTextView l;
	private String file="新建文件.txt";
	boolean destroy=false;
	Context ctx;
	private ViewGroup v;
	private Window w;
	private boolean changed=false;
	public Main(Context c,Intent e)
	{
		ctx=c;
		w=new Window(c,util.px(310),util.px(440))
			.setTitle("文本编辑器")
			.setOnButtonDown(this)
			.setOnCrash(this)
			.setIcon("lte")
			.show();
		/*if(Build.VERSION.SDK_INT>Build.VERSION_CODES.M){
			util.toast("不支持该系统");
			try
			{
				String p=e.getStringExtra("path");
				StringBuilder sb=new StringBuilder();
				if(p!=null)
				{
					File f=new File(p);
					file=f.getAbsolutePath();
					String st="";
					BufferedReader br=new BufferedReader(new FileReader(p));
					while((st=br.readLine())!=null)
					{
						sb.append(st);
					}
					br.close();
				}
				p=e.getStringExtra("text");
				if(p!=null)
				{
					sb.append(p);
				}
				w.setMessage(sb.toString());
			}
			catch(Exception ex)
			{
			}
			return;
		}
		*/
		v=(ViewGroup) w.addView(R.layout.window_longtexteditor);
		l=(LongTextView)v.findViewById(R.id.mainLongTextView1);
		v.findViewById(R.id.windowlongtexteditorVecView1).setOnClickListener(this);
		v.findViewById(R.id.windowlongtexteditorVecView2).setOnClickListener(this);
		v.findViewById(R.id.windowlongtexteditorVecView3).setOnClickListener(this);
		v.findViewById(R.id.windowlongtexteditorVecView4).setOnClickListener(this);
		v.findViewById(R.id.windowlongtexteditorVecView5).setOnClickListener(this);
		v.findViewById(R.id.windowlongtexteditorVecView6).setOnClickListener(this);
		v.findViewById(R.id.windowlongtexteditorVecView7).setOnClickListener(this);
		v.findViewById(R.id.windowlongtexteditorVecView8).setOnClickListener(this);
		v.findViewById(R.id.windowlongtexteditorVecView9).setOnClickListener(this);
		v.findViewById(R.id.windowlongtexteditorVecView10).setOnClickListener(this);
		v.findViewById(R.id.windowlongtexteditorVecView11).setOnClickListener(this);
		v.findViewById(R.id.windowlongtexteditorVecView12).setOnClickListener(this);
		v.findViewById(R.id.windowlongtexteditorVecView13).setOnClickListener(this);
		v.findViewById(R.id.windowlongtexteditorVecView14).setOnClickListener(this);
		boolean crash=e.getBooleanExtra("crash",false);
		try
        {
			String p=e.getStringExtra("path");
			String title="文本编辑器";
			if(p!=null)
			{
				File f=new File(p);
				file=f.getAbsolutePath();
				title=f.getName();
				String st="";
				BufferedReader br=new BufferedReader(new FileReader(p));
				while((st=br.readLine())!=null)
				{
					l.addText(st);
				}
				br.close();
				if(crash)
				{
					f.delete();
					changed=true;
					if(!w.getTitle().endsWith("*"))w.setTitle(w.getTitle()+"*");
				}
			}
			p=e.getStringExtra("text");
			if(p!=null)
			{
				title=e.getStringExtra("title");
				l.setText(p);
			}
			w.setTitle(title+(crash?"(从崩溃中恢复)":""));
			v.findViewById(R.id.windowlongtexteditorVecView14).setVisibility(file.toLowerCase().endsWith(".js")?0:8);
        }
        catch(Exception ex)
        {
		}
		if(!crash)try
			{
				File f=new File(util.mainDir+"/文本编辑器/crash");
				if(f.exists())
				{
					File[] fs=f.listFiles();
					for(File k:fs)
					{
						API.startService(ctx,new Intent()
										 .putExtra("path",k.getAbsolutePath())
										 .putExtra("crash",true)
										 ,cls.TEXTEDITOR);
					}
				}
			}
			catch(Throwable ee)
			{

			}
		LinearLayout sc=(LinearLayout)v.findViewById(R.id.mainLinearLayout1);
		String[] k="	 { } ( ) ; , . = \" ' | & ! ^ [ ] < > + - / * ? : _ @ # $".split(" ");
		for(final String s:k)
		{
			myTextView t=new myTextView(c);
			t.setPadding(util.px(10),util.px(5),util.px(10),util.px(5));
			t.setText(s);
			if("	".equals(s))t.setText("↹");
			t.setGravity(Gravity.CENTER);
			t.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View p1)
					{
						l.commitText(s,0);
					}
				});
			sc.addView(t);
		}
		SharedPreferences sp=util.getSPRead("longtexteditor");
		l.textColor=sp.getInt("text",0xffffffff);
		l.selectionColor=sp.getInt("sel",0xff20e0f0);
		l.backColor=sp.getInt("lback",0xff000000);
		l.lineNumColor=sp.getInt("ln",0xffaaaaaa);
		l.lineNumBackColor=sp.getInt("lnb",0xff202020);
		l.lineNumLineColor=sp.getInt("lnl",0xffffffff);
		l.curLineColor=sp.getInt("cl",0xff404040);
		l.cursorColor=sp.getInt("cc",0xff5050ff);
		l.cursorLeftColor=sp.getInt("clc",0xffff5050);
		l.cursorRightColor=sp.getInt("crc",0xff50ff50);
		l.cursorLineColor=sp.getInt("clic",0xffffffff);
		l.enterColor=sp.getInt("en",0xffc0c0c0);
		l.suggBackColor=sp.getInt("sgb",0xff505050);
		l.suggTextColor=sp.getInt("sgt",0xffffffff);
		l.suggLineColor=sp.getInt("sgl",0xffffffff);
		l.setTextWatcher(this);
		new Thread(){
			@Override public void run()
			{
				while(!destroy)try
					{
						if(l.curSyntax!=null)l.highLight();
						else l.span.clear();
						Thread.sleep(2);
					}
					catch(Throwable e)
					{}
			}
		}.start();
		new Thread(){
			@Override public void run()
			{
				while(!destroy)try
					{
						if(l.curSyntax!=null)l.highLightNow();
						else l.nowspan.clear();
						Thread.sleep(2);
					}
					catch(Throwable e)
					{}
			}
		}.start();
	}
	@Override
	public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
	{
		changed=true;
		if(!w.getTitle().endsWith("*"))w.setTitle(w.getTitle()+"*");
	}
	@Override
	public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
	{
		// TODO: Implement this method
	}
	@Override
	public void afterTextChanged(Editable p1)
	{
		// TODO: Implement this method
	}
	@Override
	public void onButtonDown(int code)
	{
		if(code==Window.ButtonCode.CLOSE)
		{
			if(changed)
				new myDialog.Builder(ctx)
					.setMessage("是否保存对 \""+new File(file).getName()+"\" 的更改?")
					.setPositiveButton("保存",new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface p1, int p2)
						{
							Main.this.onClick(v.findViewById(R.id.windowlongtexteditorVecView2));
							destroy=true;
						}
					})
					.setNegativeButton("不保存",new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface p1, int p2)
						{
							destroy=true;
						}
					})
					.setNeutralButton("返回",new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface p1, int p2)
						{
							w.show();
						}
					})
					.show()
					.setCancelable(false);
			else destroy=true;
		}
	}
	@Override
	public void onCrash(Throwable e)throws Exception
	{
		if(!changed)return;
		File f=new File(util.mainDir+"/文本编辑器/crash");
		if(!f.exists())f.mkdirs();
		File a=new File(file);
		if(a.exists())file=a.getName();
		else file="未保存.txt";
		BufferedOutputStream os=new BufferedOutputStream(new FileOutputStream(f.getAbsolutePath()+"/"+file));
		String s=l.getText();
		os.write(s.getBytes());
		os.flush();
		os.close();
	}
	@Override
	public void onClick(final View p1)
	{
		switch(p1.getId())
		{
			case R.id.windowlongtexteditorVecView1:
				API.startServiceForResult(ctx,w,new BroadcastReceiver(){
						@Override
						public void onReceive(Context c,Intent e)
						{
							try
							{
								file=e.getStringExtra("path");
								l.clear();
								l.span.clear();
								l.yOff=0;
								l.yVel=0;
								BufferedReader read=new BufferedReader(new FileReader(file));
								String b="";
								while((b=read.readLine())!=null)l.addText(b);
								read.close();
								w.setTitle(new File(file).getName());
								v.findViewById(R.id.windowlongtexteditorVecView14).setVisibility(file.toLowerCase().endsWith(".js")?0:8);
							}
							catch(Exception ex)
							{
								myToast.makeText(ctx,"打开失败",0).show();
							}
						}
					},cls.EXPLORER);
				break;
			case R.id.windowlongtexteditorVecView2:
				if(new File(file).exists())
					new myDialog.Builder(ctx)
						.setMessage("文件已存在，是否覆盖？")
						.setPositiveButton("确定",new DialogInterface.OnClickListener(){
							@Override
							public void onClick(DialogInterface p1, int p2)
							{
								save();
							}
						})
						.setNegativeButton("取消",null)
						.show();
				else API.startServiceForResult(ctx,new Intent().putExtra("save",true).putExtra("savefile","新建文件.txt"),w,new BroadcastReceiver(){
							@Override
							public void onReceive(Context c,Intent e)
							{
								file=e.getStringExtra("path");
								if(new File(file).exists())
									new myDialog.Builder(ctx)
										.setMessage("文件已存在，是否覆盖？")
										.setPositiveButton("确定",new DialogInterface.OnClickListener(){
											@Override
											public void onClick(DialogInterface p1, int p2)
											{
												save();
											}
										})
										.setNegativeButton("取消",new DialogInterface.OnClickListener(){
											@Override
											public void onClick(DialogInterface p1, int p2)
											{
												file="";
											}
										})
										.show();
								else save();
							}
						},cls.EXPLORER);
				break;
			case R.id.windowlongtexteditorVecView3:
				View g=v.findViewById(R.id.mainLinearLayout2);
				if(g.getVisibility()==0)g.setVisibility(8);
				else g.setVisibility(0);
				break;
			case R.id.windowlongtexteditorVecView4:
				l.ViewMode=!l.ViewMode;
				if(l.ViewMode)
				{
					((VecView)p1).setImageVec("view");
					l.isSelection=false;
					util.hideIME(l);
				}
				else ((VecView)p1).setImageVec("edit");
				break;
			case R.id.windowlongtexteditorVecView5:
				HorizontalScrollView h=(HorizontalScrollView)v.findViewById(R.id.mainHorizontalScrollView1);
				if(h.getVisibility()==0)h.setVisibility(8);
				else h.setVisibility(0);
				break;
			case R.id.windowlongtexteditorVecView6:
				StringBuilder b=new StringBuilder().append("无,");
				for(String s:l.syntax.keySet())
					b.append(s).append(",");
				final String[] ss=b.toString().substring(0,b.length()-1).split(",");
				new myDialog.Builder(ctx)
					.setTitle("选择高亮语法")
					.setItems(ss,new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface pp1, int p2)
						{
							if(p2==0)
							{
								l.curSyntax=null;
								((VecView)p1).setImageVec("highlightoff");
							}
							else
							{
								l.curSyntax=l.syntax.get(ss[p2]);
								((VecView)p1).setImageVec("highlight");
							}
						}
					})
					.show();
				break;
			case R.id.windowlongtexteditorVecView7:

				break;
			case R.id.windowlongtexteditorVecView8:

				break;
			case R.id.windowlongtexteditorVecView9:
API.startService(ctx,"com.yzrilyzr.longtexteditor.Settings");
				break;
			case R.id.windowlongtexteditorVecView10:
				l.findUp(((EditText)v.findViewById(R.id.mainEditText1)).getText().toString());
				break;
			case R.id.windowlongtexteditorVecView11:
				l.findDown(((EditText)v.findViewById(R.id.mainEditText1)).getText().toString());
				break;
			case R.id.windowlongtexteditorVecView12:
				if(l.isSelection)
				{
					String s=((EditText)v.findViewById(R.id.mainEditText2)).getText().toString();
					l.commitText(s,0);
				}
				break;
			case R.id.windowlongtexteditorVecView13:
				String s=((EditText)v.findViewById(R.id.mainEditText1)).getText().toString();
				String s2=((EditText)v.findViewById(R.id.mainEditText2)).getText().toString();
				String gg=l.getText();
				gg=gg.replace(s,s2);
				l.setText(gg);
				break;
			case R.id.windowlongtexteditorVecView14:
				String pp=file;
				int c=pp.lastIndexOf("/");
				if(c!=-1)pp=pp.substring(0,c);
				API.startService(ctx,new Intent().putExtra("jstext",l.getText()).putExtra("path",pp),cls.CONSOLE);
				break;

		}
	}
	private void save()
	{
		try
		{
			File f=new File(file);
			w.setTitle(f.getName());
			BufferedOutputStream os=new BufferedOutputStream(new FileOutputStream(f));
			String s=l.getText();
			os.write(s.getBytes());
			os.flush();
			os.close();
			myToast.makeText(ctx,"保存成功",0).show();
			w.setTitle(w.getTitle());
			changed=false;
		}
		catch (Exception e)
		{
			myToast.makeText(ctx,"保存失败",0).show();
		}
	}
}
