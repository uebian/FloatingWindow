package com.yzrilyzr.AudioEdit;
import java.io.InputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

public class wave
{
    public static int insert(int a,int b)
    {
        return (a+b)/2;
    }
	//**???*******
	public static int[] mono_16Bit_PCM(byte[] data)
	{
		int[] sh=new int[data.length/2];
		for(int i=0;i<data.length/2;i++)
		{
			byte a=data[i*2],b=data[i*2+1];
			sh[i]=b*0x100+a;
		}
		return sh;
	}
	public static int[] mono_8Bit_PCM(byte[] data)
	{
		int[] sh=new int[data.length];
		for(int i=0;i<sh.length;i++)
		{
			sh[i]=data[i]*256;
		}
		return sh;
	}
	public static byte[] mono_PCM_8Bit(int[] data)
	{
		byte[] sh=new byte[data.length];
		for(int i=0;i<sh.length;i++)
		{
			if(data[i]>32767)data[i]=32767;
			if(data[i]<-32767)data[i]=-32767;
			sh[i]=(byte)(data[i]/256);
		}
		return sh;
	}
	public static int[][] stereo_8Bit_PCM(byte[] data)
	{
		int[] l=new int[data.length/2];
		int[] r=new int[data.length/2];
		for(int i=0;i<data.length/2;i++)
		{
			l[i]=data[i*2]*256;
			r[i]=data[i*2+1]*256;
		}
		return new int[][]{l,r};
	}


