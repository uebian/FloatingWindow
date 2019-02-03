package com.yzrilyzr.FlappyFrog;

import android.graphics.*;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import com.yzrilyzr.floatingwindow.pluginapi.API;
import com.yzrilyzr.floatingwindow.pluginapi.Window;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
public class Main implements SurfaceHolder.Callback,Runnable,MediaPlayer.OnCompletionListener,Window.OnButtonDown,View.OnTouchListener
{
	SoundPool sp;
	HashMap<String,Integer> hm;
	int currStreamId;
	MediaPlayer bgm;
    Context ctx;
    SurfaceView view;
    Window window;
    boolean rendering=false,playBgm=false;
    Paint paint;
	int NOW=0;//0载入;1准备;2运行;3播放hurt结束;4结束
    int thisTime,totalTime=0,thisPipes=0;
    float meter=0;
	float JUMP=20,GRAVITY=1,SPEED=8,GAP=480,PREPARE=2400,CLOUDSPEED=2f,CLOUDSCALE=3F;
    Bitmap[] bmps;
    long time=0;
    ArrayList<RectObj> PIPES,CLOUDS;
    RectObj FROG,PIPE,PASS;
    RectObj restartObj,bgmObj,githubObj;
    SurfaceHolder holder;
    @Override
    public void surfaceCreated(SurfaceHolder p1)
    {
        resetMap(view);
		holder=p1;
        startRender();
    }
    @Override
    public void surfaceChanged(SurfaceHolder p1, int p2, int p3, int p4)
    {
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder p1)
    {
        rendering=false;
    }
    @Override
    public void run()
    {
        // TODO: Implement this method
        rendering=true;
        Canvas c=null;
        while(rendering)
        {
            try
            {
                c=holder.lockCanvas();
                c.drawColor(0xffdcedff);
                render(c);
                holder.unlockCanvasAndPost(c);
            }
            catch(Throwable e)
            {
            }
			try
			{
				Thread.sleep(1);
			}
			catch (InterruptedException e)
			{}
        }
    }
    public float dip(float a)
    {
        return ctx.getResources().getDisplayMetrics().density*a;
    }
    public InputStream loadAsset(String file)
    {
        try
        {
            InputStream is=null;
            String apk=ctx.getPackageManager().getPackageInfo("com.yzrilyzr.FlappyFrog",PackageInfo.INSTALL_LOCATION_AUTO).applicationInfo.publicSourceDir;
            ZipFile zf = new ZipFile(apk);
            ZipEntry entry=zf.getEntry(file);
            is=zf.getInputStream(entry);
            return is;   
        }
        catch(Throwable e)
        {}
        return null;
    }
    public Bitmap bmpFromAsset(String file) throws FileNotFoundException
    {
        InputStream is=loadAsset(file);
        if(is==null)throw new FileNotFoundException("");
        return BitmapFactory.decodeStream(is);
    }
    public Bitmap scale(Bitmap b,float r)
    {
        Matrix Matrix=new Matrix();
        Matrix.postScale(r,r);
        return Bitmap.createBitmap(b,0,0,b.getWidth(),b.getHeight(),Matrix,false);
    }
	public Main(final Context ctx,Intent intent) throws Throwable
    {
		this.ctx=ctx;
		view=new SurfaceView(ctx);
		view.setOnTouchListener(this);
		holder=view.getHolder();
        holder.setFormat(PixelFormat.RGBA_8888);
        holder.addCallback(this);
        window=new Window(ctx,(int)dip(270),(int)dip(480));
        window.setTitle("FlappyFrog");
		window.setIcon(new BitmapDrawable(loadAsset("res/drawable/ic_launcher.png")));
        window.show();
        window.setBar(8,0,0,0);
        window.addView(view);
        window.setOnButtonDown(this);
		new Thread(new Runnable(){
				@Override
				public void run()
				{
					try
					{
						init(ctx);
					}
					catch (Exception e)
					{
					}
				}
			}).start();
    }
	private void init(Context ctx) throws Exception
	{
		paint=new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(dip(18f));
		PIPES=new ArrayList<RectObj>();
        CLOUDS=new ArrayList<RectObj>();
		bmps=new Bitmap[8];
        String im="assets/images/";
        Bitmap c=bmpFromAsset(im+"clouds.png");
        for(int i=0;i<=3;i++)
            bmps[i]=Bitmap.createBitmap(c,0,i*64,128,64);
        bmps[4]=scale(bmpFromAsset(im+"frog.png"),dip(2f/3f));
        bmps[5]=scale(bmpFromAsset(im+"ground.png"),dip(5f/3f));
        bmps[6]=scale(bmpFromAsset(im+"pipe.png"),dip(1f));
        Matrix m=new Matrix();
        m.postRotate(180);
        bmps[7]=Bitmap.createBitmap(bmps[6],0,0,bmps[6].getWidth(),bmps[6].getHeight(),m,false);
        FROG=new RectObj(0,0,bmps[4].getWidth(),bmps[4].getHeight(),0,0);
        PIPE=new RectObj(0,0,bmps[6].getWidth(),bmps[6].getHeight(),0,0);
		sp = new SoundPool(15, AudioManager.STREAM_MUSIC, 0);
        hm = new HashMap<String,Integer>();
		File f=ctx.getDir("FlappyFrog_data",ctx.MODE_PRIVATE);
		meter=100;
		if(!f.exists()||f.list().length==0)
		{
			f.mkdirs();
			for(int i=1;i<=9;i++)exFile("assets/sounds/hurt"+i+".mp3",f+"/hurt"+i+".mp3");
			meter=200;
			for(int i=1;i<=10;i++)exFile("assets/sounds/score"+i+".mp3",f+"/score"+i+".mp3");
			meter=300;
			exFile("assets/sounds/flap.mp3",f+"/flap.mp3");
			exFile("assets/sounds/bgm.mp3",f+"/bgm.mp3");
			meter=350;
		}
		meter=400;
		for(int i=1;i<=9;i++)hm.put("hurt"+i,sp.load(f+"/hurt"+i+".mp3",0));
		meter=450;
		for(int i=1;i<=10;i++)hm.put("score"+i,sp.load(f+"/score"+i+".mp3",0));
		meter=480;
		hm.put("flap",sp.load(f+"/flap.mp3",0));
		bgm=new MediaPlayer();
		bgm.setDataSource(f+"/bgm.mp3");
		bgm.setOnCompletionListener(this);
		bgm.prepare();
		meter=490;
		for(int i=0;i<5;i++)
			CLOUDS.add(new RectObj(-10,-10,5,5,0,0));
		for(int i=0;i<15;i++)
			PIPES.add(new RectObj(-10,-10,5,5,0,0));
			meter=501;
		resetMap(view);
	}
	@Override
	public void onButtonDown(int code)
	{
		if(code==Window.ButtonCode.CLOSE)
		{
			rendering=false;
			bgm.stop();
			bgm.release();
			sp.release();
		}
		if(code==Window.ButtonCode.MIN)
		{
			boolean b=window.getMin();
			if(b)rendering=false;
			else startRender();
		}
	}
	@Override
	public void onCompletion(MediaPlayer p1)
	{
		bgm.seekTo(0);
		bgm.start();
	}
	public void exFile(String zip,String to)throws Exception
	{
		FileOutputStream s=new FileOutputStream(to);
		InputStream u=loadAsset(zip);
		byte[] b=new byte[1024];
		int k=0;
		while((k=u.read(b))!=-1)s.write(b,0,k);
		s.flush();
		s.close();
		u.close();
	}
	public void playSound(String sound)
	{
		sp.play(hm.get(sound),1,1,0,0,1f);
	}
	@Override
    public boolean onTouch(View v,MotionEvent e)
    {
        if(NOW==0)return false;
        if(e.getAction()==MotionEvent.ACTION_DOWN)
        {
            if(bgmObj.contains(e.getX(),e.getY()))
            {
                playBgm=!playBgm;
                if(playBgm)bgm.start();
				else
				{
					bgm.pause();
					bgm.seekTo(0);
				}
				return false;
            }
			if(githubObj.contains(e.getX(),e.getY()))
			{
				API.startService(ctx,new Intent().putExtra("url","https://www.github.com/yzrilyzr"),"com.yzrilyzr.floatingwindow.apps.WebViewer");   
				return false;
			}
			if(NOW==1)
			{
				NOW=2;
				PASS=null;
				FROG.vy=0;
				time=System.currentTimeMillis();
				thisPipes=0;
				FROG.setY(view.getHeight()/2-FROG.height()/2);
			}
			else if(NOW==4&&restartObj!=null&&restartObj.contains(e.getX(),e.getY()))
			{
				NOW=1;
				resetMap(v);
			}
			if(NOW==2)
			{
				FROG.vy=JUMP;
				playSound("flap");
			}
		}
		return false;
    }

