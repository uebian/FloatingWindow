package com.yzrilyzr.connecttogether;
import android.content.Context;
import com.yzrilyzr.icondesigner.VecView;
import com.yzrilyzr.ui.myTextView;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import com.yzrilyzr.floatingwindow.R;
import com.yzrilyzr.icondesigner.VECfile;

public class UserViewHolder
{
	VecView head;
	myTextView nick,sign;
	public ViewGroup get(Context c){
		ViewGroup vg=(ViewGroup)LayoutInflater.from(c).inflate(R.layout.ct_user_entry,null),vg2=null;
		head=(VecView) vg.getChildAt(0);
		vg2=(ViewGroup) vg.getChildAt(1);
		nick=(myTextView) vg2.getChildAt(0);
		sign=(myTextView) vg2.getChildAt(1);
		vg.setTag(this);
		return vg;
	}
	public void setNick(String s){
		nick.setText(s);
	}
	public void setSign(String s){
		sign.setText(s);
	}
	public void setHeadAsset(String s){
		head.setImageVec(s);
	}
	public void setHead(VECfile s){
		head.setImageVecFile(s);
	}
}
