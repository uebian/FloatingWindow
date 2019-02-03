package com.yzrilyzr.AudioEdit;

import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.yzrilyzr.floatingwindow.pluginapi.API;

public class AddSet implements SeekBar.OnSeekBarChangeListener,OnEditorActionListener
{
	SeekBar s;
	EditText e;
	TextView t;
	int flo;
	int off;
	public AddSet(View v,String txt,int pro,int max,int flo,int off){
		this.flo=flo;
		this.off=off;
		ViewGroup vg=(ViewGroup) API.parseXmlViewFromFile(v.getContext(),"com.yzrilyzr.AudioEdit","res/layout/l_set.xml");
		((ViewGroup)v).addView(vg);
		t=(TextView)vg.getChildAt(0);
		t.setText(txt);
		s=(SeekBar) vg.getChildAt(1);
		e=(EditText)vg.getChildAt(2);
		e.setOnEditorActionListener(this);
		s.setOnSeekBarChangeListener(this);
		s.setMax(max);
		s.setProgress(pro);
	}
	@Override
	public void onProgressChanged(SeekBar p1, int p2, boolean p3)
	{
		// TODO: Implement this method
		float a=(float)(p2-off)/(float)Math.pow(10f,flo);
		e.setText(""+a);
		if(flo==0)e.setText(""+(int)a);
		try{
		onSeek(a);
		onSeek(p2-off);
		}catch(Throwable e){}
	}
	@Override
	public void onStartTrackingTouch(SeekBar p1)
	{
		// TODO: Implement this method
	}
	@Override
	public void onStopTrackingTouch(SeekBar p1)
	{
		// TODO: Implement this method
	}

	@Override
	public boolean onEditorAction(TextView p1, int p2, KeyEvent p3)
	{
		// TODO: Implement this method
		try
		{
			int i=(int)(Float.parseFloat(p1.getText().toString())*(float)Math.pow(10f,flo))+off;
			s.setProgress(i);
		}
		catch(Throwable e)
		{}
		return true;
	}
	public void onSeek(float f){}
	public void onSeek(int i){}
}
