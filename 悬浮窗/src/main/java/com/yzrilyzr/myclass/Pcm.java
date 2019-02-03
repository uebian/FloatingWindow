package com.yzrilyzr.myclass;

import java.io.IOException;
import java.io.RandomAccessFile;

public class Pcm
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


	public static void mono_PCM_16Bit(byte[] data,int[] sh)
	{
		for(int i=0;i<data.length/2;i++)
		{
			if(sh[i]>32767)sh[i]=32767;
			if(sh[i]<-32767)sh[i]=-32767;
			byte a=(byte) (sh[i]-sh[i]/0x100*0x100),b=(byte) (sh[i]/0x100);
			data[i*2]=a;
			data[i*2+1]=b;
		}

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
    public static void stereo_PCM_16Bit(byte[] data,int[] left,int[] right)
    {
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
	public static class Capacitor
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
	public static Complex[] fft(Complex[] x) {
		int n = x.length;

		// 因为exp(-2i*n*PI)=1，n=1时递归原点
		if (n == 1){
			return x;
		}

		// 如果信号数为奇数，使用dft计算
		if (n % 2 != 0) {
			return dft(x);
		}

		// 提取下标为偶数的原始信号值进行递归fft计算
		Complex[] even = new Complex[n / 2];
		for (int k = 0; k < n / 2; k++) {
			even[k] = x[2 * k];
		}
		Complex[] evenValue = fft(even);

		// 提取下标为奇数的原始信号值进行fft计算
		// 节约内存
		Complex[] odd = even;
		for (int k = 0; k < n / 2; k++) {
			odd[k] = x[2 * k + 1];
		}
		Complex[] oddValue = fft(odd);

		// 偶数+奇数
		Complex[] result = new Complex[n];
		for (int k = 0; k < n / 2; k++) {
			// 使用欧拉公式e^(-i*2pi*k/N) = cos(-2pi*k/N) + i*sin(-2pi*k/N)
			double p = -2 * k * Math.PI / n;
			Complex m = new Complex(Math.cos(p), Math.sin(p));
			result[k] = evenValue[k].plus(m.multiple(oddValue[k]));
			// exp(-2*(k+n/2)*PI/n) 相当于 -exp(-2*k*PI/n)，其中exp(-n*PI)=-1(欧拉公式);
			result[k + n / 2] = evenValue[k].minus(m.multiple(oddValue[k]));
		}
		return result;
	}

	public static Complex[] dft(Complex[] x) {
		int n = x.length;

		// 1个信号exp(-2i*n*PI)=1
		if (n == 1)
			return x;

		Complex[] result = new Complex[n];
		for (int i = 0; i < n; i++) {
			result[i] = new Complex(0, 0);
			for (int k = 0; k < n; k++) {
				//使用欧拉公式e^(-i*2pi*k/N) = cos(-2pi*k/N) + i*sin(-2pi*k/N)
				double p = -2 * k * Math.PI / n;
				Complex m = new Complex(Math.cos(p), Math.sin(p));
				result[i].plus(x[k].multiple(m));
			}
		}
		return result;
	}
	public static class Complex {
		public  double re;   // the real part
		public  double im;   // the imaginary part

		public Complex() {
			re = 0;
			im = 0;
		}

		// create a new object with the given real and imaginary parts
		public Complex(double real, double imag) {
			re = real;
			im = imag;
		}

		// return a string representation of the invoking Complex object
		public String toString() {
			if (im == 0) return re + "";
			if (re == 0) return im + "i";
			if (im <  0) return re + " - " + (-im) + "i";
			return re + " + " + im + "i";
		}

		// return abs/modulus/magnitude and angle/phase/argument
		public double abs()   { return Math.hypot(re, im); }  // Math.sqrt(re*re + im*im)
		public double phase() { return Math.atan2(im, re); }  // between -pi and pi

		// return a new Complex object whose value is (this + b)
		public Complex plus(Complex b) {
			Complex a = this;             // invoking object
			double real = a.re + b.re;
			double imag = a.im + b.im;
			return new Complex(real, imag);
		}

		// return a new Complex object whose value is (this - b)
		public Complex minus(Complex b) {
			Complex a = this;
			double real = a.re - b.re;
			double imag = a.im - b.im;
			return new Complex(real, imag);
		}

		// return a new Complex object whose value is (this * b)
		public Complex multiple(Complex b) {
			Complex a = this;
			double real = a.re * b.re - a.im * b.im;
			double imag = a.re * b.im + a.im * b.re;
			return new Complex(real, imag);
		}

		// scalar multiplication
		// return a new object whose value is (this * alpha)
		public Complex times(double alpha) {
			return new Complex(alpha * re, alpha * im);
		}

		// return a new Complex object whose value is the conjugate of this
		public Complex conjugate() {  return new Complex(re, -im); }

		// return a new Complex object whose value is the reciprocal of this
		public Complex reciprocal() {
			double scale = re*re + im*im;
			return new Complex(re / scale, -im / scale);
		}

		// return the real or imaginary part
		public double re() { return re; }
		public double im() { return im; }

		// return a / b
		public Complex divides(Complex b) {
			Complex a = this;
			return a.multiple(b.reciprocal());
		}

		// return a new Complex object whose value is the complex exponential of this
		public Complex exp() {
			return new Complex(Math.exp(re) * Math.cos(im), Math.exp(re) * Math.sin(im));
		}

		// return a new Complex object whose value is the complex sine of this
		public Complex sin() {
			return new Complex(Math.sin(re) * Math.cosh(im), Math.cos(re) * Math.sinh(im));
		}

		// return a new Complex object whose value is the complex cosine of this
		public Complex cos() {
			return new Complex(Math.cos(re) * Math.cosh(im), -Math.sin(re) * Math.sinh(im));
		}

		// return a new Complex object whose value is the complex tangent of this
		public Complex tan() {
			return sin().divides(cos());
		}



		// a static version of plus
		public static Complex plus(Complex a, Complex b) {
			double real = a.re + b.re;
			double imag = a.im + b.im;
			Complex sum = new Complex(real, imag);
			return sum;
		}
	}
}
