package com.yzrilyzr.myclass;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.io.ByteArrayInputStream;
public class MusicID3
{
	public int length=0;
	public String 
	TEXT,//: 歌词作者 
	TENC,//: 编码 
	WXXX,//: URL链接(URL) 
	TCOP,//: 版权(Copyright) 
	TOPE,//: 原艺术家 
	TCOM,//: 作曲家 
	TDAT,//: 日期 
	TPE3,//: 指挥者 
	TPE2,//: 乐队 
	TPE1,//: 艺术家相当于ID3v1的Artist 
	TPE4,//: 翻译（记录员、修改员） 
	TYER,//: 年代相当于ID3v1的Year 
	USLT,//: 歌词 
	TALB,//: 专辑相当于ID3v1的Album 
	TIT1,//: 内容组描述 
	TIT2,//: 标题相当于ID3v1的Title 
	TIT3,//: 副标题 
	TCON,//: 流派（风格）相当于ID3v1的Genre见下表 
	TBPM,//: 每分钟节拍数 
	COMM,//: 注释相当于ID3v1的Comment 
	TDLY,//: 播放列表返录 
	TRCK,//: 音轨（曲号）相当于ID3v1的Track 
	TFLT,//: 文件类型 
	TIME,//: 时间　 
	TKEY,//: 最初关键字 
	TLAN,//: 语言 
	TLEN,//: 长度 
	TMED,//: 媒体类型 
	TOAL,//: 原唱片集 
	TOFN,//: 原文件名 
	TOLY,//: 原歌词作者 
	TORY,//: 最初发行年份 
	TOWM,//: 文件所有者（许可证者） 
	TPOS,//: 作品集部分 
	TPUB,//: 发行人 
	TRDA,//: 录制日期 
	TRSN,//: Intenet电台名称 
	TRSO,//: Intenet电台所有者 
	TSIZ,//: 大小 　 
	TSRC,//: ISRC（国际的标准记录代码） 
	TSSE,//: 编码使用的软件（硬件设置） 
	UFID;//: 唯一的文件标识符 ;
	public byte[] APIC;
	public String path;
	public static final String[] p=new String[]{
	"APIC",
	"TEXT",//: 歌词作者 
	"TENC",//: 编码 
	"WXXX",//: URL链接(URL) 
	"TCOP",//: 版权(Copyright) 
	"TOPE",//: 原艺术家 
	"TCOM",//: 作曲家 
	"TDAT",//: 日期 
	"TPE3",//: 指挥者 
	"TPE2",//: 乐队 
	"TPE1",//: 艺术家相当于ID3v1的Artist 
	"TPE4",//: 翻译（记录员、修改员） 
	"TYER",//: 年代相当于ID3v1的Year 
	"USLT",//: 歌词 
	"TALB",//: 专辑相当于ID3v1的Album 
	"TIT1",//: 内容组描述 
	"TIT2",//: 标题相当于ID3v1的Title 
	"TIT3",//: 副标题 
	"TCON",//: 流派（风格）相当于ID3v1的Genre见下表 
	"TBPM",//: 每分钟节拍数 
	"COMM",//: 注释相当于ID3v1的Comment 
	"TDLY",//: 播放列表返录 
	"TRCK",//: 音轨（曲号）相当于ID3v1的Track 
	"TFLT",//: 文件类型 
	"TIME",//: 时间　 
	"TKEY",//: 最初关键字 
	"TLAN",//: 语言 
	"TLEN",//: 长度 
	"TMED",//: 媒体类型 
	"TOAL",//: 原唱片集 
	"TOFN",//: 原文件名 
	"TOLY",//: 原歌词作者 
	"TORY",//: 最初发行年份 
	"TOWM",//: 文件所有者（许可证者） 
	"TPOS",//: 作品集部分 
	"TPUB",//: 发行人 
	"TRDA",//: 录制日期 
	"TRSN",//: Intenet电台名称 
	"TRSO",//: Intenet电台所有者 
	"TSIZ",//: 大小 　 
	"TSRC",//: ISRC（国际的标准记录代码） 
	"TSSE",//: 编码使用的软件（硬件设置） 
	"UFID"//: 唯一的文件标识符 ;
	};
	public MusicID3(String mp3path)
	{
		path=mp3path;
	}
	public void loadInfo()
	{
		try
		{
			InputStream is=new FileInputStream(path);
			byte[] id3=new byte[3];
			is.read(id3,0,3);
			if(new String(id3).equalsIgnoreCase("ID3"))
			{
				//System.out.println("ID3");
				int ver=is.read();
				int revision=is.read();
				int flag=is.read();
				/*1.标志字节
				 标志字节一般为0，定义如下：
				 abc00000
				 a -- 表示是否使用不同步(一般不设置)
				 b -- 表示是否有扩展头部，一般没有(至少Winamp没有记录)，所以一般也不设置
				 c -- 表示是否为测试标签(99.99%的标签都不是测试用的啦，所以一般也不设置)*/

				byte[] size = new byte[4];
				is.read(size);
				int l = toSize(size);
				byte[] data = new byte[l-10];
				is.read(data);
				is.close();
				int index = 0;
				while (index < data.length)
				{
					String FrameID=new String(data,index,4);
					index+=4;
					int FrameSize=toSize2(data, index);
					//System.out.println(FrameID+":"+FrameSize);
					index+=4;
					index+=2;
					String enc="utf-8";
					switch(data[index])
					{
						case 0:enc="ISO-8859-1";break;
						case 1:enc="UTF-16";break;
						case 2:enc="UTF-16BE";break;
						case 3:enc="UTF-8";break;
					}
					try
					{
						if(FrameID.equals("APIC"))
						{
							byte[] bs=new byte[FrameSize-13];
							System.arraycopy(data,index+13,bs,0,bs.length);
							APIC=bs;
						}
						else
							MusicID3.class.getField(FrameID).set(this,new String(data,1+index, FrameSize-1,enc));
					}
					catch(Throwable e)
					{}
					index+=FrameSize;
				}
				//System.out.println(data.length+"="+ index);
			}
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}
	public static int toSize(byte[] bytes)
	{
		int result =toByte(bytes[0]) << 21
		| toByte(bytes[1]) << 14
		| toByte(bytes[2]) << 7
		| toByte(bytes[3]);
        return result;
    }
    public static int toSize2(byte[] data, int offset)
	{
		int result =toByte(data[offset])<<24
		|toByte(data[offset+1])<<16
		|toByte(data[offset+2])<<8
		|toByte(data[offset+3]);
        return result;

    }
	private static int toByte(byte a)
	{
		return a<0?a+256:a;
	}
	public boolean saveInfo()
	{
		try
		{
			BufferedOutputStream o=new BufferedOutputStream(new FileOutputStream(path+"_1"));
			InputStream is=new FileInputStream(path);
			byte[] id3=new byte[3];
			is.read(id3,0,3);
			if(new String(id3).equalsIgnoreCase("ID3"))
			{
				is.skip(3);
				byte[] osize = new byte[4];
				is.read(osize);
				is.skip(toSize(osize)-10);
				byte[] left=new byte[is.available()];
				is.read(left);
				is.close();
				ByteArrayOutputStream os=new ByteArrayOutputStream();
				for(String x:p)
				{
					Object s=MusicID3.class.getField(x).get(this);
					if(s!=null)
					{
						os.write(x.getBytes());
						if(x.equals("APIC"))
						{
							byte[] d=APIC;
							byte[] d2=new byte[d.length+13];
							System.arraycopy(new byte[]{3,'i','m','a','g','e','/','j','p','g','a','a','a'},0,d2,0,13);
							System.arraycopy(d,0,d2,13,d.length);
							int si=d2.length;
							os.write(si/0x1000000);
							os.write(si%0x1000000/0x10000);
							os.write(si%0x10000/0x100);
							os.write(si%0x100);
							os.write(0);
							os.write(0);
							os.write(d2);
						}
						else
						{
							byte[] d=((String)s).getBytes();
							int si=d.length+1;
							os.write(si/0x1000000);
							os.write(si%0x1000000/0x10000);
							os.write(si%0x10000/0x100);
							os.write(si%0x100);
							os.write(0);
							os.write(0);
							os.write(3);
							os.write(d);
						}
					}
				}
				os.flush();
				os.close();
				int s=os.toByteArray().length+10;
				byte[] hd=new byte[]{'I','D','3',3,0,0,
				(byte)((s>>21)&0x7f),
				(byte)((s>>14)&0x7f),
				(byte)((s>>7)&0x7f),
				(byte)(s&0x7f)
				};
				o.write(hd);
				o.write(os.toByteArray());
				o.write(left);
				o.flush();
				o.close();
			}

			return true;
		}
		catch(Throwable e)
		{
			return false;
		}
	}
}
