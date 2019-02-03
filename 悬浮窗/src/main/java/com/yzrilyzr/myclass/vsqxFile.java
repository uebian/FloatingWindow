package com.yzrilyzr.myclass;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
public class vsqxFile
{
	public String vsqx="vsq3";
	public String vender,version;
	public List<vVoice> vVoiceTable=new ArrayList<vVoice>();
	public mixer mixer=new mixer();
	public masterTrack masterTrack=new masterTrack();
	public vsTrack vsTrack=new vsTrack();
	public seTrack seTrack=new seTrack();
	public karaokeTrack karaokeTrack=new karaokeTrack();
	public aux aux=new aux();
	public static class vVoice
	{
		public int vBS;
		public int vPC;
		public String compID;
		public String vVoiceName;
		public vVoiceParam vVoiceParam=new vVoiceParam();
		public static class vVoiceParam
		{
			public int bre=-1;
			public int bri=-1;
			public int cle=-1;
			public int gen=-1;
			public int ope=-1;
		}
	}
	public class mixer
	{
		public masterUnit masterUnit=new masterUnit();
		public vsUnit vsUnit=new vsUnit();
		public seUnit seUnit=new seUnit();
		public karaokeUnit karaokeUnit=new karaokeUnit();
		public class masterUnit
		{
			public int outDev,
			retLevel,
			vol;
		}
		public class vsUnit
		{
			public int vsTrackNo,
			inGain,
			sendLevel,
			sendEnable,
			mute,
			solo,
			pan,
			vol;
		}
		public class seUnit
		{
			public int inGain,
			sendLevel,
			sendEnable,
			mute,
			solo,
			pan,
			vol;
		}
		public class karaokeUnit
		{
			public int inGain,
			mute,
			solo,
			vol;
		}
	}
	public class masterTrack
	{
		public String seqName,comment;
		public int resolution,preMeasure;
		public timeSig timeSig=new timeSig();
		public tempo tempo=new tempo();
		public class timeSig
		{
			public int posMes,
			nume,
			denomi;
		}
		public class tempo
		{
			public int posTick,
			bpm;
		}
	}
	public class aux
	{
		public String auxID,content;
	}
	/*
	 <attr id="accent">50</attr>
	 <attr id="bendDep">8</attr>
	 <attr id="bendLen">0</attr>
	 <attr id="decay">50</attr>
	 <attr id="fallPort">0</attr>
	 <attr id="opening">127</attr>
	 <attr id="risePort">0</attr>
	 <attr id="vibLen">0</attr>
	 <attr id="vibType">0</attr>
	 */


	public class vsTrack
	{
		public int vsTrackNo;
		public String trackName,comment;
		public musicalPart musicalPart=new musicalPart();
		public class musicalPart
		{
			public int posTick
			,playTime;
			public String partName
			,comment;
			public stylePlugin stylePlugin=new stylePlugin();
			public Map<String,Object> partStyle=new HashMap<String,Object>();
			public singer singer=new singer();

			public List<note> notes=new ArrayList<note>();

			public class stylePlugin
			{
				public String stylePluginID,
				stylePluginName
				,version;
			}
			public class singer
			{
				public int posTick,vBS,vPC;
			}

		}
	}
	public static class note
	{
		public int posTick,
		durTick,
		noteNum,
		velocity;
		public String lyric,
		phnms;
		public Map<String,Object> noteStyle=new HashMap<String,Object>();
	}

