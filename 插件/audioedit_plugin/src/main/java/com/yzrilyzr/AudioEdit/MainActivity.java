package com.yzrilyzr.AudioEdit;

import android.media.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.util.*;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView.OnEditorActionListener;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MainActivity extends Activity// implements OnEditorActionListener,OnSeekBarChangeListener
{
/*
    private SeekBar sampleRateSeekbar;

    private SeekBar playSpeeds;


    @Override
    public boolean onEditorAction(TextView p1, int p2, KeyEvent p3)
    {
        // TODO: Implement this method
        try
		{
			float f=Float.parseFloat(p1.getText().toString());
			switch(p1.getId())
			{
				case R.id.mainEditText3:
					leftGain=f;
					leftgain.setProgress((int)(leftGain*10)+20);
					break;
				case R.id.mainEditText4:
					rightGain=f;
					rightgain.setProgress((int)(rightGain*10)+20);
					break;
				case R.id.mainEditText5:
					TEST_SR=(int)f;
					sampleRateSeekbar.setProgress(TEST_SR-4000);
					track.setPlaybackRate(TEST_SR);
					break;
				case R.id.mainEditText6:
					skip=(int)(f*10)-1;
					playSpeeds.setProgress((int)(f*10)-1);
					break;
			}}
		catch(Throwable e)
		{}
        return true;
    }

    static Context ctx;
    String file="";
	int TEST_SR =44100;
    int BufferSize=1080;
	int TEST_CONF =AudioFormat.CHANNEL_CONFIGURATION_STEREO;
	int TEST_FORMAT= AudioFormat.ENCODING_PCM_16BIT;
	int TEST_MODE =AudioTrack.MODE_STREAM;
	int TEST_STREAM_TYPE = AudioManager.STREAM_MUSIC;
	int minBuffSize = AudioTrack.getMinBufferSize(TEST_SR, TEST_CONF, TEST_FORMAT);
	AudioTrack track;
	WaveView WaveView;
    SeekBar progress,leftgain,rightgain;
    boolean record=false,isPlay=false,write=false,mono=false,playBack=false,compress=false;
    double leftGain=1.0,rightGain=1.0;
    static ImageView peak;
    int maxfps=0,skip=0;
    EditText fileName,Elg,Erg,Esr,Esp;

	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        ctx=this;
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        progress=(SeekBar)findViewById(R.id.mainSeekBar2);
		//WaveView=new Wa(WaveView)findViewById(R.id.mainImageView1);
		sampleRateSeekbar=(SeekBar)findViewById(R.id.mainSeekBar1);
        peak=(ImageView) findViewById(R.id.mainImageView2);
		sampleRateSeekbar.setMax(44000);
		sampleRateSeekbar.setProgress(40100);
        fileName=(EditText) findViewById(R.id.mainEditText1);
        try
        {
            file=getIntent().getDataString();
            file=Uri.parse(file).getPath();//.decode(file);
        }
        catch(Exception e)
        {}
        progress.setOnSeekBarChangeListener(this);
        sampleRateSeekbar.setOnSeekBarChangeListener(this);
        playSpeeds=(SeekBar)findViewById(R.id.mainSeekBar5);
        playSpeeds.setMax(19);
        playSpeeds.setProgress(9);
        playSpeeds.setOnSeekBarChangeListener(this);
        leftgain=(SeekBar)findViewById(R.id.mainSeekBar3);
        rightgain=(SeekBar)findViewById(R.id.mainSeekBar4);
        leftgain.setMax(40);leftgain.setProgress(30);
        rightgain.setMax(40);rightgain.setProgress(30);
        leftgain.setOnSeekBarChangeListener(this);
        rightgain.setOnSeekBarChangeListener(this);
        new Thread(new Runnable(){
				@Override
				public void run()
				{
					// TODO: Implement this method
					while(true)
						try
                        {
                            maxfps=(int) Math.max(maxfps,1000/WaveView.deltaTime);
                            if(track!=null)toast(track.getPlaybackHeadPosition()+"("+(track.getPlaybackHeadPosition()/TEST_SR)+" s) FPS:"+(1000/WaveView.deltaTime)+"("+maxfps+")");
                            Thread.sleep(50);
						}
                        catch(Throwable e)
                        {}

				}
			}).start();
        Elg=(EditText) findViewById(R.id.mainEditText3);
        Erg=(EditText) findViewById(R.id.mainEditText4);
        Esr=(EditText) findViewById(R.id.mainEditText5);
        Esp=(EditText) findViewById(R.id.mainEditText6);
        Elg.setOnEditorActionListener(this);
        Erg.setOnEditorActionListener(this);
        Esp.setOnEditorActionListener(this);
        Esr.setOnEditorActionListener(this);
    }
    public void mixsine(View v)
    {
        new Thread(new Runnable(){
                @Override
                public void run()
                {
                    try
                    {
                        String[] s=((EditText)findViewById(R.id.mainEditText2)).getText().toString().split(",");
                        float[] p=new float[s.length];
                        for(int i=0;i<p.length;i++)
                        {
                            p[i]=Float.parseFloat(s[i]);
                        }
                        wave.sin[] sin=new wave.sin[p.length/4];
                        for(int i=0;i<sin.length;i++)
                        {
                            sin[i]=new wave().new sin(TEST_SR,p[i*4],p[i*4+1],p[i*4+2],p[i*4+3]);
                        }
                        int[][] d=new int[sin.length][];
                        for(int i=0;i<sin.length;i++)
                        {
                            int[] e=new int[BufferSize/4];
                            for(int u=0;u<e.length;u++)
                            {
                                e[u]=(int)sin[i].getY();sin[i].next();
                            }
                            d[i]=e;
                        }
                        int[] b=wave.mix(d);
                        byte[] c=wave.stereo_PCM_16Bit(b,b);
                        WaveView.setData(c);
                        byte[] f=WaveView.getData();
                        for(int i=0;i<1000;i++)track.write(f,0,f.length);
                    }
                    catch(Throwable e)
                    {toast(e);}
                }}).start();
    }
    public void out(View v)
    {
        new Thread(new Runnable(){
                @Override
                public void run()
                {
                    int errline=0;
                    try
                    {
                        Map<String,wave.sin[]> sampleNote=new HashMap<String,wave.sin[]>();
                        ArrayList<Map<String,Object>> lrc=new ArrayList<Map<String,Object>>();
                        float duration=0;

                        BufferedReader a=new BufferedReader(new FileReader("/sdcard/yzr的app/audioedit/text.txt"));
                        String bb="";
                        while((bb=a.readLine())!=null)
                        {
                            errline++;
                            if(bb.indexOf("//")!=-1)continue;
                            String[] cc=bb.split(":");
                            String[] dd=cc[1].split(" ");
                            float[] ee=new float[dd.length];
                            for(int i=0;i<dd.length;i++)
                            {
                                ee[i]=Float.parseFloat(dd[i]);
                            }
                            wave.sin[] sin=new wave.sin[ee.length/4];
                            for(int i=0;i<sin.length;i++)
                            {
                                sin[i]=new wave().new sin(TEST_SR,ee[i*4],ee[i*4+1],ee[i*4+2],ee[i*4+3]);
                            }
                            sampleNote.put(cc[0],sin);
                        }
                        a.close();
                        errline=0;
                        a=new BufferedReader(new FileReader("/sdcard/yzr的app/audioedit/lrc.txt"));
                        bb="";
                        while((bb=a.readLine())!=null)
                        {
                            errline++;
                            if(bb.indexOf("//")!=-1)continue;
                            String[] cc=bb.split(":");
                            String[] dd=cc[1].split(" ");
                            float[] ee=new float[dd.length];
                            for(int i=0;i<dd.length;i++)
                            {
                                ee[i]=Float.parseFloat(dd[i]);
                            }
                            duration+=ee[0];
                            Map f=new HashMap<String,Object>();
                            f.put("lrc",cc[0]);
                            f.put("dur",ee[0]);
                            lrc.add(f);
                        }

                        int durationlength=(int) (duration*100);
                        int[] data=new int[durationlength];
                        int index=0;
                        for(Map m:lrc)
                        {
                            wave.sin[] sin=sampleNote.get(m.get("lrc"));
                            int[][] d=new int[sin.length][];
                            for(int i=0;i<sin.length;i++)
                            {
                                int[] e=new int[(int)((float)m.get("dur"))*100];
                                for(int u=0;u<e.length;u++)
                                {
                                    e[u]=(int)sin[i].getY();sin[i].next();
                                }
                                d[i]=e;
                            }
                            int[] b=wave.mix(d);
                            for(int c:b)
                            {
                                data[index++]=c;
                            }
                        }
                        byte[] c=wave.stereo_PCM_16Bit(data,data);
                        byte[] e=new byte[BufferSize];
                        index=0;
                        for(int i=0;i<c.length;i++)
                        {
                            e[index++]=c[i];
                            if(index==e.length)
                            {
                                WaveView.setData(e);
                                track.write(e,0,e.length);
                                index=0;
                                e=new byte[e.length];
                            }
                        }
                    }
                    catch(Throwable e)
                    {toast(e+"\n\n\n"+errline);}
                }}).start();
    }
    int oo=0;
    @Override
    public void onProgressChanged(SeekBar p1, int p2, boolean p3)
    {
        // TODO: Implement this method
        oo=p2;
        switch(p1.getId())
        {
            case R.id.mainSeekBar1:
                TEST_SR=p2+4000;
                //toast(TEST_SR);
                if(track!=null)track.setPlaybackRate(TEST_SR);
                Esr.setText(""+TEST_SR);
                break;
            case R.id.mainSeekBar3:
                leftGain=((double)p2-20.0)/10.0;
                Elg.setText(""+leftGain);
                break;
            case R.id.mainSeekBar4:
                rightGain=((double)p2-20.0)/10.0;
                Erg.setText(""+rightGain);
                break;
            case R.id.mainSeekBar5:
                skip=p2-9;
                Esp.setText(""+((float)skip+10)/10.0);
                break;
        }

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
        switch(p1.getId())
        {
            case R.id.mainSeekBar2:
                try
                {
                    if(raf!=null)raf.seek(oo/4*4);
                }
                catch(Throwable e)
                {}
                break;
        }
    }



    public void playback(View v)
	{playBack=!playBack;}
    public void compress(View v)
    {compress=!compress;}
    public void up(View v)
    {WaveView.up();}
    public void down(View v)
    {WaveView.down();}
    public void prev(View v)
    {WaveView.prev();}
    public void next(View v)
    {WaveView.next();}
    public void cut(View v)
    {
        new Thread(new Runnable(){
                @Override
                public void run()
                {
                    try
                    {
                        init();
                        byte[] a=WaveView.getData();
                        for(int u=0;u<1000;u++)track.write(a,0,a.length);
                        //playTrack(a);
                    }
                    catch(Throwable e)
                    {toast("select"+e);}
                }}).start();

    }
    public void savecut(View v)
    {
        try
        {
            new FileOutputStream("/sdcard/yzr的app/"+fileName.getText()+".wav").write(WaveView.getData());
        }
        catch(Throwable e)
        {toast(e);}
    }
    public void init(View v)
    {init();}
    public void record(View v)
    {
        //((ToggleButton)v).get;
        record=!record;
        if(record)
        {
            new Thread(new Runnable(){
                    @Override
                    public void run()
                    {
                        // TODO: Implement this method
                        init();
                        try
                        {
                            AudioRecord ar=new AudioRecord(MediaRecorder.AudioSource.MIC,TEST_SR,TEST_CONF,TEST_FORMAT,2*minBuffSize);
                            ar.startRecording();
                            fos=new FileOutputStream("/sdcard/output.wav");
                            byte[] data=new byte[BufferSize];
                            while(record)
                            {
                                ar.read(data,0,data.length);
                                //WaveView.setData(data);
                                playTrack(data);
                            }
                            ar.stop();
                            ar.release();
                            track.stop();
                            track.release();
                            fos.flush();
                            fos.close();
                            fos=null;
                            track=null;
                        }
                        catch(Throwable e)
                        {}
                    }
                }).start();
        }
    }
    public void playTrack(byte[] data)
    {
        try
        {

            //拆分
            int[][] buff=wave.stereo_16Bit_PCM(data,leftGain,rightGain);
            if(playBack)
            {
                buff[0]=wave.reverse(buff[0]);
                buff[1]=wave.reverse(buff[1]);
            }
            if(compress)
			{
                int[] a=new int[buff[0].length/2];
                int[] b=new int[buff[0].length/2];
                for(int i=0;i<a.length;i++)
				{
                    a[i]=buff[0][i*2];
                    b[i]=buff[1][i*2];
                }
                buff[0]=a;
                buff[1]=b;
            }
            if(mono)
            {
                int[] data2=wave.mix(buff[0],buff[1]);
                data=wave.stereo_PCM_16Bit(data2,data2);
            }
            else data=wave.stereo_PCM_16Bit(buff[0],buff[1]);
            //****
            WaveView.setData(data);
            if(write)fos.write(data);
            //init((int)wfr.getSampleRate(),wfr.getNumChannels(),wfr.getBitPerSample());
            //raf.read(data);
            //raf.close();
            //for(int i=0;i<9;i++)
            //fos.write(data);
            if(!record)track.write(data,0,data.length);
        }
        catch(Throwable e)
        {}
    }
    FileOutputStream fos;
    public void mono(View v)
    {mono=!mono;}
    public void pgu(View v)
    {
        try
        {
            byte[] data=new byte[BufferSize];
            raf.seek(raf.getFilePointer()-2*data.length);
            raf.read(data);
            WaveView.setData(data);
        }
        catch (IOException e)
        {}
    }
    public void pgd(View v)
    {
        try
        {
            byte[] data=new byte[BufferSize];
            raf.seek(raf.getFilePointer());
            raf.read(data);
            WaveView.setData(data);
        }
        catch (IOException e)
        {}

    }
    public void pgu10(View v)
    {
        try
        {
            byte[] data=new byte[BufferSize];
            raf.seek(raf.getFilePointer()-11*data.length);
            raf.read(data);
            WaveView.setData(data);
        }
        catch (IOException e)
        {}
    }
    public void pgd10(View v)
    {
        try
        {
            byte[] data=new byte[BufferSize];
            raf.seek(raf.getFilePointer()+data.length*9);
            raf.read(data);
            WaveView.setData(data);
        }
        catch (IOException e)
        {}

    }
	private void init()
    {
        if(track!=null)
        {track.stop();track.release();}
		TEST_MODE =AudioTrack.MODE_STREAM;
		TEST_STREAM_TYPE = AudioManager.STREAM_MUSIC;
		minBuffSize = AudioTrack.getMinBufferSize(TEST_SR, TEST_CONF, TEST_FORMAT);
		track=new AudioTrack(TEST_STREAM_TYPE, TEST_SR, TEST_CONF, TEST_FORMAT,2*minBuffSize, TEST_MODE);
		track.play();
	}
    public void write(View v)
    {write=!write;}
    RandomAccessFile raf;
	public void play(View v)
	{
        isPlay=!isPlay;
		if(isPlay)new Thread(new Runnable(){
                    @Override
                    public void run()
                    {
                        // TODO: Implement this meth
                        try
                        {
                            BufferSize=WaveView.getWidth();
                            raf=new RandomAccessFile(file,"r");
                            //FileOutputStream fos=new FileOutputStream("/sdcard/Download/output.wav");
                            //WaveFileReader wfr=new WaveFileReader("/sdcard/Download/66ccff.wav");
                            //int[] dat=wfr.getData()[0];
                            byte[] data=new byte[BufferSize]; 
                            //WaveView.setGap(1);
                            init();
                            int ref=0;
                            int skipcount=0;
                            fos=new FileOutputStream("/sdcard/output.wav");
                            progress.setMax((int)raf.length());
                            while(raf.getFilePointer()<raf.length()&&isPlay)
                            { 
                                if(ref==0)runOnUiThread(new Runnable(){
                                            @Override
                                            public void run()
                                            {
                                                // TODO: Implement this method
                                                try
                                                {progress.setProgress((int)raf.getFilePointer());

                                                }
                                                catch(Throwable e)
                                                {}
                                            }
                                        });
                                if(++ref>=100)ref=0;
                                raf.read(data);

                                if(skip!=0&&skipcount>=10)
                                {

                                    long a=(int)((float)skip*data.length*(float)skipcount/10)+raf.getFilePointer();
                                    if(raf.length()>a)raf.seek(a);
                                    skipcount=0;
                                }
                                skipcount++;
                                if(playBack)
                                {
                                    long f=raf.getFilePointer()-2*data.length;
                                    if(f<0)break;
                                    raf.seek(f);
                                }
                                //WaveView.setData(data);
                                playTrack(data);
                            }
                            //track.play();
                            track.release();
                            track=null;
                            fos.flush();
                            fos.close();
                            fos=null;
                        }
                        catch (Throwable e)
                        {
                            toast(e);
                        }
                    }}).start();
	}
    @Override
    protected void onDestroy()
    {
        // TODO: Implement this method
        super.onDestroy();
        record=false;
        try
        {
            if(track!=null)
            {
                track.stop();
                track.release();
                track=null;
            }
        }
        catch(Throwable e)
        {}
        System.exit(0);
    }

    public byte lerp(byte a,byte b)
    {
        return (byte)((a+b)/2);
    }
	public static void toast(final Object o)
	{
		((Activity)ctx).runOnUiThread(new Runnable(){

				@Override
				public void run()
				{
					// TODO: Implement this method
					//Toast.makeText(MainActivity.this, o.toString(), 0).show();
					((TextView)((Activity)ctx).findViewById(R.id.mainTextView1)).setText(o.toString());
				}});
	} */
}
