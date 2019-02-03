/*package com.yzrilyzr.ui;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import com.yzrilyzr.floatingwindow.R;
import com.yzrilyzr.myclass.myActivity;
import com.yzrilyzr.myclass.util;
public class uiSettingsActivity extends myActivity
{
	myColorView q,w,e,r,t,y,cc,dd;
	myEditText a,s,d,f,ccc;
	mySwitch z,x;
	boolean isChanged=false;
	String[] colorlist=new String[]{
		"yzr的算法","#ffddffaa","#ff00ddff","#ff8cff00",
		"红(red)","#fffde0dc","#fff9bdbb","#ffe51c23",
		"粉(pink)","#fffce4ec","#fff8bbd0","#ffe91e63",
		"紫(purple)","#fff3e5f5","#ffe1bee7","#ff9c27b0",
		"深紫(deep purple)","#ffede7f6","#ffd1c4e9","#ff673ab7",
		"靛(indigo)","#ffe8eaf6","#ffc5cae9","#ff3f51b5",
		"蓝(blue)","#ffe7e9fd","#ffd0d9ff","#ff5677fc",
		"亮蓝(light blue)","#ffe1f5fe","#ffb3e5fc","#ff03a9f4",
		"蓝绿(cyan)","#ffe0f7fa","#ffb2ebf2","#ff00bcd4",
		"水鸭(teal)","#ffe0f2f1","#ffb2dfdb","#ff009688",
		"绿(green)","#ffd0f8ce","#ffa3e9a4","#ff259b24",
		"亮绿(light green)","#fff1f8e9","#ffdcedc8","#ff8bc34a",
		"青(lime)","#fff9fbe7","#fff0f4c3","#ffcddc39",
		"黄(yellow)","#fffffde7","#fffff9c4","#ffffeb3b",
		"琥珀(amber)","#fffff8e1","#ffffecb3","#ffffc107",
		"橙(orange)","#fffff3e0","#ffffe0b2","#ffff9800",
		"深橙(deep orange)","#fffbe9e7","#ffffccbc","#ffff5722",
		"棕(brown)","#ffefebe9","#ffd7ccc8","#ff795548",
		"灰(grey)","#fffafafa","#fff5f5f5","#ff9e9e9e",
		"蓝灰(blue grey)","#ffeceff1","#ffcfd8dc","#ff607d8b"};
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);

		try
		{
			setContentView(R.layout.layout_uisettings);
			q = (myColorView)findViewById(R.id.layoutuisettingsImageView1);
			w = (myColorView)findViewById(R.id.layoutuisettingsImageView2);
			e = (myColorView)findViewById(R.id.layoutuisettingsImageView3);
			r = (myColorView)findViewById(R.id.layoutuisettingsImageView4);
			t = (myColorView)findViewById(R.id.layoutuisettingsImageView5);
			y = (myColorView) findViewById(R.id.layoutuisettingsImageView6);
			a = (myEditText)findViewById(R.id.layoutuisettingsmyEditText1);
			s = (myEditText)findViewById(R.id.layoutuisettingsmyEditText2);
			d = (myEditText)findViewById(R.id.layoutuisettingsmyEditText3);
			f = (myEditText)findViewById(R.id.layoutuisettingsmyEditText4);
			z = (mySwitch)findViewById(R.id.layoutuisettingsmySwitch1);
			x=(mySwitch) findViewById(R.id.layoutuisettingsmySwitch2);
			z.setListener(new mySwitch.OnCheckedChangeListener(){@Override public void onCheckedChange(mySwitch m, boolean b)
					{
						uidata.UI_USETYPEFACE = b;
						isChanged = true;
					}});
			x.setListener(new mySwitch.OnCheckedChangeListener(){@Override public void onCheckedChange(mySwitch m, boolean b)
					{
						uidata.UI_USESHADOW = b;
						isChanged = true;
					}});
			init();
		}
		catch (Exception e)
		{util.check(this, e);}
	}
	public void showAbout(View v)
	{
		new myAlertDialog(uiSettingsActivity.this)
			.setMessage("<CWUI>\n全新的自定义组件UI\n给你不同的感受\n支持更改组件颜色、字体样式\n提供基本控件、控件工具、集成控件，etc…\n\n作者:yzrilyzr")
			.setTitle("关于<CWUI>").setPositiveButton("我知道了", null).show();

	}
	public void s1(View v)
	{show(0);dd = (myColorView) v;}
	public void s2(View v)
	{show(1);dd = (myColorView) v;}
	public void s3(View v)
	{show(2);dd = (myColorView) v;}
	public void s4(View v)
	{show(3);dd = (myColorView) v;}
	public void s5(View v)
	{show(4);dd = (myColorView) v;}
	public void s6(View v)
	{show(5);dd = (myColorView) v;}
	int gg,hh,jj,kk;
	public void show(final int i)
	{
		int c=0xffffffff;
		if (i == 0)c = uidata.UI_COLOR_MAIN;
		if (i == 1)c = uidata.UI_COLOR_BACK;
		if (i == 2)c = uidata.UI_COLOR_MAINHL;
		if (i == 3)c = uidata.UI_TEXTCOLOR_MAIN;
		if (i == 4)c = uidata.UI_TEXTCOLOR_BACK;
		if (i == 5)c = uidata.UI_TEXTCOLOR_HL;
		final View mview=LayoutInflater.from(this).inflate(R.layout.layout_colorset, null);
		final myAlertDialog mad=new myAlertDialog(this);
		final myDialogInterface preset=new myDialogInterface(){
			@Override public void click(View v, int t)
			{
				int h=Color.parseColor(colorlist[t * 4 + 1]);
				int b=Color.parseColor(colorlist[t * 4 + 2]);
				int m=Color.parseColor(colorlist[t * 4 + 3]);
				uidata.UI_COLOR_MAIN = m;
				uidata.UI_COLOR_BACK = b;
				uidata.UI_COLOR_MAINHL = h;
				isChanged = true;
			}
		};
		final myDialogInterface colorpicker=new myDialogInterface(){
			@Override public void click(View p1, int po)
			{
				String[] ss=new String[colorlist.length / 4];
				for (int i=0;i < ss.length;i++)ss[i] = colorlist[i * 4];
				mad.setItems(ss, preset);
				preventDefault();
				mad.setPositiveButton(null, null);
				mad.setTitle("预设颜色");
			}};
		mad.setTitle("调色板")
			.setView(mview)
			.setPositiveButton("预设", colorpicker)
			.setNegativeButton("取消", null).show();
		myColorView k=(myColorView)mview.findViewById(R.id.layoutcolorsetImageView4),
			l=(myColorView)mview.findViewById(R.id.layoutcolorsetImageView5);
		k.setColorView(c);
		l.setColorView(c);
		cc = l;
		ccc = (myEditText)mview.findViewById(R.id.layoutcolorsetEditText1);
		mySeekBar g=(mySeekBar)mview.findViewById(R.id.layoutcolorsetSeekBar1),
			h=(mySeekBar)mview.findViewById(R.id.layoutcolorsetSeekBar2),
			o=(mySeekBar)mview.findViewById(R.id.layoutcolorsetSeekBar3),
			p=(mySeekBar)mview.findViewById(R.id.layoutcolorsetSeekBar4);
		g.setMax(255);h.setMax(255);o.setMax(255);p.setMax(255);
		gg = Color.alpha(c);
		hh = Color.red(c);
		jj = Color.green(c);
		kk = Color.blue(c);
		g.setProgress(gg);
		h.setProgress(hh);
		o.setProgress(jj);
		p.setProgress(kk);

		g.setOnChangeListener(new mySeekBar.OnChange(){
				@Override public void onDown(mySeekBar msb)
				{}
				@Override public void onUp(mySeekBar msb)
				{}
				@Override public void onChange(mySeekBar msb, int p)
				{gg = p;changeC();}
			});
		h.setOnChangeListener(new mySeekBar.OnChange(){
				@Override public void onDown(mySeekBar msb)
				{}
				@Override public void onUp(mySeekBar msb)
				{}
				@Override public void onChange(mySeekBar msb, int p)
				{hh = p;changeC();}
			});
		o.setOnChangeListener(new mySeekBar.OnChange(){
				@Override public void onDown(mySeekBar msb)
				{}
				@Override public void onUp(mySeekBar msb)
				{}
				@Override public void onChange(mySeekBar msb, int p)
				{jj = p;changeC();}
			});

		p.setOnChangeListener(new mySeekBar.OnChange(){
				@Override public void onDown(mySeekBar msb)
				{}
				@Override public void onUp(mySeekBar msb)
				{}
				@Override public void onChange(mySeekBar msb, int p)
				{kk = p;changeC();}
			});
		l.setOnClickListener(new View.OnClickListener(){
				@Override public void onClick(View v)
				{
					int c=Color.argb(gg, hh, jj, kk);
					dd.setColorView(c);
					isChanged = true;
					if (i == 0)uidata.UI_COLOR_MAIN = c;
					if (i == 1)uidata.UI_COLOR_BACK = c;
					if (i == 2)uidata.UI_COLOR_MAINHL = c;
					if (i == 3)uidata.UI_TEXTCOLOR_MAIN = c;
					if (i == 4)uidata.UI_TEXTCOLOR_BACK = c;
					if (i == 5)uidata.UI_TEXTCOLOR_HL = c;
					mad.dismiss();
				}
			});
		ccc.setText("#" + Integer.toHexString(c));
		ccc.addTextChangedListener(new TextWatcher(){
				@Override public void beforeTextChanged(java.lang.CharSequence p1, int p2, int p3, int p4)
				{}
				@Override public void onTextChanged(java.lang.CharSequence p1, int p2, int p3, int p4)
				{
					if (p1.length() == 9)
					{
						try
						{
							int iii=Color.parseColor(p1.toString());
							cc.setColorView(iii);
							gg = Color.alpha(iii);
							hh = Color.red(iii);
							jj = Color.green(iii);
							kk = Color.blue(iii);
						}
						catch (Exception e)
						{}
					}
				}
				@Override public void afterTextChanged(android.text.Editable p1)
				{}
			});
	}

	@Override
	public void exit(View v)
	{
		// TODO: Implement this method
		if (isChanged ||
			uidata.UI_TEXTSIZE_DEFAULT != Float.parseFloat(a.getText().toString()) * uidata.UI_DENSITY ||
			uidata.UI_TEXTSIZE_TITLE != Float.parseFloat(s.getText().toString()) * uidata.UI_DENSITY ||
			uidata.UI_PADDING_DEFAULT != (int)(Float.parseFloat(d.getText().toString()) * uidata.UI_DENSITY) ||
			uidata.UI_RADIUS != Float.parseFloat(f.getText().toString()) * uidata.UI_DENSITY

			)
			new myAlertDialog(this)
				.setMessage("还没有保存，是否保存？\n新的应用将会在本程序完全退出后再重启生效")
				.setTitle("提示")
				.setNegativeButton("退出", new myDialogInterface(){@Override public void click(View v, int i)
					{
						finish();
					}})
				.setNeutralButton("返回", null)
				.setPositiveButton("保存", new myDialogInterface(){@Override public void click(View v, int i)
					{
						saveText();
						uidata.saveData(ctx);
						util.toast(ctx,"保存成功");
						finish();
					}})
				.show();

		else super.exit(v);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		// TODO: Implement this method
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			if (isChanged ||
				uidata.UI_TEXTSIZE_DEFAULT != Float.parseFloat(a.getText().toString()) * uidata.UI_DENSITY ||
				uidata.UI_TEXTSIZE_TITLE != Float.parseFloat(s.getText().toString()) * uidata.UI_DENSITY ||
				uidata.UI_PADDING_DEFAULT != (int)(Float.parseFloat(d.getText().toString()) * uidata.UI_DENSITY) ||
				uidata.UI_RADIUS != Float.parseFloat(f.getText().toString()) * uidata.UI_DENSITY

				)
			{exit(null);return true;}
		}
		return super.onKeyDown(keyCode, event);
	}

	public void saveText()
	{
		uidata.UI_TEXTSIZE_DEFAULT = Float.parseFloat(a.getText().toString()) * uidata.UI_DENSITY;
		uidata.UI_TEXTSIZE_TITLE = Float.parseFloat(s.getText().toString()) * uidata.UI_DENSITY;
		uidata.UI_PADDING_DEFAULT = (int)(Float.parseFloat(d.getText().toString()) * uidata.UI_DENSITY);
		uidata.UI_RADIUS = Float.parseFloat(f.getText().toString()) * uidata.UI_DENSITY;

	}
	public void changeC()
	{
		int c=Color.argb(gg, hh, jj, kk);
		cc.setColorView(c);
		ccc.setText("#" + Integer.toHexString(c));
	}
	public void init()
	{
		q.setColorView(uidata.UI_COLOR_MAIN);
		w.setColorView(uidata.UI_COLOR_BACK);
		e.setColorView(uidata.UI_COLOR_MAINHL);
		r.setColorView(uidata.UI_TEXTCOLOR_MAIN);
		t.setColorView(uidata.UI_TEXTCOLOR_BACK);
		y.setColorView(uidata.UI_TEXTCOLOR_HL);
		z.setChecked(uidata.UI_USETYPEFACE);
		x.setChecked(uidata.UI_USESHADOW);
		float de=uidata.UI_DENSITY;
		a.setText(uidata.UI_TEXTSIZE_DEFAULT / de + "");
		s.setText(uidata.UI_TEXTSIZE_TITLE / de + "");
		d.setText(uidata.UI_PADDING_DEFAULT / de + "");
		f.setText(uidata.UI_RADIUS / de + "");
	}
}*/
