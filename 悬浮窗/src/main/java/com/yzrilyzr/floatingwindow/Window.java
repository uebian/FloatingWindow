package com.yzrilyzr.floatingwindow;
import android.graphics.*;
import android.view.*;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import com.yzrilyzr.floatingwindow.view.ScaleWin;
import com.yzrilyzr.icondesigner.VECfile;
import com.yzrilyzr.icondesigner.VecView;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.ui.myTextView;
import com.yzrilyzr.ui.uidata;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParser;
public class Window implements View.OnClickListener,View.OnTouchListener,View.OnLongClickListener
{
    public static final ArrayList<Window> windowList=new ArrayList<Window>();
	private ArrayList<Window> child=new ArrayList<Window>();
	private Window parent;
	private static Window top;
	private Context ctx;
    private WindowManager window;
    private WindowManager.LayoutParams windowParam;
    private ViewGroup winView,contentView,titleBar,mask;
    private VecView icon;
	private Bitmap iconBmp;
    private myTextView title;
    private boolean focusable=true,lfocusable=true,maxwin=false,minwin=false;
    private VecView addButton2,buttonMinWin,buttonMaxWin,buttonCloseWin,minButton,addButton;
    private LinearLayout.LayoutParams outline;
	private byte outlineW=0;//1:l,2:lb,3:b,4:rb,5:r
    private int width=-2,height=-2;
    private OnButtonDown onButtonDown;
	private OnPositionChanged onPositionChanged;
	private OnCrash onCrash;
	private OnSizeChanged onSizeChanged;
    private WindowManager scaleWindow;
	private ScaleWin scaleView;
    private boolean resize=true,showing=false,canFocus=true;
	private static int[] anim=new int[]{
	android.R.style.Animation_Dialog,
	android.R.style.Animation_InputMethod,
	android.R.style.Animation_Toast,
	android.R.style.Animation_Translucent
	};
	public static boolean nolimit=true,crashdialog=false,startonboot=true,anr=false,notify=true,usejs=false;
	public static boolean xposed=false,devmode=false;
	public static int type=WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
    public Window(Context ctx,int widt,int heigh)
    {
		if(widt==-2||heigh==-2)util.toast("窗口大小不建议为-2");
        this.ctx=ctx;
        width=widt;
        height=heigh;
        window=(WindowManager)ctx.getSystemService(ctx.WINDOW_SERVICE);
        windowParam=new WindowManager.LayoutParams(
		width,height,
		type,
		WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|
		(nolimit?WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS|WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN:0)|
		WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED|WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
		PixelFormat.RGBA_8888
        );
        windowParam.gravity=Gravity.LEFT|Gravity.TOP;
        winView=(ViewGroup)LayoutInflater.from(ctx).inflate(R.layout.window_main,null);
        icon=(VecView) winView.findViewById(R.id.mainwindowImageView1);
        title=(myTextView) winView.findViewById(R.id.mainwindowTextView1);
		addButton=(VecView) winView.findViewById(R.id.mainwindowAddButton);
		addButton2=(VecView) winView.findViewById(R.id.mainwindowButton1);
        buttonMinWin=(VecView) winView.findViewById(R.id.mainwindowButton2);
        buttonMaxWin=(VecView) winView.findViewById(R.id.mainwindowButton3);
        buttonCloseWin=(VecView) winView.findViewById(R.id.mainwindowButton4);
        addButton.setOnClickListener(this);
        addButton.setOnLongClickListener(this);
		addButton2.setOnClickListener(this);
        addButton2.setOnLongClickListener(this);
        buttonMinWin.setOnClickListener(this);
        buttonMinWin.setOnLongClickListener(this);
        buttonMaxWin.setOnClickListener(this);
        buttonMaxWin.setOnLongClickListener(this);
        buttonCloseWin.setOnClickListener(this);
        buttonCloseWin.setOnLongClickListener(this);
		addButton.setOnTouchListener(null);
        addButton2.setOnTouchListener(null);
        buttonMinWin.setOnTouchListener(null);
        buttonMaxWin.setOnTouchListener(null);
        buttonCloseWin.setOnTouchListener(null);
        minButton=(VecView) winView.findViewById(R.id.mainwindowButton5);
        minButton.setOnClickListener(this);
        minButton.setOnTouchListener(this);
        titleBar=(ViewGroup) winView.findViewById(R.id.mainTitleBar);
        titleBar.setOnTouchListener(this);
        contentView=(ViewGroup) winView.findViewById(R.id.mainContent);
        winView.setOnTouchListener(this);
		outline=(LinearLayout.LayoutParams)contentView.getLayoutParams();
        minButton.setImageResource(R.drawable.icon);
        winView.setBackgroundDrawable(new Back());
		if(width>0&&height>0)
		{
			windowParam.x=(util.getScreenWidth()-width)/2;
			windowParam.y=(util.getScreenHeight()-height)/2;
		}
    }

