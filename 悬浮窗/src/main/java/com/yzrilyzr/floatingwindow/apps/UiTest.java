package com.yzrilyzr.floatingwindow.apps;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Switch;
import com.yzrilyzr.floatingwindow.R;
import android.widget.CompoundButton;
import com.yzrilyzr.floatingwindow.Window;
import com.yzrilyzr.myclass.util;

public class UiTest
{
	public UiTest(Context c,Intent e){
		final ViewGroup v=(ViewGroup) LayoutInflater.from(c).inflate(R.layout.uitest,null);
		((Switch)v.getChildAt(0)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
				@Override
				public void onCheckedChanged(CompoundButton p1, boolean p2)
				{
					ViewGroup g=(ViewGroup) ((ViewGroup)v.getChildAt(1)).getChildAt(0);
					for(int i=0;i<g.getChildCount();i++){
						g.getChildAt(i).setEnabled(p2);
					}
					g=(ViewGroup) ((ViewGroup)v.getChildAt(2)).getChildAt(0);
					for(int i=0;i<g.getChildCount();i++){
						g.getChildAt(i).setEnabled(p2);
					}
				}	
			});
		new Window(c,util.px(300),util.px(500)).setTitle("UI控件测试").addView(v).show();
	}
}
