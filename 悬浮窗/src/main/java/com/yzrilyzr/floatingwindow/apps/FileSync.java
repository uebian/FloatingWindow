package com.yzrilyzr.floatingwindow.apps;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import com.yzrilyzr.floatingwindow.R;
import com.yzrilyzr.floatingwindow.Window;
import com.yzrilyzr.myclass.util;

public class FileSync implements OnClickListener
{
	Window w,w2;
	View p1,p2;
	public FileSync(Context c,Intent e){
		String script=e.getStringExtra("script");
		w=new Window(c,util.px(260),util.px(300))
		.setTitle("文件同步")
		.setIcon("sync")
		.show();
		ViewGroup vg=(ViewGroup) w.addView(R.layout.window_filesync);
		
	}
	@Override
	public void onClick(View p1)
	{
		// TODO: Implement this method
	}
}