	public class seTrack
	{}
	public class karaokeTrack
	{}
	public static vsqxFile ParseVSQX(String file) throws XmlPullParserException, IOException
	{
		//StringBuffer test=new StringBuffer();
		vsqxFile vsqxFile=new vsqxFile();
		XmlPullParser parser=XmlPullParserFactory.newInstance().newPullParser();
		FileInputStream fis=new FileInputStream(file);
		parser.setInput(fis,"utf-8");
		int event=parser.getEventType();
		String tag="",txt="",secondClass="";
		int index=-1;
		int thirdindex=-1;
		//Object list
		vsqxFile.vVoice vVoice=new vsqxFile.vVoice();
		vsqxFile.note note=new vsqxFile.note();
		Map<String,Object> attr=new HashMap<String,Object>();
		//Object list**
		while(event!=XmlPullParser.END_DOCUMENT)
		{
			String tmptag=parser.getName();
			String tmptxt=parser.getText();
			if(tmptag!=null)tag=tmptag;
			if(tmptxt!=null)txt=tmptxt;
			switch (event)
			{
				case XmlPullParser.TEXT:
					//test.append("text tag: "+tag+" ,text: "+txt+"\n");

					break;
				case XmlPullParser.START_TAG:
					//test.append("start tag: "+tag+"\n");
					if(parser.getAttributeCount()!=0)
					{
						attr.clear();
						for(int i=0;i<parser.getAttributeCount();i++)
						{
							attr.put(i+"",parser.getAttributeValue(i));
						}
					}
					if(tag.equals("mixer"))secondClass="mixer";
					if(tag.equals("masterTrack"))secondClass="masterTrack";
					if(tag.equals("aux"))secondClass="aux";
					if(tag.equals("vsTrack"))secondClass="vsTrack";
					if(tag.equals("seTrack"))secondClass="seTrack";
					if(tag.equals("karaokeTrack"))secondClass="karaokeTrack";

					if(tag.equals("masterUnit")||tag.equals("timeSig")||tag.equals("musicalPart"))index=0;
					if(tag.equals("vsUnit")||tag.equals("tempo"))index=1;
					if(tag.equals("seUnit"))index=2;
					if(tag.equals("karaokeUnit"))index=3;

					if(tag.equals("stylePlugin"))thirdindex=0;
					if(tag.equals("singer"))thirdindex=1;
					if(tag.equals("partStyle"))thirdindex=2;
					if(tag.equals("note"))thirdindex=3;
					//init Object
					if(tag.equals("vVoice"))vVoice=new vsqxFile.vVoice();
					if(tag.equals("note"))note=new vsqxFile.note();
					//init Object**
					break;
				case XmlPullParser.END_TAG:
					//test.append("end tag: "+tag+"\n\n");
					//if(tag.equals("attr")&&parser.getAttributeCount()==1)attrs.put(parser.getAttributeValue(0),txt);
					//writeIn data
					if(tag.equals("vender"))vsqxFile.vender=txt;
					if(tag.equals("version"))vsqxFile.version=txt;
					if(tag.equals("vBS"))vVoice.vBS=S2I(txt);
					if(tag.equals("vPC"))vVoice.vPC=S2I(txt);
					if(tag.equals("compID"))vVoice.compID=txt;
					if(tag.equals("vVoiceName"))vVoice.vVoiceName=txt;
					if(tag.equals("bre"))vVoice.vVoiceParam.bre=S2I(txt);
					if(tag.equals("bri"))vVoice.vVoiceParam.bri=S2I(txt);
					if(tag.equals("cle"))vVoice.vVoiceParam.cle=S2I(txt);
					if(tag.equals("gen"))vVoice.vVoiceParam.gen=S2I(txt);
					if(tag.equals("ope"))vVoice.vVoiceParam.ope=S2I(txt);
					if(secondClass.equals("masterTrack"))
					{
						if(tag.equals("seqName"))vsqxFile.masterTrack.seqName=txt;
						if(tag.equals("comment"))vsqxFile.masterTrack.comment=txt;
						if(tag.equals("resolution"))vsqxFile.masterTrack.resolution=S2I(txt);
						if(tag.equals("preMeasure"))vsqxFile.masterTrack.preMeasure=S2I(txt);
					}
					if(secondClass.equals("aux"))
					{
						if(tag.equals("auxID"))vsqxFile.aux.auxID=txt;
						if(tag.equals("content"))vsqxFile.aux.content=txt;
					}
					if(secondClass.equals("vsTrack"))
					{
						if(tag.equals("vsTrackNo"))vsqxFile.vsTrack.vsTrackNo=S2I(txt);
						if(tag.equals("trackName"))vsqxFile.vsTrack.trackName=txt;
						if(tag.equals("comment"))vsqxFile.vsTrack.comment=txt;
					}

					if(index==0)
					{
						if(tag.equals("outDev"))vsqxFile.mixer.masterUnit.outDev=S2I(txt);
						if(tag.equals("retLevel"))vsqxFile.mixer.masterUnit.retLevel=S2I(txt);
						if(tag.equals("vol"))vsqxFile.mixer.masterUnit.vol=S2I(txt);
						//timeSig
						if(tag.equals("posMes"))vsqxFile.masterTrack.timeSig.posMes=S2I(txt);
						if(tag.equals("nume"))vsqxFile.masterTrack.timeSig.nume=S2I(txt);
						if(tag.equals("denomi"))vsqxFile.masterTrack.timeSig.denomi=S2I(txt);
						//musicalPart
						if(tag.equals("posTick"))vsqxFile.vsTrack.musicalPart.posTick=S2I(txt);
						if(tag.equals("playTime"))vsqxFile.vsTrack.musicalPart.playTime=S2I(txt);
						if(tag.equals("partName"))vsqxFile.vsTrack.musicalPart.partName=txt;
						if(tag.equals("comment"))vsqxFile.vsTrack.musicalPart.comment=txt;
					}
					if(index==1)
					{
						if(tag.equals("vsTrackNo"))vsqxFile.mixer.vsUnit.vsTrackNo=S2I(txt);
						if(tag.equals("inGain"))vsqxFile.mixer.vsUnit.inGain=S2I(txt);
						if(tag.equals("sendLevel"))vsqxFile.mixer.vsUnit.sendLevel=S2I(txt);
						if(tag.equals("sendEnable"))vsqxFile.mixer.vsUnit.sendEnable=S2I(txt);
						if(tag.equals("mute"))vsqxFile.mixer.vsUnit.mute=S2I(txt);
						if(tag.equals("solo"))vsqxFile.mixer.vsUnit.solo=S2I(txt);
						if(tag.equals("pan"))vsqxFile.mixer.vsUnit.pan=S2I(txt);
						if(tag.equals("vol"))vsqxFile.mixer.vsUnit.vol=S2I(txt);
						//tempo
						if(tag.equals("posTick"))vsqxFile.masterTrack.tempo.posTick=S2I(txt);
						if(tag.equals("bpm"))vsqxFile.masterTrack.tempo.bpm=S2I(txt);
					}
					if(index==2)
					{
						if(tag.equals("inGain"))vsqxFile.mixer.seUnit.inGain=S2I(txt);
						if(tag.equals("sendLevel"))vsqxFile.mixer.seUnit.sendLevel=S2I(txt);
						if(tag.equals("sendEnable"))vsqxFile.mixer.seUnit.sendEnable=S2I(txt);
						if(tag.equals("mute"))vsqxFile.mixer.seUnit.mute=S2I(txt);
						if(tag.equals("solo"))vsqxFile.mixer.seUnit.solo=S2I(txt);
						if(tag.equals("pan"))vsqxFile.mixer.seUnit.pan=S2I(txt);
						if(tag.equals("vol"))vsqxFile.mixer.seUnit.vol=S2I(txt);
					}
					if(index==3)
					{
						if(tag.equals("inGain"))vsqxFile.mixer.karaokeUnit.inGain=S2I(txt);
						if(tag.equals("mute"))vsqxFile.mixer.karaokeUnit.mute=S2I(txt);
						if(tag.equals("solo"))vsqxFile.mixer.karaokeUnit.solo=S2I(txt);
						if(tag.equals("vol"))vsqxFile.mixer.karaokeUnit.vol=S2I(txt);
					}
					if(thirdindex==0)
					{
						if(tag.equals("stylePluginID"))vsqxFile.vsTrack.musicalPart.stylePlugin.stylePluginID=txt;
						if(tag.equals("stylePluginName"))vsqxFile.vsTrack.musicalPart.stylePlugin.stylePluginName=txt;
						if(tag.equals("version"))vsqxFile.vsTrack.musicalPart.stylePlugin.version=txt;
					}
					if(thirdindex==1)
					{
						if(tag.equals("posTick"))vsqxFile.vsTrack.musicalPart.singer.posTick=S2I(txt);
						if(tag.equals("vBS"))vsqxFile.vsTrack.musicalPart.singer.vBS=S2I(txt);
						if(tag.equals("vPC"))vsqxFile.vsTrack.musicalPart.singer.vPC=S2I(txt);

					}
					if(thirdindex==2)
					{
						for(int i=0;i<attr.size();i++)
							vsqxFile.vsTrack.musicalPart.partStyle.put((String)attr.get(i+""),txt);
						//System.out.println(vsqxFile.vsTrack.musicalPart.partStyle.hashCode());
					}
					if(thirdindex==3)
					{
						if(tag.equals("posTick"))note.posTick=S2I(txt);
						if(tag.equals("durTick"))note.durTick=S2I(txt);
						if(tag.equals("noteNum"))note.noteNum=S2I(txt);
						if(tag.equals("velocity"))note.velocity=S2I(txt);
						if(tag.equals("lyric"))note.lyric=txt;
						if(tag.equals("phnms"))note.phnms=txt;
						//if(tag.equals("noteStyle"))
						for(int i=0;i<attr.size();i++)
							note.noteStyle.put((String)attr.get(i+""),txt);
						//System.out.println(vsqxFile.vsTrack.musicalPart.partStyle.hashCode());
					}
					//writeIn data**
					//add obj
					if(tag.equals("vVoice"))vsqxFile.vVoiceTable.add(vVoice);
					if(tag.equals("note"))vsqxFile.vsTrack.musicalPart.notes.add(note);
					//add obj**
					break;
			}
			event=parser.next();
		}

		//解析结束，返回集
		fis.close();
		System.out.println("parse");
		//new BufferedWriter(new FileWriter("/sdcard/test/log.txt")).write(test.toString());
		//System.out.println("ok");
		return vsqxFile;
	}
	public static int S2I(String s)
	{
		return Integer.parseInt(s);
	}
}