	private void resetMap(View v)
	{
		for(int i=0;i<PIPES.size();i+=3)
		{
			RectObj top=PIPES.get(i);
			RectObj bot=PIPES.get(i+1);
			RectObj ppp=PIPES.get(i+2);
			float s=v.getHeight()/3;
			float x=i/3*GAP+PREPARE;
			float yy=new Random().nextInt(v.getHeight()/2);
			top.set(x,yy-PIPE.height(),x+PIPE.width(),yy);
			bot.set(x,yy+s,x+PIPE.width(),yy+s+PIPE.height());
			ppp.set(x,yy,x+PIPE.width(),yy+s);
			top.vx=-SPEED;
		}
	}
    public void startRender()
    {
        if(rendering)return;
        new Thread(this).start();
    }
    public void render(Canvas canvas)
    {
        int vw=view.getWidth(),vh=view.getHeight();
        FROG.setX(vw/3);
		if(NOW==1)FROG.setY(vh/2-FROG.height()+(float)Math.sin(meter/60f)*vh/40f);
        else if(NOW==2||NOW==4)
        {
			if(NOW==2)thisTime=(int)(System.currentTimeMillis()-time)/1000;
            FROG.setY(FROG.top-FROG.vy);
            FROG.vy-=GRAVITY;
			if(FROG.top<0)FROG.setY(0);
			if(FROG.bottom>vh)
			{
				FROG.setY(vh-FROG.height());
				if(NOW==2)NOW=3;
			}
        }
		else if(NOW==3)
        {
            NOW=4;
            totalTime+=thisTime;
            playSound("hurt"+(new Random().nextInt(8)+1));
        }
		if(NOW!=4&&NOW!=0)meter+=SPEED;
		for(int i=0;i<PIPES.size()&&NOW!=1;i+=3)
		{
			RectObj top=PIPES.get(i);
			RectObj bot=PIPES.get(i+1);
			RectObj ppp=PIPES.get(i+2);
			if(NOW==2)
			{
				top.setX(top.left+top.vx);
				bot.setX(top.left);
				ppp.setX(top.left);
				if(top.intersect(FROG)||bot.intersect(FROG))NOW=3;
				else if(ppp.intersect(FROG)&&PASS!=ppp)
				{
					PASS=ppp;
					thisPipes++;
					playSound("score"+(new Random().nextInt(9)+1));
				}
			}
			canvas.drawBitmap(bmps[7],top.left,top.top,paint);
			canvas.drawBitmap(bmps[6],bot.left,bot.top,paint);
			if(top.right<0)
			{
				float s=vh/3;
				float x=0;
				if(i-1>=0)x=PIPES.get(i-1).left+GAP;
				else x=PIPES.get(PIPES.size()-1).left+GAP;
				float yy=new Random().nextInt(vh/2);
				top.set(x,yy-PIPE.height(),x+PIPE.width(),yy);
				bot.set(x,yy+s,x+PIPE.width(),yy+s+PIPE.height());
				ppp.set(x,yy,x+PIPE.width(),yy+s);
				top.vx=-SPEED;
			}
		}
		//drawfrog
		Matrix m=new Matrix();
		if(NOW==4)m.postRotate(180,FROG.width()/2,FROG.height()/2);
		else if(NOW==2&&FROG.vy<0)m.postRotate(-2*FROG.vy,FROG.width()/2,FROG.height()/2);
		m.postTranslate(FROG.left,FROG.top);
		canvas.drawBitmap(bmps[4],m,paint);
		//cloud
        for(int i=0;i<CLOUDS.size();i++)
		{
			RectObj r=CLOUDS.get(i);
			if(NOW!=4)r.setX(r.left+r.vx-SPEED);
			else r.setX(r.left+r.vx);
			m=new Matrix();
			m.postScale(r.width()/128f,r.height()/64f);
			m.postTranslate(r.left,r.top);
			canvas.drawBitmap(bmps[(int)r.vy],m,paint);
			if(r.right<0)
			{
				Random ran=new Random();
				float scale=(float)(Math.random()*CLOUDSCALE)+CLOUDSCALE;
				float xx=ran.nextInt(vw)+vw;
				float yy=ran.nextInt(vh/2);
				r.set(xx,yy,xx+128f*scale,yy+64f*scale);
				r.vx=-CLOUDSPEED-ran.nextInt((int)CLOUDSPEED);
				r.vy=ran.nextInt(4);
			}
		}
		//ground
		float w5=bmps[5].getWidth();
        for(float x=-meter%w5-w5;x<vw;x+=w5)
			canvas.drawBitmap(bmps[5],x,vh-bmps[5].getHeight(),paint);
		//text ui
        paint.setTextSize(dip(18));
		paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(0xff000000);
        canvas.drawText(String.format("+%ds",thisPipes),vw/2,vh/3-paint.getTextSize(),paint);
        paint.setColor(0xffff0000);
        canvas.drawText(String.format("-%ds",thisTime),vw/2,vh/3,paint);
        paint.setTextSize(dip(13));
        if(NOW!=2)canvas.drawText(String.format("累计被续%d秒",totalTime),vw/2,paint.getTextSize(),paint);
        paint.setColor(0xff000000);
		paint.setTextAlign(Paint.Align.LEFT);
		float tw=paint.measureText("\"5\"可奉告");
		if(githubObj==null)githubObj=new RectObj(0,0,tw,paint.getTextSize(),0,0);
		canvas.drawText("\"5\"可奉告",0,paint.getTextSize(),paint);
		paint.setTextAlign(Paint.Align.RIGHT);
        tw=paint.measureText("请州长夫人演唱");
		if(bgmObj==null)bgmObj=new RectObj(vw-tw,0,tw,paint.getTextSize(),0,0);
		bgmObj.setX(vw-tw);
        canvas.drawText("请州长夫人演唱",bgmObj.right,bgmObj.bottom,paint);
		paint.setTextSize(dip(18));
        if(NOW==4)
        {
			paint.setTextAlign(Paint.Align.CENTER);
            paint.setColor(0xff0000000);
            canvas.drawText(String.format("我为长者续命%d秒",thisPipes),vw/2,vh/2,paint);
            canvas.drawText(String.format("志己的生命减少%d秒",thisTime),vw/2,vh/2+paint.getTextSize(),paint);
            canvas.drawText(String.format("而且这个效率efficiency:%d%s",(int)(((float)thisPipes/(float)thisTime)*100),"%"),vw/2,vh/2+2*paint.getTextSize(),paint);
            paint.setTextSize(dip(21));
            tw=paint.measureText("重新续");
            if(restartObj==null)restartObj=new RectObj(vw/2-tw/2,vh*3/4,tw,paint.getTextSize(),0,0);
			restartObj.setX(vw/2-tw/2);
			restartObj.setY(vh*3/4);
			canvas.drawText("重新续",restartObj.centerX(),restartObj.bottom,paint);
        }
        if(NOW==0)
        {
            canvas.drawColor(0xff000000);
            paint.setColor(0xffffffff);
            paint.setTextSize(dip(18));
			paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("[微小的提示]",vw/2,vh/4,paint);
            canvas.drawText("为了获得坠好的游戏体验，请:",vw/2,vh/4+paint.getTextSize(),paint);
            canvas.drawText("打开音量",vw/2,vh/4+2*paint.getTextSize(),paint);
            canvas.drawText("穿上红色的衣服",vw/2,vh/4+3*paint.getTextSize(),paint);
            paint.setColor(0xffff0000);
            paint.setTextSize(dip(20));
            canvas.drawText("Loading…",vw/2,vh/2+paint.getTextSize(),paint);
            canvas.drawText(String.format("历史的行程:%d%s",(int)(meter/5),"%"),vw/2,vh/2+3*paint.getTextSize(),paint);
            if(meter>500)
			{
				NOW=1;
				meter=0;
			}
        }
    }

    public class RectObj extends RectF
    {
		float vx,vy;
        public RectObj(float x,float y,float w,float h,float vx,float vy)
        {
            super(x,y,x+w,y+h);
			this.vx=vx;
			this.vy=vy;
        }
		public void setY(float y)
		{
			bottom=y+height();
			top=y;
		}
		public void setX(float x)
		{
			right=x+width();
			left=x;
		}
		@Override
		public boolean intersect(RectF r)
		{
			if(Math.max(left,r.left)<Math.min(right,r.right)&&
			   Math.max(top,r.top)<Math.min(bottom,r.bottom))return true;
			return false;
		}

    }
}
