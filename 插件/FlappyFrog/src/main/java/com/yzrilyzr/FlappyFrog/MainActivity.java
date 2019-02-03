package com.yzrilyzr.FlappyFrog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.yzrilyzr.floatingwindow.pluginapi.API;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class MainActivity extends Activity 
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        try
        {
            API.startService(this,new Intent(),"com.yzrilyzr.FlappyFrog.Main");
            //API.startMainActivity(this,"com.yzrilyzr.FlappyFrog.ToActivity");
        }
        catch(Throwable e)
        {
            ByteArrayOutputStream sw=new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(sw));
            Toast.makeText(this,sw.toString(),0).show();
        }
        finish();
    }
}
