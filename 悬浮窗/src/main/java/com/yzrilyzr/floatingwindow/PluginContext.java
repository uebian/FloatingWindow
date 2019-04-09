package com.yzrilyzr.floatingwindow;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.ui.uidata;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class PluginContext extends ContextWrapper
{
	private Resources res;
	private AssetManager am;
	private Context ctx;
	private String pkg;
	private Intent intent;
	public PluginContext(Context ctx,String pkg,String apkPath) throws Exception
	{
		super(ctx);
		am=AssetManager.class.newInstance();
		AssetManager.class.getDeclaredMethod("addAssetPath", String.class).invoke(am,apkPath);
		DisplayMetrics dis=ctx.getResources().getDisplayMetrics();
		dis.density=uidata.UI_DENSITY;//(util.getScreenHeight()>util.getScreenWidth()?1:0.5f);
		res=new Resources(am,dis,ctx.getResources().getConfiguration());
		this.ctx=ctx;
		this.pkg=pkg;
	}
	public void setIntent(Intent i){
		intent=i;
	}
	public void print(Object o){
		util.toast(""+o);
	}
	public Context ctx(){
		return this;
	}
	public Intent intent(){
		return intent;
	}
	@Override
	public AssetManager getAssets()
	{
		return am;
	}
	@Override
	public Resources getResources()
	{
		return res;
	}
	@Override
	public SharedPreferences getSharedPreferences(String p1, int p2)
	{
		return ctx.getSharedPreferences(String.format("%s_%s",pkg,p1),p2);
	}
	@Override
	public String getPackageName()
	{
		return pkg;
	}
	@Override
	public File getDir(String p1, int p2)
	{
		return ctx.getDir(String.format("%s_%s",pkg,p1),p2);
	}
	@Override
	public void setWallpaper(Bitmap p1) throws IOException
	{
		throw new IllegalStateException("无法设置壁纸");
	}
	@Override
	public void setWallpaper(InputStream p1) throws IOException
	{
		throw new IllegalStateException("无法设置壁纸");
	}
	@Override
	public void clearWallpaper() throws IOException
	{
		throw new IllegalStateException("无法设置壁纸");
	}
	@Override
	public Context getApplicationContext()
	{
		return util.ctx;
	}
/*@Override
	public PackageManager getPackageManager()
	{
		return ctx.getPackageManager();
	}
	@Override
	public ContentResolver getContentResolver()
	{
		return ctx.getContentResolver();
	}
	@Override
	public Looper getMainLooper()
	{
		return ctx.getMainLooper();
	}
	@Override
	public Context getApplicationContext()
	{
		return ctx.getApplicationContext();
	}
	@Override
	public void setTheme(int p1)
	{

	}
	@Override
	public Resources.Theme getTheme()
	{
		return ctx.getTheme();
	}
	@Override
	public ClassLoader getClassLoader()
	{
		return ctx.getClassLoader();
	}

	@Override
	public String getPackageName()
	{
		return pkg;
	}
	@Override
	public ApplicationInfo getApplicationInfo()
	{
		return ctx.getApplicationInfo();
	}
	@Override
	public String getPackageResourcePath()
	{
		return ctx.getPackageResourcePath();
	}
	@Override
	public String getPackageCodePath()
	{
		return ctx.getPackageCodePath();
	}
		@Override
	public FileInputStream openFileInput(String p1) throws FileNotFoundException
	{
		return ctx.openFileInput(p1);
	}

	@Override
	public FileOutputStream openFileOutput(String p1, int p2) throws FileNotFoundException
	{
		return ctx.openFileOutput(p1,p2);
	}

	@Override
	public boolean deleteFile(String p1)
	{
		return false;
	}

	@Override
	public File getFileStreamPath(String p1)
	{
		return ctx.getFileStreamPath(p1);
	}
	@Override
	public File getDataDir()
	{
		return ctx.getDataDir();
	}
	@Override
	public File getFilesDir()
	{
		return ctx.getFilesDir();
	}
	@Override
	public File getNoBackupFilesDir()
	{
		return ctx.getNoBackupFilesDir();
	}
	@Override
	public File getExternalFilesDir(String p1)
	{
		return ctx.getExternalFilesDir(p1);
	}
	@Override
	public File[] getExternalFilesDirs(String p1)
	{
		return ctx.getExternalFilesDirs(p1);
	}
	@Override
	public File getObbDir()
	{
		return ctx.getObbDir();
	}
	@Override
	public File[] getObbDirs()
	{
		return ctx.getObbDirs();
	}
	@Override
	public File getCacheDir()
	{
		return ctx.getCacheDir();
	}
	@Override
	public File getCodeCacheDir()
	{
		return ctx.getCodeCacheDir();
	}
	@Override
	public File getExternalCacheDir()
	{
		return ctx.getExternalCacheDir();
	}
	@Override
	public File[] getExternalCacheDirs()
	{
		return ctx.getExternalCacheDirs();
	}
	@Override
	public File[] getExternalMediaDirs()
	{
		return ctx.getExternalMediaDirs();
	}
	@Override
	public String[] fileList()
	{
		return ctx.fileList();
	}
	@Override
	public SQLiteDatabase openOrCreateDatabase(String p1, int p2, SQLiteDatabase.CursorFactory p3)
	{
		return null;
	}
	@Override
	public SQLiteDatabase openOrCreateDatabase(String p1, int p2, SQLiteDatabase.CursorFactory p3, DatabaseErrorHandler p4)
	{
		return null;
	}
	@Override
	public boolean moveDatabaseFrom(Context p1, String p2)
	{
		return false;
	}
	@Override
	public boolean deleteDatabase(String p1)
	{
		return false;
	}
	@Override
	public File getDatabasePath(String p1)
	{
		return null;
	}
	@Override
	public String[] databaseList()
	{
		return null;
	}
	@Override
	public Drawable getWallpaper()
	{
		return ctx.getWallpaper();
	}
	@Override
	public Drawable peekWallpaper()
	{
		return ctx.peekWallpaper();
	}
	@Override
	public int getWallpaperDesiredMinimumWidth()
	{
		return 0;
	}
	@Override
	public int getWallpaperDesiredMinimumHeight()
	{
		return 0;
	}
	@Override
	public void setWallpaper(Bitmap p1) throws IOException
	{
		throw new IllegalStateException("无法设置壁纸");
	}
	@Override
	public void setWallpaper(InputStream p1) throws IOException
	{
		throw new IllegalStateException("无法设置壁纸");
	}
	@Override
	public void clearWallpaper() throws IOException
	{
	}
	@Override
	public void startActivity(Intent p1)
	{
	}
	@Override
	public void startActivity(Intent p1, Bundle p2)
	{
	}
	@Override
	public void startActivities(Intent[] p1)
	{
	}
	@Override
	public void startActivities(Intent[] p1, Bundle p2)
	{
	}
	@Override
	public void startIntentSender(IntentSender p1, Intent p2, int p3, int p4, int p5) throws IntentSender.SendIntentException
	{
	}
	@Override
	public void startIntentSender(IntentSender p1, Intent p2, int p3, int p4, int p5, Bundle p6) throws IntentSender.SendIntentException
	{

	}

	@Override
	public void sendBroadcast(Intent p1)
	{

	}

	@Override
	public void sendBroadcast(Intent p1, String p2)
	{

	}

	@Override
	public void sendOrderedBroadcast(Intent p1, String p2)
	{

	}

	@Override
	public void sendOrderedBroadcast(Intent p1, String p2, BroadcastReceiver p3, Handler p4, int p5, String p6, Bundle p7)
	{

	}

	@Override
	public void sendBroadcastAsUser(Intent p1, UserHandle p2)
	{

	}

	@Override
	public void sendBroadcastAsUser(Intent p1, UserHandle p2, String p3)
	{

	}

	@Override
	public void sendOrderedBroadcastAsUser(Intent p1, UserHandle p2, String p3, BroadcastReceiver p4, Handler p5, int p6, String p7, Bundle p8)
	{

	}

	@Override
	public void sendStickyBroadcast(Intent p1)
	{

	}

	@Override
	public void sendStickyOrderedBroadcast(Intent p1, BroadcastReceiver p2, Handler p3, int p4, String p5, Bundle p6)
	{

	}

	@Override
	public void removeStickyBroadcast(Intent p1)
	{

	}

	@Override
	public void sendStickyBroadcastAsUser(Intent p1, UserHandle p2)
	{

	}

	@Override
	public void sendStickyOrderedBroadcastAsUser(Intent p1, UserHandle p2, BroadcastReceiver p3, Handler p4, int p5, String p6, Bundle p7)
	{

	}

	@Override
	public void removeStickyBroadcastAsUser(Intent p1, UserHandle p2)
	{

	}

	@Override
	public Intent registerReceiver(BroadcastReceiver p1, IntentFilter p2)
	{

		return ctx.registerReceiver(p1,p2);
	}

	@Override
	public Intent registerReceiver(BroadcastReceiver p1, IntentFilter p2, String p3, Handler p4)
	{

		return ctx.registerReceiver(p1,p2,p3,p4);
	}

	@Override
	public void unregisterReceiver(BroadcastReceiver p1)
	{
		ctx.unregisterReceiver(p1);
	}

	@Override
	public ComponentName startService(Intent p1)
	{
		return ctx.startService(p1);
	}

	@Override
	public boolean stopService(Intent p1)
	{
		return ctx.stopService(p1);
	}

	@Override
	public boolean bindService(Intent p1, ServiceConnection p2, int p3)
	{
		return ctx.bindService(p1,p2,p3);
	}

	@Override
	public void unbindService(ServiceConnection p1)
	{
ctx.unbindService(p1);
	}

	@Override
	public boolean startInstrumentation(ComponentName p1, String p2, Bundle p3)
	{
		return ctx.startInstrumentation(p1,p2,p3);
	}

	@Override
	public Object getSystemService(String p1)
	{
		return ctx.getSystemService(p1);
	}
	@Override
	public String getSystemServiceName(Class<?> p1)
	{
		return ctx.getSystemServiceName(p1);
	}
	@Override
	public int checkPermission(String p1, int p2, int p3)
	{
		return 0;
	}
	@Override
	public int checkCallingPermission(String p1)
	{
		return 0;
	}

	@Override
	public int checkCallingOrSelfPermission(String p1)
	{

		return 0;
	}

	@Override
	public int checkSelfPermission(String p1)
	{

		return 0;
	}

	@Override
	public void enforcePermission(String p1, int p2, int p3, String p4)
	{

	}

	@Override
	public void enforceCallingPermission(String p1, String p2)
	{

	}

	@Override
	public void enforceCallingOrSelfPermission(String p1, String p2)
	{

	}

	@Override
	public void grantUriPermission(String p1, Uri p2, int p3)
	{

	}

	@Override
	public void revokeUriPermission(Uri p1, int p2)
	{

	}

	@Override
	public int checkUriPermission(Uri p1, int p2, int p3, int p4)
	{

		return 0;
	}

	@Override
	public int checkCallingUriPermission(Uri p1, int p2)
	{

		return 0;
	}

	@Override
	public int checkCallingOrSelfUriPermission(Uri p1, int p2)
	{

		return 0;
	}

	@Override
	public int checkUriPermission(Uri p1, String p2, String p3, int p4, int p5, int p6)
	{

		return 0;
	}

	@Override
	public void enforceUriPermission(Uri p1, int p2, int p3, int p4, String p5)
	{

	}

	@Override
	public void enforceCallingUriPermission(Uri p1, int p2, String p3)
	{

	}

	@Override
	public void enforceCallingOrSelfUriPermission(Uri p1, int p2, String p3)
	{

	}

	@Override
	public void enforceUriPermission(Uri p1, String p2, String p3, int p4, int p5, int p6, String p7)
	{

	}

	@Override
	public Context createPackageContext(String p1, int p2) throws PackageManager.NameNotFoundException
	{
		return ctx.createPackageContext(p1,p2);
	}

	@Override
	public Context createConfigurationContext(Configuration p1)
	{
		return ctx.createConfigurationContext(p1);
	}

	@Override
	public Context createDisplayContext(Display p1)
	{
		return ctx.createDisplayContext(p1);
	}

	@Override
	public Context createDeviceProtectedStorageContext()
	{
		return ctx.createDeviceProtectedStorageContext();
	}
	@Override
	public boolean isDeviceProtectedStorage()
	{
		return true;
	}
*/

}
