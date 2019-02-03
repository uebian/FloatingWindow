package com.yzrilyzr.floatingwindow.apps;
import com.yzrilyzr.ui.*;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.yzrilyzr.floatingwindow.API;
import com.yzrilyzr.floatingwindow.R;
import com.yzrilyzr.floatingwindow.Window;
import com.yzrilyzr.floatingwindow.view.PianoRoll;
import com.yzrilyzr.icondesigner.VECfile;
import com.yzrilyzr.myclass.Pcm;
import com.yzrilyzr.myclass.util;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class SignalGenerator extends BaseAdapter implements Window.OnButtonDown,Runnable,OnItemClickListener,OnClickListener
{
	//WaveView view;
	AudioTrack track;
	int TEST_SR =48000;
    int BufferSize=2000;
	int TEST_CONF =AudioFormat.CHANNEL_CONFIGURATION_MONO;
	int TEST_FORMAT= AudioFormat.ENCODING_PCM_16BIT;
	int TEST_MODE =AudioTrack.MODE_STREAM;
	int TEST_STREAM_TYPE = AudioManager.STREAM_MUSIC;
	int minBuffSize = AudioTrack.getMinBufferSize(TEST_SR, TEST_CONF, TEST_FORMAT);
	myListView list;
	boolean run=true;
	CopyOnWriteArrayList<float[]> waves=new CopyOnWriteArrayList<float[]>();
	private Context ctx;
	Oscilloscope osc=null;
	private LinearLayout addl;
	View sosc,play,open;
	String path;
	boolean parse=false;
	private Window w;

	private PianoRoll roll=null;
	View file,inst,qua,trac,start,end,play2,edit,copy,paste;
	TextView filestat;
	public SignalGenerator(Context c,Intent e)
	{
		ctx=c;
		w=new Window(c,util.px(260),util.px(450))
		.setIcon("signal")
		.setTitle("信号发生器")
		.setOnButtonDown(this)
		.show();
		path=util.getSPRead().getString("SignalGeneratorPath",null);
		ViewGroup vg=(ViewGroup)w.addView(R.layout.window_signalgenerator);
		myTabLayout t=new myTabLayout(ctx);
		myViewPager v=new myViewPager(ctx);
		t.setItems("信号发生器","VVVF模拟器","8BIT音乐制作");
		t.setViewPager(v);
		v.setTabLayout(t);
		vg.addView(t,0);
		vg.addView(v,1);
		util.setWeight(v);
		list=new myListView(c);
		myButton b=new myButton(c);
		ViewGroup vg2=(ViewGroup)LayoutInflater.from(c).inflate(R.layout.window_abmusic,null);
		roll=(PianoRoll)vg2.getChildAt(1);
		ViewGroup vz=(ViewGroup) vg2.findViewById(R.id.windowabmusicLinearLayout1);
		ViewGroup vx=(ViewGroup) vg2.findViewById(R.id.windowabmusicLinearLayout2);
		file=vz.getChildAt(0);
		inst=vz.getChildAt(1);
		filestat=(TextView) vz.getChildAt(2);
		qua=vz.getChildAt(3);
		trac=vz.getChildAt(4);
		start=vx.getChildAt(0);
		end=vx.getChildAt(1);
		play2=vx.getChildAt(2);
		edit=vx.getChildAt(3);
		copy=vx.getChildAt(4);
		paste=vx.getChildAt(5);
		file.setOnClickListener(this);
		inst.setOnClickListener(this);
		qua.setOnClickListener(this);
		trac.setOnClickListener(this);
		start.setOnClickListener(this);
		end.setOnClickListener(this);
		play2.setOnClickListener(this);
		edit.setOnClickListener(this);
		copy.setOnClickListener(this);
		paste.setOnClickListener(this);
				
		v.setPages(list,b,vg2);
		sosc=vg.findViewById(R.id.windowsignalgeneratormyButton1);
		play=vg.findViewById(R.id.windowsignalgeneratorVecView2);
		open=vg.findViewById(R.id.windowsignalgeneratorVecView1);
		sosc.setOnClickListener(this);
		play.setOnClickListener(this);
		open.setOnClickListener(this);
		addl=(LinearLayout) LayoutInflater.from(ctx).inflate(R.layout.window_signal_addentry,null);
		for(int i=0;i<3;i++)
		{
			final int l=i;
			addl.getChildAt(i).setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1)
				{
					//x A f fai
					//x A f width rise fall delay
					//x A f fai   ftri sync syncRatio
					if(l==0)waves.add(new float[]{0,10000,1000,0});
					else if(l==1)waves.add(new float[]{0,10000,1000,0.4f,0.1f,0.1f,0});
					else if(l==2)waves.add(new float[]{0,10000,1000,0,10000,0,1});
					for(float[] c:waves)c[0]=0;
				}
			});
		}
		list.setAdapter(this);
		list.setOnItemClickListener(this);
		track=new AudioTrack(TEST_STREAM_TYPE,TEST_SR,TEST_CONF,TEST_FORMAT,minBuffSize*2,TEST_MODE);
		track.play();
		new Thread(this,"信号发生器").start();
		filestat.setText(String.format("BPM:%d %d%d Q:%d",roll.bpm,roll.beats,roll.beatsUnit,roll.qlen));
	}
	@Override
	public void onClick(View p1)
	{
		if(p1==sosc)
		{
			if(osc==null||!osc.w.isShowing())osc=new Oscilloscope(ctx,null);
			else
			{
				osc.w.dismiss();
				osc=null;
			}
		}
		else if(p1==open)API.startServiceForResult(ctx,w,new BroadcastReceiver(){
				@Override
				public void onReceive(Context c,Intent e)
				{
					try
					{
						path=e.getStringExtra("path");
						util.getSPWrite()
						.putString("SignalGeneratorPath",path)
						.commit();
					}
					catch(Exception ex)
					{
						util.toast("打开失败");
					}
				}
			},cls.EXPLORER);
		else if(p1==play)
		{
			parse=true;
		}
		else{
			if(p1==file){
				LinearLayout l=new LinearLayout(ctx);
				l.setOrientation(1);
				final TextView q=new myTextView(ctx);
				q.setText("BPM(每分钟节拍数):"+roll.bpm);
				l.addView(q);
				mySeekBar w=new mySeekBar(ctx);
				w.setMax(500);
				w.setProgress(roll.bpm-30);
				w.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
					@Override
					public void onProgressChanged(SeekBar p1, int p2, boolean p3)
					{
						q.setText("BPM(每分钟节拍数):"+(p2+30));
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
				});
				l.addView(w);
				new myDialog.Builder(ctx)
				.setTitle("歌曲设置")
				.setView(l)
				.setPositiveButton("确定",new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface p1, int p2)
					{
						// TODO: Implement this method
					}
				})
				.setNegativeButton("取消",null)
				.show();
			}
			else if(p1==inst){
				new myDialog.Builder(ctx)
				.setTitle("波形设置")
				.setPositiveButton("确定",new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface p1, int p2)
					{
						// TODO: Implement this method
					}
				})
				.setNegativeButton("取消",null)
				.show();
			}
			filestat.setText(String.format("BPM:%d %d%d Q:%d",roll.bpm,roll.beats,roll.beatsUnit,roll.qlen));
		}
	}
	@Override
	public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
	{
		if(p3==waves.size())
		{
			//waves.add(new Wave(1000,10000,0));
			notifyDataSetChanged();
			//for(Wave w:waves)w.ω=0;
		}
	}
	@Override
	public void onButtonDown(int code)
	{
		if(code==Window.ButtonCode.CLOSE)
		{
			if(osc!=null)osc.w.dismiss();
			run=false;
		}
	}
	@Override
	public void run()
	{
		int[] data=new int[BufferSize/2];
		byte[] bdata=new byte[BufferSize];
		int[] parsedata=null;
		int pc=0;
		while(run)
		{
			try
			{
				if(parse)
				{
					pc=0;
					parse=false;
					parsedata=parse();
					util.toast("解析完毕");
				}
				if(parsedata==null||pc>=parsedata.length)
					for(int i=0;i<data.length;i++)
					{
						data[i]=0;
						/*for(int j=0;j<waves.size();j++)
						 {
						 Wave w=waves.get(j);
						 data[i]+=w.getY();
						 w.next();
						 }*/
					}
				else
				{
					System.arraycopy(parsedata,pc,data,0,Math.min(data.length,parsedata.length-pc-1));
					pc+=data.length;

				}
				if(osc!=null)osc.append(data);
				Pcm.mono_PCM_16Bit(bdata,data);
				track.write(bdata,0,bdata.length);
				track.flush();
			}
			catch(语法错误 z)
			{
				util.toast(z.toString());
			}
			catch(Throwable e)
			{
				break;
			}
		}
		track.stop();
		track.release();
	}

	@Override
	public int getCount()
	{
		// TODO: Implement this method
		return waves.size()+1;
	}
	@Override
	public Object getItem(int p1)
	{
		// TODO: Implement this method
		return null;
	}
	@Override
	public long getItemId(int p1)
	{
		// TODO: Implement this method
		return 0;
	}
	@Override
	public View getView(int p1, View convertView, ViewGroup p3)
	{
		//if(p1==waves.size())
		return addl;
		/*else
		 {
		 Holder hd=null;
		 if(convertView==null)
		 {
		 hd=new Holder(ctx);
		 convertView=hd.vg;
		 convertView.setTag(hd);
		 }
		 else hd=(Holder)convertView.getTag();
		 if(hd==null)
		 {
		 hd=new Holder(ctx);
		 convertView=hd.vg;
		 convertView.setTag(hd);
		 }
		 hd.set(waves.get(p1));
		 return convertView;
		 }*/
	}
	static String[] ws=new String[]{"正弦波","PWM波","SPWM波","自己画…"};
	//A f fai
	//A f width rise fall delay
	//A f fai   ftri sync syncRatio
	/*private class Holder extends BaseHolder
	 {
	 EditText d,e,f;
	 Wave w;
	 boolean j=false;
	 public Holder(Context ctx)
	 {
	 super(ctx,R.layout.window_signal_entry);
	 }
	 @Override
	 public void set(Object data)
	 {
	 w=(Wave)data;

	 }
	 }*/
	
	static float pwm(float x,float period,float width,float rise,float fall,float delay)
	{
		float r=period*rise;
		float w=width*period;
		float f=fall*period;
		x=(x+period*delay+r/2f)%period;
		if(width==-1)
			if(x>=r&&x<r+f)return (2f*(float)Math.random()-1f);
			else return 0;
		if(x<r)return x*2f/r-1f;
		else if(x>=r&&x<r+w)return 1;
		else if(x>=r+w&&x<r+w+f)return 1f-(x-r-w)*2f/f;
		else return -1;
	}
	static float smooth(float x,float p,float y)
	{
		return y*pwm(x,p,0.999f,0,0.0005f,0);
	}
	static float vvvf(float x,float p,boolean sync)
	{
		float c=0;
		if(sync)c=p;
		else
		{
			c=p/10f;
		}
		return spwm(x,c,sin(x,p,0));
	}
	static float spwm(float x,float p,float y)
	{
		return y>pwm(x,p,0,0.5f,0.5f,0)?1:-1;
	}
	static float sin(float x,float p,float fai)
	{
		return (float)Math.sin(2.0*Math.PI*x/p+fai);
	}
