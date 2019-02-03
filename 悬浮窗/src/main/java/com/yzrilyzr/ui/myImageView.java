package com.yzrilyzr.ui;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class myImageView extends ImageView
{
	public myImageView(Context c,AttributeSet a)
    {
        super(c,a);
        setScaleType(ImageView.ScaleType.FIT_CENTER);
        WidgetUtils.setIcon(this,a);
    }
	public myImageView(Context c){
		this(c,null);
	}
	
}
