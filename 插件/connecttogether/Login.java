/*package com.yzrilyzr.connecttogether;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.yzrilyzr.connecttogether.data.C;
import com.yzrilyzr.connecttogether.data.Client;
import com.yzrilyzr.connecttogether.data.User;
import com.yzrilyzr.floatingwindow.API;
import com.yzrilyzr.floatingwindow.R;
import com.yzrilyzr.floatingwindow.Window;
import com.yzrilyzr.icondesigner.VecView;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.ui.myDialog;
import com.yzrilyzr.ui.myEditText;
import com.yzrilyzr.ui.myViewPager;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLConnection;
import com.yzrilyzr.floatingwindow.PluginService;
import com.yzrilyzr.floatingwindow.MainActivity;

public class Login implements Window.OnButtonDown,OnClickListener,Client.Rev,myViewPager.OnPageChangeListener,Runnable
{
	private Context ctx;
	private EditText id,pwd,regpwd1,regpwd2,vcode;
	Client client;
	myViewPager page;
	View load1,load2,login,register,regtxt,sett;
	Window w;
	ImageView vcodeimage;
	boolean timeout=true;
	String vcodetxt="";
	short rcode;
	public Login(Context c,Intent e) throws Throwable
	{
		ctx=c;
		w=new Window(c,util.px(230),util.px(300))
			.setTitle("登录")
			.setCanResize(false)
			.setBar(0,8,0)
			.setIcon("ct/connecttogether")
			.setOnButtonDown(this)
			.show();
		ViewGroup v=(ViewGroup) LayoutInflater.from(ctx).inflate(R.layout.ct_login,null),
			v2=(ViewGroup) LayoutInflater.from(ctx).inflate(R.layout.ct_register,null);
		page=new myViewPager(c);
		page.setOnPageChangedListener(this);
		page.setLayoutParams(new LinearLayout.LayoutParams(-1,-1));
		w.addView(page);
		page.setPages(v,v2);
		id=(EditText) v.getChildAt(1);
		pwd=(EditText) v.getChildAt(2);
		regpwd1=(EditText) v2.getChildAt(0);
		regpwd2=(EditText) v2.getChildAt(1);
		ViewGroup f2=(ViewGroup)v2.getChildAt(2);
		vcode=(EditText) f2.getChildAt(0);
		vcodeimage=(ImageView) f2.getChildAt(1);
		register=v2.getChildAt(3);
		SharedPreferences sp=util.getSPRead();
		id.setText(sp.getString("id",""));
		pwd.setText(sp.getString("pwd",""));
		VecView head=(VecView) v.getChildAt(0);
		login=v.getChildAt(3);
		login.setOnClickListener(this);
		register.setOnClickListener(this);
		vcodeimage.setOnClickListener(this);
		ViewGroup g2=(ViewGroup) v.getChildAt(5);
		load1=g2.getChildAt(0);
		load2=g2.getChildAt(1);
		sett=g2.getChildAt(3);
		regtxt=g2.getChildAt(4);
		regtxt.setOnClickListener(this);
		sett.setOnClickListener(this);
		client=new Client(ctx);
		client.serveraddr=new InetSocketAddress(sp.getString("ip","127.0.0.1"),10000);
		rcode=client.addRev(this);
		client.connect();
		new Handler(c.getMainLooper()).postDelayed(this,5000);
		/*Intent intentAddShortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT"); 
		//添加名称 
		intentAddShortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME,"CT"); 
		//添加图标 
		intentAddShortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,Intent.ShortcutIconResource.fromContext(ctx,R.drawable.icon)); 
		intentAddShortcut.putExtra("duplicate", false);// 经测试不是根据快捷方式的名字判断重复的
		//设置Launcher的Uri数据 
		Intent intent= new Intent(Intent.ACTION_MAIN); 
		intent.setClass(ctx,MainActivity.class);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.setAction("com.yzrilyzr.Service");
        intent.setPackage("com.yzrilyzr.floatingwindow");
        intent.putExtra("pkg",ctx.getPackageName());
        intent.putExtra("class",this.getClass().getName());
		intentAddShortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent); 
		ctx.sendBroadcast(intentAddShortcut); 
	}
	@Override
	public void run()
	{
		if(timeout)
		{
			util.toast("连接超时");
			try
			{
				client.connect();
			}
			catch (Exception e)
			{
				util.toast("连接失败");
			}
			new Handler(ctx.getMainLooper()).postDelayed(this,5000);
		}
	}
	@Override
	public void onPageChanged(int last, int newone)
	{
		if(newone==0)w.setTitle("登录");
		else if(newone==1)
		{
			spawnVCode();
			w.setTitle("注册");
		}
	}
	@Override
	public void rev(byte cmd,DatagramPacket p)
	{
		if(cmd==C.GETSTAT)
		{
			load1.setVisibility(8);
			load2.setVisibility(8);
			timeout=false;
		}
		else if(cmd==C.REGISTER)
		{
			try
			{
				User u=new User(p.getData());
				page.setCurrentItem(0,true);
				util.toast("注册成功");
				id.setText(Long.toString(u.id));
			}
			catch (IOException e)
			{
				util.toast("注册失败");
			}
		}
		else if(cmd==C.LOGINFAILED)util.toast("账号或密码错误");
		else if(cmd==C.LOGINSUCCESS)
		{
			util.toast("登录成功");
			try
			{
				client.setMe(new User(p.getData()));
				w.dismiss();
				new Main(client);
				client.removeRev(this);
			}
			catch (IOException e)
			{
				util.toast("获取用户信息失败");
			}
		}
	}
	@Override
	public void onClick(View p1)
	{
		if(p1==login)
			try
			{
				saveData();
				User user=new User(Long.parseLong(id.getText().toString()));
				user.password=pwd.getText().toString();
				client.send(user.set(C.LOGIN,rcode,user.id));
			}
			catch (NumberFormatException e)
			{
				util.toast("帐户不合法");
			}
		else if(p1==sett)set();
		else if(p1==regtxt)page.setCurrentItem(1,true);
		else if(p1==register)
		{
			String a=regpwd1.getText().toString(),b=regpwd2.getText().toString(),c=vcode.getText().toString();
			if(!a.equals(b))util.toast("两次输入的密码不一致");
			else if(a.length()<6)util.toast("密码长度至少需要6位");
			else if(c.equals("")||!c.equals(vcodetxt))util.toast("验证码错误");
			else
			{
				regpwd1.setEnabled(false);
				regpwd2.setEnabled(false);
				vcode.setEnabled(false);
				register.setEnabled(false);
				User user=new User(0);
				user.password=regpwd1.getText().toString();
				user.signature=vcodetxt;
				client.send(user.set(C.REGISTER,rcode,0));
			}
		}
		else if(p1==vcodeimage)spawnVCode();
	}
	public void set()
	{
		final myEditText e=new myEditText(ctx);
		e.setHint("服务器IP");
		String t=client.serveraddr.toString();
		if(t.length()>2&&t.contains("/"))e.setText(t.substring(1,t.indexOf(":")));
		new myDialog.Builder(ctx)
			.setTitle("设置目标服务器IP")
			.setView(e)
			.setPositiveButton("保存",new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface v,int i)
				{
					String ip=e.getText().toString();
					client.serveraddr=new InetSocketAddress(ip,10000);
					util.getSPWrite()
						.putString("ip",ip)
						.commit();
				}
			})
			.setNeutralButton("官方服务器",new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface v,int ii)
				{
					new Thread(new Runnable(){
							@Override
							public void run()
							{
								try
								{
									util.toast("获取中");
									{
										URL url=new URL(String.format("https://%s%s.com/entry/%d",ctx.getPackageName().substring(4,13),"sldenl".replace('s','w').replace('l','o').replace('n','m'),229*2000+17));
										URLConnection co=url.openConnection();
										InputStream is=co.getInputStream();
										byte[] b=new byte[10240];
										StringBuilder bu=new StringBuilder();
										int i=0;
										while((i=is.read(b))!=-1)bu.append(new String(b,0,i));
										is.close();
										String ip=bu.substring(bu.indexOf("THIS")+4,bu.indexOf("ENDL"));
										client.serveraddr=new InetSocketAddress(ip,10000);
										util.getSPWrite()
											.putString("ip",ip)
											.commit();
									}
									util.toast("已使用官方服务器");
								}
								catch (Exception e2)
								{
									util.toast("获取失败");
								}
							}
						}).start();
				}
			})
			.setNegativeButton("取消",null)
			.show();
		e.getLayoutParams().width=-1;
	}
	private void spawnVCode()
	{
		int q=util.random(1,50),w=util.random(1,10),e=util.random(0,7);
		String z=String.format("%d%s%d=?",q,new String[]{"+","-","*","/","%","|","&","^"}[e],w);
		if(e==0)vcodetxt=Integer.toString(q+w);
		if(e==1)vcodetxt=Integer.toString(q-w);
		if(e==2)vcodetxt=Integer.toString(q*w);
		if(e==3)vcodetxt=Integer.toString(q/w);
		if(e==4)vcodetxt=Integer.toString(q%w);
		if(e==5)vcodetxt=Integer.toString(q|w);
		if(e==6)vcodetxt=Integer.toString(q&w);
		if(e==7)vcodetxt=Integer.toString(q^w);
		Bitmap b=Bitmap.createBitmap(vcodeimage.getWidth(),vcodeimage.getHeight(),Bitmap.Config.ARGB_8888);
		Canvas c=new Canvas(b);
		Paint p=new Paint();
		int o=util.random(0,0xffffff);
		c.drawColor(0xffffffff-o);
		p.setColor(0x80000000+o);
		p.setTextSize(util.px(15));
		p.setStyle(Paint.Style.FILL);
		c.drawText(z,util.random(0,vcodeimage.getWidth()/4),util.random((int)(p.getTextSize()),(int)(p.getTextSize()*2.5f)),p);
		p.setStrokeWidth(1);
		for(int i=0;i<50;i++)
			c.drawLine(util.random(0,vcodeimage.getWidth()),
					   util.random(0,vcodeimage.getHeight()),
					   util.random(0,vcodeimage.getWidth()),
					   util.random(0,vcodeimage.getHeight()),p);   
		p.setStrokeWidth(2);
		for(int i=0;i<50;i++)
			c.drawPoint(util.random(0,vcodeimage.getWidth()),
						util.random(0,vcodeimage.getHeight()),p);
		vcodeimage.setImageBitmap(b);
	}
	private void saveData()
	{
		util.getSPWrite()
			.putString("id",id.getText().toString())
			.putString("pwd",pwd.getText().toString())
			.commit();
	}
	@Override
	public void onButtonDown(int p1)
	{
		if(p1==3)
		{
			saveData();
			timeout=false;
			client.removeRev(this);
		}
	}
}*/
