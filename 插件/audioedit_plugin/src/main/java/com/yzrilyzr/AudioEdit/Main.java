package com.yzrilyzr.AudioEdit;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import com.yzrilyzr.AudioEdit.WaveView;
import com.yzrilyzr.floatingwindow.pluginapi.API;
import com.yzrilyzr.floatingwindow.pluginapi.Window;
public class Main implements View.OnClickListener//SeekBar.OnSeekBarChangeListener,OnEditorActionListener
{
	private Window WaveWindow,ControlWindow;
	private MusicPlayer track;
	@Override
	public void onClick(View p1)
	{
		// TODO: Implement this method
		switch(p1.getId())
		{
			case R.id.playButton1:
				track.togglePause();
				break;
			case R.id.playButton2:
				track.play();
				break;
			case R.id.playButton3:
				if(track.isWriteOut())track.writeOut(null);
				else track.writeOut("/sdcard/写出.wav");
				break;
			case R.id.playButton4:
				WaveWindow.show();
				break;
			case R.id.playButton5:
				break;
			case R.id.playButton6:
				ControlWindow.show();
				break;
		}
	}


	Context ctx;
	WaveView WaveView;
	public Main(Context ctx,Intent intent)
	{
		this.ctx=ctx;

		WaveView=new WaveView(ctx,null);
		WaveView.setLayoutParams(new LinearLayout.LayoutParams(-1,-1));
		WaveWindow=new Window(ctx,API.px(240),API.px(280));
		WaveWindow.setTitle("波形");
		WaveWindow.addView(WaveView);
		WaveWindow.setBar(8,0,0,0);
		ViewGroup v=null;


		ControlWindow=new Window(ctx,API.px(300),-2);
		ControlWindow.setTitle("全局控制");
		v=ControlWindow.getContentView();
		new AddSet(v,"速度",30,40,1,20){public void onSeek(int f)
			{track.p_skip=f;}};
		new AddSet(v,"播放采样率",40100,44000,0,-4000){public void onSeek(int f)
			{track.setSR(f);}};
		new AddSet(v,"音频采样率",40100,92000,0,-4000){public void onSeek(int f)
			{track.p_sr=f;}};
		int maxGain=20;
		new AddSet(v,"左增益",maxGain*10+10,maxGain*20,1,maxGain*10){public void onSeek(float f)
			{track.p_leftGain=f;}};
		new AddSet(v,"右增益",maxGain*10+10,maxGain*20,1,maxGain*10){public void onSeek(float f)
			{track.p_rightGain=f;}};
		new AddSet(v,"左滤波",0,999,0,-1){public void onSeek(float f)
			{track.p_leftCap=1f/f;}};
		new AddSet(v,"右滤波",0,999,0,-1){public void onSeek(float f)
			{track.p_rightCap=1f/f;}};

		Window window=new Window(ctx,-2,-2);
		window.show();
		window.setTitle("AudioEdit");
		v=(ViewGroup) API.parseXmlViewFromFile(ctx,"com.yzrilyzr.AudioEdit","res/layout/play.xml");
		window.addView(v);
		SeekBar seek_progress=(SeekBar) v.findViewById(R.id.playSeekBar1);
		seek_progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
				@Override
				public void onProgressChanged(SeekBar p1, int p2, boolean p3)
				{
					if(track.onSeek)track.seek(p2);
				}
				@Override
				public void onStartTrackingTouch(SeekBar p1)
				{
					track.onSeek=true;
				}
				@Override
				public void onStopTrackingTouch(SeekBar p1)
				{
					track.onSeek=false;
				}
			});
		//===
		String file=intent.getStringExtra("file");
		track=new MusicPlayer(file,seek_progress,WaveView);
		track.play();
		//===
		
		v.findViewById(R.id.playButton1).setOnClickListener(this);
		v.findViewById(R.id.playButton2).setOnClickListener(this);
		v.findViewById(R.id.playButton3).setOnClickListener(this);
		v.findViewById(R.id.playButton4).setOnClickListener(this);
		v.findViewById(R.id.playButton5).setOnClickListener(this);
		v.findViewById(R.id.playButton6).setOnClickListener(this);
		final wave.sin sin=new wave().new sin(track.TEST_SR,0,20000,0);
		new AddSet(v,"正弦波",0,250,0,0){public void onSeek(float f){sin.setHz(track.TEST_SR,f);}};
		((CheckBox)v.findViewById(R.id.playCheckBox1)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
				@Override
				public void onCheckedChanged(CompoundButton p1, boolean p2)
				{
					// TODO: Implement this method
					if(p2)
					{
						track.inter=new MusicPlayer.Interface(){
							@Override
							public byte[] getData()
							{
								// TODO: Implement this method
								int[] a=new int[track.BufferSize];
								for(int i=0;i<a.length;i++){
									a[i]=(int)sin.getY();
									sin.next();
									}
								return wave.stereo_PCM_16Bit(a,a);
							}
						};
						track.playI();
					}
					else track.stop();
				}
			});
		window.setWindowInterface(new Window.WindowInterface(){
				@Override
				public void onButtonDown(int code)
				{
					// TODO: Implement this method
					if(code==Window.ButtonCode.CLOSE&&track!=null)
					{
						track.stop();
						WaveWindow.dismiss();
						ControlWindow.dismiss();
					}
				}
				@Override
				public void onSizeChanged(int w, int h, int oldw, int oldh)
				{
					// TODO: Implement this method
				}
				@Override
				public void onPositionChanged(int x, int y)
				{
					// TODO: Implement this method
				}
			});

	}


}
