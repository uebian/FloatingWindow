package com.yzrilyzr.icondesigner;

import android.graphics.*;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public final class Shape
{
	public ArrayList<Point> pts=new ArrayList<Point>();
	public int[] par=new int[]{
		0xffff0000,
		0xff000000,
		0,
		100,
		0,
		0,
		0,
		0xff000000,
		0,
		0,
		0,
		0
	};
	//0:color,1:strokecolor,2:miter,3:strokewidth
	//4:shadowD.x,5:.y,6:shadowR,7:shadowcolor
	//8:dashPhase,9:segmentLength,10:deviation
	//11:radius
	public CopyOnWriteArrayList<Point> linear=new CopyOnWriteArrayList<Point>();
	public CopyOnWriteArrayList<Point> radial=new CopyOnWriteArrayList<Point>();
	public CopyOnWriteArrayList<Point> sweep=new CopyOnWriteArrayList<Point>();
	public String dashpoint="";
	public long flag=0;
	public String txt="";
	public void setColor(int color)
	{
		par[0]=color;
	}
	public int getColor()
	{
		return par[0];
	}
	public void setStrokeColor(int color)
	{
		par[1]=color;
	}
	public int getStrokeColor()
	{
		return par[1];
	}
	public static final class TEXT
	{
		public static final long
		LEFT=			0x1l,
		CENTER=			0x2l,
		RIGHT=			0x4l,
		DEFAULT_TYPE=	0x8l,
		BOLD_TYPE=		0x10l,
		MONOSPACE=		0x20l,
		SANS_SERIF=		0x40l,
		SERIF=			0x80l,
		ALL_ALIGN=LEFT+CENTER+RIGHT,
		ALL_TYPEFACE=DEFAULT_TYPE+BOLD_TYPE+MONOSPACE+SANS_SERIF+SERIF;
	}
	public static final class STROKE
	{
		public static final long
		BUTT=		0x100l,
		ROUND_CAP=	0x200l,
		SQUARE=		0x400l,
		ROUND_JOIN=	0x800l,
		MITER=		0x1000l,
		BEVEL=		0x2000l,
		ALL_CAP=BUTT+ROUND_CAP+SQUARE,
		ALL_JOIN=ROUND_JOIN+MITER+BEVEL;
	}
	public static final class STYLE
	{
		public static final long
		FILL=	0x4000l,
		STROKE=	0x8000l,
		FS=		0x10000l,
		SF=		0x20000l,
		ALL=FILL+STROKE+FS+SF;
	}
	public static final class XFERMODE
	{
		public static final long
		CLEAR=			0x40000l,
		DARKEN=			0x80000l,
		DST=			0x100000l,
		DST_ATOP=		0x200000l,
		DST_IN=			0x400000l,
		DST_OUT=		0x800000l,
		DST_OVER=		0x1000000l,
		LIGHTEN=		0x2000000l,
		MULTIPLY=		0x4000000l,
		OVERLAY=		0x8000000l,
		SCREEN=			0x10000000l,
		SRC=			0x20000000l,
		SRC_ATOP=		0x40000000l,
		SRC_IN=			0x80000000l,
		SRC_OUT=		0x100000000l,
		SRC_OVER=		0x200000000l,
		XOR=			0x400000000l,
		ADD=			0x800000000l,
		ALL=CLEAR+
		DARKEN+
		DST+
		DST_ATOP+
		DST_IN+
		DST_OUT+
		DST_OVER+
		LIGHTEN+
		MULTIPLY+
		OVERLAY+
		SCREEN+
		SRC+
		SRC_ATOP+
		SRC_IN+
		SRC_OUT+
		SRC_OVER+
		XOR+
		ADD;
	}
	public static final class SHAPEPAR
	{
		public static final long
		NEWLAYER=		0x1000000000l,
		RESTORELAYER=	0x2000000000l,
		CENTER=			0x4000000000l;
	}
	public static final class TYPE
	{
		public static final long
		RECT=		0x800000000000l,
		CIRCLE=		0x1000000000000l,
		OVAL=		0x2000000000000l,
		ARC=		0x4000000000000l,
		ROUNDRECT=	0x8000000000000l,
		PATH=		0x10000000000000l,
		POINT=		0x20000000000000l,
		LINE=		0x40000000000000l,
		TEXT=		0x80000000000000l,
		ALL=
		RECT+
		CIRCLE+
		OVAL+
		ARC+
		ROUNDRECT+
		PATH+
		POINT+
		LINE+
		TEXT;
	}
	public static final class TILEMODE
	{
		public static final long
		L_CLAMP=		0x100000000000000l,
		L_MIRROR=		0x200000000000000l,
		L_REPEAT=		0x400000000000000l,
		R_CLAMP=		0x800000000000000l,
		R_MIRROR=		0x1000000000000000l,
		R_REPEAT=		0x2000000000000000l,
		L_ALL=
		L_CLAMP+
		L_MIRROR+
		L_REPEAT,
		R_ALL=
		R_CLAMP+
		R_MIRROR+
		R_REPEAT
		;//62个
	}

	public boolean hasFlag(long f)
	{
		return (flag&f)==f;
	}
	public void setFlag(Shape src,long all)
	{
		long sr=src.flag;
		flag=(flag|all)-(sr|all)+sr;
	}
	public void setFlag(long f,long all)
	{
		flag=(flag|all)-all+f;
	}
	public final void onDraw(Canvas c,boolean md,boolean antia,boolean dither,float xx,float yy,float sc,float dp,Paint sp)
	{
		Shader shader=createShader(xx,yy,dp,sc);
		PathEffect pathEffect=createPathEffect(dp,sc);
		sp.reset();
		sp.setAntiAlias(antia);
		sp.setDither(dither);
		Paint.Cap cp=Paint.Cap.BUTT;
		if(hasFlag(STROKE.BUTT))cp=Paint.Cap.BUTT;
		else if(hasFlag(STROKE.SQUARE))cp=Paint.Cap.SQUARE;
		else if(hasFlag(STROKE.ROUND_CAP))cp=Paint.Cap.ROUND;
		sp.setStrokeCap(cp);

		Paint.Join jn=Paint.Join.MITER;
		if(hasFlag(STROKE.BEVEL))jn=Paint.Join.BEVEL;
		else if(hasFlag(STROKE.MITER))jn=Paint.Join.MITER;
		else if(hasFlag(STROKE.ROUND_JOIN))jn=Paint.Join.ROUND;
		sp.setStrokeJoin(jn);

		sp.setStrokeMiter((float)par[2]/100f*dp*sc);
		sp.setStrokeWidth((float)par[3]/100f*dp*sc);
		sp.setPathEffect(pathEffect);
		sp.setShader(shader);
		if(par[6]!=0)sp.setShadowLayer((float)par[6]/100f*dp*sc,par[4]*dp*sc,par[5]*dp*sc,par[7]);
		else sp.setShadowLayer(0,0,0,0);

		if(hasFlag(SHAPEPAR.NEWLAYER))c.saveLayer(0,0,c.getWidth(),c.getHeight(),sp,Canvas.ALL_SAVE_FLAG);
		PorterDuff.Mode xm=null;
		if(hasFlag(XFERMODE.ADD))xm=PorterDuff.Mode.ADD;
		else if(hasFlag(XFERMODE.CLEAR))xm=PorterDuff.Mode.CLEAR;
		else if(hasFlag(XFERMODE.DARKEN))xm=PorterDuff.Mode.DARKEN;
		else if(hasFlag(XFERMODE.DST))xm=PorterDuff.Mode.DST;
		else if(hasFlag(XFERMODE.DST_ATOP))xm=PorterDuff.Mode.DST_ATOP;
		else if(hasFlag(XFERMODE.DST_IN))xm=PorterDuff.Mode.DST_IN;
		else if(hasFlag(XFERMODE.DST_OUT))xm=PorterDuff.Mode.DST_OUT;
		else if(hasFlag(XFERMODE.DST_OVER))xm=PorterDuff.Mode.DST_OVER;
		else if(hasFlag(XFERMODE.LIGHTEN))xm=PorterDuff.Mode.LIGHTEN;
		else if(hasFlag(XFERMODE.MULTIPLY))xm=PorterDuff.Mode.MULTIPLY;
		else if(hasFlag(XFERMODE.OVERLAY))xm=PorterDuff.Mode.OVERLAY;
		else if(hasFlag(XFERMODE.SCREEN))xm=PorterDuff.Mode.SCREEN;
		else if(hasFlag(XFERMODE.SRC))xm=PorterDuff.Mode.SRC;
		else if(hasFlag(XFERMODE.SRC_ATOP))xm=PorterDuff.Mode.SRC_ATOP;
		else if(hasFlag(XFERMODE.SRC_IN))xm=PorterDuff.Mode.SRC_IN;
		else if(hasFlag(XFERMODE.SRC_OUT))xm=PorterDuff.Mode.SRC_OUT;
		else if(hasFlag(XFERMODE.SRC_OVER))xm=PorterDuff.Mode.SRC_OVER;
		else if(hasFlag(XFERMODE.XOR))xm=PorterDuff.Mode.XOR;
		if(xm!=null)sp.setXfermode(new PorterDuffXfermode(xm));
		if(hasFlag(TYPE.RECT))
		{
			Point p1=pts.get(0);
			Point p2=pts.get(1);
			RectF re=new RectF((p1.x*dp+xx)*sc,(p1.y*dp+yy)*sc,(p2.x*dp+xx)*sc,(p2.y*dp+yy)*sc);
			if(st())fill(sp);
			else
			{
				sf(sp);
				c.drawRect(re,sp);
				fs(sp);
			}
			c.drawRect(re,sp);
		}
		else if(hasFlag(TYPE.CIRCLE))
		{
			Point p1=pts.get(0);
			Point p2=pts.get(1);
			float r=(float)Math.sqrt(Math.pow(p2.x-p1.x,2)+Math.pow(p2.y-p1.y,2));
			if(st())fill(sp);
			else
			{
				sf(sp);
				c.drawCircle((p1.x*dp+xx)*sc,(p1.y*dp+yy)*sc,r*dp*sc,sp);
				fs(sp);
			}
			c.drawCircle((p1.x*dp+xx)*sc,(p1.y*dp+yy)*sc,r*dp*sc,sp);
		}
		else if(hasFlag(TYPE.OVAL))
		{
			Point p1=pts.get(0);
			Point p2=pts.get(1);
			RectF re=new RectF((p1.x*dp+xx)*sc,(p1.y*dp+yy)*sc,(p2.x*dp+xx)*sc,(p2.y*dp+yy)*sc);
			if(st())fill(sp);
			else
			{
				sf(sp);
				c.drawOval(re,sp);
				fs(sp);
			}
			c.drawOval(re,sp);
		}
		else if(hasFlag(TYPE.ARC))
		{
			Point p1=pts.get(0);
			Point p2=pts.get(1);
			Point p3=pts.get(2);
			Point p4=pts.get(3);
			float cx=(p1.x+p2.x)/2;
			float cy=(p1.y+p2.y)/2;
			float r1=(float)Math.sqrt(Math.pow((p3.x-cx),2)+Math.pow((p3.y-cy),2));
			float r2=(float)Math.sqrt(Math.pow((p4.x-cx),2)+Math.pow((p4.y-cy),2));
			float start=(float)(Math.asin((p3.y-cy)/r1)*180f/Math.PI);
			float end=(float)(Math.asin((p4.y-cy)/r2)*180f/Math.PI);
			if(p3.x-cx<0)start=180f-start;
			if(p3.x-cx>0&&p3.y-cy<0)start+=360f;
			if(p4.x-cx<0)end=180f-end;
			if(p4.x-cx>0&&p4.y-cy<0)end+=360f;
			if(start>end)end+=360f;
			RectF re=new RectF((p1.x*dp+xx)*sc,(p1.y*dp+yy)*sc,(p2.x*dp+xx)*sc,(p2.y*dp+yy)*sc);
			if(st())fill(sp);
			else
			{
				sf(sp);
				c.drawArc(re,start,end-start,hasFlag(SHAPEPAR.CENTER),sp);
				fs(sp);
			}
			c.drawArc(re,start,end-start,hasFlag(SHAPEPAR.CENTER),sp);
		}
		else if(hasFlag(TYPE.ROUNDRECT))
		{
			Point p1=pts.get(0);
			Point p2=pts.get(1);
			Point p3=pts.get(2);
			float r1=p3.x-p1.x;
			float r2=p3.y-p1.y;
			RectF re=new RectF((p1.x*dp+xx)*sc,(p1.y*dp+yy)*sc,(p2.x*dp+xx)*sc,(p2.y*dp+yy)*sc);
			if(st())fill(sp);
			else
			{
				sf(sp);
				c.drawRoundRect(re,r1*dp*sc,r2*dp*sc,sp);
				fs(sp);
			}
			c.drawRoundRect(re,r1*dp*sc,r2*dp*sc,sp);
		}
		else if(hasFlag(TYPE.POINT))
		{
			Point r=pts.get(0);
			if(st())fill(sp);
			else
			{
				sf(sp);
				c.drawPoint((r.x*dp+xx)*sc,(r.y*dp+yy)*sc,sp);
				fs(sp);
			}
			c.drawPoint((r.x*dp+xx)*sc,(r.y*dp+yy)*sc,sp);
		}
		else if(hasFlag(TYPE.LINE))
		{
			Point r1=pts.get(0),r2=pts.get(1);
			if(st())fill(sp);
			else
			{
				sf(sp);
				c.drawLine((r1.x*dp+xx)*sc,(r1.y*dp+yy)*sc,(r2.x*dp+xx)*sc,(r2.y*dp+yy)*sc,sp);
				fs(sp);
			}
			c.drawLine((r1.x*dp+xx)*sc,(r1.y*dp+yy)*sc,(r2.x*dp+xx)*sc,(r2.y*dp+yy)*sc,sp);
		}
		else if(hasFlag(TYPE.PATH))
		{
			Paint tp=new Paint();
			tp.setTextSize(16);
			Path pa=new Path();
			Point a=pts.get(0);
			if(pts.size()>1)
			{
				ArrayList<PointF> poi=new ArrayList<PointF>();
				for(int i=1;i<pts.size();i++)
				{
					Point t=pts.get(i);
					PointF pf=new PointF((t.x*dp+xx)*sc,(t.y*dp+yy)*sc);
					byte by=((PathPoint)t).type;
					if(by==1)
					{
						poi.add(pf);
						Catmull_Rom(poi,a.y,pa);
						if(a.x==1)pa.close();
						poi.clear();
						poi.add(pf);
					}
					else if(by==2||i==1)
					{
						Catmull_Rom(poi,a.y,pa);
						if(a.x==1)pa.close();
						poi.clear();
						poi.add(pf);
						pa.moveTo(pf.x,pf.y);
					}
					else poi.add(pf);
				}
				Catmull_Rom(poi,a.y,pa);
				poi.clear();poi=null;
				if(a.x==1)pa.close();
			}
			if(st())fill(sp);
			else
			{
				sf(sp);
				c.drawPath(pa,sp);
				fs(sp);
			}
			c.drawPath(pa,sp);
		}
		else if(hasFlag(TYPE.TEXT))
		{
			Point r=pts.get(0);
			Point t=pts.get(1);
			sp.setTextSize(t.x*dp*sc/100f);
			Typeface typefac=Typeface.DEFAULT;
			if(hasFlag(TEXT.DEFAULT_TYPE))typefac=Typeface.DEFAULT;
			else if(hasFlag(TEXT.BOLD_TYPE))typefac=Typeface.DEFAULT_BOLD;
			else if(hasFlag(TEXT.MONOSPACE))typefac=Typeface.MONOSPACE;
			else if(hasFlag(TEXT.SANS_SERIF))typefac=Typeface.SANS_SERIF;
			else if(hasFlag(TEXT.SERIF))typefac=Typeface.SERIF;
			sp.setTypeface(typefac);
			Paint.Align align=Paint.Align.LEFT;
			if(hasFlag(TEXT.LEFT))align=Paint.Align.LEFT;
			else if(hasFlag(TEXT.RIGHT))align=Paint.Align.RIGHT;
			else if(hasFlag(TEXT.CENTER))align=Paint.Align.CENTER;
			sp.setTextAlign(align);
			if(st())fill(sp);
			else
			{
				sf(sp);
				c.drawText(txt,(r.x*dp+xx)*sc,(r.y*dp+yy)*sc,sp);
				fs(sp);
			}
			c.drawText(txt,(r.x*dp+xx)*sc,(r.y*dp+yy)*sc,sp);
		}
		if(hasFlag(SHAPEPAR.RESTORELAYER))c.restore();
	}
	private boolean st()
	{
		return hasFlag(STYLE.FILL)||hasFlag(STYLE.STROKE);
	}
	private void fill(Paint sp)
	{
		sp.setColor(hasFlag(STYLE.FILL)?par[0]:par[1]);
		sp.setStyle(hasFlag(STYLE.FILL)?Paint.Style.FILL:Paint.Style.STROKE);
	}

	private void sf(Paint sp)
	{
		sp.setColor(hasFlag(STYLE.SF)?par[0]:par[1]);
		sp.setStyle(hasFlag(STYLE.SF)?Paint.Style.FILL:Paint.Style.STROKE);
	}

	private void fs(Paint sp)
	{
		sp.setColor(hasFlag(STYLE.FS)?par[0]:par[1]);
		sp.setStyle(hasFlag(STYLE.FS)?Paint.Style.FILL:Paint.Style.STROKE);
	}
	public Shape(long flag)
	{
		this.flag=flag;
		int len=0;
		if(hasFlag(TYPE.RECT)||hasFlag(TYPE.CIRCLE)||hasFlag(TYPE.OVAL)||hasFlag(TYPE.LINE)||hasFlag(TYPE.TEXT))len=2;
		else if(hasFlag(TYPE.ROUNDRECT)||hasFlag(TYPE.ARC))len=4;
		else if(hasFlag(TYPE.PATH)||hasFlag(TYPE.POINT))len=1;
		for(int i=0;i<len;i++)
			pts.add(new Point(0,0));
	}
	public Shape(Shape s)
	{
		this(s.flag);
		pts.clear();
		if(s.hasFlag(Shape.TYPE.PATH))
		{
			pts.add(new Point(s.pts.get(0)));
			for(int i=1;i<s.pts.size();i++)
				pts.add(new PathPoint((PathPoint)s.pts.get(i)));
		}
		else for(Point p:s.pts)
				pts.add(new Point(p));
		int k=0;
		for(int ii:s.par)
			par[k++]=ii;
		flag=s.flag;
		txt=s.txt;
	}
	public void set(Shape src)
	{
		//for(Point p:src.pts)pts.add(new Point(p.x,p.y));
		int i=0;
		for(int p:src.par)par[i++]=p;
		dashpoint=new String(src.dashpoint);
		setFlag(src,STYLE.ALL);
		setFlag(src,XFERMODE.ALL);
		setFlag(src,STROKE.ALL_CAP);
		setFlag(src,STROKE.ALL_JOIN);
		setFlag(src,SHAPEPAR.NEWLAYER+SHAPEPAR.RESTORELAYER);
	}
	@Override
	public boolean equals(Object o)
	{
		if(o!=null&&o instanceof Shape)
		{
			Shape s=(Shape)o;
			boolean e=s.flag==flag&&txt.equals(s.txt)&&pts.size()==s.pts.size();
			for(int i=0;e&&i<par.length;i++)
				if(par[i]!=s.par[i])e=false;
			for(int i=0;e&&i<pts.size();i++)
				if(!pts.get(i).equals(s.pts.get(i)))e=false;
			return e;
		}
		else return false;
	}
	private PathEffect createPathEffect(float dp,float sc)
	{
		PathEffect l=null,r=null,s=null,eff=null;
		if(!"".equals(dashpoint))
		{
			try
			{
				String[] d=dashpoint.split(",");
				float[] ps=new float[d.length];
				int k=0;
				for(String x:d)ps[k++]=Float.parseFloat(x)*dp*sc;
				l=new DashPathEffect(ps,(float)par[8]/100f*dp*sc);
			}
			catch(Throwable e)
			{}
		}
		if(par[9]!=0&&par[10]!=0)r=new DiscretePathEffect((float)par[9]/100f*dp*sc,(float)par[10]/100f*dp*sc);
		if(par[11]!=0)s=new CornerPathEffect((float)par[11]/100f*dp*sc);
		if(l==null)
			if(r==null)
				if(s==null)eff=null;
				else eff=s;
			else
			if(s==null)eff=r;
			else eff=new ComposePathEffect(r,s);
		else
		if(r==null)
			if(s==null)eff=l;
			else eff=new ComposePathEffect(l,s);
		else
		if(s==null)eff=new ComposePathEffect(l,r);
		else eff=new ComposePathEffect(
				new ComposePathEffect(l,r)
				,s);
		return eff;
	}
	private Shader createShader(float xx,float yy,float dp,float sc)
	{
		Shader l=null,r=null,s=null,shader=null;
		if(linear.size()!=0)
		{
			Point p1=linear.get(0),p2=linear.get(1);
			int[] c=new int[linear.size()-2];
			float[] p=new float[c.length];
			for(int i=0;i<c.length;i++)
			{
				Point f=linear.get(i+2);
				c[i]=f.x;
				p[i]=(float)f.y/100f;
			}
			Shader.TileMode m=Shader.TileMode.CLAMP;
			if(hasFlag(TILEMODE.L_CLAMP))m=Shader.TileMode.CLAMP;
			else if(hasFlag(TILEMODE.L_MIRROR))m=Shader.TileMode.MIRROR;
			else if(hasFlag(TILEMODE.L_REPEAT))m=Shader.TileMode.REPEAT;
			l=new LinearGradient((p1.x*dp+xx)*sc,(p1.y*dp+yy)*sc,(p2.x*dp+xx)*sc,(p2.y*dp+yy)*sc,c,p,m);
		}
		if(radial.size()!=0)
		{
			Point p1=radial.get(0),p2=radial.get(1);
			float rr=(float)Math.sqrt(Math.pow(p2.x-p1.x,2)+Math.pow(p2.y-p1.y,2));
			int[] c=new int[radial.size()-2];
			float[] p=new float[c.length];
			for(int i=0;i<c.length;i++)
			{
				Point f=radial.get(i+2);
				c[i]=f.x;
				p[i]=(float)f.y/100f;
			}
			Shader.TileMode m=Shader.TileMode.CLAMP;
			if(hasFlag(TILEMODE.R_CLAMP))m=Shader.TileMode.CLAMP;
			else if(hasFlag(TILEMODE.R_MIRROR))m=Shader.TileMode.MIRROR;
			else if(hasFlag(TILEMODE.R_REPEAT))m=Shader.TileMode.REPEAT;
			r=new RadialGradient((p1.x*dp+xx)*sc,(p1.y*dp+yy)*sc,rr*dp*sc,c,p,m);
		}
		if(sweep.size()!=0)
		{
			Point p1=sweep.get(0);
			int[] c=new int[sweep.size()-1];
			float[] p=new float[c.length];
			for(int i=0;i<c.length;i++)
			{
				Point f=sweep.get(i+1);
				c[i]=f.x;
				p[i]=(float)f.y/100f;
			}
			s=new SweepGradient((p1.x*dp+xx)*sc,(p1.y*dp+yy)*sc,c,p);
		}
		if(l==null)
			if(r==null)
				if(s==null)shader=null;
				else shader=s;
			else
			if(s==null)shader=r;
			else shader=new ComposeShader(r,s,PorterDuff.Mode.ADD);
		else
		if(r==null)
			if(s==null)shader=l;
			else shader=new ComposeShader(l,s,PorterDuff.Mode.ADD);
		else
		if(s==null)shader=new ComposeShader(l,r,PorterDuff.Mode.ADD);
		else shader=new ComposeShader(
				new ComposeShader(l,r,PorterDuff.Mode.ADD)
				,s,PorterDuff.Mode.ADD);
		return shader;
	}
	public static final void Catmull_Rom(ArrayList<PointF> point, int cha,Path path)
	{
		if(cha==0)for(PointF p:point)path.lineTo(p.x,p.y);
		else if (point.size()>1&&cha<10000)
		{
			point.add(0,point.get(0));
			point.add(point.get(point.size()-1));
			for (int index = 1; index < point.size() - 2; index++)
			{
				PointF p0 = point.get(index - 1);
				PointF p1 = point.get(index);
				PointF p2 = point.get(index + 1);
				PointF p3 = point.get(index + 2);
				for (int i = 1; i <= cha; i++)
				{
					float t = i * (1.00f / cha);
					float tt = t * t;
					float ttt = tt * t;
					float x = (float) (0.5 * (2 * p1.x + (p2.x - p0.x) * t + (2 * p0.x - 5 * p1.x + 4 * p2.x - p3.x) * tt + (3 * p1.x - p0.x - 3 * p2.x + p3.x)* ttt));
					float y = (float) (0.5 * (2 * p1.y + (p2.y - p0.y) * t + (2 * p0.y - 5 * p1.y + 4 * p2.y - p3.y) * tt + (3 * p1.y - p0.y - 3 * p2.y + p3.y)* ttt));
					path.lineTo(x,y);
				}
			}
			path.lineTo(point.get(point.size() - 1).x, point.get(point.size() - 1).y);
		}
	}
	public static final class PathPoint extends Point
	{
		public byte type=0;//normal:0,turn:1,start:2
		public PathPoint(int x,int y)
		{
			this(x,y,9);
		}
		public PathPoint(PathPoint p)
		{
			this(p.x,p.y,p.type);
		}
		public PathPoint(int x,int y,int type)
		{
			super(x,y);
			this.type=(byte)type;
		}
		public PathPoint()
		{
			this(0,0,0);
		}
	}
}
