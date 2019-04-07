package com.yzrilyzr.mcpehelper;
import android.content.Context;
import android.content.Intent;
import com.yzrilyzr.floatingwindow.Window;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.ui.uidata;
import java.lang.reflect.InvocationHandler;

public class Main
{
	private Context ctx;
	InvocationHandler call;
	public Main(final Context ctx,InvocationHandler c) throws Exception
	{
		this.ctx=ctx;
		call=c;
		util.ctx=ctx.getApplicationContext();
		Window.readData();
		uidata.readData();
		util.toast("成功载入，正在建立连接…");
		/*IntentFilter it=new IntentFilter();
		it.addAction("MCPES");
		ctx.registerReceiver(new BroadcastReceiver(){
				@Override
				public void onReceive(Context p1, Intent p2)
				{
					util.toast(p2.getStringExtra("v"));
				}
			},it);*/
		Intent intent=new Intent();
		intent.setAction("com.yzrilyzr.Service");
		intent.setPackage("com.yzrilyzr.floatingwindow");
		intent.putExtra("pkg","com.yzrilyzr.floatingwindow");
		intent.putExtra("class","com.yzrilyzr.jscalljava.MCPEHelper");
		ctx.startService(intent);
		/*String pkg="com.yzrilyzr.floatingwindow";
		PackageManager PackageManager=ctx.getPackageManager();
        String path=PackageManager.getPackageInfo(pkg,PackageInfo.INSTALL_LOCATION_AUTO).applicationInfo.publicSourceDir;
		new MCPEHelper(new PluginContext(ctx,pkg,path),null);*/
		util.toast("当前游戏版本:"+cjs("ModPE.getMinecraftVersion"));
	}
	public Object cjs(String n,Object... p)
	{
		try
		{
			return call.invoke(n,null,p);
		}
		catch(Throwable e)
		{
			util.toast(e);
			return null;
		}
	}
	public void jsc(String name,Object... obj)
	{
		switch(name)
		{
			case "newLevel":
				cjs("print","欢迎使用悬浮窗MCPE辅助!作者:yzrilyzr");
		}
	}
}
