package com.yzrilyzr.tts;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;
import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.yzrilyzr.floatingwindow.pluginapi.API;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import com.yzrilyzr.floatingwindow.pluginapi.Window;

public class Main implements SpeechSynthesizerListener,SeekBar.OnSeekBarChangeListener
{
	Window win;
	Context ctx;
	View vg;
	EditText edit;
	public Main(Context c,Intent e) throws Exception
	{
		ctx=c;
		File f=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/yzr的app/悬浮窗/文字转语音/lib");
		TEMP_DIR =f+"";
		TEXT_FILENAME = TEMP_DIR + "/" + "bd_etts_text.dat";
		MODEL_FILENAME =TEMP_DIR + "/" + "bd_etts_common_speech_m15_mand_eng_high_am-mix_v3.0.0_20170505.dat";
		String l=c.getDir("ttslib",c.MODE_PRIVATE).getAbsolutePath();
		if(new File(l).list().length==0){
			File[] fs=f.listFiles();
			for(File h:fs){
				if(!h.getName().endsWith(".so"))continue;
				FileChannel i=new FileInputStream(h).getChannel();
				FileChannel o=new FileOutputStream(l+"/"+h.getName()).getChannel();
				i.transferTo(0,i.size(),o);
				i.close();
				o.close();
			}
		}
		//File fy=new File(f.getAbsolutePath()+"/libloaded");
		//if(!fy.exists())
		try{
			//fy.createNewFile();
			//fy.deleteOnExit();
			Runtime r=Runtime.getRuntime();
			r.load(l+"/libgnustl_shared.so");
			r.load(l+"/libBDSpeechDecoder_V1.so");
			r.load(l+"/libbd_etts.so");
			r.load(l+"/libbdtts.so");
			//r.load(l+"/libgnustl_shared.so");
		}catch(Throwable pe){}
		win=new Window(c,API.px(300),API.px(300));
		vg=API.parseXmlViewFromFile(c,"com.yzrilyzr.tts","res/layout/main.xml");
		win.setTitle("文字转语音");
		win.addView(vg);
		win.setIcon(new BitmapDrawable(loadAsset("res/drawable/ic_launcher.png")));
		win.show();
		Button mSpeak = (Button) vg.findViewById(R.id.speak);
		Button mStop = (Button) vg.findViewById(R.id.stop);
		edit=(EditText) vg.findViewById(R.id.mainEditText1);
		Button paste=(Button) vg.findViewById(R.id.paste);
		Button rel=(Button) vg.findViewById(R.id.release);
		View.OnClickListener listener = new View.OnClickListener() {
			public void onClick(View v)
			{
				int id = v.getId();
				switch (id)
				{
					case R.id.speak:
						speak(edit.getText()+"");
						break;
					case R.id.stop:
						stop();
						break;
					case R.id.paste:
						String s=""+((ClipboardManager)ctx.getSystemService(ctx.CLIPBOARD_SERVICE)).getText();
						edit.setText(s);
						speak(s);
						break;
					case R.id.release:
						onDestroy();
						break;
					default:
						break;
				}
			}
		};
		mSpeak.setOnClickListener(listener);
		mStop.setOnClickListener(listener);
		paste.setOnClickListener(listener);
		rel.setOnClickListener(listener);
		new Thread(new Runnable(){
				@Override
				public void run()
				{
					initTTs();
				}
			}).start();
		((SeekBar)vg.findViewById(R.id.mainmySeekBar1)).setOnSeekBarChangeListener(this);
		((SeekBar)vg.findViewById(R.id.mainmySeekBar2)).setOnSeekBarChangeListener(this);
		((SeekBar)vg.findViewById(R.id.mainmySeekBar3)).setOnSeekBarChangeListener(this);
		((SeekBar)vg.findViewById(R.id.mainmySeekBar4)).setOnSeekBarChangeListener(this);
	}

