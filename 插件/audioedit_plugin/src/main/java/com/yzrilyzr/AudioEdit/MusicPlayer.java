package com.yzrilyzr.AudioEdit;

import android.media.*;
import java.io.*;

import android.content.Context;
import android.os.Handler;
import android.widget.SeekBar;
import java.util.ArrayList;
import java.nio.ByteBuffer;
import android.media.MediaCodec.BufferInfo;
import java.util.concurrent.CopyOnWriteArrayList;

public class MusicPlayer
{
	private String file;
	int TEST_SR =44100;
    int BufferSize=1080;
	int TEST_CONF =AudioFormat.CHANNEL_CONFIGURATION_STEREO;
	int TEST_FORMAT= AudioFormat.ENCODING_PCM_16BIT;
	int TEST_MODE =AudioTrack.MODE_STREAM;
	int TEST_STREAM_TYPE = AudioManager.STREAM_MUSIC;
	int minBuffSize = AudioTrack.getMinBufferSize(TEST_SR, TEST_CONF, TEST_FORMAT);
	private SeekBar seek_progress;
	//private RandomAccessFile raf;
	private boolean isPlay=false;
	boolean onSeek=false;
	private Context ctx;
	private AudioTrack track;
	private WaveView WaveView;
	private Handler uiHandler;
	private Runnable uiRunnable;
	private FileOutputStream fos;
	private wave.StreamPlayer wsp;
	boolean p_mono=false;
	int p_skip=10,p_sr=44100;
	float p_leftGain=1.0f,p_rightGain=1.0f;
	float p_leftCap=1,p_rightCap=1;
	public Interface inter;
	private MediaExtractor mediaExtractor;
	private MediaCodec mediaDecode;
	private ByteBuffer[] decodeInputBuffers;
	private ByteBuffer[] decodeOutputBuffers;
	private MediaCodec.BufferInfo decodeBufferInfo;
	private CopyOnWriteArrayList<byte[]> buff=new CopyOnWriteArrayList<byte[]>();
	public MusicPlayer(String file,SeekBar progress,WaveView v)
	{
		init();
		WaveView=v;
		seek_progress=progress;
		this.file=file;
		ctx=progress.getContext();
		uiRunnable=new Runnable(){
			@Override
			public void run()
			{
				if(!onSeek)seek_progress.setProgress((int)wsp.getFilePointer());
			}
		};
		uiHandler=new Handler(ctx.getMainLooper());
		//initMediaDecode();
		//srcAudioFormatToPCM();
		
	}
	private void initMediaDecode() {
        try {
            mediaExtractor=new MediaExtractor();//此类可分离视频文件的音轨和视频轨道
            mediaExtractor.setDataSource(file);//媒体文件的位置
            for (int i = 0; i < mediaExtractor.getTrackCount(); i++) {//遍历媒体轨道 此处我们传入的是音频文件，所以也就只有一条轨道
                MediaFormat format = mediaExtractor.getTrackFormat(i);
                String mime = format.getString(MediaFormat.KEY_MIME);
                if (mime.startsWith("audio")) {//获取音频轨道
//                    format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 200 * 1024);
                    mediaExtractor.selectTrack(i);//选择此音频轨道
                    mediaDecode = MediaCodec.createDecoderByType(mime);//创建Decode解码器
                    mediaDecode.configure(format, null, null, 0);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (mediaDecode == null) {
            return;
        }
        mediaDecode.start();//启动MediaCodec ，等待传入数据
        decodeInputBuffers=mediaDecode.getInputBuffers();//MediaCodec在此ByteBuffer[]中获取输入数据
        decodeOutputBuffers=mediaDecode.getOutputBuffers();//MediaCodec将解码后的数据放到此ByteBuffer[]中 我们可以直接在这里面得到PCM数据
        decodeBufferInfo=new MediaCodec.BufferInfo();//用于描述解码得到的byte[]数据的相关信息
         }

    private void srcAudioFormatToPCM() {
		int decodeSize=0;
        for (int i = 0; i < decodeInputBuffers.length-1; i++) {
			int inputIndex = mediaDecode.dequeueInputBuffer(-1);//获取可用的inputBuffer -1代表一直等待，0表示不等待 建议-1,避免丢帧
			if (inputIndex < 0) {
				isPlay =false;
				return;
			}

			ByteBuffer inputBuffer = decodeInputBuffers[inputIndex];//拿到inputBuffer
			inputBuffer.clear();//清空之前传入inputBuffer内的数据
			int sampleSize = mediaExtractor.readSampleData(inputBuffer, 0);//MediaExtractor读取数据到inputBuffer中
			if (sampleSize <0) {//小于0 代表所有数据已读取完成
                isPlay=false;
            }else {
                mediaDecode.queueInputBuffer(inputIndex, 0, sampleSize, 0, 0);//通知MediaDecode解码刚刚传入的数据
                mediaExtractor.advance();//MediaExtractor移动到下一取样处
                decodeSize+=sampleSize;
            }
        }

        //获取解码得到的byte[]数据 参数BufferInfo上面已介绍 10000同样为等待时间 同上-1代表一直等待，0代表不等待。此处单位为微秒
        //此处建议不要填-1 有些时候并没有数据输出，那么他就会一直卡在这 等待
        int outputIndex = mediaDecode.dequeueOutputBuffer(decodeBufferInfo, 10000);

//        showLog("decodeOutIndex:" + outputIndex);
        ByteBuffer outputBuffer;
        byte[] chunkPCM=null;
        while (outputIndex >= 0) {//每次解码完成的数据不一定能一次吐出 所以用while循环，保证解码器吐出所有数据
            outputBuffer = decodeOutputBuffers[outputIndex];//拿到用于存放PCM数据的Buffer
            chunkPCM = new byte[decodeBufferInfo.size];//BufferInfo内定义了此数据块的大小
            outputBuffer.get(chunkPCM);//将Buffer内的数据取出到字节数组中
            outputBuffer.clear();//数据取出后一定记得清空此Buffer MediaCodec是循环使用这些Buffer的，不清空下次会得到同样的数据
            buff.add(chunkPCM);//自己定义的方法，供编码器所在的线程获取数据,下面会贴出代码
            mediaDecode.releaseOutputBuffer(outputIndex, false);//此操作一定要做，不然MediaCodec用完所有的Buffer后 将不能向外输出数据
            outputIndex = mediaDecode.dequeueOutputBuffer(decodeBufferInfo, 10000);//再次获取数据，如果没有数据输出则outputIndex=-1 循环结束
        }

    }
	public boolean isWriteOut()
	{
		return fos!=null;
	}
	public void writeOut(String file)
	{
		if(file==null)
		{
			fos=null;
			return;
		}
		File f=new File(file);
		if(f.exists())f.delete();
		try
		{
			fos=new FileOutputStream(f);
		}
		catch (FileNotFoundException e)
		{}
	}
	public void setSR(int i)
	{
		TEST_SR=i;
		track.setPlaybackRate(i);
	}
	public long length(){
		return wsp.length();
	}
	public void seek(int p)
	{
		wsp.seek(p);
	}
	public void togglePause()
	{
		if(track.getPlayState()==AudioTrack.PLAYSTATE_PLAYING)track.pause();
		else if(track.getPlayState()==AudioTrack.PLAYSTATE_PAUSED)track.play();
	}
	public void stop()
	{
		track.stop();
		isPlay=false;
		init();
	}
	public void play()
	{
		new Thread(new Runnable() {
				@Override public void run()
				{
					init();
					if(file==null)return;
					if(isPlay)return;
					try
					{
						wsp=new wave().new StreamPlayer(new RandomAccessFile(file,"r"));
						seek_progress.setMax((int)wsp.length());
						isPlay=true;
						while (wsp.getFilePointer()<wsp.length()&&isPlay)
						{ 
							if (track.getPlayState() == AudioTrack.PLAYSTATE_PLAYING)
							{
								wsp.LEFT_CAP=p_leftCap;
								wsp.RIGHT_CAP=p_rightCap;
								wsp.LEFT_GAIN=p_leftGain;
								wsp.RIGHT_GAIN=p_rightGain;
								wsp.SKIP=p_skip;
								wsp.SAMPLE_RATE=p_sr;
								byte[] data=wsp.read();
								if(fos!=null)fos.write(data);
								WaveView.setData(data);
								track.write(data,0,data.length);
								uiHandler.post(uiRunnable);
							}
							else
								Thread.sleep(100);
						}
						isPlay=false;
						init();
					}
					catch (Exception e)
					{
						System.out.println(e);
						isPlay=false;
					}
				}
			}).start();
	}
	public void playI()
	{
		new Thread(new Runnable() {
				@Override public void run()
				{
					init();
					if(isPlay)return;
					if(inter==null)return;
					try
					{
						isPlay=true;
						while (isPlay)
						{ 
							if (track.getPlayState() == AudioTrack.PLAYSTATE_PLAYING)
							{
								wsp.LEFT_CAP=p_leftCap;
								wsp.RIGHT_CAP=p_rightCap;
								wsp.LEFT_GAIN=p_leftGain;
								wsp.RIGHT_GAIN=p_rightGain;
								wsp.SKIP=p_skip;
								wsp.SAMPLE_RATE=p_sr;
								byte[] data=inter.getData();
								int[][] a=wave.stereo_16Bit_PCM(data);
								a=wsp.convert(a);
								data=wave.stereo_PCM_16Bit(a[0],a[1]);
								if(fos!=null)fos.write(data);
								WaveView.setData(data);
								track.write(data,0,data.length);
								uiHandler.post(uiRunnable);
							}
							else
								Thread.sleep(100);
						}
						isPlay=false;
						init();
					}
					catch (Exception e)
					{
						System.out.println(e);
						isPlay=false;
					}
				}
			}).start();
	}
	private void init()
    {
		if(track!=null)
		{
			track.stop();
			track.release();
		}
		minBuffSize = AudioTrack.getMinBufferSize(TEST_SR, TEST_CONF, TEST_FORMAT);
		track=new AudioTrack(TEST_STREAM_TYPE,TEST_SR,TEST_CONF,TEST_FORMAT,minBuffSize*5,TEST_MODE);
		track.play();
	}
	public interface Interface{
		public abstract byte[] getData();
	}
}
