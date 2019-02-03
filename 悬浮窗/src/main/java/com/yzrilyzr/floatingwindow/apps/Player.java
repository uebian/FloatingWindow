package com.yzrilyzr.floatingwindow.apps;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.yzrilyzr.floatingwindow.API;
import com.yzrilyzr.floatingwindow.R;
import com.yzrilyzr.floatingwindow.Window;
import com.yzrilyzr.floatingwindow.apps.cls;
import com.yzrilyzr.floatingwindow.viewholder.HolderList;
import com.yzrilyzr.icondesigner.VecView;
import com.yzrilyzr.myclass.MusicID3;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.ui.myListView;
import com.yzrilyzr.ui.myTabLayout;
import com.yzrilyzr.ui.myViewPager;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.Adapter;
import android.widget.LinearLayout;
import com.yzrilyzr.ui.myTextView;
import android.widget.EditText;
import com.yzrilyzr.ui.myEditText;
import com.yzrilyzr.icondesigner.VECfile;
import android.content.BroadcastReceiver;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import com.yzrilyzr.ui.myDialog;
import android.content.DialogInterface;
import android.text.TextWatcher;
import android.text.Editable;
public class Player implements Window.OnButtonDown,MediaPlayer.OnCompletionListener
{
	int index=0,mode=1;//0播放列表后停止，1播放列表后循环，2单曲循环,3随机
	VecView p3;
	boolean isListOpen=false,refseek=true;
	MediaPlayer mp;
	ArrayList<File> files=new ArrayList<File>();
	private Context ctx;
	private String path;
	private Window w;
	private TextView name,albumname,artist;
	private TextView currTime;
	private TextView fullTime;
	private SeekBar progress;
	ImageView album;
	myListView queue;
	public Player(final Context c,Intent e)
	{
		ctx=c;
		path=e.getStringExtra("path");
		w=new Window(c,util.px(310),util.px(400))
		.setIcon("music")
		.setTitle("音乐")
		.setOnButtonDown(this)
		.show();
		myTabLayout t=new myTabLayout(c);
		myViewPager v=new myViewPager(c);
		t.setItems("正在播放","队列","歌曲","文件夹","播放列表","调音","搜索");
		t.setViewPager(v);
		v.setTabLayout(t);
		w.addView(t);
		w.addView(v);
		util.setWeight(v);
		ViewGroup view=(ViewGroup) LayoutInflater.from(ctx).inflate(R.layout.window_player,null);
		v.setPages(view,queue=new myListView(ctx));
		currTime=(TextView)view.findViewById(R.id.musicplayerTextView1);
		fullTime=(TextView)view.findViewById(R.id.musicplayerTextView2);
		progress=(SeekBar)view.findViewById(R.id.musicplayerSeekBar1);
		artist=(TextView)view.findViewById(R.id.musicplayerartist);
		name=(TextView)view.findViewById(R.id.musicplayername);
		albumname=(TextView)view.findViewById(R.id.musicplayeralbum);
		album=(ImageView)view.findViewById(R.id.windowplayerImageView1);
		util.setWeight(album);
		view.findViewById(R.id.musicplayerImageButton1)
		.setOnClickListener(new OnClickListener(){@Override public void onClick(View v)
			{
				if(++mode>3)mode=0;
				if(mode==0)((VecView)v).setImageVec("round0");
				else if(mode==1)((VecView)v).setImageVec("round");
				else if(mode==2)((VecView)v).setImageVec("round1");
				else if(mode==3)((VecView)v).setImageVec("random");
			}});
		view.findViewById(R.id.musicplayerImageButton2)
		.setOnClickListener(new OnClickListener(){@Override public void onClick(View v)
			{
				try
				{
					boolean isPlaying=mp.isPlaying();
					if(mode==2)
					{mp.seekTo(0);if(isPlaying)mp.start();return;}
					index--;
					if(index<0)
					{index=files.size()-1;}
					if(mode==3)
					{index=util.random(0,files.size());}
					mp.stop();mp=new MediaPlayer();
					mp.setOnCompletionListener(Player.this);
					mp.setDataSource(files.get(index).toString());
					mp.prepare();if(isPlaying)mp.start();
					readMusic();
				}
				catch(Exception e)
				{util.toast("出错"+e);}
			}});
		p3=(VecView)view.findViewById(R.id.musicplayerImageButton3);
		p3.setOnClickListener(new OnClickListener(){@Override public void onClick(View v)
			{
				if(mp.isPlaying())
				{mp.pause();((VecView)v).setImageVec("play");}
				else
				{mp.start();((VecView)v).setImageVec("pause");}
			}});
		p3.setOnLongClickListener(new OnLongClickListener(){@Override public boolean onLongClick(View p)
			{
				mp.pause();mp.seekTo(0);
				((VecView)p).setImageVec("play");
				return true;
			}});
		view.findViewById(R.id.musicplayerImageButton4)
		.setOnClickListener(new OnClickListener(){@Override public void onClick(View v)
			{
				try
				{
					boolean isPlaying=mp.isPlaying();
					if(mode==2)
					{mp.seekTo(0);if(isPlaying)mp.start();return;}
					index++;
					if(index>files.size()-1)
					{index=0;}
					if(mode==3)
					{index=util.random(0,files.size());}
					mp.stop();mp=new MediaPlayer();
					mp.setOnCompletionListener(Player.this);
					mp.setDataSource(files.get(index).toString());
					mp.prepare();if(isPlaying)mp.start();
					readMusic();
				}
				catch(Exception e)
				{util.toast("出错"+e);}

			}});
		view.findViewById(R.id.musicplayerImageButton5)
		.setOnClickListener(new OnClickListener(){@Override public void onClick(View v)
			{

			}});
		view.findViewById(R.id.windowplayerEdit)
		.setOnClickListener(new OnClickListener(){@Override public void onClick(View v)
			{
				edit();
			}});

		progress.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
			public void onProgressChanged(SeekBar p,int i,boolean b)
			{
				currTime.setText(getTime(i));
			}
			public void onStartTrackingTouch(SeekBar p)
			{
				refseek=false;
			}
			public void onStopTrackingTouch(SeekBar p)
			{
				mp.seekTo(p.getProgress());
				refseek=true;
			}
		});
		progress.setDuplicateParentStateEnabled(true);
		try
		{
			File[] pf=new File(path).getParentFile().listFiles();
			for(File ff:pf)
				if(util.getMIMEType(ff).contains("audio/"))files.add(ff);
			index=files.indexOf(new File(path));
			mp=new MediaPlayer();
			mp.setDataSource(files.get(index).getAbsolutePath());
			mp.prepare();
			mp.start();
			mp.setOnCompletionListener(this);
			ref();
			readMusic();
		}
		catch(Exception pe)
		{}
		queue.setAdapter(new BaseAdapter(){
			@Override
			public int getCount()
			{
				// TODO: Implement this method
				return files.size();
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
			public View getView(int position, View convertView, ViewGroup p3)
			{
				HolderList holder;
				if(convertView==null)
				{
					holder=new HolderList(ctx);
					convertView=holder.vg;
					convertView.setTag(holder);
				}
				else holder=(HolderList) convertView.getTag();
				if(position<files.size())
				{
					//holder.v[0].setVisibility(8);
					holder.text.setText(files.get(position).getName());
					holder.v[1].setVisibility(8);
					holder.v[2].setVisibility(8);
					holder.v[3].setVisibility(8);
				}
				return convertView;
			}
		});
		queue.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
			{
				try
				{
					index=p3;
					mp.stop();mp=new MediaPlayer();
					mp.setOnCompletionListener(Player.this);
					mp.setDataSource(files.get(index).toString());
					mp.prepare();mp.start();
					readMusic();
				}
				catch(Throwable e)
				{util.toast("播放失败");}
			}
		});
	}

	private void readMusic()
	{
		MusicID3 id=new MusicID3(files.get(index).getAbsolutePath());
		id.loadInfo();
		if(id.APIC!=null)
		{
			try
			{
				final byte[] b=id.APIC;
				Bitmap g=BitmapFactory.decodeByteArray(b,0,b.length);
				album.setImageBitmap(g);
				album.setVisibility(0);
				album.setOnClickListener(new OnClickListener(){@Override public void onClick(View v)
					{
						API.startService(ctx,new Intent().putExtra("data",b).putExtra("type",3),cls.IMAGEVIEWER);
					}});
			}
			catch(Throwable ep)
			{}
		}
		else album.setVisibility(8);
		artist.setText(id.TPE1==null?"":id.TPE1+"");
		albumname.setText(id.TALB==null?"":id.TALB+"");
		if(id.TIT2!=null)name.setText(id.TIT2+"");
		else name.setText(files.get(index).getName());
		p3.setImageVec(mp.isPlaying()?"pause":"play");
	}
	@Override
	public void onButtonDown(int code)
	{
		if(code==Window.ButtonCode.CLOSE)
		{
			mp.stop();
			mp.release();
		}
	}
	@Override
	public void onCompletion(MediaPlayer p)
	{
		try
		{
			if(mode!=2)
			{index++;
				if(index>files.size()-1)
				{index=0;if(mode==0&&mode!=3)
					{index=files.size()-1;mp.seekTo(0);readMusic();return;}}
				if(mode==3)
				{index=util.random(0,files.size());}
				mp.stop();mp=new MediaPlayer();
				mp.setOnCompletionListener(Player.this);
				mp.setDataSource(files.get(index).toString());
				mp.prepare();mp.start();}
			else
			{mp.start();}
			readMusic();
		}
		catch(IOException e)
		{util.toast(e+"");}
	}
	void ref()
	{
		new Handler(ctx.getMainLooper()).postDelayed(new Runnable(){@Override public void run()
			{
				try
				{
					//fileName.setText("("+(播放序号+1)+"/"+files.size()+")"+files[播放序号].getName().split("\\.")[0]);
					int c=mp.getCurrentPosition();
					int d=mp.getDuration();
					if(refseek)progress.setProgress(c);
					progress.setMax(d);
					if(refseek)currTime.setText(getTime(c));
					fullTime.setText(getTime(d));	
					ref();
				}
				catch(Exception e)
				{}
			}},50);
	}
	public String getTime(int ms)
	{
		int m=(int)Math.floor(ms/60000);
		int s=(int)Math.floor((ms-60000*m)/1000);
		return (m<10?"0"+m:m)+":"+(s<10?"0"+s:s);
	}
	private void edit()
	{
		myListView l=new myListView(ctx);
		File f=files.get(index);
		final MusicID3 id=new MusicID3(f.getAbsolutePath());
		id.loadInfo();
		final Window ww=new Window(ctx,util.px(264),util.px(362))
		.setTitle("ID3编辑器:"+f.getName())
		.setIcon("musicidt")
		.addView(l);
		
		ww.setOnButtonDown(new Window.OnButtonDown(){
			@Override
			public void onButtonDown(int code)
			{
				if(code==Window.ButtonCode.CLOSE)
				new myDialog.Builder(ctx)
				.setMessage("是否保存改动？")
				.setNegativeButton("不保存",null)
				.setNeutralButton("返回",new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface p1, int p2)
					{
						ww.show();
					}
				})
				.setCancelable(false)
				.setPositiveButton("保存",new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface p1, int p2)
					{
						if(id.saveInfo())
						util.toast("保存成功");
						else util.toast("保存失败");
					}
				})
				.show();
			}
		})
		.show();
		String[] d=new String[]{
		"专辑图片",
		"歌词作者",
		"编码",
		"URL链接(URL)",
		"版权(Copyright)",
		"原艺术家",
		"作曲家",
		"日期",
		"指挥者",
		"乐队",
		"艺术家",
		"翻译（记录员、修改员）",
		"年代",
		"歌词",
		"专辑",
		"内容组描述",
		"标题",
		"副标题",
		"流派（风格）",
		"每分钟节拍数",
		"注释",
		"播放列表返录",
		"音轨（曲号）",
		"文件类型",
		"时间",
		"最初关键字",
		"语言",
		"长度",
		"媒体类型",
		"原唱片集",
		"原文件名",
		"原歌词作者",
		"最初发行年份",
		"文件所有者（许可证者）",
		"作品集部分",
		"发行人",
		"录制日期",
		"Intenet电台名称",
		"Intenet电台所有者",
		"大小",
		"ISRC（国际的标准记录代码）",
		"编码使用的软件（硬件设置）",
		"唯一的文件标识符"
		};
		final View[] v=new View[d.length];
		for(int p1=0;p1<v.length;p1++)
			if(p1==0)
			{
				final ImageView vp=new ImageView(ctx);
				vp.setScaleType(ImageView.ScaleType.FIT_CENTER);
				try
				{
					if(id.APIC!=null)
					{
						final byte[] b=id.APIC;
						Bitmap g=BitmapFactory.decodeByteArray(b,0,b.length);
						vp.setImageBitmap(g);
					}
					else vp.setImageBitmap(VECfile.createBitmap(ctx,"add",util.px(60),util.px(60)));
					vp.setOnClickListener(new OnClickListener(){@Override public void onClick(View v)
						{
							API.startServiceForResult(ctx,new Intent().putExtra("category","image"),ww,new BroadcastReceiver(){
								@Override
								public void onReceive(Context p1, Intent p2)
								{
									try
									{
										String file=p2.getStringExtra("path");
										BufferedInputStream in=new BufferedInputStream(new FileInputStream(file));
										byte[] APIC=new byte[in.available()];
										in.read(APIC);
										in.close();
										Bitmap g=BitmapFactory.decodeByteArray(APIC,0,APIC.length);
										if(g==null)throw new Exception();
										id.APIC=APIC;
										vp.setImageBitmap(g);
									}
									catch(Exception ex)
									{
										util.toast("选定的文件不能作为专辑图片");
									}
								}
							},cls.EXPLORER);
						}});
				}
				catch(Throwable ep)
				{}
				v[p1]=vp;
			}
			else
			{
				EditText e=new myEditText(ctx);
				e.setHint(d[p1]);
				final int pp1=p1;
				try
				{
					Object s=MusicID3.class.getField(MusicID3.p[pp1]).get(id);
					if(s!=null)e.setText((String)s);
				}
				catch (Exception ep)
				{
					ep.printStackTrace();
				}
				e.addTextChangedListener(new TextWatcher(){

					@Override
					public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
					{
						// TODO: Implement this method
					}

					@Override
					public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
					{
						try
						{
							MusicID3.class.getField(MusicID3.p[pp1]).set(id,p1+"");
						}
						catch (Exception ep)
						{
							ep.printStackTrace();
						}
					}

					@Override
					public void afterTextChanged(Editable p1)
					{
						// TODO: Implement this method
					}
				});
				v[p1]=e;
			}

		l.setAdapter(new BaseAdapter(){
			@Override
			public int getCount()
			{
				// TODO: Implement this method
				return v.length;
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
			public View getView(int p1, View p2, ViewGroup p3)
			{

				return v[p1];
			}
		});
	}
}
