package com.yzrilyzr.floatingwindow;

import android.content.Context;
import com.yzrilyzr.myclass.util;

public class PluginJsCbk
{
	Context ctx;
	String ppk,ppth;
	public PluginJsCbk(Context ctx, String ppk, String ppth)
	{
		this.ctx = ctx;
		this.ppk = ppk;
		this.ppth = ppth;
	}
	public void print(Object o){
		util.toast(""+o);
	}
	public Context ctx() throws Exception{
		return new PluginContext(ctx,ppk,ppth);
	}
}
