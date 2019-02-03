package com.yzrilyzr.doutuzhushou;
import android.content.*;
import android.graphics.*;
import android.text.*;
import android.widget.*;
import java.io.*;

import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import com.yzrilyzr.FloatWindow.API;
import java.nio.channels.FileChannel;

public class doutu implements TextWatcher,OnClickListener
{

    private EditText keyedit;




    @Override
    public void onClick(View p1)
    {
        // TODO: Implement this method
        switch(p1.getId())
        {
            case R.id.doutuButton1:
                setList();
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
    {
        // TODO: Implement this method
    }

    @Override
    public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
    {
        // TODO: Implement this method
        //key=p1.toString();
        setList();
    }

    @Override
    public void afterTextChanged(Editable p1)
    {
        // TODO: Implement this method
    }
    Context ctx = null;
    Object window;
    float den;
    String mainDir;
    int width,height;
    LinearLayout list;
    public doutu(Context ctx,Intent ient) throws Throwable
    {
        this.ctx=ctx;
        View v=API.parseXmlViewFromFile(ctx,"com.yzrilyzr.doutuzhushou","res/layout/doutu.xml");
        den=ctx.getResources().getDisplayMetrics().density;
        window=API.class2Object(API.WINDOW_CLASS,new Class[]{Context.class,int.class,int.class},new Object[]{ctx,width=(int)(260*den),height=(int)(350*den)});
        API.invoke(window,"show",null,null);
        API.invoke(window,"addView",new Class[]{View.class},new Object[]{v});
        API.invoke(window,"setTitle","斗图助手");
        list=(LinearLayout)v.findViewById(R.id.maindoutuLinearLayout1);
        keyedit=((EditText)v.findViewById(R.id.doutuEditText1));
        keyedit.addTextChangedListener(this);
        ((Button)v.findViewById(R.id.doutuButton1)).setOnClickListener(this);

        setList();
    }
    private void setList()
    {
        mainDir=Environment.getExternalStorageDirectory().getAbsolutePath()+"/yzr的app/FloatWindow/斗图";
        File f=new File(mainDir+"/图片");
        if(!f.exists())f.mkdirs();
        File[] files=f.listFiles();
        int count=3;
        int linecount=0,lll=0,width2=width/count;
        list.removeAllViews();
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(width2,width2);
        LinearLayout line=new LinearLayout(ctx);
        String key=keyedit.getText().toString();
        for(int i=0;i<files.length;i++)
        {
            if(lll>20)break;
            if(files[i].getName().toLowerCase().indexOf(key)!=-1)
            {
                try
                {
                    ImageView iv=new ImageView(ctx);
                    iv.setLayoutParams(p);
                    final String ppp=files[i].getAbsolutePath();
                    iv.setImageBitmap(BitmapFactory.decodeFile(ppp));
                    line.addView(iv);
                    iv.setOnClickListener(new OnClickListener(){
                            @Override
                            public void onClick(View p1)
                            {
                                // TODO: Implement this method
                                try
                                {
                                    writeBmp(ppp,mainDir+"/tmp.png");
                                    Toast.makeText(ctx,"ok",0).show();
                                }
                                catch (Exception e)
                                {}
                            }
                        });
                    linecount++;
                }
                catch(Throwable e)
                {}
            }
            if(linecount==count)
            {
                linecount=0;
                lll++;
                list.addView(line);
                line=new LinearLayout(ctx);
            }
            else if(i+1==files.length)
            {
                list.addView(line);
            }
        }
    }
    private void writeBmp(String src,String dst) throws Exception
    {
        new File(dst).delete();
        scan(dst);
        FileChannel in=new FileInputStream(src).getChannel();
        FileChannel out=new FileOutputStream(dst).getChannel();
        in.transferTo(0,in.size(),out);
        scan(dst);


    }

    private void scan(String dst)
    {
        MediaScannerConnection.scanFile(ctx,
            new String[] { dst }, null,
            new MediaScannerConnection.OnScanCompletedListener() {
                @Override
                public void onScanCompleted(String path, Uri uri)
                {

                }
            });
    }
}
