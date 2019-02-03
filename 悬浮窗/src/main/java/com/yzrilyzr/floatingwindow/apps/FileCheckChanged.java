package com.yzrilyzr.floatingwindow.apps;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import com.yzrilyzr.floatingwindow.API;
import com.yzrilyzr.floatingwindow.R;
import com.yzrilyzr.floatingwindow.Window;
import com.yzrilyzr.icondesigner.VecView;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.ui.myListView;
import com.yzrilyzr.ui.myTextView;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

public class FileCheckChanged implements OnClickListener,Runnable
{
	boolean run=false;
	VecView ref;
	myTextView time;
	myListView list;
	HashMap<String,String> origin=new HashMap<String,String>();
	TreeSet<String> added=new TreeSet<String>(),deleted=new TreeSet<String>(),now=new TreeSet<String>();
	public FileCheckChanged(Context c,Intent e)
	{
		Window w=new Window(c,util.px(264),util.px(305))
            .setTitle("文件改动校验")
            .setIcon("filechanged")
            .show();
		ViewGroup v=(ViewGroup)w.addView(R.layout.window_filecheckchanged);
		View p1=v.findViewById(R.id.windowfilecheckchangedLinearLayout1);
		p1.setOnClickListener(this);
		ref=(VecView) v.findViewById(R.id.windowfilecheckchangedVecView1);
		time=(myTextView) v.findViewById(R.id.windowfilecheckchangedmyTextView1);
		list=(myListView) v.findViewById(R.id.windowfilecheckchangedListView1);
		ref.setOnClickListener(this);
		SharedPreferences sp=util.getSPRead("filecheckfile");
		Set<String> set=sp.getStringSet("filecheckfile",null);
		if(set!=null)
		{
			((myTextView)v.findViewById(R.id.windowfilecheckchangedmyTextView1)).setText(sp.getString("filechecktime",""));
			for(String s:set)origin.put(s,"a");
			p1.setVisibility(8);
			Set<String> addedm=sp.getStringSet("filecheckfileadd",null);
			Set<String> deletedm=sp.getStringSet("filecheckfiledeleted",null);
			if(addedm!=null)for(String s:addedm)added.add(s);
			if(deletedm!=null)for(String s:deletedm)deleted.add(s);
			((ViewGroup)p1.getParent()).getChildAt(1).setVisibility(0);
			on(false);
		}
	}

	@Override
	public void onClick(View p1)
	{
		switch(p1.getId())
		{
			case R.id.windowfilecheckchangedLinearLayout1:
				p1.setVisibility(8);
				((ViewGroup)p1.getParent()).getChildAt(1).setVisibility(0);
				refresh();
				break;
			case R.id.windowfilecheckchangedVecView1:
				refresh();
				break;
		}
	}
	@Override
	public void run()
	{
		on(true);
		try
		{
			File r=new File(util.sdcard);
			now.clear();
			added.clear();
			deleted.clear();
			list(r);
			if(run)
			{
				for(String s:origin.keySet())deleted.add(s);
				origin.clear();
				for(String f:now)origin.put(f,"a");
			}
			else{
				Set<String> set=util.getSPRead("filecheckfile").getStringSet("filecheckfile",null);
				origin.clear();
				if(set!=null)for(String s:set)origin.put(s,"a");
			}
			if(run)util.getSPWrite("filecheckfile")
					.putStringSet("filecheckfile",now)
					.putStringSet("filecheckfileadd",added)
					.putStringSet("filecheckfiledeleted",deleted)
					.commit();
		}
		finally
		{
			on(false);
		}
	}
	private void list(File f)
	{
		if(!run)return;
		now.add(f.getAbsolutePath());
		String k=origin.get(f.getAbsolutePath());
		if(k==null)added.add(f.getAbsolutePath());
		origin.remove(f.getAbsolutePath());
		if(f.isDirectory())
		{
			File[] g=f.listFiles();
			if(g!=null)for(File z:g)list(z);
		}
	}
	private void refresh()
	{
		run=!run;
		if(run)new Thread(this).start();
	}
	private void on(final boolean start)
	{
		new Handler(util.ctx.getMainLooper()).post(new Runnable(){
				@Override
				public void run()
				{
					run=start;
					if(start)
					{
						String ti=new SimpleDateFormat().format(new Date(System.currentTimeMillis()));
						time.setText(ti);
						util.getSPWrite("filecheckfile").putString("filechecktime",ti).commit();
						RotateAnimation r=new RotateAnimation(0f,359f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
						r.setRepeatCount(Animation.INFINITE);
						r.setRepeatMode(Animation.RESTART);
						LinearInterpolator lin = new LinearInterpolator();  
						r.setInterpolator(lin);
						r.setDuration(1000);
						ref.startAnimation(r);
					}
					else
					{
						ref.clearAnimation();
						Adapter a = new Adapter();
						list.setAdapter(a);
						list.setOnItemClickListener(a);
					}
				}
			});
	}
	class Adapter extends BaseAdapter implements OnItemClickListener
	{
		String[] add=new String[added.size()],del=new String[deleted.size()];
		public Adapter()
		{
			added.toArray(add);
			deleted.toArray(del);
		}
		@Override
		public int getCount()
		{
			// TODO: Implement this method
			return add.length+del.length;
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
			myTextView t=new myTextView(util.ctx);
			t.setText(p1<add.length?add[p1]:del[p1-add.length]);
			if(p1>=add.length)
			{
				t.setTextColor(0xffff0000);
				t.setPaintFlags(t.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
			}
			else t.setTextColor(0xff00ff00);
			if(t.getText().toString().toLowerCase().contains("appprojects"))t.setTextColor(0xffffff00);
			return t;
		}
		@Override
		public void onItemClick(AdapterView<?> pp1, View p2, int p1, long p4)
		{
			String s=p1<add.length?add[p1]:del[p1-add.length];
			API.startService(util.ctx,new Intent().putExtra("path",s),cls.EXPLORER);
		}
	}
}
