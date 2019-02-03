package com.yzrilyzr.floatingwindow;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.yzrilyzr.myclass.util;
import java.util.HashMap;

public class mBroadcastReceiver extends BroadcastReceiver
{
	public static int index=1;
	public static final HashMap<Integer,BroadcastReceiver> cbk=new HashMap<Integer,BroadcastReceiver>();
    @Override
    public void onReceive(Context p1, Intent p2)
    {
		util.ctx=p1;
		Window.readData();
		System.out.println(p2);
		String act=p2.getAction();
		if("com.yzrilyzr.close".equals(act))PluginService.fstop(p1);
		else if("com.yzrilyzr.callback".equals(act)){
			int code=p2.getIntExtra("rescode",0);
			if(code!=0){
				try
				{
					BroadcastReceiver h=cbk.remove(code);
					h.onReceive(p1,p2);
				}
				catch (Throwable e)
				{}
			}
		}
		else if(Intent.ACTION_PACKAGE_ADDED.equals(act)||Intent.ACTION_PACKAGE_INSTALL.equals(act)){
			String packageName=p2.getData().getSchemeSpecificPart();
			p1.startActivity(new Intent(Intent.ACTION_DELETE,Uri.parse("package:"+packageName)));
		}
		else if(Intent.ACTION_BOOT_COMPLETED.equals(act)&&Window.startonboot)p1.startService(new Intent(p1,PluginService.class));
    }

}