	@Override
	public void onStopTrackingTouch(SeekBar p1)
	{
		// TODO: Implement this method
	}
	@Override
	public void onProgressChanged(SeekBar p1, int p2, boolean p3)
	{
		switch(p1.getId()){
			case R.id.mainmySeekBar1:
				pq=Integer.toString(p2);
				break;
			case R.id.mainmySeekBar2:
				pw=Integer.toString(p2);
				break;
			case R.id.mainmySeekBar3:
				pe=Integer.toString(p2);
				break;
			case R.id.mainmySeekBar4:
				pr=Integer.toString(p2);
				break;
		}
	}
	@Override
	public void onStartTrackingTouch(SeekBar p1)
	{
		// TODO: Implement this method
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
	public InputStream loadAsset(String file)
    {
        try
        {
            InputStream is=null;
            String apk=ctx.getPackageManager().getPackageInfo("com.yzrilyzr.tts",PackageInfo.INSTALL_LOCATION_AUTO).applicationInfo.publicSourceDir;
            ZipFile zf = new ZipFile(apk);
            ZipEntry entry=zf.getEntry(file);
            is=zf.getInputStream(entry);
            return is;   
        }
        catch(Throwable e)
        {}
        return null;
    }
	protected String pq="0",pw="5",pe="5",pr="5";
	protected static final String appId = 10828635+"";
	protected static final String appKey = 4+"SQz"+4+"Yr"+2+"F3pTk7Goz"+1+"BFTGdw";
	protected static final String secretKey = "dde509ef215e680c"+10792199+1064391+"a";
	// TtsMode.MIX; 离在线融合，在线优先； TtsMode.ONLINE 纯在线； 没有纯离线
	private TtsMode ttsMode =TtsMode.MIX;
	// ================选择TtsMode.ONLINE  不需要设置以下参数; 选择TtsMode.MIX 需要设置下面2个离线资源文件的路径
	private String TEMP_DIR; // 重要！请手动将assets目录下的3个dat 文件复制到该目录
	// 请确保该PATH下有这个文件
	private String TEXT_FILENAME;
	// 请确保该PATH下有这个文件 ，m15是离线男声
	private String MODEL_FILENAME;
	// ===============初始化参数设置完毕，更多合成参数请至getParams()方法中设置 =================
	protected SpeechSynthesizer mSpeechSynthesizer;
	// =========== 以下为UI部分 ==================================================
	private void initTTs()
	{
		boolean isMix = ttsMode.equals(TtsMode.MIX);
		// 1. 获取实例
		mSpeechSynthesizer = SpeechSynthesizer.getInstance();
		mSpeechSynthesizer.setContext(ctx);
		// 2. 设置listener
		mSpeechSynthesizer.setSpeechSynthesizerListener(this);
		// 3. 设置appId，appKey.secretKey
		mSpeechSynthesizer.setAppId(appId);
		mSpeechSynthesizer.setApiKey(appKey, secretKey);
		// 4. 支持离线的话，需要设置离线模型
		if (isMix)
		{
			// 检查离线授权文件是否下载成功，离线授权文件联网时SDK自动下载管理，有效期3年，3年后的最后一个月自动更新。
			mSpeechSynthesizer.auth(ttsMode);
			// 文本模型文件路径 (离线引擎使用)， 注意TEXT_FILENAME必须存在并且可读
			mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, TEXT_FILENAME);
			// 声学模型文件路径 (离线引擎使用)， 注意TEXT_FILENAME必须存在并且可读
			mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, MODEL_FILENAME);
		}
		// 5. 以下setParam 参数选填。不填写则默认值生效
		// 设置在线发声音人： 0 普通女声（默认） 1 普通男声 2 特别男声 3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
		mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER,pq);
		// 设置合成的音量，0-9 ，默认 5
		mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME,pw);
		// 设置合成的语速，0-9 ，默认 5
		mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED,pe);
		// 设置合成的语调，0-9 ，默认 5
		mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH,pr);
		mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);
		// 该参数设置为TtsMode.MIX生效。即纯在线模式不生效。
		// MIX_MODE_DEFAULT 默认 ，wifi状态下使用在线，非wifi离线。在线状态下，请求超时6s自动转离线
		// MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI wifi状态下使用在线，非wifi离线。在线状态下， 请求超时1.2s自动转离线
		// MIX_MODE_HIGH_SPEED_NETWORK ， 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
		// MIX_MODE_HIGH_SPEED_SYNTHESIZE, 2G 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
		mSpeechSynthesizer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		// x. 额外 ： 自动so文件是否复制正确及上面设置的参数
		Map<String, String> params = new HashMap<>();
		// 复制下上面的 mSpeechSynthesizer.setParam参数
		// 上线时请删除AutoCheck的调用
		if (isMix)
		{
			params.put(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, TEXT_FILENAME);
			params.put(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, MODEL_FILENAME);
		}
		// 6. 初始化
		mSpeechSynthesizer.initTts(ttsMode);
	}
	/**
	 * 检查appId ak sk 是否填写正确，另外检查官网应用内设置的包名是否与运行时的包名一致。本demo的包名定义在build.gradle文件中
	 *
	 * @return
	 */
	private void speak(String s)
	{
		/* 以下参数每次合成时都可以修改
		 *  mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0");
		 *  设置在线发声音人： 0 普通女声（默认） 1 普通男声 2 特别男声 3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
		 *  mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, "5"); 设置合成的音量，0-9 ，默认 5
		 *  mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, "5"); 设置合成的语速，0-9 ，默认 5
		 *  mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, "5"); 设置合成的语调，0-9 ，默认 5
		 *
		 *  mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);
		 *  MIX_MODE_DEFAULT 默认 ，wifi状态下使用在线，非wifi离线。在线状态下，请求超时6s自动转离线
		 *  MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI wifi状态下使用在线，非wifi离线。在线状态下， 请求超时1.2s自动转离线
		 *  MIX_MODE_HIGH_SPEED_NETWORK ， 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
		 *  MIX_MODE_HIGH_SPEED_SYNTHESIZE, 2G 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
		 */
		mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER,pq);
		// 设置合成的音量，0-9 ，默认 5
		mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME,pw);
		// 设置合成的语速，0-9 ，默认 5
		mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED,pe);
		// 设置合成的语调，0-9 ，默认 5
		mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH,pr);
		if (mSpeechSynthesizer == null)
		{
			return;
		}
		mSpeechSynthesizer.speak(s);
	}
	private void stop()
	{
		mSpeechSynthesizer.stop();
	}
	protected void onDestroy()
	{
		if (mSpeechSynthesizer != null)
		{
			mSpeechSynthesizer.stop();
			mSpeechSynthesizer.release();
			mSpeechSynthesizer = null;
		}
	}
	@Override
	public void onError(String p1, SpeechError p2)
	{
		Toast.makeText(ctx,p1+","+p2.description,1).show();
	}
	@Override
	public void onSpeechFinish(String p1)
	{
		// TODO: Implement this method
	}
	@Override
	public void onSynthesizeStart(String p1)
	{
		// TODO: Implement this method
	}
	@Override
	public void onSynthesizeDataArrived(String p1, byte[] p2, int p3)
	{
		// TODO: Implement this method
	}
	@Override
	public void onSpeechStart(String p1)
	{
		// TODO: Implement this method
	}
	@Override
	public void onSynthesizeFinish(String p1)
	{
		// TODO: Implement this method
	}
	@Override
	public void onSpeechProgressChanged(String p1, int p2)
	{
		// TODO: Implement this method
	}
}
