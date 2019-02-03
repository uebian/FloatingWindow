package com.yzrilyzr.floatingwindow.apps;
import android.content.Context;
import android.content.Intent;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.floatingwindow.Window;
import com.yzrilyzr.ui.myCanvas;

public class DrawingBoard
{
	Window w;
	myCanvas can;
	public DrawingBoard(Context c,Intent e){
		can=new myCanvas(c,null);
		w=new Window(c,util.px(300),util.px(300))
		.setTitle("画板")
		.setBar(0,0,0)
		.setCanFocus(false)
		.setIcon("drawingboard")
		.addView(can)
		.show();
	}
}
