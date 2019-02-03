package com.yzrilyzr.floatingwindow.viewholder;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.yzrilyzr.floatingwindow.R;
import com.yzrilyzr.myclass.util;
import java.util.Map;
import com.yzrilyzr.ui.uidata;

public class HolderGrid extends BaseHolder
{
	TextView text1,text2;
	ImageView icon;
	public HolderGrid(Context ctx)
	{
		super(ctx,R.layout.window_applist_entry);
		text1=(TextView)find(R.id.windowapplistentryTextView1);
		text2=(TextView)find(R.id.windowapplistentryTextView2);
		icon=(ImageView)find(R.id.windowapplistentryImageView1);
		text1.getPaint().setTextSize(util.px(uidata.TEXTSIZE*0.9f));
		text2.getPaint().setTextSize(util.px(uidata.TEXTSIZE*0.8f));
	}
	public static View get(Context c,Map data,View convertView){
		BaseHolder hd=null;
		if(convertView==null)
		{
			hd=new HolderGrid(c);
			convertView=hd.vg;
			convertView.setTag(hd);
		}
		else hd=(BaseHolder)convertView.getTag();
		hd.set(data);
		return convertView;
	}
	@Override
	public void set(Object dataq)
	{
		Map data=(Map)dataq;
		text1.setText(""+data.get("text1"));
		text2.setText(""+data.get("text2"));
		text2.setVisibility(data.get("text2")==null?8:0);
		icon.setImageDrawable((Drawable)data.get("icon"));
	}
}
