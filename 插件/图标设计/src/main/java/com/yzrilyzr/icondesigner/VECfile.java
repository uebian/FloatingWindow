package com.yzrilyzr.icondesigner;

import java.io.*;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import com.yzrilyzr.icondesigner.Shape;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.Attributes;
import android.graphics.Color;
import java.math.BigInteger;

public class VECfile
{
	public int width,height;//图像大小
	public float dp;//精度
	int backgcolor=0;
	public CopyOnWriteArrayList<Shape> shapes=new CopyOnWriteArrayList<Shape>();
	public Paint sp=new Paint();//形状
	String name="",info="",bgpath=null;
	public Bitmap front=null,back=null,front2=null;
	Canvas can,can2;
	public boolean antialias=true,dither=false,which=false,lock=false;
	Shape tmpShape;
	byte version=3;
	boolean MD=false;
	public VECfile(int width,int height,float dp,String b)
	{
		bgpath=b;
		this.width=width;
		this.height=height;
		this.dp=dp;
		bgpath=b;
	}

	public void setMD(boolean mD)
	{
		MD = mD;
	}
	public void setWidth(int width)
	{
		this.width = width;
	}

	public int getWidth()
	{
		return width;
	}

	public void setHeight(int height)
	{
		this.height = height;
	}

	public int getHeight()
	{
		return height;
	}

	public void setDp(float dp)
	{
		this.dp = dp;
	}

	public float getDp()
	{
		return dp;
	}

	public void setBackgcolor(int backgcolor)
	{
		this.backgcolor = backgcolor;
	}

	public int getBackgcolor()
	{
		return backgcolor;
	}

	public void setShapes(CopyOnWriteArrayList<Shape> shapes)
	{
		this.shapes = shapes;
	}

