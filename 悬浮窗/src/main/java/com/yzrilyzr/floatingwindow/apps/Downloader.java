package com.yzrilyzr.floatingwindow.apps;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import com.yzrilyzr.floatingwindow.API;
import com.yzrilyzr.floatingwindow.R;
import com.yzrilyzr.floatingwindow.Window;
import com.yzrilyzr.icondesigner.VecView;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.ui.myProgressBar;
import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
public class Downloader implements OnClickListener,Window.OnButtonDown,TextWatcher
{
	Context ctx;
	Window w;
	EditText e1,e2;
	View cc,dd;
	myProgressBar pgb;
	private AsyncTask task;
	int[] start=new int[3];
	public Downloader(Context c,Intent e)
	{
		ctx=c;
		String p1=e.getStringExtra("url");
		if(p1==null)p1="";
		w=new Window(c,util.px(300),util.px(210))
		.setTitle("下载器")
		.setIcon("download")
		.setOnButtonDown(this)
		.show();
		ViewGroup v=(ViewGroup) w.addView(R.layout.window_webviewer_download);
		cc=v.getChildAt(0);
		dd=v.getChildAt(1);
		e1=(EditText)v.findViewById(R.id.windowwebviewerdownloadmyEditText1);
		e2=(EditText) v.findViewById(R.id.windowwebviewerdownloadmyEditText2);
		((View)v.findViewById(R.id.windowwebviewerdownloadVecView1)).setOnClickListener(this);
		pgb=(myProgressBar) v.findViewById(R.id.windowwebviewerdownloadmyProgressBar1);
		e1.setText(p1);
		e1.addTextChangedListener(this);
	
	}
	@Override
	public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
	{
		// TODO: Implement this method
	}
	@Override
	public void afterTextChanged(Editable p1)
	{
		// TODO: Implement this method
	}
	@Override
	public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
	{
		try
		{
			e2.setText(util.mainDir+"下载的文件/"+URLDecoder.decode(p1.toString().substring(p1.toString().lastIndexOf("/")+1)));
		}
		catch(Throwable q)
		{q.printStackTrace();}
	}
	@Override
	public void onButtonDown(int code)
	{
		if(code==Window.ButtonCode.CLOSE)
		{
			if(task!=null) {
				task.cancel(false);
				task=null;
			}
		}
	}
	/*@Override
	public void run()
	{
		try
		{
			if(isrun)new Handler(ctx.getMainLooper()).postDelayed(this,300);
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}*/
	@Override
	public void onClick(View py1)
	{
		if(task==null)task=new DownloaderTask().execute(e1.getText()+"",e2.getText()+"");
		else {
			task.cancel(false);
			task=null;
		}
		((VecView)py1).setImageVec(task!=null?"pause":"play");
	}
	private class DownloaderTask extends AsyncTask
	{
		final int[] pro=new int[]{0,0,0,0,0};

		private String to;
		
		@Override
		protected void onPreExecute()
		{

			super.onPreExecute();
		}
		@Override
		protected void onPostExecute(Object result)
		{
			super.onPostExecute(result);
			util.toast(result);
			if(result.equals("下载成功"))API.startService(ctx,new Intent().putExtra("path",to),cls.EXPLORER);
		}
		@Override
		protected void onCancelled(Object result)
		{
			// TODO: Implement this method
			super.onCancelled(result);
			util.toast(result);
		}
		@Override
		protected void onProgressUpdate(Object[] values)
		{
			int pp=0;
			for(int c=2;c<pro.length;c++)pp+=pro[c];
			pgb.setProgress(pp);
			pgb.setMax(pro[1]);
			super.onProgressUpdate(values);
		}
		@Override
		protected Object doInBackground(Object[] p1)
		{
			String urls=(String)p1[0];
			to=(String)p1[1];
			try
			{    
				File file=new File(to.substring(0,to.lastIndexOf("/")));   
				if(!file.exists())file.mkdirs();
				file=new File(to);
                URL url=new URL(urls);
				HttpURLConnection conn=(HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setConnectTimeout(8000);
				conn.setReadTimeout(8000);
				conn.connect();
				int threadCount=3;
				if(start.length!=threadCount)start=new int[threadCount];
				if (conn.getResponseCode()==200)
				{
					int length=conn.getContentLength();//返回文件大小//占据文件空间
					RandomAccessFile mAccessFile=new RandomAccessFile(file, "rwd");//"rwd"可读，可写
					mAccessFile.setLength(length);//占据文件的空间
					pro[1]=length;
					pro[0]=0;
					int size=length/threadCount;
					for (int id = 0; id < threadCount; id++)
					{
						int startIndex=id*size;
						int endIndex=(id+1)*size-1;
						if (id==threadCount-1)
						{
							endIndex=length-1;
						}
						//System.out.println("第"+id+"个线程的下载区间为"+startIndex+"--"+endIndex);
						new DownLoadThread(startIndex+start[id], endIndex, urls, id,file).start();
					}
				}
				else
				{   
					return "无法连接服务器";
				}   
				while(pro[0]!=threadCount){
					publishProgress();
					Thread.sleep(50);
					if(isCancelled())return "下载暂停";
				}
				return "下载成功";
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return "下载失败";
			}
		}
	private class DownLoadThread extends Thread
	{
		private int startIndex,endIndex,threadId;
		private String urlString;
		private File file;
		public DownLoadThread(int startIndex,int endIndex,String urlString,int threadId,File f)
		{
			this.endIndex=endIndex;
			this.startIndex=startIndex;
			this.urlString=urlString;
			this.threadId=threadId;
			file=f;
		}
		@Override
		public void run()
		{
			try
			{
				URL url=new URL(urlString);
				HttpURLConnection conn=(HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setConnectTimeout(8000);
				conn.setReadTimeout(8000);
				conn.setRequestProperty("Range", "bytes="+startIndex+"-"+endIndex);//设置头信息属性,拿到指定大小的输入流
				if (conn.getResponseCode()==206)
				{//拿到指定大小字节流，由于拿到的使部分的指定大小的流，所以请求的code为206
					InputStream is=conn.getInputStream();
					RandomAccessFile mAccessFile=new RandomAccessFile(file, "rwd");//"rwd"可读，可写
					mAccessFile.seek(startIndex);
					byte[] bs=new byte[1024];
					int len=0;
					int current=0;
					while ((len=is.read(bs))!=-1&&!isCancelled())
					{
						mAccessFile.write(bs,0,len);
						current+=len;
						//System.out.println("第"+threadId+"个线程下载了"+current);
						pro[2+threadId]=current+start[threadId];
					}
					start[threadId]+=current;
					mAccessFile.close();
					//System.out.println("第"+threadId+"个线程下载完毕");
					if(!isCancelled())pro[0]++;
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			super.run();
		}
	}
	}
}
