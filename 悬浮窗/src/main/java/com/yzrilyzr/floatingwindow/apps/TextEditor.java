package com.yzrilyzr.floatingwindow.apps;
import android.content.Context;
import android.content.Intent;
import com.yzrilyzr.floatingwindow.Window;
import com.yzrilyzr.longtexteditor.LongTextView;
import com.yzrilyzr.myclass.util;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class TextEditor
{
	Context ctx;
	public TextEditor(Context c,Intent e) throws IOException{
		ctx=c;
		String p=e.getStringExtra("path");
		String title="文本编辑器";
		LongTextView ltv=new LongTextView(ctx);
		if(p!=null){
			File f=new File(p);
			title=f.getName();
			String st="";
			BufferedReader br=new BufferedReader(new FileReader(p));
			while((st=br.readLine())!=null)
			{
				ltv.addText(st);
			}
			br.close();
		}
		p=e.getStringExtra("text");
		if(p!=null){
			title=e.getStringExtra("title");
			ltv.setText(p);
		}
		new Window(ctx,util.px(320),util.px(400))
			.setTitle(title)
			.setIcon("lte")
			.addView(ltv)
			.show();
	}
}
