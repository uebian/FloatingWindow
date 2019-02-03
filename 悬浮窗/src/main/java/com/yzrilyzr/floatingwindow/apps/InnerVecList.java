package com.yzrilyzr.floatingwindow.apps;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.yzrilyzr.floatingwindow.R;
import com.yzrilyzr.floatingwindow.Window;
import com.yzrilyzr.icondesigner.VecView;
import com.yzrilyzr.myclass.util;
import java.io.IOException;
import java.util.ArrayList;
import com.yzrilyzr.icondesigner.VECfile;
import com.yzrilyzr.ui.uidata;

public class InnerVecList implements Window.OnSizeChanged
{
	private GridView mlv;
	public InnerVecList(final Context c,Intent e) throws Exception
	{
		AssetManager am=c.getAssets();
		String[] f=am.list("");
		final ArrayList<Bitmap> pd=new ArrayList<Bitmap>();
		final ArrayList<String> pd2=new ArrayList<String>();
		for(String z:f)
		{
			if(z.endsWith(".vec")){
				try{
				pd.add(VECfile.createBitmap(c,z.substring(0,z.length()-4),util.px(40),util.px(40)));
				pd2.add(z);
				}catch(Throwable ep){
					System.out.println("Error read vecfile:"+z);
				}
			}
		}
		BaseAdapter adap=new BaseAdapter(){
			@Override
			public int getCount()
			{
				// TODO: Implement this method
				return pd.size();
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
            public View getView(int position, View convertView, ViewGroup parent)
            {
                ViewHolder holder;
                if(convertView==null)
                {
                    convertView=LayoutInflater.from(c).inflate(R.layout.window_applist_entry,parent,false);
                    holder=new ViewHolder();
                    holder.text1 = (TextView) convertView.findViewById(R.id.windowapplistentryTextView1);
                    holder.text2 = (TextView) convertView.findViewById(R.id.windowapplistentryTextView2);
                    holder.icon = (VecView) convertView.findViewById(R.id.windowapplistentryImageView1);
                    convertView.setTag(holder);
                }
                else holder=(ViewHolder) convertView.getTag();
				holder.text1.setText(pd2.get(position));
                holder.text2.setVisibility(8);
				holder.text1.getPaint().setTextSize(util.px(uidata.TEXTSIZE*0.9f));
				holder.icon.setImageBitmap(pd.get(position));
				return convertView;
			}
        };
		mlv=new GridView(c);
        mlv.setAdapter(adap);
		mlv.setLayoutParams(new LinearLayout.LayoutParams(-1,-1));
        Window w=new Window(c,util.px(260),util.px(320))
            .setIcon("class")
			.setTitle("内置图标字段查看")
            .setBar(0,0,0)
			.setCanFocus(false)
			.setOnSizeChanged(this)
			.addView(mlv)
			.show();
		mlv.setNumColumns(w.getLayoutParams().width/util.px(50));
	}
	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		mlv.setNumColumns(w/util.px(50));
	}
	static class ViewHolder
	{
		TextView text1,text2;
		VecView icon;
	}
}
