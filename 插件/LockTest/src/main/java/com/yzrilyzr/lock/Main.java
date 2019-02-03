package com.yzrilyzr.lock;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.view.WindowManager;

public class Main
{
	Object win;
	public Main(Context c,Intent e) throws Exception{
		win=API.class2Object("com.yzrilyzr.floatingwindow.Window",new Class[]{Context.class,int.class,int.class},c,-1,-1);
		View v=API.parseXmlViewFromFile(c,"com.yzrilyzr.lock","res/layout/main.xml");
		WindowManager.LayoutParams l=(WindowManager.LayoutParams) API.invoke(win,"getLayoutParams");
		l.type=WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
		l.flags=WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
		WindowManager ll=(WindowManager) API.getField(win,"window");
		ll.addView(v,l);
	}
}
