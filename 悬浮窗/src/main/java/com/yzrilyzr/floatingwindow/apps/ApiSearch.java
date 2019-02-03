package com.yzrilyzr.floatingwindow.apps;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import com.yzrilyzr.floatingwindow.API;
import com.yzrilyzr.floatingwindow.Window;
import com.yzrilyzr.floatingwindow.apps.cls;
import com.yzrilyzr.myclass.ClassSrc;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.ui.myButton;
import com.yzrilyzr.ui.myEditText;
import com.yzrilyzr.ui.myTextView;

public class ApiSearch implements OnClickListener
{
	private myEditText met;
	private Context ctx;
	private myButton mb;
	private myButton mb2;
	public ApiSearch(Context c,Intent e){
		ctx=c;
		Window w=new Window(ctx,util.px(315),util.px(185))
            .setTitle("API查询")
            .setIcon("api")
            .setBar(0,8,0)
            .show();
        myTextView m=new myTextView(ctx);
        m.setText("请输入完整的Java包名和类名，内部类用$连接");
        w.addView(m);
        met=new myEditText(ctx);
        mb=new myButton(ctx);
		mb2=new myButton(ctx);
        w.addView(met);
        w.addView(mb);
		w.addView(mb2);
		util.setWeight(met);
        met.setText("com.yzrilyzr.floatingwindow.API");
        mb.setText("查询");
        mb.setOnClickListener(this);
		mb2.setText("制作ProxyApi");
		mb2.setOnClickListener(this);
	}
	@Override
	public void onClick(View p1)
	{
		// TODO: Implement this method
		try
		{
			String s=met.getText().toString();
			Class<?> c=Class.forName(s);
			API.startService(ctx,new Intent()
			.putExtra("title",c.getSimpleName()+".class")
			.putExtra("text",new ClassSrc(c,p1==mb2).get()),cls.TEXTEDITOR);
		}
		catch(ClassNotFoundException e)
		{
			util.toast("找不到该类");
		}
	}
}