	public void setResize(boolean resize)
	{
		this.resize = resize;
	}

	public boolean isResize()
	{
		return resize;
	}

	public void setShowing(boolean showing)
	{
		this.showing = showing;
	}

	public boolean isShowing()
	{
		return showing;
	}

	public Window setCanFocus(boolean canFocus)
	{
		this.canFocus = canFocus;
		focusable=canFocus;
		windowParam.flags=
		(focusable?windowParam.FLAG_NOT_TOUCH_MODAL:windowParam.FLAG_NOT_FOCUSABLE)|
		(maxwin||minwin?0:(nolimit?WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS|WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN:0))|
		WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED|WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
		return this;
	}
	public Window setColor(int c)
	{
		Back b=(Window.Back)winView.getBackground();
		b.setColor(c);
		return this;
	}
	public Window setBColor(int c)
	{
		Back b=(Window.Back)winView.getBackground();
		b.setBcolor(c);
		return this;
	}
	public boolean isCanFocus()
	{
		return canFocus;
	}
	public Window setAddButton(String vecAsset)
	{
		addButton.setVisibility(vecAsset==null?8:0);
		setIcon(addButton,null,vecAsset);
		return this;
	}
	public Window setAddButton(Drawable b)
	{
		addButton.setVisibility(b==null?8:0);
		setIcon(addButton,b,null);
		return this;
	}
	public Window setAddButton2(String vecAsset)
	{
		addButton2.setVisibility(vecAsset==null?8:0);
		setIcon(addButton2,null,vecAsset);
		return this;
	}
	public Window setAddButton2(Drawable b)
	{
		addButton2.setVisibility(b==null?8:0);
		setIcon(addButton2,b,null);
		return this;
	}
	public Window setOnButtonDown(OnButtonDown onButtonDown)
	{
		this.onButtonDown = onButtonDown;
		return this;
	}
	public OnButtonDown getOnButtonDown()
	{
		return onButtonDown;
	}
	public Window setOnPositionChanged(OnPositionChanged onPositionChanged)
	{
		this.onPositionChanged = onPositionChanged;
		return this;
	}
	public OnPositionChanged getOnPositionChanged()
	{
		return onPositionChanged;
	}
	public Window setOnCrash(OnCrash onCrash)
	{
		this.onCrash = onCrash;
		return this;
	}
	public OnCrash getOnCrash()
	{
		return onCrash;
	}
	public Window setOnSizeChanged(OnSizeChanged onSizeChanged)
	{
		this.onSizeChanged = onSizeChanged;
		return this;
	}
	public OnSizeChanged getOnSizeChanged()
	{
		return onSizeChanged;
	}
    public Window setBar(int min,int max,int close)
    {
        buttonMinWin.setVisibility(min);
        buttonMaxWin.setVisibility(max);
        buttonCloseWin.setVisibility(close);
        return this;
    }
    @Override
    public boolean onLongClick(View p1)
    {
        // TODO: Implement this method
        if(child.size()!=0)
		{
			return true;
		}
		switch(p1.getId())
        {
            case R.id.mainwindowButton1:
                if(onButtonDown!=null)onButtonDown.onButtonDown(ButtonCode.ADD2_LONG);
                break;
            case R.id.mainwindowButton2:
                if(onButtonDown!=null)onButtonDown.onButtonDown(ButtonCode.MIN_LONG);
                break;
            case R.id.mainwindowButton3:
                if(onButtonDown!=null)onButtonDown.onButtonDown(ButtonCode.MAX_LONG);
                break;
            case R.id.mainwindowButton4:
                if(onButtonDown!=null)onButtonDown.onButtonDown(ButtonCode.CLOSE_LONG);
                break;
            case R.id.mainwindowButton5:
                if(onButtonDown!=null)onButtonDown.onButtonDown(ButtonCode.MIN_LONG);
                break;
			case R.id.mainwindowAddButton:
                if(onButtonDown!=null)onButtonDown.onButtonDown(ButtonCode.ADD_LONG);
                break;
        }
        return true;
    }
    public Window setMessage(String msg)
	{
		ScrollView s=new ScrollView(ctx);
        myTextView mtv=new myTextView(ctx);
        mtv.setText(msg);
        s.addView(mtv);
		addView(s);
        //setSize(util.dip2px(240),util.dip2px(360));
        return this;
    }
    public Window show(boolean ani)
    {
		if(showing)return this;
		showing=true;
		if(ani)windowParam.windowAnimations=anim[util.random(0,anim.length)];
       	window.addView(winView,windowParam);
		windowList.add(this);
		titleBar.setFocusable(true);
		titleBar.setFocusableInTouchMode(true);
		titleBar.requestFocus();
		if(parent!=null)
		{
			parent.child.add(this);
			//parent.mask.setVisibility(0);
			if(parent.winView.getBackground()!=null&&!parent.canFocus)
			{
				int c=uidata.getMainSColor(0.15f);
				if(parent.minwin)c=uidata.MAIN;
				Back b=(Window.Back)parent.winView.getBackground();
				b.setColor(c);
				b.setRadius(parent.maxwin?0:uidata.UI_RADIUS);
			}
		}
		checkTop();
		checkFocus();
		return this;
    }
	public Window show()
	{
		return show(true);
	}
	public Window dismiss()
    {
		if(!showing)return this;
		showing=false;
        window.removeViewImmediate(winView);
		windowList.remove(this);
		if(parent!=null)parent.child.remove(this);
		checkTop();
		checkFocus();
		if(parent!=null)
		{
			parent.setFocusable(true);
			//parent.mask.setVisibility(8);
			if(parent.winView.getBackground()!=null&&!parent.canFocus)
			{
				int c=uidata.MAIN;
				Back b=(Window.Back)parent.winView.getBackground();
				b.setColor(c);
				b.setRadius(parent.maxwin?0:uidata.UI_RADIUS);
			}
		}
		if(top!=null)top.setFocusable(true);
        return this;
    }
	private void setIcon(VecView a,Drawable b,String c)
	{
		if(c==null)
		{
			a.setImageVec(null);
			a.setImageDrawable(b);
		}
		else
		{
			a.setImageVec(c);
		}
	}
	private void checkFocus()
	{
		checkFocus(true);
	}
	private void checkFocus(boolean t)
	{
		if(child.size()!=0)
		{
			return;
		}
		boolean topfl=false;
		for(Window w:windowList)
			if(w==this&&!w.minwin)
			{
				if(w.isShowing()&&top!=w&&t)
				{
					window.removeViewImmediate(winView);
					window.addView(winView,windowParam);
					topfl=true;
				}
				w.setFocusable(true);
			}
			else w.setFocusable(false);
		if(topfl)
		{
			windowList.remove(this);
			windowList.add(this);
			top=this;
		}
	}
	private static void checkTop()
	{
		top=null;
		for(int i=windowList.size()-1;i>=0;i--)
		{
			Window w=windowList.get(i);
			if(w.minwin||!w.isShowing())continue;
			top=w;
			break;
		}
	}
	public Window setParent(Intent e)
	{
		int k=e.getIntExtra("parentIndex",-1);
		if(k!=-1)setParent(windowList.get(k));
		return this;
	}
	public Window setParent(Window p)
	{
		parent=p;
		return this;
	}
    public Window setIcon(Drawable b)
    {
        setIcon(icon,b,null);
		setIcon(minButton,b,null);
		int w=util.px(30);
		iconBmp=Bitmap.createBitmap(w,w,Bitmap.Config.ARGB_8888);
		Canvas c=new Canvas(iconBmp);
		b.setBounds(0,0,w,w);
		b.draw(c);
        return this;
    }
    public Window setIcon(String vecAsset)
	{
		setIcon(icon,null,vecAsset);
		setIcon(minButton,null,vecAsset);
		int w=util.px(30);
		try
		{
			iconBmp=VECfile.createBitmap(ctx,vecAsset,w,w);
		}
		catch (Exception e)
		{}
		return this;
	}
	public Bitmap getIcon()
	{
		return iconBmp;
	}
	public String getTitle()
	{
		return title.getText().toString();
	}
	public View addView(int id)
	{
		View v=LayoutInflater.from(ctx).inflate(id,getContentView(),false);
		addView(v);
		return v;
	}
	public View addView(XmlPullParser parser)
	{
		View v=LayoutInflater.from(ctx).inflate(parser,getContentView(),false);
		addView(v);
		return v;
	}
    private void setFocusable(boolean f)
    {
		if(canFocus)
		{
			focusable=f;
			windowParam.flags=
			(focusable?windowParam.FLAG_NOT_TOUCH_MODAL:windowParam.FLAG_NOT_FOCUSABLE)|
			(maxwin||minwin?0:(nolimit?WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS|WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN:0))|
			WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED|WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
			window.updateViewLayout(winView,windowParam);
			if(winView.getBackground()!=null)
			{
				int c=f?uidata.MAIN:uidata.getMainSColor(0.15f);
				if(minwin)c=uidata.MAIN;
				Back b=(Window.Back)winView.getBackground();
				b.setColor(c);
				b.setRadius(maxwin?0:uidata.UI_RADIUS);
			}
		}
    }
    public Window setPosition(float x,float y)
    {
		if(onPositionChanged!=null)onPositionChanged.onPositionChanged((int)x,(int)y);
        windowParam.x=(int) x;
        windowParam.y=(int) y;
		return this;
    }
    public boolean getMin()
    {
        return minwin;
    }
    public boolean getMax()
    {
        return maxwin;
    }
    public boolean getFocus()
    {
        return focusable;
    }
    public WindowManager.LayoutParams getLayoutParams()
    {
        return windowParam;
    }
    public Window setLayoutParams(WindowManager.LayoutParams l)
    {
        window.updateViewLayout(winView,l);
        windowParam=l;
        return this;
    }
    public Window setTitle(String t)
    {
        title.setText(t);
        return this;
    }
    public Window setCanResize(boolean resiz)
    {
        resize=resiz;
        return this;
    }
    public Window setMaxWin(boolean b)
    {
		if(onSizeChanged!=null)
			if(b)onSizeChanged.onSizeChanged(util.getScreenWidth(),util.getScreenHeight(),width,height);
			else onSizeChanged.onSizeChanged(width,height,util.getScreenWidth(),util.getScreenHeight());
		maxwin=b;
		windowParam.flags=
		(focusable?windowParam.FLAG_NOT_TOUCH_MODAL:windowParam.FLAG_NOT_FOCUSABLE)|
		(maxwin||minwin?0:(nolimit?WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS|WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN:0))|
		WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED|WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
		windowParam.width=b?-1:width;
        windowParam.height=b?-1:height;
        buttonMaxWin.setImageVec(b?"restorewin":"maxwin");
		LinearLayout.LayoutParams lp=(LinearLayout.LayoutParams) contentView.getLayoutParams();
		int mg=b?0:util.px(4);
		lp.setMargins(mg,0,mg,mg);
		Back pb=(Window.Back)winView.getBackground();
		pb.setColor(uidata.MAIN);
		pb.setRadius(b?0:uidata.UI_RADIUS);
		window.updateViewLayout(winView,windowParam);
		checkFocus();
        return this;
    }
    public Window toggleMaxWin()
    {
        maxwin=!maxwin;
        setMaxWin(maxwin);
        return this;
    }
    public ViewGroup getContentView()
    {
        return contentView;
    }
    public ViewGroup getMainView()
    {
        return winView;
    }
    public Window setMinWin(boolean b)
    {
		minwin=b;
        if(b)
        {
			winView.removeView(contentView);
            titleBar.setVisibility(8);
            minButton.setVisibility(0);
            windowParam.width=-2;
            windowParam.height=-2;
			lfocusable=focusable;
            setFocusable(false);
		}
        else
        {
            winView.addView(contentView);
            titleBar.requestFocus();
			titleBar.setVisibility(0);
            minButton.setVisibility(8);
            setMaxWin(maxwin);
            setFocusable(lfocusable);
        }
		dismiss();
		show();
        update();
        return this;
    }
    public Window addView(View view)
    {
        contentView.addView(view);
        return this;
    }
    public Window update()
    {
        window.updateViewLayout(winView,windowParam);
        return this;
    }
    public Window toggleMinWin()
    {
        minwin=!minwin;
        setMinWin(minwin);
        return this;
    }
	public Window setSize(int w,int h)
    {
        if(onSizeChanged!=null)onSizeChanged.onSizeChanged(w,h,width,height);
        width=w;
        height=h;
        windowParam.width=w;
        windowParam.height=h;
        update();
        return this;
    }
    @Override
    public void onClick(View p1)
    {
        // TODO: Implement this method
        if(child.size()!=0)
		{
			return;
		}
		switch(p1.getId())
        {
            case R.id.mainwindowButton1:
                if(onButtonDown!=null)onButtonDown.onButtonDown(ButtonCode.ADD2);
                break;
            case R.id.mainwindowButton2:
                if(onButtonDown!=null)onButtonDown.onButtonDown(ButtonCode.MIN);
                toggleMinWin();
                break;
            case R.id.mainwindowButton3:
                if(onButtonDown!=null)onButtonDown.onButtonDown(ButtonCode.MAX);
                toggleMaxWin();
                break;
            case R.id.mainwindowButton4:
                if(onButtonDown!=null)onButtonDown.onButtonDown(ButtonCode.CLOSE);
                dismiss();
                break;
            case R.id.mainwindowButton5:
                if(onButtonDown!=null)onButtonDown.onButtonDown(ButtonCode.MIN);
                toggleMinWin();
                break;
			case R.id.mainwindowAddButton:
                if(onButtonDown!=null)onButtonDown.onButtonDown(ButtonCode.ADD);
                break;
        }
    }
    @Override
    public boolean onTouch(View p1, MotionEvent p2)
    {
        // TODO: Implement this method
		int act=p2.getAction();
		if(act==MotionEvent.ACTION_OUTSIDE&&focusable)setFocusable(false);
        else if(act==MotionEvent.ACTION_DOWN)
		{
			paramX = windowParam.x;
			paramY = windowParam.y;
			lastX2=paramX;
			lastY2=paramY;
		}
		else if(act==MotionEvent.ACTION_UP&&
		Math.abs(windowParam.x-lastX2)<util.px(5)&&
		Math.abs(windowParam.y-lastY2)<util.px(5))
			checkFocus();
		if(maxwin&&!minwin)return true;
		if(p1==minButton||p1==titleBar)return moveableView(p1,p2);
		if(child.size()!=0)
		{
			return true;
		}
		int x=(int) p2.getX(),
		y=(int) p2.getY(),
		rx=(int) p2.getRawX(),
		ry=(int) p2.getRawY()-util.getStatusBarHeight();
        if(resize)
            switch (act)
            {
                case MotionEvent.ACTION_DOWN:
					float k=3;
					if(x<outline.leftMargin*k)
						if(y>windowParam.height-outline.bottomMargin*k)outlineW=2;
						else outlineW=1;
					else if(x>windowParam.width-outline.rightMargin*k)
						if(y>windowParam.height-outline.bottomMargin*k)outlineW=4;
						else outlineW=5;
					else if(y>windowParam.height-outline.bottomMargin*k)outlineW=3;
					else outlineW=0;
					if(outlineW!=0)
					{
						util.toast("拖动以改变窗口大小");
						scaleWindow=(WindowManager)ctx.getSystemService(ctx.WINDOW_SERVICE);
						WindowManager.LayoutParams swindowParam=new WindowManager.LayoutParams(
						-1,-1,
						type,
						WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|
						(nolimit?WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS|WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN:0),
						PixelFormat.RGBA_8888
						);
						swindowParam.gravity=Gravity.LEFT|Gravity.TOP;
						scaleView=new ScaleWin(ctx);
						scaleWindow.addView(scaleView,swindowParam);
						scaleView.set(windowParam.x,windowParam.y,windowParam.width,windowParam.height);
					}
					break;
                case MotionEvent.ACTION_MOVE:
					switch(outlineW)
					{
						case 1:
							windowParam.width=windowParam.x+windowParam.width-rx;
							windowParam.x=rx;
							break;
						case 2:
							windowParam.height=ry-windowParam.y;
							windowParam.width=windowParam.x+windowParam.width-rx;
							windowParam.x=rx;
							break;
						case 3:
							windowParam.height=ry-windowParam.y;
							break;
						case 4:
							windowParam.width=rx-windowParam.x;
							windowParam.height=ry-windowParam.y;
							break;
						case 5:
							windowParam.width=rx-windowParam.x;
							break;
					}
					if(outlineW!=0)
					{
						scaleView.set(windowParam.x,windowParam.y,windowParam.width,windowParam.height);
					}
					break;
                case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_CANCEL:
					if(outlineW!=0)
					{
						scaleWindow.removeView(scaleView);
						setSize(windowParam.width,windowParam.height);
						update();
					}
                    break;
            }
        return true;
    }
    private int lastX, lastY;
    private int paramX, paramY;
    private int lastX2,lastY2;
    public boolean moveableView(View p1,MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                paramX = windowParam.x;
                paramY = windowParam.y;
                lastX2=paramX;
                lastY2=paramY;
				break;
            case MotionEvent.ACTION_MOVE:
                int dx = (int) event.getRawX() - lastX;
                int dy = (int) event.getRawY() - lastY;
                windowParam.x = paramX + dx;
                windowParam.y = paramY + dy;
				if(!nolimit)
				{
					windowParam.x=(int) util.limit(windowParam.x,0,util.getScreenWidth()-(minwin?minButton.getWidth():width));
					windowParam.y=(int) util.limit(windowParam.y,0,util.getScreenHeight()-(minwin?minButton.getHeight():height));
				}
				else windowParam.y=(int) util.limit(windowParam.y,0,util.getScreenHeight());
                window.updateViewLayout(winView,windowParam);
				if(onPositionChanged!=null)onPositionChanged.onPositionChanged(windowParam.x,windowParam.y);
                break;
            case MotionEvent.ACTION_UP:
                if(Math.abs(windowParam.x-lastX2)<util.px(5)&&
				Math.abs(windowParam.y-lastY2)<util.px(5))
                {
                    if(p1.getId()==R.id.mainwindowButton5)
                    {
                        if(onButtonDown!=null)onButtonDown.onButtonDown(ButtonCode.MIN);
                        toggleMinWin();
                    }
                    return false;
                }
                break;
        }
        return true;
    }
	public static final void saveData()
	{
		util.getSPWrite()
		.putBoolean("nolimit",nolimit)
		.putInt("wintype",type)
		.putBoolean("crashdialog",crashdialog)
		.putBoolean("xposed",xposed)
		.putBoolean("anr",anr)
		.putBoolean("notify",notify)
		.putBoolean("usejs",usejs)
		.putBoolean("startonboot",startonboot)
		.putBoolean("devmode",devmode)
		.commit();
	}
	static boolean read=false;
	public static final void readData()
	{
		if(read)return;
		read=true;
		SharedPreferences sp=util.getSPRead();
		nolimit=sp.getBoolean("nolimit",nolimit);
		anr=sp.getBoolean("anr",anr);
		xposed=sp.getBoolean("xposed",xposed);
		devmode=sp.getBoolean("devmode",devmode);
		usejs=sp.getBoolean("usejs",usejs);
		notify=sp.getBoolean("notify",notify);
		crashdialog=sp.getBoolean("crashdialog",crashdialog);
		startonboot=sp.getBoolean("startonboot",util.sup);
		type=sp.getInt("wintype",WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
	}
	public static final class ButtonCode
    {
        public static final int MIN=0,MAX=1,ADD2=2,CLOSE=3,ADD2_LONG=4,MIN_LONG=5,MAX_LONG=6,CLOSE_LONG=7,ADD=8,ADD_LONG=9;
    }
    public interface OnButtonDown
    {
        public void onButtonDown(int code);
    }
	public interface OnPositionChanged
    {
        public void onPositionChanged(int x,int y);
    }
	public interface OnSizeChanged
    {
        public void onSizeChanged(int w,int h,int oldw,int oldh);
    }
	public interface OnCrash
    {
        public void onCrash(Throwable e)throws Throwable;
    }
	class Back extends Drawable
	{
		protected Paint paint;
		protected RectF rectF,rectF2;
		protected float radius;
		protected int margin=0,color=uidata.MAIN,bcolor=uidata.BACK;
		public Back()
		{
			this.radius=util.px(uidata.UI_RADIUS);
			paint=new Paint(Paint.ANTI_ALIAS_FLAG);
		}

		public void setBcolor(int bcolor)
		{
			this.bcolor = bcolor;
		}

		public int getBcolor()
		{
			return bcolor;
		}
		public void setMargin(int margin)
		{
			this.margin = margin;
		}
		public int getMargin()
		{
			return margin;
		}

		public void setColor(int color)
		{
			this.color = color;
		}

		public int getColor()
		{
			return color;
		}

		public void setRadius(float radius)
		{
			this.radius = radius;
		}

		public float getRadius()
		{
			return radius;
		}
		public void setLayer(View v)
		{
			if(v==null)paint.setShadowLayer(0,0,0,0);
			else
			{
				margin=util.px(4);
				paint.setShadowLayer(util.px(1.5f),0,util.px(2),0x50000000);
				v.setLayerType(View.LAYER_TYPE_SOFTWARE,null);
			}
		}
		@Override
		public void setBounds(int left, int top, int right, int bottom)
		{
			super.setBounds(left, top, right, bottom);
			rectF = new RectF(margin,margin, right-margin, bottom-margin);
			rectF2 = new RectF(margin+outline.leftMargin,margin+titleBar.getHeight(), right-margin-outline.rightMargin, bottom-margin-outline.bottomMargin);
		}
		@Override
		public void draw(Canvas canvas)
		{
			paint.setColor(color);
			canvas.drawRoundRect(rectF,radius,radius,paint);
			if(!minwin)
			{
				paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
				paint.setColor(bcolor);
				canvas.drawRect(rectF2,paint);
				paint.setXfermode(null);
				canvas.drawRect(rectF2,paint);
			}
			//int sc=canvas.saveLayer(rectF,paint2,Canvas.ALL_SAVE_FLAG);
			//canvas.clipRect(rectF);
			//paint2.setColor(color);
		}
		@Override
		public void setAlpha(int alpha)
		{
			color=(color|0xff000000)-0xff000000+alpha;
		}
		@Override
		public void setColorFilter(ColorFilter colorFilter)
		{
			paint.setColorFilter(colorFilter);
		}
		@Override
		public int getOpacity()
		{
			return PixelFormat.TRANSLUCENT;
		}
	}
}
