package com.yzrilyzr.floatingwindow;
import java.io.*;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.widget.ScrollView;
import com.yzrilyzr.floatingwindow.apps.Console;
import com.yzrilyzr.floatingwindow.apps.cls;
import com.yzrilyzr.myclass.AES;
import com.yzrilyzr.myclass.JSEnv;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.ui.myDialog;
import com.yzrilyzr.ui.myTextView;
import com.yzrilyzr.ui.uidata;
import dalvik.system.PathClassLoader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class PluginService extends android.app.Service implements Thread.UncaughtExceptionHandler
{
	public static final ArrayList<JSEnv> jsenv=new ArrayList<JSEnv>();
	public static boolean started=false;
	@Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
		if(Window.notify)
		{
			Intent inte =new Intent(this,mBroadcastReceiver.class);
			inte.setAction("com.yzrilyzr.close");
			PendingIntent pendingIntent =PendingIntent.getBroadcast(this,0,inte,0);
			Notification.Builder builder1 = new Notification.Builder(this)
			.setSmallIcon(R.drawable.icon) //设置图标
			.setTicker("悬浮窗主服务已开始运行")
			.setContentTitle("悬浮窗主服务正在运行")//设置标题
			.setContentText("点击这里停止运行")//消息内容
			.setSound(null)
			.setVibrate(null)
			.setAutoCancel(false)//打开程序后图标消失
			.setContentIntent(pendingIntent);
			Notification notify=builder1.build();
			startForeground(1,notify);
		}
		else stopForeground(true);
        if(intent!=null)loadPlugin(this,intent);
		//System.out.println("startcommand");
        return START_STICKY;
    }
	public static final void fstop(Context c)
	{
		c.stopService(new Intent(c,PluginService.class));
		System.exit(0);
	}
	public static final void loadJS()
	{
		if(!Window.usejs)return;
		File d=new File(util.mainDir+"js辅助");
		if(!d.exists())d.mkdirs();
		String[] fs=d.list();
		callJSEnv("remove");
		jsenv.clear();
		Set<String> set=util.getSPRead().getStringSet("enabledjs",null);
		if(set!=null)
			for(String s:fs)
				try
				{
					String js=util.readwithN(util.mainDir+"js辅助/"+ s);
					if(set.contains(AES.encrypt(s,js)))
						jsenv.add(new JSEnv(js,new PluginContext(util.ctx,util.ctx.getPackageName(),util.ctx.getPackageCodePath())));
				}
				catch(Throwable e)
				{
					e.printStackTrace();
					util.toast(String.format("载入 %s 时出现错误,详情查看控制台",s));
				}
	}
	public static final void callJSEnv(String name,Object... params)
	{
		for(JSEnv j:jsenv)
			try
			{
				j.function(name,params);
			}
			catch(Throwable e)
			{
				e.printStackTrace();
			}
	}
	public static final InputStream getPluginPkgFile(String pkg,String file) throws Exception{
		File f=new File(pkg);
		if(f.isDirectory())return new BufferedInputStream(new FileInputStream(f.getAbsolutePath()+"/"+file));
		else if(f.isFile()){
			ZipFile ff=new ZipFile(f);
			ZipEntry en=ff.getEntry(file);
			return ff.getInputStream(en);
		}
		throw new Exception("未知文件");
	}
    public static final void loadPlugin(final Context ctx,final Intent intent)
    {
        String pkg="",clazz="";
        PackageManager PackageManager=ctx.getPackageManager();
        try
        {
			pkg=intent.getStringExtra("pkg");
			if(pkg==null)return;
            clazz=intent.getStringExtra("class");
            if(pkg.startsWith("pkg:")){
				pkg=pkg.substring(4);
				PluginContext pc=new PluginContext(ctx,pkg,pkg);
				pc.setIntent(intent);
				new JSEnv(util.readwithN(getPluginPkgFile(pkg,clazz)),pc);
				return;
			}
			String path=PackageManager.getPackageInfo(pkg,PackageInfo.INSTALL_LOCATION_AUTO).applicationInfo.publicSourceDir;
            ClassLoader mainloader=ctx.getClassLoader();
			Class c=null;
			try
			{
				c=mainloader.loadClass(clazz);
			}
			catch(Throwable e)
			{
				PathClassLoader pcl=new PathClassLoader(path,mainloader);
				c=pcl.loadClass(clazz);
			}
			c.getConstructor(Context.class,Intent.class).newInstance(new PluginContext(ctx,pkg,path),intent);
        }
        catch(Throwable e)
        {
			String pkginfo="";
			try
			{
				pkginfo=PackageManager.getPackageInfo(pkg,PackageInfo.INSTALL_LOCATION_AUTO).applicationInfo.loadLabel(PackageManager)+"";
			}
			catch (PackageManager.NameNotFoundException e2)
			{
				pkginfo="未知";
			}
			myTextView mtv=new myTextView(ctx);
			mtv.setText("插件:"+pkginfo+"(Package:"+pkg+",Class:"+clazz+")\n\nstacktrace:\n"+handleException(e));
			mtv.setTextIsSelectable(true);
			mtv.getPaint().setTextSize(util.px(uidata.TEXTSIZE*0.8f));
			ScrollView sv=new ScrollView(ctx);
			sv.addView(mtv);
			new Window(ctx,util.px(300),util.px(200))
			.addView(sv)
			.setIcon("msgerror")
			.setTitle("出错了")
			.show();
        }
    }
	public static final String handleException(Throwable e)
    {
        String msg=null;
        if(e instanceof InvocationTargetException)
			while(e instanceof InvocationTargetException)
			{
				e=((InvocationTargetException)e).getTargetException();
				if(e!=null)msg=util.getStackTrace(e);
			}
        else msg=util.getStackTrace(e);
        return msg;
    }

    @Override
    public void onCreate()
    {
		super.onCreate();
		if(!util.getSPRead("abouta").getBoolean("abouta",false))fstop(this);
		util.ctx=getApplicationContext();
        Copyright.k();
		started=true;
		System.setOut(new PrintStream(Console.out));
		System.setErr(new PrintStream(Console.err));
		Thread ct=Thread.currentThread();
		ct.setName(getString(R.string.app_name));
		ct.setPriority(Thread.MAX_PRIORITY);
		/*String value=null;
		 try
		 {
		 Object roSecureObj = Class.forName("android.os.SystemProperties")
		 .getMethod("get", String.class)
		 .invoke(null, "gsm.version.baseband");
		 if (roSecureObj != null) value = (String) roSecureObj;
		 }
		 catch (Exception e)
		 {
		 value = null;
		 }
		 if(value==null)throw new RuntimeException("");*/
		if(!util.sup)util.toast("不支持的设备");
		Thread.setDefaultUncaughtExceptionHandler(this);
		ct.setUncaughtExceptionHandler(this);
		//if(Resources.getSystem().getDisplayMetrics().density!=3f)
		//Toast.makeText(this,"安全警告:\n不支持的DPI\n您可以在设置中调节显示效果",1).show();
		API.startService(this,cls.LOAD);

		if(util.getSPRead().getBoolean("first",true))API.startService(this,cls.SETTINGS);
    }
	@Override
	public void uncaughtException(Thread p1, final Throwable p2)
	{
		new Thread() {  
			@Override  
			public void run()
			{
				for(Window w:Window.windowList)
				{
					try
					{
						Window.OnCrash in=w.getOnCrash();
						if(in!=null)in.onCrash(p2);
					}
					catch(Throwable e)
					{
					}
				}
				if(!Window.crashdialog)
				{
					ByteArrayOutputStream os=new ByteArrayOutputStream();
					PrintWriter ps=new PrintWriter(os);
					ps.println("很抱歉，程序崩溃了(⊙﹏⊙)");
					ps.println("(无操作10秒后退出)");
					ps.println("错误信息已保存至 "+util.mainDir+"错误信息.txt");
					ps.println("");
					ps.println("错误堆栈:");
					p2.printStackTrace(ps);
					ps.println("");
					try
					{
						ps.println("设备信息:");
						Field[] fields = Build.class.getDeclaredFields();
						for(Field field: fields)
						{
							field.setAccessible(true);
							String name = field.getName();
							String value = field.get(null).toString();
							ps.println(name+"="+value);
						}
					}
					catch (Exception e)
					{
						ps.println("(无法获取设备信息)");
					}
					ps.flush();
					ps.close();
					try
					{
						util.write(util.mainDir+"错误信息.txt",os.toString());
					}
					catch(Throwable e)
					{

					}
					System.exit(0);
				}
				else
					try
					{
						final boolean[] bo=new boolean[]{true};
						new Thread(){
							@Override public void run()
							{
								try
								{
									Thread.sleep(10000);
									if(bo[0])System.exit(0);
								}
								catch (InterruptedException e)
								{}
							}
						}.start();
						ByteArrayOutputStream os=new ByteArrayOutputStream();
						PrintWriter ps=new PrintWriter(os);
						ps.println("很抱歉，程序崩溃了(⊙﹏⊙)");
						ps.println("(无操作10秒后退出)");
						ps.println("错误信息已保存至 "+util.mainDir+"错误信息.txt");
						ps.println("");
						ps.println("错误堆栈:");
						p2.printStackTrace(ps);
						ps.println("");
						try
						{
							ps.println("设备信息:");
							Field[] fields = Build.class.getDeclaredFields();
							for(Field field: fields)
							{
								field.setAccessible(true);
								String name = field.getName();
								String value = field.get(null).toString();
								ps.println(name+"="+value);
							}
						}
						catch (Exception e)
						{
							ps.println("(无法获取设备信息)");
						}
						ps.flush();
						ps.close();
						try
						{
							util.write(util.mainDir+"错误信息.txt",os.toString());
						}
						catch(Throwable e)
						{

						}
						Looper.prepare();
						new myDialog.Builder(PluginService.this)
						.setTitle("提示")
						.setCancelable(false)  
						.setMessage(os.toString())
						.setPositiveButton("退出并重启",new DialogInterface.OnClickListener() {  
							@Override  
							public void onClick(DialogInterface dialog, int which)
							{  
								System.exit(0);  
							}  
						})
						.setNegativeButton("继续使用(不推荐)",new DialogInterface.OnClickListener() {  
							@Override  
							public void onClick(DialogInterface dialog, int which)
							{  
								bo[0]=false;  
							}  
						})
						.show();
						Looper.loop();
					}
					catch (Exception e)
					{
						System.exit(0);
					}
			}  
		}.start();
	}
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        stopForeground(false);
		started=false;
    }
	@Override
	public IBinder onBind(Intent p1)
	{
		return null;
	}

	@Override
	public void onLowMemory()
	{
		super.onLowMemory();
		Runtime ru = Runtime.getRuntime();
		long usable = ru.maxMemory() - ru.totalMemory() + ru.freeMemory();
		System.gc();
		long usable2 = ru.maxMemory() - ru.totalMemory() + ru.freeMemory();
		util.toast("运行内存不足，已清理"+util.getFileSizeStr(usable-usable2));
	}

}
