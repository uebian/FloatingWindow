package com.yzrilyzr.floatingwindow;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.XposedBridge;

public class XposedHook implements IXposedHookLoadPackage
{
	@Override
	public void handleLoadPackage(XC_LoadPackage.LoadPackageParam p1) throws Throwable
	{
		XposedBridge.log("#FW CALLED");
		//			XposedHelpers.findAndHookMethod(Class.forName("libcore.io.BlockGuardOs"),"open",String.class,int.class,int.class,new XC_MethodHook(){
//					@Override
//					protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable
//					{
//						String s=new File((String)param.args[0]).getAbsolutePath();//请求的文件
//						String r=Environment.getExternalStorageDirectory().getAbsolutePath();//存储目录
//						//if(s.startsWith(r))XposedBridge.log((String)param.args[0]+"  ;  "+param.args[1]+"  ;  "+param.args[2]);
//						String b=new File("/sdcard/保护文件夹").getAbsolutePath();//保护目录
//						if(s.startsWith(b)){
//							XposedBridge.log("已阻止 "+lpparam.packageName+" 操作保护文件夹");
//							param.setThrowable(new SecurityException("<文件保护>你没有权限操作这个文件"));
//						}
//					}
//				});
//			XposedHelpers.findAndHookMethod(Environment.class,"getExternalStorageDirectory",new XC_MethodHook(){
//					@Override
//					protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable
//					{
//						param.setResult(new File("/sdcard/应用数据文件夹/"+lpparam.packageName));
//					}
//				});
//			XposedHelpers.findAndHookMethod(Environment.class,"getRootDirectory",new XC_MethodHook(){
//					@Override
//					protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable
//					{
//						param.setResult(new File("/sdcard/应用数据文件夹"));
//					}
//				});
//			XposedHelpers.setStaticObjectField(Environment.class,"DIRECTORY_DCIM","/sdcard/应用数据文件夹/拍的照片");
		
	}
}
