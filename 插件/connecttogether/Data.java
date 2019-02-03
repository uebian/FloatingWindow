package com.yzrilyzr.connecttogether;
import com.yzrilyzr.connecttogether.data.Group;
import com.yzrilyzr.connecttogether.data.Message;
import com.yzrilyzr.connecttogether.data.User;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

public class Data
{
	public static final ConcurrentHashMap<Long,User> users=new ConcurrentHashMap<Long,User>();
	public static final ConcurrentHashMap<Long,Group> groups=new ConcurrentHashMap<Long,Group>();
	public static final CopyOnWriteArrayList<Message> messages=new CopyOnWriteArrayList<Message>();
	public static final ConcurrentHashMap<Long,Address> online=new ConcurrentHashMap<Long,Address>();
	public static final CopyOnWriteArrayList<Long> remove=new CopyOnWriteArrayList<Long>();
	public static final ConcurrentHashMap<Long,CopyOnWriteArrayList<Long>> addfriend=new ConcurrentHashMap<Long,CopyOnWriteArrayList<Long>>();
	public static final String DIR="/sdcard/yzr的app/CT_Server";
	public synchronized static void loadUser() throws Exception
	{
		File f=new File(DIR);
		if(!f.exists())f.mkdirs();
		if(!new File(DIR+"/users.xml").exists())saveUser();
		SAXParserFactory sf=SAXParserFactory.newInstance();
		SAXParser sp=sf.newSAXParser();
		DefaultHandler r=new DefaultHandler(){
			User tmp;
			@Override
			public void startElement(String uri,String localName,String qName,Attributes a) throws org.xml.sax.SAXException
			{
				if(a.getLength()>0)
				{
					switch(qName)
					{
						case "U":
							tmp=new User(0);
							tmp.id=Long.parseLong(a.getValue("i"));
							tmp.registerTime=Long.parseLong(a.getValue("t"));
							tmp.password=a.getValue("p");
							tmp.nick=a.getValue("n");
							tmp.signature=a.getValue("s");
							tmp.ip=a.getValue("o");
							users.put(tmp.id,tmp);
							break;
						case "F":
							if(tmp!=null)tmp.friends.add(Long.parseLong(a.getValue("i")));
							break;
						case "G":
							if(tmp!=null)tmp.groups.add(Long.parseLong(a.getValue("i")));
							break;
					}
				}
			}
		};
		BufferedInputStream in=new BufferedInputStream(new FileInputStream(DIR+"/users.xml"));
		sp.parse(in,r);
		in.close();
	}
	public synchronized static void saveUser()
	{
		File f=new File(DIR);
		if(!f.exists())f.mkdirs();
		SAXTransformerFactory sf=(SAXTransformerFactory)SAXTransformerFactory.newInstance();
		ByteArrayOutputStream is=null;
		FileOutputStream os=null;
		try
		{
			TransformerHandler h=sf.newTransformerHandler();
			Transformer tr=h.getTransformer();
			tr.setOutputProperty(OutputKeys.STANDALONE,"yes");
			tr.setOutputProperty(OutputKeys.ENCODING,"utf-8");
			tr.setOutputProperty(OutputKeys.INDENT,"yes");
			is=new ByteArrayOutputStream();
			Result r=new StreamResult(is);
			h.setResult(r);
			h.startDocument();
			AttributesImpl a=new AttributesImpl();
			h.startElement("","","Users",a);
			for(User u:users.values())
			{
				a.addAttribute("","","i","",Long.toString(u.id));
				a.addAttribute("","","p","",u.password);
				a.addAttribute("","","t","",Long.toString(u.registerTime));
				a.addAttribute("","","n","",u.nick);
				a.addAttribute("","","s","",u.signature);
				a.addAttribute("","","o","",u.ip);
				h.startElement("","","U",a);
				a.clear();
				for(long l:u.friends)
				{
					a.addAttribute("","","i","",Long.toString(l));
					h.startElement("","","F",a);
					h.endElement("","","F");
					a.clear();
				}
				for(long l:u.groups)
				{
					a.addAttribute("","","i","",Long.toString(l));
					h.startElement("","","G",a);
					h.endElement("","","G");
					a.clear();
				}
				h.endElement("","","U");
			}
			h.endElement("","","Users");
			h.endDocument();
			is.close();
			os=new FileOutputStream(DIR+"/users.xml");
			is.writeTo(os);
			os.flush();
			os.close();
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}

	}
	public synchronized static void loadGroup() throws Exception
	{
		File f=new File(DIR);
		if(!f.exists())f.mkdirs();
		if(!new File(DIR+"/groups.xml").exists())saveGroup();
		SAXParserFactory sf=SAXParserFactory.newInstance();
		SAXParser sp=sf.newSAXParser();
		DefaultHandler r=new DefaultHandler(){
			Group tmp;
			@Override
			public void startElement(String uri,String localName,String qName,Attributes a) throws org.xml.sax.SAXException
			{
				if(a.getLength()>0)
				{
					switch(qName)
					{
						case "G":
							tmp=new Group(0);
							tmp.id=Long.parseLong(a.getValue("i"));
							tmp.ownerid=Long.parseLong(a.getValue("o"));
							tmp.registerTime=Long.parseLong(a.getValue("t"));
							tmp.nick=a.getValue("n");
							tmp.signature=a.getValue("s");
							groups.put(tmp.id,tmp);
							break;
						case "U":
							if(tmp!=null)tmp.users.add(Long.parseLong(a.getValue("i")));
							break;
						case "A":
							if(tmp!=null)tmp.admins.add(Long.parseLong(a.getValue("i")));
							break;
					}
				}
			}
		};
		BufferedInputStream in=new BufferedInputStream(new FileInputStream(DIR+"/groups.xml"));
		sp.parse(in,r);
		in.close();
	}
	public synchronized static void saveGroup()
	{
		File f=new File(DIR);
		if(!f.exists())f.mkdirs();
		SAXTransformerFactory sf=(SAXTransformerFactory)SAXTransformerFactory.newInstance();
		ByteArrayOutputStream is=null;
		FileOutputStream os=null;
		try
		{
			TransformerHandler h=sf.newTransformerHandler();
			Transformer tr=h.getTransformer();
			tr.setOutputProperty(OutputKeys.STANDALONE,"yes");
			tr.setOutputProperty(OutputKeys.ENCODING,"utf-8");
			tr.setOutputProperty(OutputKeys.INDENT,"yes");
			is=new ByteArrayOutputStream();
			Result r=new StreamResult(is);
			h.setResult(r);
			h.startDocument();
			AttributesImpl a=new AttributesImpl();
			h.startElement("","","Groups",a);
			for(Group u:groups.values())
			{
				a.addAttribute("","","i","",Long.toString(u.id));
				a.addAttribute("","","o","",Long.toString(u.ownerid));
				a.addAttribute("","","t","",Long.toString(u.registerTime));
				a.addAttribute("","","n","",u.nick);
				a.addAttribute("","","s","",u.signature);
				h.startElement("","","G",a);
				a.clear();
				for(Long l:u.users)
				{
					a.addAttribute("","","i","",Long.toString(l));
					h.startElement("","","U",a);
					h.endElement("","","U");
					a.clear();
				}
				for(Long l:u.admins)
				{
					a.addAttribute("","","i","",Long.toString(l));
					h.startElement("","","A",a);
					h.endElement("","","A");
					a.clear();
				}
				h.endElement("","","G");
			}
			h.endElement("","","Groups");
			h.endDocument();
			is.close();
			os=new FileOutputStream(DIR+"/groups.xml");
			is.writeTo(os);
			os.flush();
			os.close();
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}
}
