package com.yzrilyzr.floatingwindow;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import com.yzrilyzr.floatingwindow.apps.cls;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.ui.myDialog;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

public class GetServer implements Runnable
{
	private static final String s="https://yzrilyzr.wodemo.net/entry/";
	private static int main=485517;
	private static HashMap<String,String> vs=new HashMap<String,String>();
	public static void get()
	{
		new Thread(new GetServer()).start();
	}
	@Override
	public void run()
	{
		int times=0;
		while(++times<=10)
			try
			{
				URL u=new URL(s+main);
				URLConnection c=u.openConnection();
				c.setConnectTimeout(10000);
				c.connect();
				BufferedReader is=new BufferedReader(new InputStreamReader(c.getInputStream()));
				String r=null;
				StringBuilder sb=new StringBuilder();
				while((r=is.readLine())!=null)sb.append(r).append("\n");
				is.close();
				String x=sb.substring(sb.indexOf("↔"),sb.lastIndexOf("↔"));
				String[] kv=x.split("■");
				for(String p:kv)
					if(!p.startsWith("//")&&p.contains(":"))
					{
						int f=p.indexOf(":");
						vs.put(p.substring(0,f),p.substring(f+1,p.length()-1));
					}
				times=11;
				new Handler(util.ctx.getMainLooper()).post(new Runnable(){
					@Override
					public void run()
					{
						try
						{
							int versionCode = util.ctx.getPackageManager().getPackageInfo(util.ctx.getPackageName(), 0).versionCode;
							if(Integer.parseInt(vs.get("update"))>versionCode)
								new myDialog.Builder(util.ctx)
								.setTitle("更新提示")
								.setMessage(vs.get("updateMsg"))
								.setPositiveButton("更新",new DialogInterface.OnClickListener(){
									@Override
									public void onClick(DialogInterface p1, int p2)
									{
										API.startService(util.ctx,new Intent().putExtra("url",vs.get("updateUrl")),cls.DOWNLOADER);
									}
								})
								.setNegativeButton("取消",null)
								.show()
								.setCancelable(false);
						}
						catch (Exception e)
						{
							//e.printStackTrace();
						}
						final String 公告=vs.get("公告");
						if(公告!=null&&!util.getSPRead().getString("公告","").equals(公告))
						{
							util.getSPWrite().putString("公告",公告).commit();
							new myDialog.Builder(util.ctx)
							.setTitle("公告")
							.setMessage(公告)
							.setPositiveButton("了解！",null)
							.setNeutralButton("复制",new DialogInterface.OnClickListener(){
								@Override
								public void onClick(DialogInterface p1, int p2)
								{
									util.copy(公告);
								}
							})
							.show()
							.setCancelable(false);
						}
					}
				});
			}
			catch(Throwable e)
			{
				//e.printStackTrace();
			}
	}
}