	public CopyOnWriteArrayList<Shape> getShapes()
	{
		return shapes;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public void setInfo(String info)
	{
		this.info = info;
	}

	public String getInfo()
	{
		return info;
	}

	public void setBgpath(String bgpath)
	{
		this.bgpath = bgpath;
	}

	public String getBgpath()
	{
		return bgpath;
	}

	public void setAntialias(boolean antialias)
	{
		this.antialias = antialias;
	}

	public boolean isAntialias()
	{
		return antialias;
	}

	public void setDither(boolean dither)
	{
		this.dither = dither;
	}

	public boolean isDither()
	{
		return dither;
	}

	public void loadoutTxtFile(String f)
	{
		try
		{
			PrintWriter os=new PrintWriter(new FileOutputStream(f));
			os.println("VEC 版本:1");
			os.println("文件名:"+name+";描述:"+info);
			os.println("宽:"+width+";高:"+height+";精度:"+(int)(width/dp));
			os.println("抗锯齿:"+antialias+";防抖动:"+dither);
			os.println("背景色:"+Integer.toHexString(backgcolor));
			os.println("图形个数:"+shapes.size());
			os.println("");
			for(Shape s:shapes)
			{
				/*	public Shader shader=null;
				 public PathEffect pathEffect=null;*/
				os.println("标识:"+getFlagName(s));
				if(s.hasFlag(Shape.TYPE.TEXT))os.println("图形文字:"+s.txt);
				os.println("点个数:"+s.pts.size());
				boolean bool=false;
				if(s.hasFlag(Shape.TYPE.PATH))
					for(Point po:s.pts)
					{
						if(bool)
						{
							int type=((Shape.PathPoint)po).type;
							String[] k=new String[]{"普通点","拐点","起点"};
							os.println(po.x+"  "+po.y+"  type:"+k[type]);
						}
						else os.println(po.x+"  "+po.y);
						bool=true;
					}
				else 
					for(Point po:s.pts)
					{
						os.println(po.x+"  "+po.y);
					}
				String[] k=new String[]{"颜色","描线颜色","锐角","描线粗细","阴影偏移x","阴影偏移y","阴影半径","阴影颜色"};
				for(int i=0;i<s.par.length;i++)
				{
					if(i==0||i==1||i==7)os.println(k[i]+":"+Integer.toHexString(s.par[i]));
					else os.println(k[i]+":"+s.par[i]);
				}
				os.println("");
			}
			os.println("背景图片目录:"+bgpath);
			os.flush();
			os.close();
		}
		catch (Exception e)
		{}
	}

	private String getFlagName(Shape s)
	{
		StringBuilder b=new StringBuilder();
		String[] t=Long.toBinaryString(s.flag).split("");
		String[] n=new String[]{
			"左对齐","居中","右对齐","默认","粗体",
			"MONOSPACE","SANS_SERIF","SERIF",
			"无线帽","圆帽","方帽","圆拐角","锐角",
			"直线","填充","描线","填充描线","描线填充",
			"CLEAR",
			"DARKEN",
			"DST",
			"DST_ATOP",
			"DST_IN",
			"DST_OUT",
			"DST_OVER",
			"LIGHTEN",
			"MULTIPLY",
			"OVERLAY",
			"SCREEN",
			"SRC",
			"SRC_ATOP",
			"SRC_IN",
			"SRC_OUT",
			"SRC_OVER",
			"XOR",
			"ADD",
			"新图层","回图层","中心","封闭","扫描",
			"辐射","线性","虚线","离散","圆角","组合",
			"矩形","圆形","椭圆","弧","圆角矩形","路径",
			"点","直线","文本"};
		for(int i=t.length-1;i>=0;i--)
			if("1".equals(t[i]))
			{
				b.append(n[t.length-i-1]);
				b.append(" ");
			}
		return b.toString();
	}
	public void addShape(Shape s)
	{
		if(s!=null)shapes.add(s);
	}
	public void createBackground()
	{
		Paint pp=new Paint();
		Bitmap b2=back,f2=front,c2=front2;
		back=Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
		front=Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
		front2=Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
		Canvas c=new Canvas(back);
		can=new Canvas(front);
		can2=new Canvas(front2);
		if(b2!=null)b2.recycle();
		if(f2!=null)f2.recycle();
		if(c2!=null)c2.recycle();
		boolean grey=false;
		int w=width/25;
		if(w<=0)w=1;
		for(int i=0;i<width;i+=w)
		{
			grey=!grey;
			boolean g2=grey;
			for(int j=0;j<height;j+=w)
			{
				g2=!g2;
				pp.setColor(g2?0xffffffff:0xffaaaaaa);
				c.drawRect(i,j,i+w,j+w,pp);
			}
		}
		if(bgpath!=null)
		{
			Bitmap bb=BitmapFactory.decodeFile(bgpath);
			if(bb!=null)
			{
				c.drawBitmap(bb,0,0,pp);
				bb.recycle();
			}
		}
	}
	public void recycle()
	{
		if(back!=null)back.recycle();
		if(front!=null)front.recycle();
		if(front2!=null)front2.recycle();
	}
	public VECfile()
	{}
	public void onDraw()
	{
		if(!lock)
		{
			which=!which;
			checkIfNull();
			if(which)
			{
				sp.reset();
				can.drawBitmap(back,0,0,sp);
				can.saveLayer(0,0,can.getWidth(),can.getHeight(),sp,Canvas.ALL_SAVE_FLAG);
				can.drawColor(backgcolor);
				for(Shape s:shapes)s.onDraw(can,MD,antialias,dither,0,0,1,dp,sp);
				if(tmpShape!=null&&!shapes.contains(tmpShape))tmpShape.onDraw(can,MD,antialias,dither,0,0,1,dp,sp);
				can.restoreToCount(1);
			}
			else
			{
				sp.reset();
				can2.drawBitmap(back,0,0,sp);
				can2.saveLayer(0,0,can.getWidth(),can.getHeight(),sp,Canvas.ALL_SAVE_FLAG);
				can2.drawColor(backgcolor);
				for(Shape s:shapes)s.onDraw(can2,MD,antialias,dither,0,0,1,dp,sp);
				if(tmpShape!=null&&!shapes.contains(tmpShape))tmpShape.onDraw(can2,MD,antialias,dither,0,0,1,dp,sp);
				can2.restoreToCount(1);
			}
		}
	}
	public Bitmap lock(Shape tmp)
	{
		tmpShape=tmp;
		lock=true;
		checkIfNull();
		return which?front2:front;
	}
	public void checkIfNull(){
		if(front==null||front2==null||back==null||
		front.isRecycled()||front2.isRecycled()||back.isRecycled())
		createBackground();
	}
	public void unlock()
	{
		lock=false;
	}
	public void loadoutFile(String f)
	{
		try
		{
			Bitmap bit=Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
			Canvas c=new Canvas(bit);
			c.drawColor(backgcolor);
			Paint pp=new Paint();
			for(Shape s:shapes)s.onDraw(c,MD,antialias,dither,0,0,1,dp,pp);
			BufferedOutputStream os=new BufferedOutputStream(new FileOutputStream(f));
			bit.compress(Bitmap.CompressFormat.PNG,0,os);
			os.close();
			bit.recycle();
		}
		catch (Exception e)
		{}
	}
	public static Bitmap createBitmap(VECfile vv,int width,int height) throws IllegalStateException
	{
		Bitmap b=Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
		Canvas can=new Canvas(b);
		can.drawColor(vv.backgcolor);
		for(Shape s:vv.shapes)
			s.onDraw(can,vv.MD,vv.antialias,vv.dither,0,0,(float)width/(float)vv.width,vv.dp,vv.sp);
		vv.recycle();
		vv.shapes.clear();
		return b;
	}
	public static Bitmap createBitmap(Context ctx,String assetPath,int width,int height)throws Exception
	{
		InputStream is=ctx.getAssets().open(assetPath+".vec");
		VECfile vec=VECfile.readFileFromIs(is);
		return createBitmap(vec,width,height);
	}
	public void saveFile(String path)
	{
		try
		{
			DataOutputStream os=new DataOutputStream(new FileOutputStream(path));
			os.writeBytes("VEC");
			os.writeByte(3);
			os.writeUTF(name);
			os.writeUTF(info);
			os.writeInt(width);
			os.writeInt(height);
			os.writeBoolean(antialias);
			os.writeBoolean(dither);
			os.writeInt(backgcolor);
			os.writeFloat(dp);
			os.writeInt(shapes.size());
			for(Shape s:shapes)
			{
				/*	public Shader shader=null;
				 public PathEffect pathEffect=null;*/
				os.writeLong(s.flag);
				if(s.hasFlag(Shape.TYPE.TEXT))os.writeUTF(s.txt);
				os.writeInt(s.pts.size());
				boolean bool=false;
				if(s.hasFlag(Shape.TYPE.PATH))
					for(Point po:s.pts)
					{
						os.writeInt(po.x);
						os.writeInt(po.y);
						if(bool)os.writeByte(((Shape.PathPoint)po).type);
						bool=true;
					}
				else 
					for(Point po:s.pts)
					{
						os.writeInt(po.x);
						os.writeInt(po.y);
					}
				for(int po:s.par)os.writeInt(po);
				os.writeUTF(s.dashpoint);
				os.writeInt(s.linear.size());
				os.writeInt(s.radial.size());
				os.writeInt(s.sweep.size());
				for(Point t:s.linear)
				{
					os.writeInt(t.x);
					os.writeInt(t.y);
				}
				for(Point t:s.radial)
				{
					os.writeInt(t.x);
					os.writeInt(t.y);
				}
				for(Point t:s.sweep)
				{
					os.writeInt(t.x);
					os.writeInt(t.y);
				}
			}
			os.writeUTF(bgpath);
			os.flush();
			os.close();
		}
		catch (Exception e)
		{}
	}
	public static VECfile readFileFromIs(InputStream inp)throws IllegalStateException,IOException
	{
		VECfile v=new VECfile();
		DataInputStream os=new DataInputStream(inp);
		byte[] h=new byte[4];
		os.read(h);
		if(!new String(h,0,3).equals("VEC"))throw new IllegalStateException("不是标准的vec文件");
		else if(h[3]==1)return readV1(os);
		else if(h[3]==2)return readV2(os);
		else if(h[3]==v.version)
		{
			v.name=os.readUTF();
			v.info=os.readUTF();
			v.width=os.readInt();
			v.height=os.readInt();
			v.antialias=os.readBoolean();
			v.dither=os.readBoolean();
			v.backgcolor=os.readInt();
			v.dp=os.readFloat();
			v.shapes.clear();
			int siz=os.readInt();
			for(int i=0;i<siz;i++)
			{
				Shape s=new Shape(0);
				s.flag=os.readLong();
				if(s.hasFlag(Shape.TYPE.TEXT))s.txt=os.readUTF();
				int ptsl=os.readInt();
				s.pts.clear();
				if(s.hasFlag(Shape.TYPE.PATH))
					for(int u=0;u<ptsl;u++)
					{
						Shape.PathPoint po=new Shape.PathPoint();
						po.x=os.readInt();
						po.y=os.readInt();
						if(u!=0)po.type=os.readByte();
						s.pts.add(po);
					}
				else
					for(int u=0;u<ptsl;u++)
					{
						Point po=new Point();
						po.x=os.readInt();
						po.y=os.readInt();
						s.pts.add(po);
					}
				for(int u=0;u<12;u++)s.par[u]=os.readInt();
				s.dashpoint=os.readUTF();
				int li=os.readInt(),ra=os.readInt(),sw=os.readInt();
				for(int u=0;u<li;u++)
				{
					Point po=new Point();
					po.x=os.readInt();
					po.y=os.readInt();
					s.linear.add(po);
				}
				for(int u=0;u<ra;u++)
				{
					Point po=new Point();
					po.x=os.readInt();
					po.y=os.readInt();
					s.radial.add(po);
				}
				for(int u=0;u<sw;u++)
				{
					Point po=new Point();
					po.x=os.readInt();
					po.y=os.readInt();
					s.sweep.add(po);
				}
				v.shapes.add(s);
			}
			if(os.available()>0)v.bgpath=os.readUTF();
			os.close();
			return v;
		}
		else throw new IllegalStateException("不支持的vec文件");
	}
	public static VECfile readFile(String path)throws IllegalStateException,IOException
	{
		DataInputStream os=new DataInputStream(new FileInputStream(path));
		return readFileFromIs(os);
	}
	public static VECfile readV2(DataInputStream os)throws IllegalStateException,IOException
	{
		VECfile v=new VECfile();
		v.version=2;
		v.name=os.readUTF();
		v.info=os.readUTF();
		v.width=os.readInt();
		v.height=os.readInt();
		v.antialias=os.readBoolean();
		v.dither=os.readBoolean();
		v.backgcolor=os.readInt();
		v.dp=os.readFloat();
		v.shapes.clear();
		int siz=os.readInt();
		for(int i=0;i<siz;i++)
		{
			Shape s=new Shape(0);
			s.flag=os.readLong();
			if(s.hasFlag(Shape.TYPE.TEXT))s.txt=os.readUTF();
			int ptsl=os.readInt();
			s.pts.clear();
			if(s.hasFlag(Shape.TYPE.PATH))
				for(int u=0;u<ptsl;u++)
				{
					Shape.PathPoint po=new Shape.PathPoint();
					po.x=os.readInt();
					po.y=os.readInt();
					if(u!=0)po.type=os.readByte();
					s.pts.add(po);
				}
			else
				for(int u=0;u<ptsl;u++)
				{
					Point po=new Point();
					po.x=os.readInt();
					po.y=os.readInt();
					s.pts.add(po);
				}
			for(int u=0;u<8;u++)s.par[u]=os.readInt();
			int li=os.readInt(),ra=os.readInt(),sw=os.readInt();
			for(int u=0;u<li;u++)
			{
				Point po=new Point();
				po.x=os.readInt();
				po.y=os.readInt();
				s.linear.add(po);
			}
			for(int u=0;u<ra;u++)
			{
				Point po=new Point();
				po.x=os.readInt();
				po.y=os.readInt();
				s.radial.add(po);
			}
			for(int u=0;u<sw;u++)
			{
				Point po=new Point();
				po.x=os.readInt();
				po.y=os.readInt();
				s.sweep.add(po);
			}
			v.shapes.add(s);
		}
		if(os.available()>0)v.bgpath=os.readUTF();
		os.close();
		return v;
	}
	public static VECfile readV1(DataInputStream os)throws IllegalStateException,IOException
	{
		VECfile v=new VECfile();
		v.version=1;
		v.name=os.readUTF();
		v.info=os.readUTF();
		v.width=os.readInt();
		v.height=os.readInt();
		v.antialias=os.readBoolean();
		v.dither=os.readBoolean();
		v.backgcolor=os.readInt();
		v.dp=os.readFloat();
		v.shapes.clear();
		int siz=os.readInt();
		for(int i=0;i<siz;i++)
		{
			Shape s=new Shape(0);
			s.flag=os.readLong();
			if(s.hasFlag(Shape.TYPE.TEXT))s.txt=os.readUTF();
			int ptsl=os.readInt();
			s.pts.clear();
			if(s.hasFlag(Shape.TYPE.PATH))
				for(int u=0;u<ptsl;u++)
				{
					Shape.PathPoint po=new Shape.PathPoint();
					po.x=os.readInt();
					po.y=os.readInt();
					if(u!=0)po.type=os.readByte();
					s.pts.add(po);
				}
			else
				for(int u=0;u<ptsl;u++)
				{
					Point po=new Point();
					po.x=os.readInt();
					po.y=os.readInt();
					s.pts.add(po);
				}
			for(int u=0;u<8;u++)s.par[u]=os.readInt();
			v.shapes.add(s);
		}
		if(os.available()>0)v.bgpath=os.readUTF();
		os.close();
		return v;
	}
	public static VECfile readXmlFromIs(InputStream is) throws SAXException, ParserConfigurationException, IOException
	{
		final VECfile v=new VECfile();
		SAXParserFactory sf=SAXParserFactory.newInstance();
		SAXParser sp=sf.newSAXParser();
		DefaultHandler reader=new DefaultHandler(){
			Shape tmp=null;
			public void startElement(String uri,String localName,String qName,Attributes a) throws SAXException
			{
				if(a.getLength() >=0)
				{
					switch (qName)
					{
						case "VEC":
							v.version=Byte.parseByte(a.getValue("version"));
							//if("VEC")throw new IllegalStateException("不支持的vec文件");
							break;
						case "Data":
							v.name=a.getValue("name");
							v.info=a.getValue("info");
							v.width=Integer.parseInt(a.getValue("width"));
							v.height=Integer.parseInt(a.getValue("height"));
							v.antialias=Boolean.parseBoolean(a.getValue("antialias"));
							v.dither=Boolean.parseBoolean(a.getValue("dither"));
							v.backgcolor=new BigInteger(a.getValue("backgroundColor"),16).intValue();
							v.dp=Float.parseFloat(a.getValue("dp"));
							break;
						case "BackgroundImg":
							String p=a.getValue("path");
							if(!"null".equals(p))v.bgpath=p;
							break;
						case "Shape":
							tmp=new Shape(0);
							tmp.flag=Long.parseLong(a.getValue("flag"),2);
							if(tmp.hasFlag(Shape.TYPE.TEXT))tmp.txt=a.getValue("text");
							break;
						case "Points":
							tmp.pts.clear();
							break;
						case "P":
							if(tmp.hasFlag(Shape.TYPE.PATH))
							{
								Shape.PathPoint po=new Shape.PathPoint();
								po.x=Integer.parseInt(a.getValue("x"));
								po.y=Integer.parseInt(a.getValue("y"));
								String h=a.getValue("t");
								if(h!=null)po.type=Byte.parseByte(h);
								tmp.pts.add(po);
							}
							else
							{
								Point po=new Point();
								po.x=Integer.parseInt(a.getValue("x"));
								po.y=Integer.parseInt(a.getValue("y"));
								tmp.pts.add(po);
							}
							break;
						case "Params":
							for(int u=0;u<12&&a.getValue("p"+u)!=null;u++)tmp.par[u]=u==0||u==1||u==7?new BigInteger(a.getValue("p"+u),16).intValue():Integer.parseInt(a.getValue("p"+u));
							break;
						case "Linear":
							Point po=new Point();
							po.x=tmp.linear.size()>=2?new BigInteger(a.getValue("x"),16).intValue():Integer.parseInt(a.getValue("x"));
							po.y=Integer.parseInt(a.getValue("y"));
							tmp.linear.add(po);
							break;
						case "Radial":
							po=new Point();
							po.x=tmp.radial.size()>=2?new BigInteger(a.getValue("x"),16).intValue():Integer.parseInt(a.getValue("x"));
							po.y=Integer.parseInt(a.getValue("y"));
							tmp.radial.add(po);
							break;
						case "Sweep":
							po=new Point();
							po.x=tmp.sweep.size()>=1?new BigInteger(a.getValue("x"),16).intValue():Integer.parseInt(a.getValue("x"));
							po.y=Integer.parseInt(a.getValue("y"));
							tmp.sweep.add(po);
							break;
						case "Dash":
							String x=a.getValue("arr");
							tmp.dashpoint=x==null?"":x;
							break;
						default:
							break;
					}
				}
			}
			public void endElement(String uri,String localName,String qName) throws SAXException
			{
				switch (qName)
				{
					case "Shape":
						v.addShape(tmp);
						tmp=null;
						break;
				}
			}
		};
		sp.parse(is,reader);
		is.close();
		return v;
	}
	public static VECfile readXml(String path)throws IllegalStateException,IOException, SAXException, ParserConfigurationException
	{
		return readXmlFromIs(new BufferedInputStream(new FileInputStream(path)));
	}
	public void saveXml(String path)
	{
		SAXTransformerFactory sf = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
		ByteArrayOutputStream in =null;
		FileOutputStream os=null;
		try
		{
			TransformerHandler handler = sf.newTransformerHandler();
			Transformer transformer = handler.getTransformer();
			transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			in = new ByteArrayOutputStream();
			Result result = new StreamResult(in);
			handler.setResult(result);
			handler.startDocument();
			AttributesImpl a = new AttributesImpl();
			a.addAttribute("","","version","",""+version);
			handler.startElement("","","VEC",a);
			a.clear();
			a.addAttribute("","","name","",name);
			a.addAttribute("","","info","",info);
			a.addAttribute("","","width","",Integer.toString(width));
			a.addAttribute("","","height","",Integer.toString(height));
			a.addAttribute("","","antialias","",Boolean.toString(antialias));
			a.addAttribute("","","dither","",Boolean.toString(dither));
			a.addAttribute("","","backgroundColor","",Integer.toHexString(backgcolor));
			a.addAttribute("","","dp","",Float.toString(dp));
			handler.startElement("","","Data",a);
			handler.endElement("","","Data");
			a.clear();
			a.addAttribute("","","path","",bgpath==null?"null":bgpath);
			handler.startElement("","","BackgroundImg",a);
			handler.endElement("","","BackgroundImg");
			a.clear();
			for(Shape s:shapes)
			{
				a.addAttribute("","","flag","",Long.toBinaryString(s.flag));
				if(s.hasFlag(Shape.TYPE.TEXT))a.addAttribute("","","text","",s.txt);
				handler.startElement("","","Shape",a);
				a.clear();
				handler.startElement("","","Points",a);
				boolean bool=false;
				if(s.hasFlag(Shape.TYPE.PATH))
					for(Point po:s.pts)
					{
						a.addAttribute("","","x","",Integer.toString(po.x));
						a.addAttribute("","","y","",Integer.toString(po.y));
						if(bool)a.addAttribute("","","t","",Integer.toString(((Shape.PathPoint)po).type));
						handler.startElement("","","P",a);
						handler.endElement("","","P");
						a.clear();
						bool=true;
					}
				else 
					for(Point po:s.pts)
					{
						a.addAttribute("","","x","",Integer.toString(po.x));
						a.addAttribute("","","y","",Integer.toString(po.y));
						handler.startElement("","","P",a);
						handler.endElement("","","P");
						a.clear();
					}
				handler.endElement("","","Points");

				int f=-1;
				for(int po:s.par)a.addAttribute("","","p"+(++f),"",f==0||f==1||f==7?Integer.toHexString(po):Integer.toString(po));
				handler.startElement("","","Params",a);
				handler.endElement("","","Params");
				a.clear();
				a.addAttribute("","","arr","",s.dashpoint);
				handler.startElement("","","Dash",a);
				handler.endElement("","","Dash");
				a.clear();
				handler.startElement("","","Shader",a);
				f=0;
				for(Point t:s.linear)
				{
					if(f<2)a.addAttribute("","","x","",Integer.toString(t.x));
					else a.addAttribute("","","x","",Integer.toHexString(t.x));
					a.addAttribute("","","y","",Integer.toString(t.y));
					handler.startElement("","","Linear",a);
					handler.endElement("","","Linear");
					a.clear();
					f++;
				}
				f=0;
				for(Point t:s.radial)
				{
					if(f<2)a.addAttribute("","","x","",Integer.toString(t.x));
					else a.addAttribute("","","x","",Integer.toHexString(t.x));
					a.addAttribute("","","y","",Integer.toString(t.y));
					handler.startElement("","","Radial",a);
					handler.endElement("","","Radial");
					a.clear();
					f++;
				}
				f=0;
				for(Point t:s.sweep)
				{
					if(f<1)a.addAttribute("","","x","",Integer.toString(t.x));
					else a.addAttribute("","","x","",Integer.toHexString(t.x));
					a.addAttribute("","","y","",Integer.toString(t.y));
					handler.startElement("","","Sweep",a);
					handler.endElement("","","Sweep");
					a.clear();
					f++;
				}
				handler.endElement("","","Shader");
				handler.endElement("","","Shape");
			}
			handler.endElement("", "", "VEC");
			handler.endDocument();
			os=new FileOutputStream(path);
			in.writeTo(os);
			os.flush();
			os.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				in.close();
				os.close();
			}
			catch (IOException e)
			{
			}
		}
	}
	public static class Builder
	{
		int width=500,height=500;//图像大小
		float dp=20;//精度
		int backgcolor=0;
		String name="未命名",comm="请输入描述",bgpath=null;
		boolean antialias=true,dither=false;
		public void setWidth(int width)
		{
			this.width = width;
		}

