package com.yzrilyzr.floatingwindow.viewholder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.Map;

public abstract class BaseHolder
{
	public ViewGroup vg;
	public BaseHolder(Context c,int id){
		vg=(ViewGroup)LayoutInflater.from(c).inflate(id,null);
	}
	protected View find(int id){
		return vg.findViewById(id);
	}
	public abstract void set(Object data);
}
