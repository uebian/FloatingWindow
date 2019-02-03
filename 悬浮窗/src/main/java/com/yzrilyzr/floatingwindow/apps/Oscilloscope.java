package com.yzrilyzr.floatingwindow.apps;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import com.yzrilyzr.floatingwindow.R;
import com.yzrilyzr.floatingwindow.Window;
import com.yzrilyzr.floatingwindow.view.FloatPicker;
import com.yzrilyzr.floatingwindow.view.OscilloscopeView;
import com.yzrilyzr.myclass.Pcm;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.ui.mySpinner;
import com.yzrilyzr.ui.mySpinnerAdapter;

public class Oscilloscope implements FloatPicker.FloatPickerEvent,Runnable,Window.OnButtonDown
{
	AudioRecord record;
	OscilloscopeView osc;
	FloatPicker pa,pb;
	boolean recing=false;

	protected Window w;
	public Oscilloscope(final Context c,Intent e)
	{
		w=new Window(c,util.px(260),util.px(400))
		.setTitle("示波器")
		.setIcon("signal")
		.setOnButtonDown(this)
		.show();
		ViewGroup vg=(ViewGroup) w.addView(R.layout.window_oscilloscope);
		osc=(OscilloscopeView) vg.getChildAt(0);
		mySpinner sp=(mySpinner) vg.findViewById(R.id.windowoscilloscopemySpinner1);
		sp.setAdapter(new mySpinnerAdapter("内部指定,麦克风".split(",")));
		pa=(FloatPicker) vg.findViewById(R.id.windowoscilloscopeFloatPicker1);
		pb=(FloatPicker) vg.findViewById(R.id.windowoscilloscopeFloatPicker2);
		pa.setListener(this);
		pb.setListener(this);
		sp.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onNothingSelected(AdapterView<?> p1)
			{
				// TODO: Implement this method
			}
			@Override
			public void onItemSelected(AdapterView<?> p1, View p2, int p3, long p4)
			{
				if(p3==0)recing=false;
				if(p3==1)
				{
					util.toast("警告:此示波器不带输入衰减\n请勿输入高电压(>±0.1v)！！！");
					if(!recing)new Thread(Oscilloscope.this).start();
				}
			}
		});
	}

	@Override
	public void onButtonDown(int code)
	{
		if(code==Window.ButtonCode.CLOSE)recing=false;
	}
	@Override
	public void run()
	{
		try
		{
			util.toast("正在录音");
			recing=true;
			record=new AudioRecord(MediaRecorder.AudioSource.MIC,
			48000,
			AudioFormat.CHANNEL_CONFIGURATION_MONO,
			AudioFormat.ENCODING_PCM_16BIT,
			AudioTrack.getMinBufferSize(48000,
			AudioFormat.CHANNEL_CONFIGURATION_MONO,
			AudioFormat.ENCODING_PCM_16BIT)
			);
			record.startRecording();
			byte[] data=new byte[2400*2];
			osc.setSr(48000);
			while(recing)
			{
				record.read(data,0,data.length);
				int[] d=Pcm.mono_16Bit_PCM(data);
				append(d);
			}
			record.stop();
			record.release();
		}
		catch(Throwable e)
		{
			util.toast("录音失败");
			recing=false;
			e.printStackTrace();
		}
	}


	public void append(int[] data)
	{
		osc.append(data);
	}
	@Override
	public void onChange(FloatPicker p, float f)
	{
		if(f==0)f=0.01f;
		if(p==pa)osc.setScan(1f/f);
		else if(p==pb)osc.setGain(f);
	}
}