	public static byte[] mono_PCM_16Bit(int[] sh)
	{
		byte[] data=new byte[sh.length*2];
		for(int i=0;i<data.length/2;i++)
		{
			if(sh[i]>32767)sh[i]=32767;
			if(sh[i]<-32767)sh[i]=-32767;
			byte a=(byte) (sh[i]-sh[i]/0x100*0x100),b=(byte) (sh[i]/0x100);
			data[i*2]=a;
			data[i*2+1]=b;
		}
		return data;

	}
	public static byte[] stereo_PCM_8Bit(int[] l,int[] r)
	{
		byte[] data=new byte[l.length+r.length];
		for(int i=0;i<data.length/2;i++)
		{
			if(l[i]>32767)l[i]=32767;
			if(l[i]<-32767)l[i]=-32767;
			if(r[i]>32767)r[i]=32767;
			if(r[i]<-32767)r[i]=-32767;
			data[i*2]=(byte)(l[i]/256);
			data[i*2+1]=(byte)(r[i]/256);
		}
		return data;
	}
	//*********?******?*
	public static int[] gain(int[] src,float gain)
	{
		for(int i=0;i<src.length;i++)
		{
			src[i]=(int)((float)src[i]*gain);
		}
		return src;
	}
    public static int[][] stereo_16Bit_PCM(byte[] data)
    {
        int[] left=new int[data.length/4];
        int[] right=new int[data.length/4];
        for(int i=0;i<data.length/4;i++)
        {
            byte a=data[i*4],b=data[i*4+1];
            left[i]=b*0x100+a;
            a=data[i*4+2];b=data[i*4+3];
            right[i]=b*0x100+a;

        }
        return new int[][]{left,right};
    }
    public static byte[] stereo_PCM_16Bit(int[] left,int[] right)
    {
        byte[] data=new byte[left.length*2+right.length*2];
        for(int i=0;i<data.length/4;i++)
        {
			if(left[i]>32767)left[i]=32767;
			if(left[i]<-32767)left[i]=-32767;
			if(right[i]>32767)right[i]=32767;
			if(right[i]<-32767)right[i]=-32767;
            byte a=(byte) (left[i]-left[i]/0x100*0x100),b=(byte) (left[i]/0x100);
            data[i*4]=a;
            data[i*4+1]=b;
            a=(byte) (right[i]-right[i]/0x100*0x100);b=(byte) (right[i]/0x100);
            data[i*4+2]=a;
            data[i*4+3]=b;
        }
        return data;
    }
    public static int[] mix(int[]... a)
    {
        int[] b=a[0];
        for(int i=0;i<b.length;i++)
        {
            int c=0;
            for(int u=0;u<a.length;u++)c+=a[u][i];
            b[i]=c/a.length;
        }
        return b;
    }
    public class sin
	{
        public float n,r,dn;
        public void setHz(float sr,float hz)
		{
            //hz=sr*dn/360
            dn=360f*hz/sr;
        }
        public sin(float sr,float hz,float r,float n)
		{
            this.n=n;
            this.r=r;
            setHz(sr,hz);
        }
        public float getY()
		{
            return (float)Math.cos(2.0*Math.PI/360.0*n)*r;
        }
        public void next()
		{
            n+=dn;
        }
    }
    public static int[] reverse(int[] a)
	{
		int[] data2=new int[a.length];
		for(int i=0;i<a.length;i++)
		{
			data2[a.length-1-i]=a[i];
		}
		return data2;
    }
	public static int[] convertSampleRate(int[] a,int sr)
	{
		float b=(float)a.length/(float)sr;
		int[] c=new int[sr];
		c[0]=a[0];
		for(int i=0;i<sr-2;i++)
		{
			float xx=(float)i*b;
			int x=(int)Math.floor(xx);
			int y1=0,y2=0;
			try
			{
				y1=a[x];
				y2=a[x+1];
			}
			catch(Throwable e)
			{}
			c[i+1]=(int)((float)(y2-y1)*(xx-(float)x)+(float)y2);
		}
		c[sr-1]=a[a.length-1];
		return c;
	}
	public class StreamPlayer
	{
		private RandomAccessFile is;
		public int SKIP=10;
		//public long length=0;
		//public long Pointer=0;
		public float LEFT_GAIN=1F,RIGHT_GAIN=1F;
		public float LEFT_CAP=1F,RIGHT_CAP=1F;
		public Capacitor Lcap,Rcap;
		public boolean MONO=false;
		public int SAMPLE_RATE=44100;
		public int BUFFER_SIZE=1024;
		public ENCODE ENC=ENCODE.STEREO_16BIT;
		private int skipcount=0;
		public final enum ENCODE
		{
			MONO_8BIT,
			MONO_16BIT,
			STEREO_8BIT,
			STEREO_16BIT;
		}
		public StreamPlayer(RandomAccessFile is)
		{
			this.is=is;
			Lcap=new Capacitor();
			Rcap=new Capacitor();
			/*try
			 {
			 length=is.length();
			 }
			 catch (IOException e)
			 {}*/
		}
		public long getFilePointer()
		{
			try
			{
				return is.getFilePointer();
			}
			catch (IOException e)
			{
				return 0;
			}
		}
		public long length()
		{
			try
			{
				return is.length();
			}
			catch (IOException e)
			{ 
				return 0;
			}
		}
		public void seek(long to)
		{
			try
			{
				//is.mark(is.available());
				is.seek(to);
			}
			catch (IOException e)
			{
				System.out.println(e);
			}
		}
		public byte[] read() throws IOException
		{
			byte[] data=new byte[BUFFER_SIZE];
			is.read(data);
			//Pointer+=BUFFER_SIZE;
			if(skipcount>=10)
			{
				int p=SKIP;
				if(p<0)p=20+p;
				long a=(Math.abs(p)-10)*BUFFER_SIZE+is.getFilePointer();
				a=a/4*4;
				if(a<is.length()&&a>=0)seek(a);
				skipcount=0;
			}
			skipcount++;
			int[][] buff=new int[0][0];
			switch(ENC)
			{
				case MONO_8BIT:
					buff[0]=wave.mono_8Bit_PCM(data);
					buff[1]=buff[0];
					break;
				case MONO_16BIT:
					buff[0]=wave.mono_16Bit_PCM(data);
					buff[1]=buff[0];
					break;
				case STEREO_8BIT:
					buff=wave.stereo_8Bit_PCM(data);
					break;
				case STEREO_16BIT:
					buff=wave.stereo_16Bit_PCM(data);
					break;
			}

		if(SKIP<0)
			{
				long f=is.getFilePointer()-2*BUFFER_SIZE;
				if(f<0)return new byte[0];
				seek(f);
				buff[0]=wave.reverse(buff[0]);
				buff[1]=wave.reverse(buff[1]);
			}
			buff=convert(buff);
			if(MONO)
			{
				int[] data2=wave.mix(buff[0],buff[1]);
				data=wave.stereo_PCM_16Bit(data2,data2);
			}
			else data=wave.stereo_PCM_16Bit(buff[0],buff[1]);
			//if(fos!=null)fos.write(data);
			//WaveView.setData(data);
			//track.write(data,0,data.length);
			//uiHandler.post(uiRunnable);
			return data;
		}

		public int[][] convert(int[][] buff)
		{
			buff[0]=wave.gain(buff[0],LEFT_GAIN);
			buff[1]=wave.gain(buff[1],RIGHT_GAIN);

			for(int i=0;i<buff[0].length;i++)
			{
				buff[0][i]=Lcap.getFilterY(buff[0][i],LEFT_CAP);
				buff[1][i]=Rcap.getFilterY(buff[1][i],RIGHT_CAP);
			}
			int rat=(int)((float)SAMPLE_RATE/44100f*(float)buff[0].length);
			buff[0]=wave.convertSampleRate(buff[0],rat);
			buff[1]=wave.convertSampleRate(buff[1],rat);
			return buff;
		}
	}
	public class Capacitor
	{
		private float V0=0f;
		public int getFilterY(float Vu,float C)
		{
			int Vt=(int)(V0+(Vu-V0)*(1f-Math.exp(-C)));
			V0=Vt;
			return Vt;
		}/*
		 //假设有电源Vu通过电阻R给电容C充电
		 //V0为电容上的初始电压值
		 //Vu为电容充满电后的电压值
		 //Vt为任意时刻t时电容上的电压
		 }*/

	}
}
