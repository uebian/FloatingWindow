package com.yzrilyzr.ui;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import com.yzrilyzr.myclass.util;
import android.graphics.Color;
public final class uidata
{
	public static float TEXTSIZE=12f;
	public static boolean UI_USETYPEFACE=true;
	public static Typeface UI_TYPEFACE=null;
	public static float UI_DENSITY=Resources.getSystem().getDisplayMetrics().density;
	public static float UI_RADIUS=3.0f;
	
	public static int TEXTMAIN=-1447447;
	public static int TEXTBACK=-1;
	public static int CONTROL=-1118482;
	public static int MAIN=-13070228;
	public static int BACK=-12895429;
	public static int ACCENT=-27859;
	public static int BUTTON=-9539986;
	public static int UNENABLED=-2236963;
	
	public static int getAlphacolor(int c,int alpha){
		return (c|0xff000000)-(0x01000000*(0xff-alpha));
	}
	public static int getAFColor(){
		return getAlphacolor(ACCENT,80);
	}
   	public static int getASColor(){
		return getAlphacolor(ACCENT,30);
	}
   	public static int getBFColor(){
		return getAlphacolor(BUTTON,30);
	}
   	public static int getEFColor(){
		return getAlphacolor(UNENABLED,200);
	}
   	public static int getESColor(){
		return getAlphacolor(UNENABLED,150);
	}
	public static int getMainSColor(float p){
		float[] hsv=new float[3];
		Color.colorToHSV(MAIN,hsv);
		hsv[1]=p;
		return Color.HSVToColor(MAIN>>24,hsv);
	}
   	public static final void saveData()
	{
		util.getSPWrite()
			.putBoolean("typeface",UI_USETYPEFACE)
			.putInt("main",MAIN)
			.putInt("back",BACK)
			.putInt("textmain",TEXTMAIN)
			.putInt("textback",TEXTBACK)
			.putInt("control",CONTROL)
			.putInt("accent",ACCENT)
			.putInt("button",BUTTON)
			.putFloat("density",UI_DENSITY)
			.putInt("unenabled",UNENABLED)
			.putFloat("sizetext",TEXTSIZE)
			.putFloat("radius",UI_RADIUS)
			.commit();
	}

	public static final void readData()
	{
		SharedPreferences sp=util.getSPRead();
		UI_TYPEFACE=Typeface.MONOSPACE;//.createFromAsset(ctx.getAssets(),"font.ttf");
		UI_DENSITY=sp.getFloat("density",(float)util.getScreenWidth()/360f);
		UI_USETYPEFACE=sp.getBoolean("typeface",UI_USETYPEFACE);
		MAIN=sp.getInt("main",MAIN);
		BACK=sp.getInt("back",BACK);
		TEXTMAIN=sp.getInt("textmain",TEXTMAIN);
		TEXTBACK=sp.getInt("textback",TEXTBACK);
		CONTROL=sp.getInt("control",CONTROL);
		ACCENT=sp.getInt("accent",ACCENT);
		BUTTON=sp.getInt("button",BUTTON);
		UNENABLED=sp.getInt("unenabled",UNENABLED);
		TEXTSIZE=sp.getFloat("sizetext",TEXTSIZE);
		UI_RADIUS=sp.getFloat("radius",UI_RADIUS);
	}
}
