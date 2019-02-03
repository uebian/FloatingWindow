package com.yzrilyzr.floatingwindow;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.Handler;
import com.yzrilyzr.myclass.util;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ANRThread extends Thread
{
	private long time=0;
	public static ANRThread inst=null;
    @Override
	public void run()
	{
		inst=this;
		setName("ANR检测");
		Handler h=new Handler(util.ctx.getMainLooper());
		while(Window.anr||!util.sup)
		{
			h.postAtFrontOfQueue(new Runnable(){
					@Override
					public void run()
					{
						time=System.currentTimeMillis();
					}
				});
			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{}
			if(System.currentTimeMillis()-time>3000){
				new Thread(){
					@Override public void run(){
						try
						{
							Thread.sleep(5000);
							System.exit(0);
						}
						catch (InterruptedException e)
						{}
					}
				}.start();
				throw new RuntimeException("发生了ANR，5秒后自动退出:"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis())));
			}
		}
		h=null;
		inst=null;
	}
	public static void startMain(){
		if(inst==null)new ANRThread().start();
	}
}
