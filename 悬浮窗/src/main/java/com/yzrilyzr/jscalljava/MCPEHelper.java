package com.yzrilyzr.jscalljava;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.yzrilyzr.floatingwindow.Window;
import com.yzrilyzr.myclass.util;

public class MCPEHelper implements Window.OnButtonDown
{
	private Context ctx;
	public MCPEHelper(Context c,Intent e)
	{
		ctx=c;
		/*IntentFilter it=new IntentFilter();
		it.addAction("MCPEC");
		c.registerReceiver(new BroadcastReceiver(){
				@Override
				public void onReceive(Context p1, Intent p2)
				{
					
				}
			},it);*/
		new Window(c,util.px(230),util.px(260))
		.setTitle("MCPE助手")
		.show();
	}
	public void cjs(String n,Object... p)
	{
			Intent c=new Intent("MCPES");
			int op=0;
			if(p!=null)
			for(Object o:p){
				if(o instanceof int)c.putExtra(Integer.toString(op++),(int)o);
			}
			ctx.sendBroadcast(c);
	}
	@Override
	public void onButtonDown(int code)
	{
		if(code==Window.ButtonCode.CLOSE)
		{
			
		}
	}
	
}