//.左 降8度   右. 升8度
//_ 时长/2   - 时长*2     < 时长取负数
//# 升调   b 降调
//* 附点   + 连嘤   () 三连嘤   [] 一起嘤
	class 语法错误 extends Throwable
	{
		String reason;
		int line;
		public 语法错误(String reason, int line)
		{
			this.reason = reason;
			this.line = line;
		}

		@Override
		public String toString()
		{
			// TODO: Implement this method
			return String.format("语法错误:%s\n在第 %d 行",reason,line);
		}
	}
	int[] parse() throws 语法错误,IOException
	{
		int[] ids=new int[]{-100,48,50,52,53,55,57,59};
		ArrayList<ArrayList<Integer>> list=new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> clist=null;
		ArrayList<Float> ins=new ArrayList<Float>();
		ArrayList<Integer> pitches=new ArrayList<Integer>();
		String st="";
		float bpm=120;
        BufferedReader br=new BufferedReader(new FileReader(path));
		int line=1;
		while((st=br.readLine())!=null)
        {
			try
			{
				if(st.startsWith("#"));
				else if(st.startsWith("BPM:"))bpm=Float.parseFloat(st.substring(4));
				else if(st.startsWith("PWM:"))
				{
					String[] k=st.substring(4).split(" ");
					for(String z:k)ins.add(Float.parseFloat(z));
					if(ins.size()%4!=0)throw new 语法错误("参数错误:PWM 应该有4个参数",line);
				}
				else if(st.startsWith("PITCH:"))pitches.add(Integer.parseInt(st.substring(6)));
				else if(st.startsWith("PART:"))clist=new ArrayList<Integer>();
				else if(st.startsWith("END"))
				{
					list.add(clist);
					clist=null;
				}
				else
				{
					if(bpm==0)throw new 语法错误("BPM不能为0",line);
					String[] k=st.split(" ");
					int ti=-1;
					int ilen=(int)(60f*TEST_SR/bpm);
					for(String a:k)
					{
						if("".equals(a))continue;
						int id=0,li=0;
						int[] len=new int[]{ilen,0,0};
						for(int u=0,ni=-1;u<a.length();u++)
						{
							char c=a.charAt(u);
							if(c>=48&&c<=55)
							{id+=ids[(int)c-48];ni=u;}
							else if(c=='.')
								if(ni==-1)id-=12;
								else id+=12;
							else if(c=='_')len[li]/=2;
							else if(c=='-')len[li]+=ilen;
							else if(c=='#')id+=1;
							else if(c=='b')id-=1;
							else if(c=='*')len[li]=len[li]*3/2;
							else if(c=='(')ti=3;
							else if(c=='x')id=33;
							else if(c=='y')id=34;
							else if(c=='z')id=35;
							else if(c=='<')
							{
								id=-300;
								len[li]=-len[li];
							}
							else if(c=='+')len[++li]=ilen;
							else if(c=='['||c==']')
							{
								clist.add(-200);
								clist.add(0);
							}
							else if(c!=')')throw new 语法错误("未知字符:"+c,line);
						}
						if(ti>0)
						{
							len[li]/=3;ti--;
						}
						clist.add(id);
						int gli=0;
						for(int c:len)gli+=c;
						clist.add(gli);
					}
				}
				line++;
			}
			catch(Throwable e)
			{
				throw new 语法错误("解析出错:"+st,line);
			}
		}
        br.close();
		if(list.size()!=ins.size()/4||ins.size()/4!=pitches.size()||pitches.size()!=list.size())
			throw new 语法错误("参数错误:PWM PITCH PART数目应该相同",0);
		float[] da=null;
		int bl=0;
		boolean bp=false;
		for(int ii=0;ii<list.size();ii++)
			for(int i=0,o=0;i<list.get(ii).size();i+=2)
			{
				if(list.get(ii).get(i)==-200)bp=!bp;
				if(!bp)
				{
					o+=list.get(ii).get(i+1);
					bl=Math.max(bl,o);
				}
			}
		da=new float[bl];
		for(int ii=0;ii<list.size();ii++)
		{
			boolean pp=false;
			for(int i=0,o=0,ol=0;i<list.get(ii).size();i+=2)
			{
				int len=list.get(ii).get(i+1),oid=list.get(ii).get(i),olen=len;
				int id=oid+pitches.get(ii);
				if(oid==-200)
				{ol=o;pp=!pp;continue;}
				if(oid==-100||oid==-300)
				{o+=len;continue;}
				float t=1f/16.414375f/(float)Math.pow(2,id/12f);
				if(oid==33)t=olen/(float)TEST_SR;
				else if(oid==34)t=2f*olen/(float)TEST_SR;
				else if(oid==35)t=4f*olen/(float)TEST_SR;
				while(len>0)
					da[o++]+=100*
				//smooth((p.get(ii).get(i+1)-len)/(float)TEST_SR,p.get(ii).get(i+1),
					pwm((olen-(len)--)/(float)TEST_SR,t,ins.get(ii*4+1),ins.get(ii*4+2),ins.get(ii*4+3),0)
					*ins.get(ii*4);
				if(pp)o=ol;
			}
		}
		float max=0;
		for(int k=0;k<da.length;k++)max=Math.max(max,da[k]);
		int[] da2=new int[da.length];
		max=32767f/max;
		for(int k=0;k<da.length;k++)da2[k]=(int)(da[k]*max);
		return da2;
	}
}
