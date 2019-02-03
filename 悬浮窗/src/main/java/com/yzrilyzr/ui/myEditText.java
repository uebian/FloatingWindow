package com.yzrilyzr.ui;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.yzrilyzr.floatingwindow.R;
import com.yzrilyzr.myclass.util;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.graphics.drawable.ColorDrawable;

public class myEditText extends EditText implements View.OnLongClickListener
{
	private Context ctx;
	protected boolean selected=false;
	protected int start=0,end=0;
	public myEditText(Context ctx,AttributeSet attr)
	{
		super(ctx,attr);
		init(ctx);
	}
	private void init(Context c)
	{
		ctx=c;
		//setOnLongClickListener(this);
		setBackgroundDrawable(draw=new drawable());
		//setPadding(getPaddingLeft(),getPaddingTop(),getPaddingRight(),getPaddingBottom()+tool.dip2px(getContext(),3));
		setTextColor(isEnabled()?uidata.TEXTMAIN:uidata.getEFColor());
        setHintTextColor(uidata.UNENABLED);
		if(uidata.UI_USETYPEFACE)setTypeface(uidata.UI_TYPEFACE);
		getPaint().setTextSize(util.px(uidata.TEXTSIZE));
	}

	@Override
	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);
		draw.setEnabled(enabled);
		setTextColor(isEnabled()?uidata.TEXTMAIN:uidata.getEFColor());
	}
	@Override
	protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect)
	{
		super.onFocusChanged(focused, direction, previouslyFocusedRect);
		draw.setFocus(focused);
	}

	@Override
	public boolean onLongClick(View p1)
	{
		// TODO: Implement this method
		new myDialog.Builder(ctx).setItems((selected=(start=this.getSelectionStart())==(end=this.getSelectionEnd()))?
			("全选,粘贴,跳转到开头,跳转到结尾,插入时间,字数统计").split(","):
			("全选,剪切,复制,粘贴,重复选中的文本,转为大写,转为小写,首字母大写,跳转到开头,跳转到结尾,插入时间,字数统计").split(","),
			new DialogInterface.OnClickListener(){
				@Override public void onClick(DialogInterface p,int t)
				{
					clicki(t);
				}
			})
		.show();
		return true;
	}
	private class drawable extends Drawable
	{
		private int w,h,i;
		private boolean b=false,e=true;
		private Paint p;
		public drawable()
		{
			p=new Paint(Paint.ANTI_ALIAS_FLAG);
		}
		@Override
		public void draw(Canvas canvas)
		{
			// TODO: Implement this method
			i=util.px(1);
			p.setColor(e?(b?uidata.ACCENT:uidata.CONTROL):uidata.getESColor());
			if(b)i=util.px(2);
			p.setStrokeWidth(i);
			canvas.drawLine(i,h-i,w-i,h-i,p);
		}
		public void setFocus(boolean f)
		{
			b=f;
			invalidateSelf();
		}
		public void setEnabled(boolean f)
		{
			e=f;
			invalidateSelf();
		}
		@Override
		public void setBounds(int left, int top, int right, int bottom)
		{
			// TODO: Implement this method
			super.setBounds(left, top, right, bottom);
			w=right;h=bottom;
		}

		@Override
		public void setAlpha(int p1)
		{
			// TODO: Implement this method
		}

		@Override
		public void setColorFilter(ColorFilter p1)
		{
			// TODO: Implement this method
		}

		@Override
		public int getOpacity()
		{
			// TODO: Implement this method
			return 0;
		}


	}
	public void clicki(int i)
	{
		try
		{
			// TODO: Implement this method
			if(selected)
			{
				if(i==0)
				{
					setSelection(0,getText().length());
				}
				if(i==1)
				{
					String t=util.paste();
					getText().delete(start,end).insert(start,t);
					setSelection(start+t.length());
				}
				if(i==2)
				{
					setSelection(0);
				}
				if(i==3)
				{
					setSelection(getText().length());
				}
				if(i==4)
				{
					String t=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
					getText().delete(start,end).insert(start,t);
					setSelection(start+t.length());
				}
				if(i==5)
				{
					String a=getText().toString();
					util.alert(
							   "字符数:"+a.length()+"\n"+
							   "单词数:"+(a.split("\\w+").length-1)+"\n"+
							   "行数:"+getCharCount(a,'\n')+"\n"+
							   "()数:"+getCharCount(a,'(')+"|"+getCharCount(a,')')+"\n"+
							   "{}数:"+getCharCount(a,'{')+"|"+getCharCount(a,'}')+"\n"+
							   "[]数:"+getCharCount(a,'[')+"|"+getCharCount(a,']')+"\n"
							   );
				}
			}
			if(!selected)
			{
				if(i==0)
				{
					setSelection(0,getText().length());
				}
				if(i==1)
				{
					Editable ea=getText();
					util.copy(ea.toString().substring(start,end));
					ea.delete(start,end);
					setSelection(start);
				}
				if(i==2)
				{
					util.copy(getText().toString().substring(start,end));
					setSelection(end);
				}
				if(i==4)
				{
					Editable ea=getText();
					String s=ea.toString().substring(start,end);
					ea.insert(end,s);
					setSelection(start,end+s.length());
				}
				if(i==5)
				{
					Editable ea=getText();
					ea.replace(start,end,ea.toString().substring(start,end).toUpperCase());
					setSelection(start,end);
				}
				if(i==6)
				{
					Editable ea=getText();
					ea.replace(start,end,ea.toString().substring(start,end).toLowerCase());
					setSelection(start,end);
				}
				if(i==7)
				{
					Editable ea=getText();
					ea.replace(start,start+1,ea.toString().substring(start,start+1).toUpperCase());
					setSelection(start,end);
				}
				if(i==3)
				{
					String t=util.paste();
					getText().delete(start,end).insert(start,t);
					setSelection(start,start+t.length());
				}
				if(i==8)
				{
					setSelection(0);
				}
				if(i==9)
				{
					setSelection(getText().length());
				}
				if(i==10)
				{
					String t=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
					getText().delete(start,end).insert(start,t);
					setSelection(start,start+t.length());
				}
				if(i==11)
				{
					String a=getText().toString();
					util.alert(
							   "字符数:"+a.length()+"\n"+
							   "单词数:"+(a.split("\\w+").length-1)+"\n"+
							   "行数:"+getCharCount(a,'\n')+"\n"+
							   "()数:"+getCharCount(a,'(')+"|"+getCharCount(a,')')+"\n"+
							   "{}数:"+getCharCount(a,'{')+"|"+getCharCount(a,'}')+"\n"+
							   "[]数:"+getCharCount(a,'[')+"|"+getCharCount(a,']')+"\n"
							   );
				}
			}
		}
		catch(Exception e)
		{}
	}

	private int getCharCount(String s,char c)
	{
		int count=0;
		for(int i=0;i<s.length();i++)
		{
			if(s.charAt(i)==c)
			{count++;}
		}
		return count;
	}
	public myEditText(Context c)
	{
		this(c,null);
	}
	private drawable draw;
}
