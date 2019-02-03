package com.yzrilyzr.icondesigner;

import android.graphics.*;
import android.view.inputmethod.*;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View.OnClickListener;
import android.widget.EditText;
import com.yzrilyzr.icondesigner.MView;
import com.yzrilyzr.icondesigner.MainActivity;
import com.yzrilyzr.icondesigner.Shape;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.CopyOnWriteArrayList;

public class RenderThread implements InputConnection,Thread.UncaughtExceptionHandler
{
	public MainActivity ctx;
	public float dpi;
	public VECfile vec=new VECfile(384,384,19.2f,null);//文件
	public VECfile.Builder builder=new VECfile.Builder();
	public Shape tmpShape,colorShape=new Shape(Shape.STYLE.FILL|Shape.STROKE.BUTT|Shape.STROKE.MITER);//临时和颜色
	public Point tmpPoint,tmpPoint2,tmpPoint3;//临时和回退,位移
	public int alpha=255;
	public MView curView;//逻辑view
	public Bitmap icon,folder,file,vecfile,upparent,xml;
	public int ram=0,fps=0;
	public float deltax,deltay,scale,lscale,lpointLen;//上次缩放，长度
	public CopyOnWriteArrayList<CopyOnWriteArrayList<Shape>> undo=new CopyOnWriteArrayList<CopyOnWriteArrayList<Shape>>();
	public CopyOnWriteArrayList<CopyOnWriteArrayList<Shape>> redo=new CopyOnWriteArrayList<CopyOnWriteArrayList<Shape>>();
	public boolean useNet=true,moved=false;
	public int cx,cy,dcx,dcy;//指针
	public float ddx,ddy;//上次触摸
	public int pointIndex=0;//path点
	public int MODE=0;//触摸模式
	public ColorView curColorView;//当前颜色
	public CopyOnWriteArrayList<MView> mview=new CopyOnWriteArrayList<MView>();
	public StringBuilder info=new StringBuilder();
	public File localFile;
	public SurfaceView surface;
	public Toast toast;
	public int isTutorial=0;
	public boolean render=false;
	private float bs;
	public RenderThread(SurfaceView surface)
	{
		Thread.setDefaultUncaughtExceptionHandler(this);
		this.surface = surface;
		this.ctx=(MainActivity)surface.getContext();
		MView.render=this;
		dpi=ctx.getResources().getDisplayMetrics().density;
		localFile=new File(ctx.getSharedPreferences("path",ctx.MODE_PRIVATE).getString("path",Environment.getExternalStorageDirectory().getAbsolutePath()+"/yzr的app/图标设计"));
		if(!localFile.exists())localFile.mkdirs();
		Bitmap ic2=null;
		try
		{
			ic2=VECfile.createBitmap(ctx,"bestodesign",MView.px(70),MView.px(70));
			icon=VECfile.createBitmap(ctx,"icon",MView.px(80),MView.px(80));
			folder=VECfile.createBitmap(ctx,"folder",MView.px(50),MView.px(50));
			file=VECfile.createBitmap(ctx,"file",MView.px(50),MView.px(50));
			vecfile=VECfile.createBitmap(ctx,"vecfile",MView.px(50),MView.px(50));
			upparent=VECfile.createBitmap(ctx,"upparent",MView.px(50),MView.px(50));
			xml=VECfile.createBitmap(ctx,"xml",MView.px(50),MView.px(50));
		}
		catch (IllegalStateException e)
		{
			toast("不是VEC文件");
		}
		catch (Exception e)
		{
			toast("打开失败");
		}
		Bitmap la=Bitmap.createBitmap(surface.getWidth(),surface.getHeight(),Bitmap.Config.ARGB_8888);
		Canvas canvas=new Canvas(la);
		Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setTextSize(MView.px(18));
		canvas.drawColor(MView.buttoncolor);
		canvas.drawBitmap(icon,(surface.getWidth()-icon.getWidth())/2,(surface.getHeight()-icon.getHeight())/2,paint);
		canvas.drawBitmap(ic2,surface.getWidth()/2-ic2.getWidth()*2,(surface.getHeight()-ic2.getHeight())*0.85f,paint);
		ic2.recycle();
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setColor(MView.textcolor);
		canvas.drawText("图标设计",surface.getWidth()/2,surface.getHeight()*0.6f,paint);
		canvas.drawText("@Besto Design",surface.getWidth()/2,surface.getHeight()*0.8f,paint);
		icon.recycle();
		icon=la;
		initPosition();
		//if(ctx.getSharedPreferences("data",ctx.MODE_PRIVATE).getBoolean("first",true);
		//isTutorial=1;
	}
	public void run1()
	{
		Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setTextSize(MView.px(18));
		paint.setTypeface(Typeface.MONOSPACE);
		long time;
		Runtime ru=Runtime.getRuntime();
		try
		{
			Thread.sleep(200);
		}
		catch (InterruptedException e)
		{}
		while(render)
			try
			{
				time=System.nanoTime();
				Canvas c=surface.getHolder().lockCanvas();
				if(c!=null)
				{
					draw(c,paint);
					surface.getHolder().unlockCanvasAndPost(c);
				}
				else Thread.sleep(500);
				ram=(int)((ru.totalMemory()-ru.freeMemory())*100/ru.maxMemory());
				if(alpha>0)alpha-=5;
				time=System.nanoTime()-time;
				if(time!=0)fps=(int)(1000000000l/time+fps*9)/10;
			}
			catch(Throwable e)
			{
				try
				{
					check(e);
					Thread.sleep(1000);
				}
				catch (Exception e2)
				{
				}
			}
	}
	public void run2()
	{
		while(render)
			try
			{
				vec.onDraw();
			}
			catch(Throwable e)
			{
				try
				{
					toast("已暂停绘制，请检查图形参数");
					Thread.sleep(1000);
				}
				catch (Exception e2)
				{}
			}
	}
	@Override
	public void uncaughtException(Thread p1, final Throwable p2)
	{
		new Thread() {  
            @Override  
            public void run() {
				try
				{
					ByteArrayOutputStream os=new ByteArrayOutputStream();
					PrintWriter ps=new PrintWriter(os);
					p2.printStackTrace(ps);
					ps.flush();
					ps.close();
					Looper.prepare();  
					new AlertDialog.Builder(ctx)
					.setTitle("提示")
					.setCancelable(false)  
					.setMessage("程序崩溃了(⊙﹏⊙)\n您可以重新打开一个文件之后退出重新进入\n:"+os.toString())
						.setPositiveButton("退出",new DialogInterface.OnClickListener() {  
							@Override  
							public void onClick(DialogInterface dialog, int which) {  
								System.exit(0);  
							}  
						})
						.setNegativeButton("继续使用(不推荐)",null)
					.setNeutralButton("保存退出",new DialogInterface.OnClickListener() {  
							@Override  
							public void onClick(DialogInterface dialog, int which) {  
								if(vec!=null)vec.saveFile(localFile.getAbsolutePath()+"/"+vec.name+"(临时文件).vec");
							System.exit(0);  
							}  
						})  
						.create().show();  
					Looper.loop();
				}
				catch (Exception e)
				{}
            }  
        }.start();
	}
	public void draw(Canvas canvas,Paint paint)
	{
		canvas.drawColor(0xff666666);
		Matrix m=new Matrix();
		m.postTranslate(deltax,deltay);
		m.postScale(scale,scale);
		canvas.drawBitmap(vec.lock(tmpShape),m,paint);
		vec.unlock();
		canvas.restoreToCount(1);
		try
		{
			if(tmpShape!=null&&tmpShape.hasFlag(Shape.TYPE.PATH))
				for(int i=1;i<tmpShape.pts.size();i++)
				{
					Point t=tmpShape.pts.get(i);
					PointF pf=new PointF((t.x*vec.dp+deltax)*scale,(t.y*vec.dp+deltay)*scale);
					if(MainView.render.tmpPoint==t)paint.setColor(0xffff0000);
					else paint.setColor(0xff000000);
					canvas.drawText(Integer.toString(i),pf.x,pf.y,paint);
					canvas.drawPoint(pf.x,pf.y,paint);
				}
		}
		catch(Throwable t)
		{}
		if(useNet)
		{
			paint.setColor(0xff000000);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(1);
			for(float i=deltax*scale;i<(vec.width+deltax)*scale&&vec!=null;i+=vec.dp*scale)
				if(i<0)continue;
				else if(i>surface.getWidth())break;
				else canvas.drawLine(i,deltay*scale,i,(vec.height+deltay)*scale,paint);
			for(float j=deltay*scale;j<(vec.height+deltay)*scale&&vec!=null;j+=vec.dp*scale)
				if(j<0)continue;
				else if(j>surface.getHeight())break;
				else canvas.drawLine(deltax*scale,j,(vec.width+deltax)*scale,j,paint);
		}
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(1);
		paint.setColor(0xffe00000);
		float pcx=(cx*vec.dp+deltax)*scale,pcy=(cy*vec.dp+deltay)*scale;
		float hcx=(vec.width-cx*vec.dp+deltax)*scale,hcy=(vec.height-cy*vec.dp+deltay)*scale;
		canvas.drawLines(new float[]{
							 0,pcy,surface.getWidth(),pcy,
							 pcx,0,pcx,surface.getHeight(),
							 0,hcy,surface.getWidth(),hcy,
							 hcx,0,hcx,surface.getHeight()
						 },paint);
		if(mview.size()>9)
		{
			if(undo.size()==0)((Button)mview.get(7)).color=MView.unavailablecolor;
			else ((Button)mview.get(7)).color=MView.buttoncolor;
			if(redo.size()==0)((Button)mview.get(8)).color=MView.unavailablecolor;
			else ((Button)mview.get(8)).color=MView.buttoncolor;
		}
		for(MView b:mview)b.onDraw(canvas);
		paint.setStyle(Paint.Style.FILL);
		paint.setTextAlign(Paint.Align.LEFT);
		canvas.drawText(String.format("x:%d,y:%d;cx:%d,cy:%d;dcx:%d,dcy:%d",cx,cy,(int)(cx-vec.width/2/vec.dp),(int)(cy-vec.height/2/vec.dp),cx-dcx,cy-dcy),0,paint.getTextSize()*3.2f,paint);
		canvas.drawText(String.format("图形数:%d;RAM:%d%s;FPS:%d;Ver:%d",vec.shapes.size(),ram,"%",fps,vec.version),0,paint.getTextSize()*4.4f,paint);
		paint.setTextAlign(Paint.Align.CENTER);
		String txt=info.toString();
		if(!"".equals(txt)){
			float w=paint.measureText(txt),cx=surface.getWidth()/2,cy=surface.getHeight()/2;
			paint.setColor(MView.menucolor);
			canvas.drawRect(cx-w/2,cy-paint.getTextSize()*1.3f,cx+w/2,cy,paint);
			paint.setColor(MView.textcolor);
			canvas.drawText(txt,cx,cy-paint.getTextSize()*0.3f,paint);
		}
		if(alpha>0)
		{
			useNet=false;
			m=new Matrix();
			if(alpha<150)
			{
				float k=Math.max(alpha,130);
				m.postScale(k/150f,k/150f);
				m.postTranslate(icon.getWidth()/2f*(1f-k/150f),icon.getHeight()/2f*(1f-k/150f));
			}
			if(alpha<100)
			{
				m.postTranslate(0,(alpha-100f)/100f*icon.getHeight());
				paint.setAlpha((int)(alpha*2.55f));
			}
			if(alpha==150)
			{
				new Thread(){
					@Override public void run()
					{
						try
						{
							initUi();
						}
						catch(Throwable e)
						{}
					}
				}.start();
			}
			canvas.drawBitmap(icon,m,paint);
			paint.setAlpha(255);
		}
	}
	public boolean touch(MotionEvent event)
	{
		try
		{
			setInfo();
			int a=event.getAction();
			if(event.getPointerCount()==1)
			{
				float x=event.getX(),y=event.getY();
				if(a==MotionEvent.ACTION_DOWN)
				{
					curView=null;
					moved=false;
					if(MODE==1)MODE=2;
					else if(MODE==2)MODE=0;
					else if(MODE==3)MODE=4;
					else if(MODE==4)MODE=0;
					else if(MODE==5)MODE=6;
					else if(MODE==6)MODE=0;
					else if(MODE==7)MODE=8;
					else if(MODE==8)MODE=0;
					else if(MODE==9)MODE=10;
					else if(MODE==10)MODE=0;
					if(tmpPoint!=null)tmpPoint2=new Point(tmpPoint);
					else tmpPoint2=null;
					for(int i=mview.size()-1;i!=-1;i--)
					{
						MView b=mview.get(i);
						if(b.contains(x,y))
						{
							if(b instanceof Menu&&!((Menu)b).show)continue;
							curView=b;
							break;
						}
					}
				}
				if(curView!=null)curView.onTouchEvent(event);
				else if(!moved)
				{
					cx=Math.round((x-deltax*scale)/(vec.dp*scale));
					cy=Math.round((y-deltay*scale)/(vec.dp*scale));
					if(a==MotionEvent.ACTION_DOWN){
						dcx=cx;
						dcy=cy;
						saveUndo();
					}
					if(MODE==2)
					{
						curColorView.color=vec.front.getPixel(Math.round(x/scale-deltax),Math.round(y/scale-deltay));
						setTmpShape();
					}
					else if(MODE==4&&tmpShape!=null&&tmpShape.hasFlag(Shape.TYPE.PATH))
					{
						Shape.PathPoint poi=new Shape.PathPoint(cx,cy);
						int size=tmpShape.pts.size();
						Point poi2=tmpShape.pts.get(size-1);
						if(size==1||!poi.equals(poi2))tmpShape.pts.add(poi);
					}
					else if(MODE==6)
						if(a==MotionEvent.ACTION_DOWN)tmpPoint3=new Point(cx,cy);
						else
						{
							colorShape.par[4]=cx-tmpPoint3.x;
							colorShape.par[5]=cy-tmpPoint3.y;
							setTmpShape();
						}
					else if(MODE==8)
					{
						if(a==MotionEvent.ACTION_DOWN)tmpPoint3=new Point(cx,cy);
						else if(tmpShape!=null)
						{
							boolean path=false;
							for(Point p:tmpShape.pts)
							{
								if(!path&&tmpShape.hasFlag(Shape.TYPE.PATH))
								{
									path=true;
									continue;
								}
								p.x+=cx-tmpPoint3.x;
								p.y+=cy-tmpPoint3.y;
							}
							tmpPoint3.x=cx;
							tmpPoint3.y=cy;
						}
					}
					else if(MODE==10)
						if(a==MotionEvent.ACTION_DOWN)tmpPoint3=new Point(cx,cy);
						else if(tmpShape!=null)
						{
							for(int ik=tmpShape.hasFlag(Shape.TYPE.PATH)?1:0;ik<tmpShape.pts.size();ik++)
							{
								Point pp=tmpShape.pts.get(ik);
								if(cx!=tmpPoint3.x)pp.x=(int)(vec.width/vec.dp)-pp.x;
								if(cy!=tmpPoint3.y)pp.y=(int)(vec.width/vec.dp)-pp.y;
							}
							if(!tmpPoint3.equals(cx,cy))MODE=0;
						}
					if(tmpPoint!=null)
					{
						tmpPoint.x=cx;
						tmpPoint.y=cy;
					}
				}
			}
			else if(event.getPointerCount()==2)
			{
				float x1=event.getX(1),y1=event.getY(1);
				float x=event.getX(0),y=event.getY(0);
				if(!moved)
				{
					ddx=(x+x1)/2;
					ddy=(y+y1)/2;
					lpointLen=(float)Math.sqrt(Math.pow(x-x1,2)+Math.pow(y-y1,2));
					lscale=scale;
					if(tmpPoint2!=null&&tmpPoint!=null)
					{
						tmpPoint.x=tmpPoint2.x;
						tmpPoint.y=tmpPoint2.y;
					}
					moved=true;
				}
				else
				{
					float pointLen=(float)Math.sqrt(Math.pow(x-x1,2)+Math.pow(y-y1,2));
					float llsc=scale;
					scale=lscale*pointLen/lpointLen;
					float cx=(x+x1)/2f,cy=(y+y1)/2f;
					deltax=(deltax-cx/llsc)+cx/scale;
					deltay=(deltay-cy/llsc)+cy/scale;
					deltax-=(ddx-(x+x1)/2)/scale;
					deltay-=(ddy-(y+y1)/2)/scale;
					ddx=(x+x1)/2;
					ddy=(y+y1)/2;
					setInfo((int)(scale*100f),"%");
				}
			}
			else return false;
		}
		catch(Throwable e)
		{
			check(e);
		}
		return true;
	}
	public void check(final Throwable e)
	{
		new Handler(ctx.getMainLooper()).post(new Runnable(){
				@Override
				public void run()
				{
					ByteArrayOutputStream o=new ByteArrayOutputStream();
					PrintStream p=new PrintStream(o);
					e.printStackTrace(p);
					p.close();
					new AlertDialog.Builder(ctx)
						.setMessage(o.toString())
						.show();
				}
			});
	}
	public void initPosition()
	{
		vec.createBackground();
		deltax=(surface.getWidth()-vec.width)/2;
		deltay=(surface.getHeight()-vec.height)/2;
		scale=1;
	}
	public void initUi()
	{
		mview.clear();
		final Button[] bu=new Button[27];
		bs=MView.dip(surface.getWidth()/9);
		final Menu[] me=new Menu[26];
		final Button.Event ev=new Button.Event(){
			@Override
			public void e(Button b)
			{
				if(b==bu[0])showMenu(me[24]);
				else if(b==bu[1])
				{
					Menu m=me[22];
					showMenu(m);
					listfile((List)m);
				}
				else if(b==bu[2])
				{
					Menu m=me[20];
					showMenu(m);
					List lis=(List)m.views.get(2);
					lis.show=true;
					((Edit)m.views.get(0)).txt=vec.name;
					listfile(lis);
				}
				else if(b==bu[3])
				{
					Menu m=me[23];
					showMenu(m);
					List lis=(List)m.views.get(2);
					lis.show=true;
					((Edit)m.views.get(0)).txt=vec.name;
					listfile(lis);
				}
				else if(b==bu[4])
				{
					Menu m=me[25];
					((Edit)m.views.get(1)).txt=vec.name;
					((Edit)m.views.get(3)).txt=vec.info;
					((Edit)m.views.get(5)).txt=vec.width+"";
					((Edit)m.views.get(7)).txt=vec.height+"";
					((Edit)m.views.get(9)).txt=vec.width/vec.dp+"";
					((SeekBar)m.views.get(14)).setProgress(vec.antialias?1:0);
					((SeekBar)m.views.get(16)).setProgress(vec.dither?1:0);
					showMenu(me[25]);
				}
				else if(b==bu[5])
				{
					List m=(List)me[19];
					showMenu(m);
					m.views.clear();
					int k=0;
					for(Shape s:vec.shapes)
					{
						m.addView(new ShapePreview(bs,0,bs*7,bs*3,k++,s,new Button.Event(){
										  @Override
										  public void e(Button b)
										  {
											  for(int j=0;j<9;j++)me[j].show=false;
											  tmpShape=((ShapePreview)b).sh;
											  colorShape.set(tmpShape);
											  long w=tmpShape.flag;
											  w|=Shape.TYPE.ALL;
											  w-=Shape.TYPE.ALL;
											  w=tmpShape.flag-w;
											  w/=Shape.TYPE.RECT;
											  int h=0;
											  while((w/=2l)>0)h++;
											  showMenu(me[h]);
											  me[19].show=false;
										  }
									  }));
					}
					m.measure();
				}
				else if(b==bu[6])useNet=!useNet;
				else if(b==bu[7])undo();
				else if(b==bu[8])redo();
				else
				{
					for(int i=0;i<9;i++)
						if(b==bu[9+i])
						{
							if(tmpShape==null)
							{
								tmpShape=new Shape(Shape.TYPE.RECT*(long)Math.pow(2,i));
								setTmpShape();
								pointIndex=0;
								showMenu(me[i]);
							}
							else
							{
								for(int j=0;j<9;j++)
								{
									me[j].show=false;
								}
								tmpShape=null;
							}
							break;
						}
					for(int i=9;i<18;i++)
						if(b==bu[9+i])
						{
							showMenu(me,i);
							break;
						}
				}
				for(int i=0;i<18;i++)
				{
					bu[i+9].color=me[i].show?MView.buttonselectedcolor:MView.buttoncolor;
				}
				int[] map=new int[]{24,22,20,23,25,19};
				for(int i=0;i<6;i++)
				{
					bu[i].color=me[map[i]].show?MView.buttonselectedcolor:MView.buttoncolor;
				}
				bu[6].color=useNet?MView.buttonselectedcolor:MView.buttoncolor;
			}
			public void undo()
			{
				if(undo.size()!=0)
				{
					CopyOnWriteArrayList<Shape> list=undo.get(undo.size()-1);
					undo.remove(undo.size()-1);
					CopyOnWriteArrayList<Shape> list2=new CopyOnWriteArrayList<Shape>();
					for(Shape s:vec.shapes)list2.add(new Shape(s));
					redo.add(list2);
					vec.shapes.clear();
					for(Shape s:list)vec.shapes.add(new Shape(s));
				}
			}
			public void redo()
			{
				if(redo.size()!=0)
				{
					CopyOnWriteArrayList<Shape> list=redo.get(redo.size()-1);
					redo.remove(redo.size()-1);
					CopyOnWriteArrayList<Shape> list2=new CopyOnWriteArrayList<Shape>();
					for(Shape s:vec.shapes)list2.add(new Shape(s));
					undo.add(list2);
					vec.shapes.clear();
					for(Shape s:list)vec.shapes.add(new Shape(s));
				}
			}
			public void listfile(final List m)
			{
				m.views.clear();
				File[] src=localFile.listFiles();
				if(src==null)src=new File[0];
				final File[] dst=new File[src.length+1];
				Arrays.sort(src,new Comparator<File>(){
						@Override
						public int compare(File p1, File p2)
						{
							// TODO: Implement this method
							return p1.getName().compareToIgnoreCase(p2.getName());
						}
					});
				int k=0;
				dst[k++]=new File("...");
				for(File f:src)if(f.isDirectory())dst[k++]=f;
				for(File f:src)if(f.isFile())dst[k++]=f;
				k=0;
				for(File ffs:dst)
				{
					final int p3=k++;
					m.addView(new FileButton(bs,0,bs*7,bs,ffs.getName(),ffs.isFile(),new Button.Event(){
									  @Override
									  public void e(Button b)
									  {
										  File f=dst[p3];
										  if(f.isDirectory())
										  {
											  localFile=f;
											  listfile(m);
										  }
										  else if(f.isFile())
										  {
											  if(m.parent==me[20]||m.parent==me[23])
											  {
												  String ss=f.getName();
												  ((Edit)((Menu)m.parent).views.get(0)).txt=ss.substring(0,ss.lastIndexOf("."));
											  }
											  else if(m==me[22])
											  {
												  try
												  {
													  VECfile v=null;
													  if(f.getName().toLowerCase().endsWith(".vec"))
														  v=VECfile.readFile(f.getAbsolutePath());
													  else if(f.getName().toLowerCase().endsWith(".xml"))
														  v=VECfile.readXml(f.getAbsolutePath());
													  if(v!=null)
													  {
														  vec=v;
														  builder.bgpath=vec.bgpath;
														  builder.backgcolor=vec.backgcolor;
														  m.show=false;
														  initPosition();
													  }
												  }
												  catch (IllegalStateException e)
												  {toast("不是VEC文件");}
												  catch (Exception e)
												  {
													  toast("打开失败"+e);
												  }
											  }
										  }
										  else
										  {
											  localFile=localFile.getParentFile();
											  listfile(m);
										  }
									  }
								  }));
				}
				m.measure();
			}
		};
		final Button.Event sv=new Button.Event(){
			@Override
			public void e(Button b)
			{
				saveUndo();
				for(int j=0;j<9;j++)
					if(b.parent==me[j])
					{
						Menu m=(Menu)b.parent;
						for(int i=0;i<m.views.size();i++)
						{
							MView sub=m.views.get(i);
							if(sub instanceof Button)((Button)sub).color=MView.buttoncolor;
							if(sub==b)
							{
								b.color=MView.buttonselectedcolor;
								if(
									((tmpShape.hasFlag(Shape.TYPE.RECT)||tmpShape.hasFlag(Shape.TYPE.OVAL)||tmpShape.hasFlag(Shape.TYPE.CIRCLE)||tmpShape.hasFlag(Shape.TYPE.LINE))&&i<2)||
									((tmpShape.hasFlag(Shape.TYPE.ROUNDRECT))&&i<3)||
									((tmpShape.hasFlag(Shape.TYPE.ARC))&&i<4)||
									((tmpShape.hasFlag(Shape.TYPE.POINT)||tmpShape.hasFlag(Shape.TYPE.TEXT))&&i<1))tmpPoint=tmpShape.pts.get(i);
								else if(i==4&&tmpShape.hasFlag(Shape.TYPE.ARC))
								{
									if(tmpShape.hasFlag(Shape.SHAPEPAR.CENTER))tmpShape.setFlag(0,Shape.SHAPEPAR.CENTER);
									else tmpShape.setFlag(Shape.SHAPEPAR.CENTER,Shape.SHAPEPAR.CENTER);
								}
								else if(tmpShape.hasFlag(Shape.TYPE.TEXT)&&i!=3)
								{
									if(i==2)
									{
										final EditText edit=new EditText(ctx);
										edit.setText(tmpShape.txt);
										new AlertDialog.Builder(ctx)
											.setTitle("设置文本(不支持换行)")
											.setView(edit)
											.setPositiveButton("确定",new DialogInterface.OnClickListener(){
												@Override
												public void onClick(DialogInterface p1, int p2)
												{
													tmpShape.txt=edit.getText()+"";
												}
											})
											.setNegativeButton("取消",null)
											.show();
									}
									else if(i<9)tmpShape.setFlag(Shape.TEXT.DEFAULT_TYPE*(long)Math.pow(2,i-4),Shape.TEXT.ALL_TYPEFACE);
									else if(i>8)tmpShape.setFlag(Shape.TEXT.LEFT*(long)Math.pow(2,i-9),Shape.TEXT.ALL_ALIGN);
								}
								else if(tmpShape.hasFlag(Shape.TYPE.PATH)&&i!=7)
								{
									if(i==0)pointIndex--;
									else if(i==1)pointIndex++;
									else if(i==2)tmpShape.pts.add(++pointIndex,new Shape.PathPoint(cx,cy));
									else if(i==3&&tmpShape.pts.size()!=1)tmpShape.pts.remove(tmpPoint);
									else if(i==4)
									{
										Point po=tmpShape.pts.get(0);
										if(po.x==0)po.x=1;
										else po.x=0;
									}
									else if(i==5)MODE=3;
									else if(i==6)
									{
										final EditText edit=new EditText(ctx);
										edit.setText(pointIndex+"");
										new AlertDialog.Builder(ctx)
											.setTitle("选择点(0:不选择,共"+(tmpShape.pts.size()-1)+"个点)")
											.setView(edit)
											.setPositiveButton("确定",new DialogInterface.OnClickListener(){
												@Override
												public void onClick(DialogInterface p1, int p2)
												{
													try
													{
														pointIndex=Integer.parseInt(edit.getText()+"");
														if(pointIndex<0)pointIndex=0;
														if(pointIndex>tmpShape.pts.size()-1)pointIndex=tmpShape.pts.size()-1;
														if(pointIndex<tmpShape.pts.size()&&pointIndex>0)
															tmpPoint=tmpShape.pts.get(pointIndex);
														else tmpPoint=null;
													}
													catch(Throwable e)
													{toast("数值非法");}
												}
											})
											.setNegativeButton("取消",null)
											.show();
									}
									else if(i==9)((Shape.PathPoint)tmpShape.pts.get(pointIndex)).type=0;
									else if(i==10)((Shape.PathPoint)tmpShape.pts.get(pointIndex)).type=1;
									else if(i==11)((Shape.PathPoint)tmpShape.pts.get(pointIndex)).type=2;
									if(pointIndex<0)pointIndex=0;
									if(pointIndex>tmpShape.pts.size()-1)pointIndex=tmpShape.pts.size()-1;
									if(pointIndex<tmpShape.pts.size()&&pointIndex>0)
										tmpPoint=tmpShape.pts.get(pointIndex);
									else tmpPoint=null;
									((Button)m.views.get(6)).txt=pointIndex+"";
								}
								else
								{
									if(!vec.shapes.contains(tmpShape))vec.addShape(tmpShape);
									pointIndex=0;
									tmpShape=null;
									tmpPoint=null;
									m.show=false;
								}
							}
						}
					}
				for(int j=9;j<me.length;j++)
					if(b.parent==me[j])
					{
						Menu m=(Menu)b.parent;
						for(int i=0;i<m.views.size();i++)
						{
							if(m.views.get(i)==b)
							{
								if(j==24)
								{
									if(i==7)
									{
										Intent intent =new Intent(Intent.ACTION_GET_CONTENT);
										intent.addCategory(Intent.CATEGORY_OPENABLE);
										intent.setType("image/*");
										ctx.startActivityForResult(intent,3);
									}
									else if(i==8)
									{
										showMenu(me[18]);
										((ColorPicker)me[18].views.get(0)).setIColor(builder);
									}
									else if(i==9)
									{
										builder.setWidth(Integer.parseInt(((Edit)m.views.get(1)).txt));
										builder.setHeight(Integer.parseInt(((Edit)m.views.get(3)).txt));
										builder.setDp(((float)builder.getWidth())/Float.parseFloat(((Edit)m.views.get(5)).txt));
										vec=builder.build();
										initPosition();
									}
								}
								else if(j==13)
								{
									if(i>1&&i<5)colorShape.setFlag(Shape.STROKE.BUTT*(long)Math.pow(2,i-2),Shape.STROKE.ALL_CAP);
									else if(i>4)colorShape.setFlag(Shape.STROKE.ROUND_JOIN*(long)Math.pow(2,i-5),Shape.STROKE.ALL_JOIN);
								}
								else if(j==14)
								{
									if(i==6||i==7)tmpPoint=tmpShape.linear.get(i-6);
									else if(i==8||i==9)tmpPoint=tmpShape.radial.get(i-8);
									else if(i==10)tmpPoint=tmpShape.sweep.get(0);
									else if(i>10&&i<14)
									{
										for(int ii=20;ii<23;ii++)
											((List)m.views.get(ii)).views.clear();
										int k=-1;
										float l=m.views.get(20).left;
										float w=m.views.get(20).width();
										float h=m.views.get(20).height()/8;
										for(Point p:tmpShape.linear)
											if(++k>=2)((List)m.views.get(20)).addView(new ShaderEntry(MView.dip(l),0,MView.dip(w),MView.dip(h),k,p,0,me[18]));
										k=-1;
										for(Point p:tmpShape.radial)
											if(++k>=2)((List)m.views.get(21)).addView(new ShaderEntry(MView.dip(l),0,MView.dip(w),MView.dip(h),k,p,1,me[18]));
										k=-1;
										for(Point p:tmpShape.sweep)
											if(++k>=1)((List)m.views.get(22)).addView(new ShaderEntry(MView.dip(l),0,MView.dip(w),MView.dip(h),k,p,2,me[18]));
										for(int ii=20;ii<23;ii++)
											((List)m.views.get(ii)).measure();	
										showMenu((List)m.views.get(i+9));
									}
									else if(i>=14&&i<=16)
									{
										tmpShape.setFlag(Shape.TILEMODE.L_CLAMP*(long)Math.pow(2,i-14),Shape.TILEMODE.L_ALL);
									}
									else if(i>=17&&i<=19)
									{
										tmpShape.setFlag(Shape.TILEMODE.R_CLAMP*(long)Math.pow(2,i-17),Shape.TILEMODE.R_ALL);
									}

								}
								else if(j==15)
								{
									if(i==0)
									{
										int index=vec.shapes.indexOf(tmpShape);
										if(index!=-1)
										{
											Shape sh=vec.shapes.get(index);
											vec.shapes.add(index,new Shape(sh));
										}
									}
									else if(i==1)
									{
										int index=vec.shapes.indexOf(tmpShape);
										if(index!=-1)
										{
											saveUndo();
											vec.shapes.remove(index);
											tmpShape=null;
											for(int jj=0;jj<9;jj++)me[jj].show=false;
											me[15].show=false;
										}
									}
									else if(i==2)MODE=7;
									else if(i==3&&tmpShape!=null)MODE=9;
									else if(i==4)
									{
										int index=vec.shapes.indexOf(tmpShape);
										if(index!=-1)
										{
											Shape sh=vec.shapes.remove(index);
											if(--index<0)index=0;
											vec.shapes.add(index,sh);
										}
									}
									else if(i==5)
									{
										int index=vec.shapes.indexOf(tmpShape);
										if(index!=-1)
										{
											Shape sh=vec.shapes.remove(index);
											if(++index>vec.shapes.size())index=vec.shapes.size();
											vec.shapes.add(index,sh);
										}
									}
								}
								else if(j==16)
								{
									if(i==6)
									{
										final EditText edit=new EditText(ctx);
										if(tmpShape!=null)colorShape.dashpoint=tmpShape.dashpoint;
										edit.setText(colorShape.dashpoint);
										new AlertDialog.Builder(ctx)
											.setTitle("虚线参数(实,虚,实,虚…)")
											.setView(edit)
											.setPositiveButton("确定",new DialogInterface.OnClickListener(){
												@Override
												public void onClick(DialogInterface p1, int p2)
												{
													colorShape.dashpoint=edit.getText()+"";
													setTmpShape();
												}
											})
											.setNegativeButton("取消",null)
											.show();
									}
								}
								else if(j==17)
								{
									if(tmpPoint!=null)
									{
										if(i==0)--tmpPoint.x;
										else if(i==1)--tmpPoint.y;
										else if(i==2)++tmpPoint.x;
										else if(i==3)++tmpPoint.y;
										cx=tmpPoint.x;
										cy=tmpPoint.y;
									}
									if(i==4)me[17].show=false;
								}
								else if(j==25)
								{
									if(i==11)
									{
										Intent intent =new Intent(Intent.ACTION_GET_CONTENT);
										intent.addCategory(Intent.CATEGORY_OPENABLE);
										intent.setType("image/*");
										ctx.startActivityForResult(intent,3);
									}
									else if(i==12)
									{
										showMenu(me[18]);
										((ColorPicker)me[18].views.get(0)).setIColor(builder);
									}
									else if(i==17)
									{
										try
										{
											vec.name=((Edit)m.views.get(1)).txt;
											vec.info=((Edit)m.views.get(3)).txt;
											vec.antialias=((SeekBar)m.views.get(14)).getProgress()==0?false:true;
											vec.dither=((SeekBar)m.views.get(16)).getProgress()==0?false:true;
											vec.backgcolor=builder.backgcolor;
											vec.bgpath=builder.bgpath;
											vec.width=Integer.parseInt(((Edit)m.views.get(5)).txt);
											vec.height=Integer.parseInt(((Edit)m.views.get(7)).txt);
											vec.dp=(float)vec.width/Float.parseFloat(((Edit)m.views.get(9)).txt);
											initPosition();
										}
										catch(Throwable e)
										{}
									}	
								}
								else if(j==9)
								{
									if(i==0)
									{
										showMenu(me[18]);
										((ColorPicker)me[18].views.get(0)).setIColor(curColorView);
									}
									else if(i==1)MODE=1;
								}
								else if(j==10)colorShape.setFlag(Shape.STYLE.FILL*(long)Math.pow(2,i),Shape.STYLE.ALL);
								else if(j==11)
								{
									if(i<18)colorShape.setFlag(Shape.XFERMODE.CLEAR*(long)Math.pow(2,i),Shape.XFERMODE.ALL);
									else if(i==18)colorShape.setFlag(0,Shape.XFERMODE.ALL);
									else if(i==19)
									{
										boolean bo=colorShape.hasFlag(Shape.SHAPEPAR.NEWLAYER);
										b.color=bo?MView.buttoncolor:MView.buttonselectedcolor;
										colorShape.setFlag(bo?0:Shape.SHAPEPAR.NEWLAYER,Shape.SHAPEPAR.NEWLAYER);
									}
									else if(i==20)
									{
										boolean bo=colorShape.hasFlag(Shape.SHAPEPAR.RESTORELAYER);
										b.color=bo?MView.buttoncolor:MView.buttonselectedcolor;
										colorShape.setFlag(bo?0:Shape.SHAPEPAR.RESTORELAYER,Shape.SHAPEPAR.RESTORELAYER);
									}
								}
								else if(j==12&&i==0)MODE=5;
								else if(j==20&&i==1)
								{
									String f=localFile.getAbsolutePath()+"/"+((Edit)((Menu)b.parent).views.get(0)).txt+".vec";
									/*if(new File(f).exists())
									 {
									 me[20].showMenu;
									 ((Button)me[20].views.get(1)).txt="发现同名文件，是否替换？";
									 }
									 else*/
									vec.saveFile(f);
									toast("保存成功");
									me[20].show=false;
								}
								else if(j==23)
								{
									if(i==1)
									{
										String f=localFile.getAbsolutePath()+"/"+((Edit)((Menu)b.parent).views.get(0)).txt+".png";
										/*if(new File(f).exists())
										 {
										 me[20].showMenu;
										 ((Button)me[20].views.get(1)).txt="发现同名文件，是否替换？";
										 }
										 else*/
										vec.loadoutFile(f);
										MediaScannerConnection.scanFile(ctx,new String[]{f}, null,new MediaScannerConnection.OnScanCompletedListener() {
												@Override
												public void onScanCompleted(String path, Uri uri)
												{
													toast("扫描完毕，已加入相册");
												}
											});
										me[23].show=false;
									}
									else if(i==3)
									{
										String f=localFile.getAbsolutePath()+"/"+((Edit)((Menu)b.parent).views.get(0)).txt+".txt";
										/*if(new File(f).exists())
										 {
										 me[20].showMenu;
										 ((Button)me[20].views.get(1)).txt="发现同名文件，是否替换？";
										 }
										 else*/
										vec.loadoutTxtFile(f);
										toast("导出成功");
										me[23].show=false;
									}
									else if(i==4)
									{
										String f=localFile.getAbsolutePath()+"/"+((Edit)((Menu)b.parent).views.get(0)).txt+".xml";
										/*if(new File(f).exists())
										 {
										 me[20].showMenu;
										 ((Button)me[20].views.get(1)).txt="发现同名文件，是否替换？";
										 }
										 else*/
										vec.saveXml(f);
										toast("导出成功");
										me[23].show=false;
									}
								}
								setTmpShape();
							}
						}
					}
				ev.e(null);
			}
		};
		final FloatPicker.FloatPickerEvent fpe=new FloatPicker.FloatPickerEvent(){
			@Override
			public void onChange(FloatPicker b,float f)
			{
				for(int j=0;j<me.length;j++)
					if(b.parent==me[j])
					{
						Menu m=(Menu)b.parent;
						for(int i=0;i<m.views.size();i++)
						{
							if(m.views.get(i)==b)
							{
								if(j==8&&i==1&&tmpShape.hasFlag(Shape.TYPE.TEXT))
								{
									tmpShape.pts.get(1).x=(int)(f*100);
								}
								else if(j==5&&i==8&&tmpShape.hasFlag(Shape.TYPE.PATH))
								{
									tmpShape.pts.get(0).y=(int)(f*100);
								}
								else if(j==12)
								{
									colorShape.par[6]=(int)(f*100);
									setTmpShape();
								}
								else if(j==13)
								{
									if(i==0)colorShape.par[3]=(int)(f*100);
									else if(i==1)colorShape.par[2]=(int)(f*100);
									setTmpShape();
								}
								else if(j==16)
								{
									if(i==0)colorShape.par[9]=(int)(f*100);
									else if(i==1)colorShape.par[10]=(int)(f*100);
									else if(i==2)colorShape.par[11]=(int)(f*100);
									setTmpShape();
								}
							}
						}
					}
			}
		};
		final SeekBar.SeekBarEvent se=new SeekBar.SeekBarEvent(){
			@Override
			public void onChange(SeekBar b, int p)
			{
				for(int j=0;j<me.length;j++)
					if(b.parent==me[j])
					{
						Menu m=(Menu)b.parent;
						for(int i=0;i<m.views.size();i++)
						{
							if(m.views.get(i)==b)
							{
								if(j==14)
								{
									if(i==3)
										if(p==0)tmpShape.linear.clear();
										else
										{
											tmpShape.linear.add(new Point(0,0));
											tmpShape.linear.add(new Point(0,0));
											tmpShape.linear.add(new Point(0xffff0000,0));
											tmpShape.linear.add(new Point(0xff00ff00,100));
										}
									else if(i==4)
										if(p==0)tmpShape.radial.clear();
										else
										{
											tmpShape.radial.add(new Point(0,0));
											tmpShape.radial.add(new Point(0,0));
											tmpShape.radial.add(new Point(0xffff0000,0));
											tmpShape.radial.add(new Point(0xff00ff00,100));
										}
									else if(i==5)
										if(p==0)tmpShape.sweep.clear();
										else
										{
											tmpShape.sweep.add(new Point(0,0));
											tmpShape.sweep.add(new Point(0xffff0000,0));
											tmpShape.sweep.add(new Point(0xff00ff00,100));
										}
									//colorShape.createShader(0,0,vec.dp,1);
								}
								else if(j==16)
								{

								}
							}
						}
					}
			}
		};
		addView(
			new Button(0,0,bs,bs,"新建","new",ev),
			new Button(bs,0,bs,bs,"打开","open",ev),
			new Button(bs*2,0,bs,bs,"保存","save",ev),
			new Button(bs*3,0,bs,bs,"导出","loadout",ev),
			new Button(bs*5,0,bs,bs,"参数","params",ev),
			new Button(bs*4,0,bs,bs,"形状表","vecfile",ev),
			new Button(bs*6,0,bs,bs,"网格","net",ev),
			new Button(bs*7,0,bs,bs,"撤销","undo",ev),
			new Button(bs*8,0,bs,bs,"恢复","redo",ev),
			new Button(0,bs*2,bs,bs,"矩形","rect",ev),
			new Button(0,bs*3,bs,bs,"圆","circle",ev),
			new Button(0,bs*4,bs,bs,"椭圆","oval",ev),
			new Button(0,bs*5,bs,bs,"弧","arc",ev),
			new Button(0,bs*6,bs,bs,"圆角矩形","roundrect",ev),
			new Button(0,bs*7,bs,bs,"路径","path",ev),
			new Button(0,bs*8,bs,bs,"点","point",ev),
			new Button(0,bs*9,bs,bs,"直线","line",ev),
			new Button(0,bs*10,bs,bs,"文本","text",ev),
			new Button(bs*8,bs*2,bs,bs,"颜色","colorpicker",ev),
			new Button(bs*8,bs*3,bs,bs,"样式","style",ev),
			new Button(bs*8,bs*4,bs,bs,"XFERMODE","xfermode",ev),
			new Button(bs*8,bs*5,bs,bs,"阴影","shadow",ev),
			new Button(bs*8,bs*6,bs,bs,"线样式","strokestyle",ev),
			new Button(bs*8,bs*7,bs,bs,"着色器","shader",ev),
			new Button(bs*8,bs*8,bs,bs,"形状设置","shapeedit",ev),
			new Button(bs*8,bs*9,bs,bs,"线特效","patheffect",ev),
			new Button(bs*8,bs*10,bs,bs,"小键盘","dpad",ev));
		addView(
			new Menu(bs,bs*14,bs*3,bs*1,
					 new Button(bs,bs*14,bs,bs,"点1",sv),
					 new Button(bs*2,bs*14,bs,bs,"点2",sv),
					 new Button(bs*3,bs*14,bs,bs,"确定",sv)),
			new Menu(bs,bs*14,bs*3,bs*1,
					 new Button(bs,bs*14,bs,bs,"圆心",sv),
					 new Button(bs*2,bs*14,bs,bs,"半径",sv),
					 new Button(bs*3,bs*14,bs,bs,"确定",sv)),
			new Menu(bs,bs*14,bs*3,bs*1,
					 new Button(bs,bs*14,bs,bs,"点1",sv),
					 new Button(bs*2,bs*14,bs,bs,"点2",sv),
					 new Button(bs*3,bs*14,bs,bs,"确定",sv)),
			new Menu(bs,bs*14,bs*6,bs*1,
					 new Button(bs,bs*14,bs,bs,"左上",sv),
					 new Button(bs*2,bs*14,bs,bs,"右下",sv),
					 new Button(bs*3,bs*14,bs,bs,"开始",sv),
					 new Button(bs*4,bs*14,bs,bs,"结束",sv),
					 new Button(bs*5,bs*14,bs,bs,"中心",sv),
					 new Button(bs*6,bs*14,bs,bs,"确定",sv)),
			new Menu(bs,bs*14,bs*4,bs*1,
					 new Button(bs,bs*14,bs,bs,"左上",sv),
					 new Button(bs*2,bs*14,bs,bs,"右下",sv),
					 new Button(bs*3,bs*14,bs,bs,"圆角",sv),
					 new Button(bs*4,bs*14,bs,bs,"确定",sv)),
			new Menu(bs,bs*11,bs*7,bs*4,
					 new Button(bs,bs*11,bs,bs,"上个",sv),
					 new Button(bs*2,bs*11,bs,bs,"下个",sv),
					 new Button(bs,bs*12,bs,bs,"添加",sv),
					 new Button(bs*2,bs*12,bs,bs,"删除",sv),
					 new Button(bs*3,bs*12,bs,bs,"封闭",sv),
					 new Button(bs,bs*14,bs,bs,"自由选点",sv),
					 new Button(bs*3,bs*11,bs,bs,"0",sv),
					 new Button(bs*2,bs*14,bs,bs,"确定",sv),
					 new FloatPicker(bs*4,bs*11,bs*4,fpe),
					 new Button(bs,bs*13,bs,bs,"普通点",sv),
					 new Button(bs*2,bs*13,bs,bs,"拐点",sv),
					 new Button(bs*3,bs*13,bs,bs,"起点",sv)
					 ),
			new Menu(bs,bs*14,bs*2,bs*1,
					 new Button(bs,bs*14,bs,bs,"点",sv),
					 new Button(bs*2,bs*14,bs,bs,"确定",sv)),
			new Menu(bs,bs*14,bs*3,bs*1,
					 new Button(bs,bs*14,bs,bs,"点1",sv),
					 new Button(bs*2,bs*14,bs,bs,"点2",sv),
					 new Button(bs*3,bs*14,bs,bs,"确定",sv)),
			new Menu(bs,bs*11,bs*7,bs*4,
					 new Button(bs,bs*11,bs,bs,"点",sv),
					 new FloatPicker(bs*4,bs*11,bs*4,fpe),
					 new Button(bs*2,bs*11,bs,bs,"文本",sv),
					 new Button(bs*3,bs*11,bs,bs,"确定",sv),
					 new Button(bs,bs*12,bs,bs,"默认",sv),
					 new Button(bs*2,bs*12,bs,bs,"加粗",sv),
					 new Button(bs*3,bs*12,bs,bs,"等宽",sv),
					 new Button(bs,bs*13,bs,bs,"sans",sv),
					 new Button(bs*2,bs*13,bs,bs,"serif",sv),
					 new Button(bs,bs*14,bs,bs,"左对齐",sv),
					 new Button(bs*2,bs*14,bs,bs,"居中",sv),
					 new Button(bs*3,bs*14,bs,bs,"右对齐",sv)),
			new Menu(bs,bs*14,bs*2,bs*1,
					 new Button(bs,bs*14,bs,bs,"调色","colorpicker",sv),
					 new Button(bs*2,bs*14,bs,bs,"取色","pickcolor",sv)),
			new Menu(bs,bs*14,bs*4,bs*1,
					 new Button(bs,bs*14,bs,bs,"填充","fill",sv),
					 new Button(bs*2,bs*14,bs,bs,"描线","stroke",sv),
					 new Button(bs*3,bs*14,bs,bs,"描线填充","strokefill",sv),
					 new Button(bs*4,bs*14,bs,bs,"填充描线","fillstroke",sv)),
			new Menu(bs,bs*12,bs*7,bs*3,
					 new Button(bs,bs*12,bs,bs,"add","xadd",sv),
					 new Button(bs*2,bs*12,bs,bs,"clear","xclear",sv),
					 new Button(bs*3,bs*12,bs,bs,"darken","xdarken",sv),
					 new Button(bs*4,bs*12,bs,bs,"dst","xdst",sv),
					 new Button(bs*5,bs*12,bs,bs,"dst_atop","xdstatop",sv),
					 new Button(bs*6,bs*12,bs,bs,"dst_in","xdstin",sv),
					 new Button(bs,bs*13,bs,bs,"dst_out","xdstout",sv),
					 new Button(bs*2,bs*13,bs,bs,"dst_over","xdstover",sv),
					 new Button(bs*3,bs*13,bs,bs,"lighten","xlighten",sv),
					 new Button(bs*4,bs*13,bs,bs,"multiply","xmultiply",sv),
					 new Button(bs*5,bs*13,bs,bs,"overlay","xoverlay",sv),
					 new Button(bs*6,bs*13,bs,bs,"screen","xscreen",sv),
					 new Button(bs,bs*14,bs,bs,"src","xsrc",sv),
					 new Button(bs*2,bs*14,bs,bs,"src_atop","xsrcatop",sv),
					 new Button(bs*3,bs*14,bs,bs,"src_in","xsrcin",sv),
					 new Button(bs*4,bs*14,bs,bs,"src_out","xsrcout",sv),
					 new Button(bs*5,bs*14,bs,bs,"src_over","xsrcover",sv),
					 new Button(bs*6,bs*14,bs,bs,"xor","xxor",sv),
					 new Button(bs*7,bs*12,bs,bs,"无","xnone",sv),
					 new Button(bs*7,bs*13,bs,bs,"存图层",sv),
					 new Button(bs*7,bs*14,bs,bs,"回图层",sv)
					 ),
			new Menu(bs,bs*11,bs*5,bs*4,
					 new Button(bs,bs*14,bs,bs,"偏移","delta",sv),
					 new FloatPicker(bs*2,bs*11,bs*4,fpe)),
			new Menu(bs,bs*11,bs*7,bs*4,
					 new FloatPicker(bs,bs*11,bs*3,fpe),
					 new FloatPicker(bs*5,bs*11,bs*3,fpe),
					 new Button(bs,bs*14,bs,bs,"无","capbutt",sv),
					 new Button(bs*2,bs*14,bs,bs,"圆","capround",sv),
					 new Button(bs*3,bs*14,bs,bs,"方","capsquare",sv),
					 new Button(bs*5,bs*14,bs,bs,"圆角","joinround",sv),
					 new Button(bs*6,bs*14,bs,bs,"锐角","joinmiter",sv),
					 new Button(bs*7,bs*14,bs,bs,"直线","joinbevel",sv)),
			new Menu(bs,bs*12,bs*9,bs*3,
					 new Button(bs,bs*12,bs,bs,"线性","shaderlinear",null),
					 new Button(bs,bs*13,bs,bs,"辐射","shaderradial",null),
					 new Button(bs,bs*14,bs,bs,"扫描","shadersweep",null),
					 new SeekBar(bs*2,bs*12,bs,bs,0,1,se),
					 new SeekBar(bs*2,bs*13,bs,bs,0,1,se),
					 new SeekBar(bs*2,bs*14,bs,bs,0,1,se),
					 new Button(bs*3,bs*12,bs,bs,"点1",sv),
					 new Button(bs*4,bs*12,bs,bs,"点2",sv),
					 new Button(bs*3,bs*13,bs,bs,"中心",sv),
					 new Button(bs*4,bs*13,bs,bs,"半径",sv),
					 new Button(bs*3,bs*14,bs,bs,"中心",sv),
					 new Button(bs*5,bs*12,bs,bs,"参数","params",sv),
					 new Button(bs*5,bs*13,bs,bs,"参数","params",sv),
					 new Button(bs*4,bs*14,bs,bs,"参数","params",sv),
					 new Button(bs*6,bs*12,bs,bs,"边缘","shaderlinearc",sv),
					 new Button(bs*7,bs*12,bs,bs,"镜像","shaderlinearm",sv),
					 new Button(bs*8,bs*12,bs,bs,"重复","shaderlinearr",sv),
					 new Button(bs*6,bs*13,bs,bs,"边缘","shaderlinearc",sv),
					 new Button(bs*7,bs*13,bs,bs,"镜像","shaderlinearm",sv),
					 new Button(bs*8,bs*13,bs,bs,"重复","shaderlinearr",sv),
					 new List(bs,bs*4,bs*7,bs*8),
					 new List(bs,bs*4,bs*7,bs*8),
					 new List(bs,bs*4,bs*7,bs*8)
					 ),
			new Menu(bs,bs*12,bs*2,bs*3,
					 new Button(bs,bs*12,bs,bs,"复制","copy",sv),
					 new Button(bs*2,bs*12,bs,bs,"删除","delete",sv),
					 new Button(bs,bs*13,bs,bs,"平移","delta",sv),
					 new Button(bs*2,bs*13,bs,bs,"镜像","mirror",sv),
					 new Button(bs,bs*14,bs,bs,"上层","tobottom",sv),
					 new Button(bs*2,bs*14,bs,bs,"下层","totop",sv)
					 ),
			new Menu(0,bs*11,bs*9,bs*4,
					 new FloatPicker(0,bs*11,bs*3,fpe),
					 new FloatPicker(bs*3,bs*11,bs*3,fpe),
					 new FloatPicker(bs*6,bs*11,bs*3,fpe),
					 new Button(bs,bs*14,bs,bs,"离散间距","discretea",sv),
					 new Button(bs*4,bs*14,bs,bs,"离散差","discreteb",sv),
					 new Button(bs*6,bs*14,bs,bs,"圆角半径","cornerradius",sv),
					 new Button(bs*8,bs*14,bs,bs,"虚线参数","patheffect",sv)
					 ),
			new Menu(bs*5,bs*12,bs*3,bs*3,
					 new Button(bs*5,bs*13,bs,bs,"←","dpadleft",sv),
					 new Button(bs*6,bs*12,bs,bs,"↑","dpadup",sv),
					 new Button(bs*7,bs*13,bs,bs,"→","dpadright",sv),
					 new Button(bs*6,bs*14,bs,bs,"↓","dpaddown",sv),
					 new Button(bs*6,bs*13,bs,bs,"●","ok",sv)),
			new Menu(bs,bs*3,bs*7,bs*7,
					 new ColorPicker(bs,bs*3,bs*7,bs*3),
					 new SeekBar(bs,bs*6,bs*7,bs,0,255),
					 new SeekBar(bs,bs*7,bs*7,bs,0,255),
					 new SeekBar(bs,bs*8,bs*7,bs,0,255),
					 new SeekBar(bs,bs*9,bs*7,bs,0,255)),
			new List(bs,bs*3,bs*7,bs*9),//shape
			new Menu(bs,bs*3,bs*7,bs*9,
					 new Edit(bs,bs*3,bs*6,bs,""),
					 new Button(bs*7,bs*3,bs,bs,"保存","save",sv),
					 new List(bs,bs*4,bs*7,bs*8)),
			new Menu(bs*2,bs*5,bs*5,bs*4,
					 new Button(bs*2,bs*5,bs*5,bs,"提示",null),
					 new Button(bs*2,bs*6,bs*5,bs*2,"",null),
					 new Button(bs*2,bs*8,bs*2,bs,"确定",null),
					 new Button(bs*5,bs*8,bs*2,bs,"取消",null)),
			new List(bs,bs*3,bs*7,bs*9),
			new Menu(bs,bs*3,bs*7,bs*9,
					 new Edit(bs,bs*3,bs*4,bs,""),
					 new Button(bs*7,bs*3,bs,bs,"导出png","image",sv),
					 new List(bs,bs*4,bs*7,bs*8),
					 new Button(bs*6,bs*3,bs,bs,"导出清单","file",sv),
					 new Button(bs*5,bs*3,bs,bs,"导出xml","xml",sv)),
			new Menu(bs*3,bs*5,bs*3,bs*5,
					 new Button(bs*3,bs*5,bs,bs,"宽",null),
					 new Edit(bs*4,bs*5,bs*2,bs,"384"),
					 new Button(bs*3,bs*6,bs,bs,"高",null),
					 new Edit(bs*4,bs*6,bs*2,bs,"384"),
					 new Button(bs*3,bs*7,bs,bs,"精度","dp",null),
					 new Edit(bs*4,bs*7,bs*2,bs,"20"),
					 new Button(bs*3,bs*8,bs,bs,"背景色",null),
					 new Button(bs*4,bs*8,bs,bs,"图片","image",sv),
					 new Button(bs*5,bs*8,bs,bs,"调色","colorpicker",sv),
					 new Button(bs*3,bs*9,bs*3,bs,"确定",sv)),
			new Menu(bs*2,bs*4,bs*5,bs*8,
					 new Button(bs*2,bs*4,bs,bs,"名称","name",null),
					 new Edit(bs*3,bs*4,bs*4,bs,""),
					 new Button(bs*2,bs*5,bs,bs,"描述","info",null),
					 new Edit(bs*3,bs*5,bs*4,bs,""),
					 new Button(bs*2,bs*6,bs,bs,"宽",null),
					 new Edit(bs*3,bs*6,bs*4,bs,""),
					 new Button(bs*2,bs*7,bs,bs,"高",null),
					 new Edit(bs*3,bs*7,bs*4,bs,""),
					 new Button(bs*2,bs*8,bs,bs,"精度","dp",null),
					 new Edit(bs*3,bs*8,bs*4,bs,""),
					 new Button(bs*2,bs*9,bs,bs,"背景色",null),
					 new Button(bs*3,bs*9,bs*2,bs,"图片","image",sv),
					 new Button(bs*5,bs*9,bs*2,bs,"调色","colorpicker",sv),
					 new Button(bs*2,bs*10,bs,bs,"抗锯齿","antialias",null),
					 new SeekBar(bs*3,bs*10,bs,bs,vec.antialias?1:0,1) ,
					 new Button(bs*5,bs*10,bs,bs,"防抖动",null),
					 new SeekBar(bs*6,bs*10,bs,bs,vec.dither?1:0,1) ,
					 new Button(bs*2,bs*11,bs*5,bs,"确定",sv))
		);
		final int[] color=new int[]{0xffff5555,0xff55ff55,0xff5555ff,0xffffff55,0xff55ffff,0xffff55ff,0xff888888,0xffffffff,0xff000000};
		final ColorView[] cbt=new ColorView[9];
		ColorView.Event ev2=new ColorView.Event(){
			@Override public void e(ColorView b,int p)
			{
				curColorView=b;
				if(me[12].show)colorShape.par[7]=b.color;
				else if(p==0)colorShape.par[0]=b.color;
				else if(p==1)colorShape.par[1]=b.color;
				for(ColorView bb:cbt)
					if(bb==b)bb.selected=true;
					else bb.selected=false;
				setTmpShape();
			}
		};
		for(int i=0;i<9;i++)
		{
			cbt[i]=new ColorView(bs*i,bs*15,bs,bs,ev2);
			cbt[i].color=color[i];
		}
		curColorView=cbt[0];
		curColorView.selected=true;
		addView(cbt);
		for(int i=0;i<bu.length;i++)bu[i]=(Button)mview.get(i);
		for(int i=0;i<me.length;i++)me[i]=(Menu)mview.get(i+27);
		ColorPicker pi=(ColorPicker)me[18].views.get(0);
		for(int i=1;i<5;i++)
		{
			pi.argb[i-1]=(SeekBar)me[18].views.get(i);
			pi.argb[i-1].e=pi;
		}
		addView(toast=new Toast(0,bs*16,bs*9,bs));
	}
	public void toast(String s)
	{
		if(toast!=null)toast.show(s);
		mview.remove(toast);
		mview.add(toast);
	}
	public void addView(MView... b)
	{
		for(MView bb:b)if(bb!=null)
			{
				mview.add(bb);
				if(bb instanceof Button||bb instanceof ColorView)
				{
					try
					{
						Thread.sleep(20);
					}
					catch (Exception e)
					{}
				}
			}
	}
	public void showMenu(Menu m)
	{
		m.show=!m.show;
		if(m.show)
		{
			mview.remove(m);
			mview.add(m);
		}
	}

