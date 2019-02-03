package com.yzrilyzr.floatingwindow.pluginapi;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.view.ViewGroup;
import android.graphics.Bitmap;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class Window extends ProxyAPI implements OnClickListener ,OnTouchListener ,OnLongClickListener
{
    public Window addView(android.view.View p1)
    {
        invoke("addView",new Class[]{android.view.View.class},p1);
		return this;
    }

    public Window dismiss()
    {
        invoke("dismiss");
		return this;
    }

    public ViewGroup getContentView()
    {
        return (ViewGroup)invoke("getContentView");
    }

    public boolean getFocus()
    {
        return (boolean)invoke("getFocus");
    }

    public Bitmap getIcon()
    {
        return (Bitmap)invoke("getIcon");
    }

    public LayoutParams getLayoutParams()
    {
        return (LayoutParams)invoke("getLayoutParams");
    }

    public ViewGroup getMainView()
    {
        return (ViewGroup)invoke("getMainView");
    }

    public boolean getMax()
    {
        return (boolean)invoke("getMax");
    }

    public boolean getMin()
    {
        return (boolean)invoke("getMin");
    }

    public java.lang.String getTitle()
    {
        return (String)invoke("getTitle");
    }

    public boolean moveableView(android.view.View p1,android.view.MotionEvent p2)
    {
        return (boolean)invoke("moveableView",new Class[]{android.view.View.class,android.view.MotionEvent.class},p1,p2);
    }

    @java.lang.Override()
    public void onClick(android.view.View p1)
    {
        invoke("onClick",new Class[]{android.view.View.class},p1);
    }

    @java.lang.Override()
    public boolean onLongClick(android.view.View p1)
    {
        return (boolean)invoke("onLongClick",new Class[]{android.view.View.class},p1);
    }

    @java.lang.Override()
    public boolean onTouch(android.view.View p1,android.view.MotionEvent p2)
    {
        return (boolean)invoke("onTouch",new Class[]{android.view.View.class,android.view.MotionEvent.class},p1,p2);
    }

    public Window setBar(int p1,int p2,int p3,int p4)
    {
       	invoke("setBar",new Class[]{int.class,int.class,int.class,int.class},p1,p2,p3,p4);
		return this;
    }

    public Window setCanResize(boolean p1)
    {
        invoke("setCanResize",new Class[]{boolean.class},p1);
		return this;
    }

    public Window setFocusable(boolean p1)
    {
        invoke("setFocusable",new Class[]{boolean.class},p1);
		return this;
    }

    public Window setIcon(android.graphics.drawable.Drawable p1)
    {
        invoke("setIcon",new Class[]{android.graphics.drawable.Drawable.class},p1);
		return this;
    }

    public Window setIcon(java.lang.String p1)
    {
        invoke("setIcon",new Class[]{java.lang.String.class},p1);
		return this;
    }

    public Window setLayoutParams(LayoutParams p1)
    {
        invoke("setLayoutParams",new Class[]{LayoutParams.class},p1);
		return this;
    }

    public Window setMaxWin(boolean p1)
    {
        invoke("setMaxWin",new Class[]{boolean.class},p1);
		return this;
    }

    public Window setMessage(java.lang.String p1)
    {
        invoke("setMessage",new Class[]{java.lang.String.class},p1);
		return this;
    }

    public Window setMinWin(boolean p1)
    {
        invoke("setMinWin",new Class[]{boolean.class},p1);
		return this;
    }

    public Window setPosition(float p1,float p2)
    {
        invoke("setPosition",new Class[]{float.class,float.class},p1,p2);
		return this;
    }

    public Window setSize(int p1,int p2)
    {
        invoke("setSize",new Class[]{int.class,int.class},p1,p2);
		return this;
    }

    public Window setTitle(java.lang.String p1)
    {
        invoke("setTitle",new Class[]{java.lang.String.class},p1);
		return this;
    }

   	public Window setOnButtonDown(final OnButtonDown pp1)
    {
		invokeSetInterface("setOnButtonDown","OnButtonDown",new InvocationHandler(){
			@Override
			public Object invoke(Object p1, Method p2, Object[] p3) throws Throwable
			{
				if(pp1!=null)
					pp1.onButtonDown(p3[0]);
				return null;
			}
		});
        return this;
    }

    public Window setOnCrash(final OnCrash pp1)
    {
		invokeSetInterface("setOnCrash","OnCrash",new InvocationHandler(){
			@Override
			public Object invoke(Object p1, Method p2, Object[] p3) throws Throwable
			{
				if(pp1!=null)
					pp1.onCrash((Throwable)p3[0]);
				return null;
			}
		});
        return this;
    }
	public Window setOnPositionChanged(final OnPositionChanged pp1)
    {
		invokeSetInterface("setOnPositionChanged","OnPositionChanged",new InvocationHandler(){
			@Override
			public Object invoke(Object p1, Method p2, Object[] p3) throws Throwable
			{
				if(pp1!=null)
					pp1.onPositionChanged(p3[0],p3[1]);
				return null;
			}
		});
        return this;
    }
	public Window setOnSizeChanged(final OnSizeChanged pp1)
    {
		invokeSetInterface("setOnSizeChanged","OnSizeChanged",new InvocationHandler(){
			@Override
			public Object invoke(Object p1, Method p2, Object[] p3) throws Throwable
			{
				if(pp1!=null)
					pp1.onSizeChanged(p3[0],p3[1],p3[2],p3[3]);
				return null;
			}
		});
        return this;
    }
    public Window show()
    {
        invoke("show");
		return this;
    }

    public Window toggleFocusable()
    {
       	invoke("toggleFocusable");
		return this;
    }

    public Window toggleMaxWin()
    {
        invoke("toggleMaxWin");
		return this;
    }

    public Window toggleMinWin()
    {
        invoke("toggleMinWin");
		return this;
    }

    public Window update()
    {
        invoke("update");
		return this;
    }

    public Window(android.content.Context p1,int p2,int p3)
    {
        super("com.yzrilyzr.floatingwindow.Window",new Class[]{android.content.Context.class,int.class,int.class},p1,p2,p3);
    }


   	public static interface OnButtonDown
    {
        public abstract void onButtonDown(int p1) ;

    }

    public static interface OnPositionChanged
    {
        public abstract void onPositionChanged(int p1,int p2) ;

    }

    public static interface OnCrash
    {
        public abstract void onCrash(java.lang.Throwable p1) throws java.lang.Throwable ;

    }

    public static interface OnSizeChanged
    {
        public abstract void onSizeChanged(int p1,int p2,int p3,int p4) ;

    }
    public static final class ButtonCode
    {
		public static final int ADD = 8;

        public static final int ADD_LONG = 9;

        public static final int CLOSE = 3;

        public static final int CLOSE_LONG = 7;

        public static final int FOCUS = 2;

        public static final int FOCUS_LONG = 4;

        public static final int MAX = 1;

        public static final int MAX_LONG = 6;

        public static final int MIN = 0;

        public static final int MIN_LONG = 5;
    }
}
