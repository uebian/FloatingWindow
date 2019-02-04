package com.yzrilyzr.floatingwindow.apps;

import android.widget.*;
import com.yzrilyzr.ui.*;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SeekBar.OnSeekBarChangeListener;
import com.yzrilyzr.floatingwindow.API;
import com.yzrilyzr.floatingwindow.R;
import com.yzrilyzr.floatingwindow.Window;
import com.yzrilyzr.floatingwindow.apps.cls;
import com.yzrilyzr.floatingwindow.viewholder.BaseHolder;
import com.yzrilyzr.icondesigner.VECfile;
import com.yzrilyzr.icondesigner.VecView;
import com.yzrilyzr.myclass.MusicID3;
import com.yzrilyzr.myclass.util;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import android.os.AsyncTask;
public class Player implements Window.OnButtonDown,MediaPlayer.OnCompletionListener
{
	int index=0,mode=1;//0播放列表后停止，1播放列表后循环，2单曲循环,3随机
	VecView p3;
	boolean isListOpen=false,refseek=true;
	MediaPlayer mp;
	ArrayList<File> queue=new ArrayList<File>();
	ArrayList<File> music=new ArrayList<File>();
	static ConcurrentHashMap<String,MusicID3> id3=new ConcurrentHashMap<String,MusicID3>();
	private Context ctx;
	private String path;
	private Window w;
	private TextView name,albumname,artist;
	private TextView currTime;
	private TextView fullTime;
	private SeekBar progress;
	ImageView album;
	myListView queuelist,musiclist;
	String key="player";
	public Player(final Context c,Intent e)
	{
		ctx=c;
		SharedPreferences sp=util.getSPRead(key);
		int pro=sp.getInt("pro",0);
		String last=sp.getString("last",null);
		load();
		path=e.getStringExtra("path");
		if(path!=null&&!path.equals(last))pro=0;
		if(path==null)path=last;
		w=new Window(c,util.px(310),util.px(400))
		.setIcon("music")
		.setTitle("音乐")
		.setOnButtonDown(this)
		.show();
		myTabLayout t=new myTabLayout(c);
		myViewPager v=new myViewPager(c);
		t.setItems("正在播放","队列","歌曲","文件夹","播放列表","调音");
		t.setViewPager(v);
		v.setTabLayout(t);
		w.addView(t);
		w.addView(v);
		util.setWeight(v);
		ViewGroup view=(ViewGroup) LayoutInflater.from(ctx).inflate(R.layout.window_player,null);
		v.setPages(view,queuelist=new myListView(ctx),musiclist=new myListView(ctx));
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
					{index=queue.size()-1;}
					if(mode==3)
					{index=util.random(0,queue.size());}
					mp.stop();mp=new MediaPlayer();
					mp.setOnCompletionListener(Player.this);
					mp.setDataSource(queue.get(index).toString());
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
					if(index>queue.size()-1)
					{index=0;}
					if(mode==3)
					{index=util.random(0,queue.size());}
					mp.stop();mp=new MediaPlayer();
					mp.setOnCompletionListener(Player.this);
					mp.setDataSource(queue.get(index).toString());
					mp.prepare();if(isPlaying)mp.start();
					readMusic();
				}
				catch(Exception e)
				{util.toast("出错"+e);}

			}});
		view.findViewById(R.id.musicplayerImageButton5)
		.setOnClickListener(new OnClickListener(){

			private AsyncTask search;@Override public void onClick(View v)
			{
				if(search==null)search=new Search().execute();
				else search.cancel(true);
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
				if(util.getMIMEType(ff).contains("audio/"))queue.add(ff);
			Collections.sort(queue,new Comparator<File>(){
				@Override
				public int compare(File p1, File p2)
				{
					return p1.getName().compareToIgnoreCase(p2.getName());
				}
			});
			index=queue.indexOf(new File(path));
			mp=new MediaPlayer();
			mp.setDataSource(queue.get(index).getAbsolutePath());
			mp.prepare();
			mp.start();
			mp.setOnCompletionListener(this);
			ref();
			readMusic();
			mp.seekTo(pro);
		}
		catch(Exception pe)
		{
			pe.printStackTrace();
		}
		queuelist.setAdapter(new BaseAdapter(){
			@Override
			public int getCount()
			{
				// TODO: Implement this method
				return queue.size();
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
				if(position<queue.size())
				{
					String f=queue.get(position).getAbsolutePath();
					MusicID3 id=id3.get(f);
					if(id==null)
					{
						id=new MusicID3(f);
						id.loadInfo();
						id3.put(f,id);
						MediaPlayer l=new MediaPlayer();
						try
						{
							l.setDataSource(f);
							l.prepare();
						}
						catch (Exception e)
						{}
						id.length=l.getDuration();
						l.release();
					}
					holder.text1.setText(getTime(id.length));
					holder.text2.setText(id.TPE1==null?"":id.TPE1);
					holder.text3.setText(id.TALB==null?"":id.TALB);
					if(id.TIT2!=null&&!id.TIT2.equals("null"))holder.text0.setText(String.format("%d. %s",position+1,id.TIT2));
					else holder.text0.setText(String.format("%d. %s",position+1,queue.get(position).getName()));
					holder.v.setVisibility(8);
					int c=position==index?uidata.TEXTMAIN:uidata.UNENABLED;
					holder.text0.setTextColor(c);
					holder.text1.setTextColor(c);
					holder.text2.setTextColor(c);
					holder.text3.setTextColor(c);
				}
				return convertView;
			}
		});
		queuelist.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
			{
				try
				{
					index=p3;
					if(mp!=null)mp.stop();
					mp=new MediaPlayer();
					mp.setOnCompletionListener(Player.this);
					mp.setDataSource(queue.get(index).toString());
					mp.prepare();mp.start();
					readMusic();
				}
				catch(Throwable e)
				{
					util.toast("播放失败");
					e.printStackTrace();
				}
				((BaseAdapter)queuelist.getAdapter()).notifyDataSetChanged();
			}
		});
		musiclist.setAdapter(new BaseAdapter(){
			@Override
			public int getCount()
			{
				// TODO: Implement this method
				return music.size();
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
				if(position<music.size())
				{
					String f=music.get(position).getAbsolutePath();
					MusicID3 id=id3.get(f);
					if(id==null)
					{
						id=new MusicID3(f);
						id.loadInfo();
						id3.put(f,id);
						MediaPlayer l=new MediaPlayer();
						try
						{
							l.setDataSource(f);
							l.prepare();
						}
						catch (Exception e)
						{}
						id.length=l.getDuration();
						l.release();
					}
					holder.text1.setText(getTime(id.length));
					holder.text2.setText(id.TPE1==null?"":id.TPE1);
					holder.text3.setText(id.TALB==null?"":id.TALB);
					if(id.TIT2!=null&&!id.TIT2.equals("null"))holder.text0.setText(String.format("%d. %s",position+1,id.TIT2));
					else holder.text0.setText(String.format("%d. %s",position+1,music.get(position).getName()));
					holder.v.setVisibility(8);
					/*int c=position==index?uidata.TEXTMAIN:uidata.UNENABLED;
					holder.text0.setTextColor(c);
					holder.text1.setTextColor(c);
					holder.text2.setTextColor(c);
					holder.text3.setTextColor(c);*/
				}
				return convertView;
			}
		});
		musiclist.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
			{
				try
				{
					queue.clear();
					queue.addAll(music);
					index=p3;
					if(mp!=null)mp.stop();
					mp=new MediaPlayer();
					mp.setOnCompletionListener(Player.this);
					mp.setDataSource(music.get(index).toString());
					mp.prepare();mp.start();
					readMusic();
				}
				catch(Throwable e)
				{util.toast("播放失败");}
				((BaseAdapter)musiclist.getAdapter()).notifyDataSetChanged();
				((BaseAdapter)queuelist.getAdapter()).notifyDataSetChanged();
			}
		});
		
	}
	private void load(){
		SharedPreferences sp=util.getSPRead(key);
		Set<String> s=sp.getStringSet("musiclib",new TreeSet<String>());
		for(String x:s){
			String[] z=x.split("</>");
			MusicID3 i=new MusicID3(z[0]);
			i.length=Integer.parseInt(z[1]);
			i.TIT2=z[2];
			i.TALB=z[3];
			i.TPE1=z[4];
			id3.put(z[0],i);
			music.add(new File(z[0]));
		}
	}
	private void save(){
		SharedPreferences.Editor sp=util.getSPWrite(key);
		Set<String> s=new TreeSet<String>();
		for(MusicID3 x:id3.values()){
			StringBuilder b=new StringBuilder()
			.append(x.path)
			.append("</>")
			.append(x.length)
			.append("</>")
			.append(x.TIT2)
			.append("</>")
			.append(x.TALB)
			.append("</>")
			.append(x.TPE1);
			s.add(b.toString());
		}
		sp.putStringSet("musiclib",s).commit();
	}
	private void readMusic()
	{
		MusicID3 id=new MusicID3(queue.get(index).getAbsolutePath());
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
		else name.setText(queue.get(index).getName());
		p3.setImageVec(mp.isPlaying()?"pause":"play");
		try{
			((BaseAdapter)queuelist.getAdapter()).notifyDataSetChanged();
			}catch(Throwable e){}
	}
	@Override
	public void onButtonDown(int code)
	{
		if(code==Window.ButtonCode.CLOSE&&mp!=null)
		{
			util.getSPWrite(key)
			.putInt("pro",mp.getCurrentPosition())
			.putString("last",queue.get(index).getAbsolutePath())
			.commit();
			mp.stop();
			mp.release();
			save();
		}
	}
	@Override
	public void onCompletion(MediaPlayer p)
	{
		try
		{
			if(mode!=2)
			{
				index++;
				if(index>queue.size()-1)
				{index=0;if(mode==0&&mode!=3)
					{index=queue.size()-1;mp.seekTo(0);readMusic();return;}}
				if(mode==3)
				{index=util.random(0,queue.size());}
				mp.stop();mp=new MediaPlayer();
				mp.setDataSource(queue.get(index).toString());
				mp.prepare();
				mp.setOnCompletionListener(Player.this);
				mp.start();
				}
			else
			{
				mp.start();
			}
			readMusic();
		}
		catch(IOException e)
		{}
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
	public static String getTime(int ms)
	{
		int m=(int)Math.floor(ms/60000);
		int s=(int)Math.floor((ms-60000*m)/1000);
		return (m<10?"0"+m:m)+":"+(s<10?"0"+s:s);
	}
	private void edit()
	{
		myListView l=new myListView(ctx);
		File f=queue.get(index);
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
	class Search extends AsyncTask
	{
		ArrayList<File> m2=new ArrayList<File>();
		@Override
		public void onPostExecute(Object s)
		{
			Collections.sort(music,new Comparator<File>(){
				@Override
				public int compare(File p1, File p2)
				{
					return p1.getName().compareToIgnoreCase(p2.getName());
				}
			});
			save();
			((BaseAdapter)musiclist.getAdapter()).notifyDataSetChanged();
			util.toast("扫描完毕");
		}
		@Override
		public void onCancelled(Object o)
		{
			Collections.sort(music,new Comparator<File>(){
				@Override
				public int compare(File p1, File p2)
				{
					return p1.getName().compareToIgnoreCase(p2.getName());
				}
			});
			save();
			((BaseAdapter)musiclist.getAdapter()).notifyDataSetChanged();
			util.toast("扫描取消");
			
		}
		private void mFileTraversal(File dir)
		{
			if(isCancelled())return;
			if(dir.isDirectory())
			{
				File[] fs=dir.listFiles();
				if(fs!=null)
					for(File x:fs)if(!isCancelled())mFileTraversal(x);
			}
			if(dir.exists()&&util.getMIMEType(dir).contains("audio")){
				String f=dir.getAbsolutePath();
				MusicID3 id=new MusicID3(f);
				id.loadInfo();
				id3.put(f,id);
				MediaPlayer l=new MediaPlayer();
				try
				{
					l.setDataSource(f);
					l.prepare();
				}
				catch (Exception e)
				{}
				id.length=l.getDuration();
				l.release();
				m2.add(dir);
			}
		}

		@Override
		protected Object doInBackground(Object[] p1)
		{
			//queue.clear();
			music.clear();
			id3.clear();
			util.toast("扫描开始");
			mFileTraversal(new File(util.sdcard));
			music.addAll(m2);
			m2.clear();
			return null;
		}		
	}
	public static class HolderList extends BaseHolder
	{
		public myTextView text0,text1,text2,text3;
		public VecView v;
		public HolderList(Context c)
		{
			super(c,R.layout.window_player_entry);
			text0=(myTextView) find(R.id.windowplayerentrymyTextView1);
			text1=(myTextView) find(R.id.windowplayerentrymyTextView2);
			text2=(myTextView) find(R.id.windowplayerentrymyTextView3);
			text3=(myTextView) find(R.id.windowplayerentrymyTextView4);
			v=(VecView) vg.findViewById(R.id.windowplayerentryVecView1);
		}

		@Override
		public void set(Object data)
		{
			// TODO: Implement this method
		}

		
	}
}
