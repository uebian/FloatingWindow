package com.yzrilyzr.floatingwindow.view;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import com.yzrilyzr.icondesigner.VECfile;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.ui.uidata;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import org.apache.commons.codec.binary.Base64;

public class StarterView extends View
{
    private Paint paint;
    private int progress=0;
    private boolean open=false,isAnim=false,longcli=false,selmoved=false;
    private static float dd=util.px(50),ee=util.px(40);
    private float margin;
    private float kx,ky;
    private Listener listener;
    private static Bitmap[] bmp=new Bitmap[6];
    private RectF rect;
	private long longclick;
	private  float arc=(float)(Math.PI/180f);
	private int SEL=-1,lSel;
	private static String[] tip=new String[]{"添加程序","添加程序","添加程序","菜单","退出"};
	private static String[] pkg=new String[4],
							cls=new String[4];
	public StarterView(Context c)
    {
        super(c);
        paint=new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextAlign(Paint.Align.CENTER);
		margin=util.px(3);
        paint.setShadowLayer(margin,0,margin/3,0x50000000);
        setLayerType(View.LAYER_TYPE_SOFTWARE,null);
        int k=(int)ee;
        try
		{
			bmp[4]=VECfile.createBitmap(c,"exit",k,k);
			bmp[5]=VECfile.createBitmap(c,"class",k,k);
		}
		catch (Exception e)
		{}
		paint.setTextSize(util.px(uidata.TEXTSIZE*1.4f));
    }
	public String getPkg(int i)
	{
		return pkg[i];
	}
	public String getClass(int i)
	{
		return cls[i];
	}
	public static void load(Context ctx)
	{
		SharedPreferences s=util.getSPRead("pluginpicker");
		for(int i=0;i<4;i++)
		{
			pkg[i]=s.getString("pkg"+i,null);
			cls[i]=s.getString("cls"+i,null);
			tip[i]=s.getString("tip"+i,"添加程序");
			String tk=s.getString("ico"+i,null);
			try
			{
				if(tk!=null)
				{
					byte[] b=tk.getBytes();
					b=Base64.decodeBase64(b);
					bmp[i]=BitmapFactory.decodeByteArray(b,0,b.length);
					if(bmp[i]==null)bmp[i]=VECfile.createBitmap(ctx,"add",(int)ee,(int)ee);
					else bmp[i]=bmp[i];
				}
				else bmp[i]=VECfile.createBitmap(ctx,"add",(int)ee,(int)ee);
			}
			catch (Exception e)
			{}
		}
	}
    
    public void open()
    {
		if(uidata.UI_USETYPEFACE)paint.setTypeface(uidata.UI_TYPEFACE);
        if(listener!=null)listener.onAnimStart();
        open=true;
        isAnim=true;
        invalidate();
    }
    public void close()
    {
        open=false;
        isAnim=true;
        invalidate();
    }
    public void setListener(Listener l)
    {
        listener=l;
    }
    public void toggle()
    {
        open=!open;
        if(open)open();
        else close();
    }
    public void setPosition(float x,float y)
    {
        kx=util.limit(x,2f*dd,util.getScreenWidth()-2f*dd);
		ky=util.limit(y,2f*dd,util.getScreenHeight()-2f*dd);
		rect=new RectF(kx-2f*dd,ky-2f*dd,kx+2f*dd,ky+2f*dd);
    }
    @Override
    protected void onDraw(Canvas canvas)
    {
		dd=util.px(50);
		ee=util.px(40);
		paint.setColor(uidata.BACK);
		paint.setShadowLayer(margin,0,margin/3,0x50000000);
		canvas.drawArc(rect,-180,Math.min(progress,225),true,paint);
		paint.setColor(uidata.MAIN);
		if(SEL>=0&&SEL<=4&&!isAnim&&progress>=360)canvas.drawArc(rect,-180+SEL*45,45,true,paint);
		if(progress>225)
		{
			canvas.drawCircle(kx,ky,dd*((float)progress-225f)/180f,paint);
		}
		float R2=4f/3f*dd;
		paint.setShadowLayer(0,0,0,0);
		for(int i=0;i<5;i++)
			if(progress>=45*(i+1))
			{
				double d=arc*(22.5+45*i);
				Matrix Matrix=new Matrix();
				Matrix.postScale(ee/(float)bmp[i].getWidth(),ee/(float)bmp[i].getHeight());
				Matrix.postTranslate((float)(kx-Math.cos(d)*R2)-ee/2,(float)(ky-Math.sin(d)*R2)-ee/2);
				canvas.drawBitmap(bmp[i],Matrix,paint);
			}
		if(progress>=360)
		{
			if(SEL<0||SEL>=tip.length){
				Matrix Matrix=new Matrix();
				Matrix.postScale(ee/(float)bmp[5].getWidth(),ee/(float)bmp[5].getHeight());
				Matrix.postTranslate(kx-ee/2,ky-ee/2);
				canvas.drawBitmap(bmp[5],Matrix,paint);
			}
			else
			{
				paint.setColor(uidata.TEXTBACK);
				canvas.drawText(tip[SEL],kx,ky+paint.getTextSize()/2.5f,paint);
			}
		}
		if(isAnim)
		{
			if(progress<360&&open)
			{
				progress+=23;invalidate();
			}
			else if(progress>0&&!open)
			{
				progress-=23;invalidate();
			}
			if(progress<=0)
			{
				isAnim=false;
				progress=0;
				if(listener!=null)listener.onAnimEnd();
			}
			else if(progress>=360)
			{
				progress=360;
				isAnim=false;
			}
		}
    }
    public interface Listener
    {
        public abstract void onItemClick(int which);
        public abstract void onAnimEnd();
        public abstract void onAnimStart();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        // TODO: Implement this method
        if(isAnim)return false;
        float xx=event.getX(),yy=event.getY();
        float rr=(float)Math.sqrt(Math.pow(kx-xx,2)+Math.pow(ky-yy,2));
		if(rr<dd*135f/180f)SEL=5;
		else if(rr<dd*2)
        {
            int de=(int) (Math.asin((xx-kx)/rr)*180f/Math.PI)+90;
            int i=de/45;
            if(yy<ky)SEL=i;
			else if(yy>ky&&de>135&&de<180)SEL=4;
			else SEL=-1;
        }
		else SEL=-1;
		int act=event.getAction();
		if(act==MotionEvent.ACTION_DOWN){
			longcli=false;
			lSel=SEL;
			selmoved=false;
			longclick=System.currentTimeMillis();
		}
		else if(act==MotionEvent.ACTION_MOVE)
		{
			if(lSel!=SEL)selmoved=true;
		}
		else if(act==MotionEvent.ACTION_UP&&!longcli)
		{
			if(listener!=null&&rr<dd*2)listener.onItemClick(SEL);
			close();
		}
		if(!longcli&&System.currentTimeMillis()-longclick>1000&&SEL>=0&&SEL<=3&&!selmoved)
		{
			longcli=true;
			pkg[SEL]=null;
			tip[SEL]="添加程序";
			try
			{
				bmp[SEL]=VECfile.createBitmap(util.ctx,"add",(int)ee,(int)ee);
			}
			catch (Exception e)
			{}
			util.getSPWrite("pluginpicker")
				.putString("pkg"+SEL,null)
				.putString("cls"+SEL,null)
				.putString("tip"+SEL,null)
				.putString("ico"+SEL,null)
				.commit();
			}
		invalidate();
        return true;
    }

}
