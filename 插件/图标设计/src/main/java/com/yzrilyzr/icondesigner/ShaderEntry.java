package com.yzrilyzr.icondesigner;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.view.MotionEvent;
import java.util.concurrent.CopyOnWriteArrayList;

public class ShaderEntry extends Menu implements Button.Event,FloatPicker.FloatPickerEvent
{
	Point sh;
	int index,type;
	Menu colorp;
	Button up,down,addup,adddown,col,del,par;
	FloatPicker picker;
	public ShaderEntry(float x,float y,float w,float h,int ind,Point sh,int type,Menu colorp)
	{
		super(x,y,w,h);
		this.colorp=colorp;
		this.sh=sh;
		this.index=ind;
		this.type=type;
		par=new Button(x+h*1,0,h,h,sh.y+"%",this);
		del=new Button(x+h*2,0,h,h,"移除",this);
		addup=new Button(x+h*3,0,h,h,"前添加",this);
		adddown=new Button(x+h*4,0,h,h,"后添加",this);
		up=new Button(x+h*5,0,h,h,"上移",this);
		down=new Button(x+h*6,0,h,h,"下移",this);
		col=new Button(x,0,h,h,"",this);
		col.color=sh.x;
		addView(col,par,del,addup,adddown,up,down);
		show=true;
		picker=new FloatPicker(x+w/2,y+h,w/2,this);
		picker.setValue((float)sh.y/100f);
	}
	@Override
	public void onChange(FloatPicker p, float f)
	{
		sh.y=(int)(f*100f);
		par.txt=sh.y+"%";
	}
	@Override
	public void e(Button b)
	{
		if(render.tmpShape!=null)
			if(b==col)
			{
				render.showMenu(colorp);
				((ColorPicker)colorp.views.get(0)).setIColor(sh);
			}
			else if(b==par){
				List list=(List) parent;
				if(list.views.contains(picker))list.views.remove(picker);
				else list.views.add(index-(type==2?0:1),picker);
				list.measure();
			}
			else
			{
				CopyOnWriteArrayList<Point> li=null;
				if(type==0)li=render.tmpShape.linear; 
				if(type==1)li=render.tmpShape.radial;
				if(type==2)li=render.tmpShape.sweep;
				if(b==addup)
				{
					li.add(index,new Point(0xffff0000,0));
				}
				else if(b==adddown)
				{
					li.add(index+1,new Point(0xffff0000,0));
				}
				else if(b==del){
					if(li.size()==2+(type==2?1:2))render.toast("至少保留2个参数");
					else li.remove(index);
				}
				else if(b==up)
				{
					Point p=li.remove(index);
					if(--index<(type==2?1:2))index=(type==2?1:2);
					li.add(index,p);
				}
				else if(b==down)
				{
					Point p=li.remove(index);
					if(++index>li.size())index=li.size();
					li.add(index,p);
				}
				
				List list=(List)parent;
				list.views.clear();
				int k=-1;
				for(Point pp:li)
					if(++k>=(type==2?1:2))list.addView(new ShaderEntry(dip(list.left),0,dip(list.width()),dip(list.height()/8),k,pp,type,colorp));
				list.measure();	
			}
	}
	@Override
	public void onDraw(Canvas c)
	{
		for(MView v:views)
		{
			v.top=top+px(3);
			v.bottom=bottom-px(3);
		}
		col.color=sh.x;
		c.drawRect(this,paint);
		for(MView v:views)v.onDraw(c);
	}
}
