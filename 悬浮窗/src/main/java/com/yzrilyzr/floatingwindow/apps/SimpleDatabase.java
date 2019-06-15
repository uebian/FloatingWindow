package com.yzrilyzr.floatingwindow.apps;

import java.util.*;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.yzrilyzr.floatingwindow.R;
import com.yzrilyzr.floatingwindow.Window;
import com.yzrilyzr.icondesigner.VECfile;
import com.yzrilyzr.myclass.myComp;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.ui.myDialog;
import com.yzrilyzr.ui.myEditText;
import com.yzrilyzr.ui.myFab;
import com.yzrilyzr.ui.myTextView;
import java.io.File;

public class SimpleDatabase implements TextWatcher,OnClickListener,OnItemClickListener,OnItemLongClickListener
{
	ListView list;
	EditText edit;
	Map<String,HashMap<String,String>> data=new HashMap<String,HashMap<String,String>>();
	private Context ctx;
	String isvg=null;
	ArrayList<String> ml=new ArrayList<String>();
	myFab up;
	private Window w;
	public SimpleDatabase(Context c,Intent e)
	{
		ctx=c;
		w=new Window(c,util.px(285),util.px(300))
		.setTitle("简单数据库")
		.setIcon("database")
		.show();
		View v=w.addView(R.layout.window_simpledatabase);
		list=(ListView) v.findViewById(R.id.windowsimpledatabaseListView1);
		edit=(EditText)v.findViewById(R.id.windowsimpledatabasemyEditText1);
		((View)v.findViewById(R.id.windowsimpledatabasemyFab1)).setOnClickListener(this);
		up=(myFab) v.findViewById(R.id.windowsimpledatabasemyFab2);
		up.setOnClickListener(this);
		list.setAdapter(new BaseAdapter(){
			@Override
			public int getCount()
			{
				// TODO: Implement this method
				return ml.size();
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
				t.setEllipsize(TextUtils.TruncateAt.END);
				t.setText(ml.get(p1)+(isViewGroup()?"":":"+data.get(isvg).get(ml.get(p1))));
				return t;
			}
			@Override
			public void notifyDataSetChanged()
			{
				ml.clear();
				Map m=isViewGroup()?data:data.get(isvg);
				String ek=edit.getText().toString().toLowerCase();
				if("".equals(ek))for(String d:m.keySet())ml.add(d);
				else for(String d:m.keySet())if(d.toLowerCase().contains(ek))ml.add(d);
				Collections.sort(ml,new myComp<String>(){
					@Override
					public int compare(String p1, String p2)
					{
						// TODO: Implement this method
						return p1.compareToIgnoreCase(p2);
					}
				});
				super.notifyDataSetChanged();
			}
		});
		list.setOnItemClickListener(this);
		list.setOnItemLongClickListener(this);
		edit.addTextChangedListener(this);
		read();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> p1, View p2, final int p3, long p4)
	{
		if(isViewGroup())
		{
			myDialog.Builder builder = new myDialog.Builder(ctx);
			builder.setTitle("修改组名");
			final myEditText edi=new myEditText(ctx);
			edi.setText(ml.get(p3));
			edi.setHint("组名");
			LinearLayout l=new LinearLayout(ctx);
			l.setOrientation(1);
			builder.setView(l);
			l.addView(edi);
			util.setWeight(l);
			util.setWeight(edi);
			builder.setPositiveButton("修改",new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface d,int p)
				{
					String a=edi.getText().toString();
					data.put(a,data.remove(ml.get(p3)));
					save();
					((BaseAdapter)list.getAdapter()).notifyDataSetChanged();
				}});
			builder.setNeutralButton("关闭",null);
			builder.setNegativeButton("删除",new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					data.remove(ml.get(p3));
					save();
					((BaseAdapter)list.getAdapter()).notifyDataSetChanged();
				}
			});

			builder.show();
		}
		return true;
	}
	@Override
	public void onItemClick(AdapterView<?> p1, View p2, final int p3, long p4)
	{
		if(isViewGroup())
		{isvg=ml.get(p3);updateInfo();}
		else
		{
			new myDialog.Builder(ctx)
			.setTitle(ml.get(p3))
			.setMessage(data.get(isvg).get(ml.get(p3)))
			.setNegativeButton("删除",new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					data.get(isvg).remove(ml.get(p3));
					save();
					((BaseAdapter)list.getAdapter()).notifyDataSetChanged();
				}
			})
			.setNeutralButton("编辑",new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					myDialog.Builder builder = new myDialog.Builder(ctx);
					builder.setTitle("修改条目");
					final myEditText edi=new myEditText(ctx);
					final myEditText edi2=new myEditText(ctx);
					edi.setText(ml.get(p3));
					edi2.setText(data.get(isvg).get(ml.get(p3)));
					edi.setHint("字段");
					edi2.setHint("内容");
					LinearLayout l=new LinearLayout(ctx);
					l.setOrientation(1);
					builder.setView(l);
					l.addView(edi);
					util.setWeight(l);
					util.setWeight(edi);
					l.addView(edi2);
					util.setWeight(edi2);
					builder.setPositiveButton("修改",new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface d,int p)
						{
							String a=edi.getText().toString();
							String b=edi2.getText().toString();
							data.get(isvg).remove(ml.get(p3));
							data.get(isvg).put(a,b);
							save();
							((BaseAdapter)list.getAdapter()).notifyDataSetChanged();
						}});
					builder.setNegativeButton("取消",null);
					builder.show();

				}
			})
			.setPositiveButton("关闭",null)
			.show();
		}
	}

	private void updateInfo()
	{
		w.setTitle("简单数据库"+(isViewGroup()?"":" (当前组:"+isvg+")"));
		edit.setHint(isViewGroup()?"在 所有组 中搜索…":"在 "+isvg+" 中搜索");
		((BaseAdapter)list.getAdapter()).notifyDataSetChanged();
		try
		{
			up.setImageBitmap(VECfile.createBitmap(ctx,isViewGroup()?"loadout":"upparent",util.px(50),util.px(50)));
		}
		catch (Exception e)
		{}
	}


	@Override
	public void onClick(View p1)
	{
		if(p1==up)
		{
			if(!isViewGroup())
			{
				isvg=null;
				updateInfo();
			}
			else
			{
				File dir=new File(util.mainDir+"简单数据库");
				if(!dir.exists())dir.mkdirs();
				Explorer ex=new Explorer(ctx,new Intent().putExtra("path",dir.getAbsolutePath()));
				ex.clip.add(new Explorer.mFile("/data/data/com.yzrilyzr.floatingwindow/shared_prefs/com.yzrilyzr.floatingwindow_com.yzrilyzr.floatingwindow_spdtbs.xml"));
				ex.paste(new Explorer.mFile(dir.getAbsolutePath()));
			}
			return;
		}
		myDialog.Builder builder = new myDialog.Builder(ctx);
		builder.setTitle(isViewGroup()?"添加组":"添加新条目(换行以添加多项)");
		final myEditText edi=new myEditText(ctx);
		final myEditText edi2=new myEditText(ctx);
		edi.setHint(isViewGroup()?"组名称":"字段");
		edi2.setHint("内容");
		LinearLayout l=new LinearLayout(ctx);
		l.setOrientation(1);
		builder.setView(l);
		l.addView(edi);
		util.setWeight(l);
		util.setWeight(edi);
		if(!isViewGroup())
		{
			l.addView(edi2);
			util.setWeight(edi2);
		}
		builder.setPositiveButton("添加",new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface d,int p)
			{
				String[] aa=edi.getText().toString().split("\n");
				String[] bb=edi2.getText().toString().split("\n");
				for(int i=0;i<aa.length;i++)
				{
					if(isViewGroup()&&!data.containsKey(aa[i]))
					{
						data.put(aa[i],new HashMap<String,String>());
					}
					else if(!isViewGroup()&&!data.get(isvg).containsKey(aa[i]))
					{
						data.get(isvg).put(aa[i],i<bb.length?bb[i]:"");
					}
					else util.toast("已经存在该字段:"+aa[i]);
				}
				save();
				((BaseAdapter)list.getAdapter()).notifyDataSetChanged();
			}});
		builder.setNegativeButton("取消",null);
		builder.show();

	}
	private boolean isViewGroup()
	{
		return isvg==null;
	}
	private void read()
	{
		SharedPreferences s=ctx.getSharedPreferences("spdtbs",ctx.MODE_PRIVATE);
		Set<String> set=s.getStringSet("groupkeys",null);
		if(set!=null)
		{
			for(String gkey:set)
			{
				HashMap<String,String> k=new HashMap<String,String>();
				Set<String> set2=s.getStringSet(gkey+"groupdata",null);
				if(set2!=null)
					for(String keys:set2)
					{
						int in=keys.indexOf("$:::$|");
						k.put(keys.substring(0,in),keys.substring(in+6));
					}
				data.put(gkey,k);
			}
		}
		updateInfo();
	}
	private void save()
	{
		SharedPreferences.Editor s=ctx.getSharedPreferences("spdtbs",ctx.MODE_PRIVATE).edit();
		s.clear();
		s.putStringSet("groupkeys",data.keySet());
		for(Map.Entry gkey:data.entrySet())
		{
			Set<String> set2=new TreeSet<String>();
			HashMap<String,String> o=(HashMap<String,String>)gkey.getValue();
			for(String x:o.keySet())set2.add(x+"$:::$|"+o.get(x));
			s.putStringSet(gkey.getKey()+"groupdata",set2);
		}
		s.commit();	
	}
	@Override
	public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
	{
		((BaseAdapter)list.getAdapter()).notifyDataSetChanged();
	}

	@Override
	public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
	{
		// TODO: Implement this method
	}
	@Override
	public void afterTextChanged(Editable p1)
	{
		// TODO: Implement this method
	}

}
