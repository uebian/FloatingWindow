package com.yzrilyzr.longtexteditor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.yzrilyzr.floatingwindow.API;
import com.yzrilyzr.floatingwindow.R;
import com.yzrilyzr.floatingwindow.Window;
import com.yzrilyzr.floatingwindow.apps.cls;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.ui.myListView;

public class Settings implements OnItemClickListener,Window.OnButtonDown
{
	String[] des=new String[]{
		"文字颜色","选定文字颜色","背景色",
		"行号颜色","行号背景色","行号分割线颜色",
		"焦点行颜色","指针颜色","左指针颜色",
		"右指针颜色","光标颜色","换行符颜色",
		"提示框背景色","提示框文字颜色","提示框分割线颜色"
	};
	int[] par=new int[des.length];
	private myListView l;
	private mAdapter mAdapter;
	private Context ctx;

	private Window w;
	public Settings(Context c,Intent r)
	{
		ctx=c;
		par[0]=LongTextView.textColor;
		par[1]=LongTextView.selectionColor;
		par[2]=LongTextView.backColor;
		par[3]=LongTextView.lineNumColor;
		par[4]=LongTextView.lineNumBackColor;
		par[5]=LongTextView.lineNumLineColor;
		par[6]=LongTextView.curLineColor;
		par[7]=LongTextView.cursorColor;
		par[8]=LongTextView.cursorLeftColor;
		par[9]=LongTextView.cursorRightColor;
		par[10]=LongTextView.cursorLineColor;
		par[11]=LongTextView.enterColor;
		par[12]=LongTextView.suggBackColor;
		par[13]=LongTextView.suggTextColor;
		par[14]=LongTextView.suggLineColor;
		//par[12]=LongTextView.textSize;
		l=new myListView(c);
		l.setAdapter(mAdapter=new mAdapter());
		l.setOnItemClickListener(this);
		w=new Window(c,util.px(275),util.px(285))
			.setTitle("编辑器设置")
			.setBar(0,8,0)
			.setCanFocus(false)
			.setIcon("settings")
			.setOnButtonDown(this)
			.addView(l)
			.setParent(r)
			.show();
	}

	@Override
	public void onButtonDown(int code)
	{
		if(code==Window.ButtonCode.CLOSE)
		{
			LongTextView.textColor=par[0];
			LongTextView.selectionColor=par[1];
			LongTextView.backColor=par[2];
			LongTextView.lineNumColor=par[3];
			LongTextView.lineNumBackColor=par[4];
			LongTextView.lineNumLineColor=par[5];
			LongTextView.curLineColor=par[6];
			LongTextView.cursorColor=par[7];
			LongTextView.cursorLeftColor=par[8];
			LongTextView.cursorRightColor=par[9];
			LongTextView.cursorLineColor=par[10];
			LongTextView.enterColor=par[11];
			LongTextView.suggBackColor=par[12];
			LongTextView.suggTextColor=par[13];
			LongTextView.suggLineColor=par[14];
			util.getSPWrite("longtexteditor")
				.putInt("text",par[0])
				.putInt("sel",par[1])
				.putInt("lback",par[2])
				.putInt("ln",par[3])
				.putInt("lnb",par[4])
				.putInt("lnl",par[5])
				.putInt("cl",par[6])
				.putInt("cc",par[7])
				.putInt("clc",par[8])
				.putInt("crc",par[9])
				.putInt("clic",par[10])
				.putInt("en",par[11])
				.putInt("sgb",par[12])
				.putInt("sgt",par[13])
				.putInt("sgl",par[14])
				.commit();
		}
	}
	@Override
	public void onItemClick(AdapterView<?> p1, View p2, final int p3, long p4)
	{
		API.startServiceForResult(ctx,new Intent().putExtra("color",par[p3]),w,new BroadcastReceiver(){
				@Override
				public void onReceive(Context c,Intent e)
				{
					int y=e.getIntExtra("color",0);
					par[p3]=y;
					mAdapter.notifyDataSetChanged();
				}
			},cls.COLORPICKER);
	}
	class mAdapter extends BaseAdapter
	{
		@Override
		public int getCount()
		{
			return des.length;
		}
		@Override
		public Object getItem(int p1)
		{
			return null;
		}
		@Override
		public long getItemId(int p1)
		{
			return 0;
		}
		@Override
		public View getView(int p1, View convertView, ViewGroup p3)
		{
			ViewHolder holder;
			if(convertView==null)
			{
				holder=new ViewHolder(ctx);
				convertView=holder.v;
				convertView.setTag(holder);
			}
			else holder=(ViewHolder) convertView.getTag();
			holder.setText(des[p1]);
			holder.setColor(par[p1]);
			return convertView;
		}
	}
	static class ViewHolder
	{
		public ViewGroup v;
		public TextView t=null;
		public ImageView iv=null;
		public ViewHolder(Context ctx)
		{
			v=(ViewGroup) LayoutInflater.from(ctx).inflate(R.layout.window_longtexteditorsettingsentry,null);
		}
		public void setText(String s)
		{
			if(t==null)t=(TextView)v.getChildAt(0);
			t.setText(s);
		}
		public void setColor(int s)
		{
			if(iv==null)iv=(ImageView)v.getChildAt(1);
			iv.setBackgroundColor(s);
		}
	}
}
