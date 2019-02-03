package com.yzrilyzr.floatingwindow.viewholder;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.yzrilyzr.floatingwindow.R;
import com.yzrilyzr.icondesigner.VecView;
import com.yzrilyzr.ui.myTextView;
import java.util.Map;
import com.yzrilyzr.icondesigner.VECfile;
import android.graphics.drawable.Drawable;

public class HolderList extends BaseHolder
{
	public VecView[] v=new VecView[4];
	public myTextView text;
	public HolderList(Context c)
	{
		super(c,R.layout.window_manager_entry);
		v[0]=(VecView) vg.getChildAt(0);
		text=(myTextView) vg.getChildAt(1);
		v[1]=(VecView) vg.getChildAt(2);
		v[2]=(VecView) vg.getChildAt(3);
		v[3]=(VecView) vg.getChildAt(4);
	}
	public static View get(Context c,Map data,View convertView){
		BaseHolder hd=null;
		if(convertView==null)
		{
			hd=new HolderList(c);
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
		text.setText(""+data.get("text"));
		for(int i=0;i<v.length;i++){
			Object o=data.get(String.format("icon%d",i));
			v[i].setVisibility(o==null?8:0);
			if(o!=null){
				if(o instanceof String)v[i].setImageVec((String)o);
				else if(o instanceof VECfile)v[i].setImageVecFile((VECfile)o);
				else v[i].setImageDrawable((Drawable)o);
			}
		}
	}
}