		public int getWidth()
		{
			return width;
		}

		public void setHeight(int height)
		{
			this.height = height;
		}

		public int getHeight()
		{
			return height;
		}

		public void setDp(float dp)
		{
			this.dp = dp;
		}

		public float getDp()
		{
			return dp;
		}

		public void setBackgcolor(int backgcolor)
		{
			this.backgcolor = backgcolor;
		}

		public int getBackgcolor()
		{
			return backgcolor;
		}

		public void setName(String name)
		{
			this.name = name;
		}

		public String getName()
		{
			return name;
		}

		public void setComm(String comm)
		{
			this.comm = comm;
		}

		public String getComm()
		{
			return comm;
		}

		public void setBgpath(String bgpath)
		{
			this.bgpath = bgpath;
		}

		public String getBgpath()
		{
			return bgpath;
		}

		public void setAntialias(boolean antialias)
		{
			this.antialias = antialias;
		}

		public boolean isAntialias()
		{
			return antialias;
		}

		public void setDither(boolean dither)
		{
			this.dither = dither;
		}

		public boolean isDither()
		{
			return dither;
		}
		public VECfile build()
		{
			VECfile v=new VECfile(this.width,this.height,this.dp,this.bgpath);
			v.backgcolor=this.backgcolor;
			v.name=this.name;
			v.info=this.comm;
			v.antialias=this.antialias=false;
			v.dither=this.dither;
			return v;
		}
	}
}