	public void showMenu(Menu[] mm,int j)
	{
		Menu m=mm[j];
		if(tmpShape==null&&(j==14||j==15))
		{
			toast("未选择图形");
			m.show=false;
			return;
		}
		showMenu(m);
		if(j==10)
		{
			int k=0;
			for(MView v:m.views)
				((Button)v).color=colorShape.hasFlag(Shape.STYLE.FILL*(long)Math.pow(2,k++))?MView.buttonselectedcolor:MView.buttoncolor;
		}
		else if(j==11)
		{
			((Button)m.views.get(18)).color=colorShape.flag==((colorShape.flag|Shape.XFERMODE.ALL)-Shape.XFERMODE.ALL)?MView.buttonselectedcolor:MView.buttoncolor;
			((Button)m.views.get(19)).color=colorShape.hasFlag(Shape.SHAPEPAR.NEWLAYER)?MView.buttonselectedcolor:MView.buttoncolor;
			((Button)m.views.get(20)).color=colorShape.hasFlag(Shape.SHAPEPAR.RESTORELAYER)?MView.buttonselectedcolor:MView.buttoncolor;
			for(int i=0;i<18;i++)((Button)m.views.get(i)).color=colorShape.hasFlag(Shape.XFERMODE.CLEAR*(long)Math.pow(2,i))?MView.buttonselectedcolor:MView.buttoncolor;
		}
		else if(j==13)
			for(int i=2;i<8;i++)
				((Button)m.views.get(i)).color=colorShape.hasFlag(Shape.STROKE.BUTT*(long)Math.pow(2,i-2))?MView.buttonselectedcolor:MView.buttoncolor;
		else if(j==14)
		{
			((SeekBar)m.views.get(3)).setProgress(tmpShape.linear.size()==0?0:1);
			((SeekBar)m.views.get(4)).setProgress(tmpShape.radial.size()==0?0:1);
			((SeekBar)m.views.get(5)).setProgress(tmpShape.sweep.size()==0?0:1);
			if(!m.show)
			{
				for(int i=20;i<23;i++)
					((List)m.views.get(i)).show=false;
			}
		}
	}
	public void saveUndo()
	{
		redo.clear();
		CopyOnWriteArrayList<Shape> list=new CopyOnWriteArrayList<Shape>(),last=null;
		if(undo.size()>0)last=undo.get(undo.size()-1);
		boolean mod=last==null||vec.shapes.size()!=last.size();
		for(int i=0;!mod&&i<last.size();i++)
			if(!vec.shapes.get(i).equals(last.get(i))){
				mod=true;
				break;
			}
		if(mod)
		{
			for(Shape s:vec.shapes)list.add(new Shape(s));
			undo.add(list);
		}
	}
	public void setTmpShape()
	{
		if(tmpShape!=null)tmpShape.set(colorShape);
	}
	public void setInfo(Object... o)
	{
		info.delete(0,info.length());
		for(Object c:o)info.append(c);
	}
	@Override
	public CharSequence getTextBeforeCursor(int p1, int p2)
	{
		return null;
	}
	@Override
	public CharSequence getTextAfterCursor(int p1, int p2)
	{
		return null;
	}
	@Override
	public CharSequence getSelectedText(int p1)
	{
		return null;
	}
	@Override
	public int getCursorCapsMode(int p1)
	{
		return 0;
	}
	@Override
	public ExtractedText getExtractedText(ExtractedTextRequest p1, int p2)
	{
		return null;
	}
	@Override
	public boolean deleteSurroundingText(int p1, int p2)
	{
		return false;
	}
	@Override
	public boolean setComposingText(CharSequence p1, int p2)
	{
		return false;
	}
	@Override
	public boolean setComposingRegion(int p1, int p2)
	{
		return false;
	}
	@Override
	public boolean finishComposingText()
	{
		return false;
	}
	@Override
	public boolean commitText(CharSequence p1, int p2)
	{
		if(curView!=null&&curView instanceof Edit)((Edit)curView).txt+=p1.toString();
		return false;
	}
	@Override
	public boolean commitCompletion(CompletionInfo p1)
	{
		return false;
	}
	@Override
	public boolean commitCorrection(CorrectionInfo p1)
	{
		return false;
	}
	@Override
	public boolean setSelection(int p1, int p2)
	{
		return false;
	}
	@Override
	public boolean performEditorAction(int p1)
	{
		return false;
	}
	@Override
	public boolean performContextMenuAction(int p1)
	{
		return false;
	}
	@Override
	public boolean beginBatchEdit()
	{
		return false;
	}
	@Override
	public boolean endBatchEdit()
	{
		return false;
	}
	@Override
	public boolean sendKeyEvent(KeyEvent event)
	{
		try
		{
			if (event.getAction() == KeyEvent.ACTION_DOWN&&curView!=null&&curView instanceof Edit)
			{
				if (event.getKeyCode() == KeyEvent.KEYCODE_DEL)
				{
					String t=((Edit)curView).txt;
					if(t.length()>0)t=t.substring(0,t.length()-1);
					((Edit)curView).txt=t;
				}
			}
			return true;
		}
		catch(Throwable e)
		{
		}
		return false;
	}
	@Override
	public boolean clearMetaKeyStates(int p1)
	{
		return false;
	}
	@Override
	public boolean reportFullscreenMode(boolean p1)
	{
		return false;
	}
	@Override
	public boolean performPrivateCommand(String p1, Bundle p2)
	{
		return false;
	}
	@Override
	public boolean requestCursorUpdates(int p1)
	{
		return false;
	}
	public void showIME()
	{
		surface.setFocusableInTouchMode(true);
		InputMethodManager imm = (InputMethodManager)ctx
			.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(surface,InputMethodManager.SHOW_IMPLICIT);

	}
	public String getStr(int id,Object... f)
	{
		return ctx.getResources().getString(id,f);
	}
}
