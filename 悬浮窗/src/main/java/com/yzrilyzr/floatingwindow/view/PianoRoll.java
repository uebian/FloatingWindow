package com.yzrilyzr.floatingwindow.view;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.ui.uidata;
import java.util.ArrayList;

public class PianoRoll extends View
{
    private Paint 填充画笔,描线画笔;
    private int 红纵线=0xffff0000,黑键=0xff444444,绿纵线=0xff00ff00,音符色=0xff77ff00,灰横线=0xffcccccc,背景色=0xff888888,音高字色=0xff222222,选定色=0xff00ffff,指针色=0xffffff00,触摸轨迹=0xffff00ff;
    private ArrayList<Note> Notes=new ArrayList<Note>();
    private ArrayList<Note> shownNotes=new ArrayList<Note>();
    public int NoteWidth=20,NoteHeight=15,maxMapSize=0;
    private float xOff=0,yOff=0,dxOff=0,dyOff=0,xVel=0,yVel=0;
    public Note currentNote,newNote,cursorPointedNote;
    private float cursorX=0,cur2X=0;
    private boolean secondTouch=false,touchable=true,isTouch=false,onCur2=false;
    private Path path=new Path();
    private String[] baseFreq=new String[]{"C1","C2","C3","C4","C5","C6","C7","C8"};
    private String[] cdefgab=new String[]{"#1","2","#2","3","#3","4","5","#5","6","#6","7"};
	private boolean[] black=new boolean[]{false,true,false,true,false,false,true,false,true,false,true,false};
    public String info="";
    private long curTime=0;
    private int fps=0,deltaTime=0;
    public int beats=4,beatsUnit=4,bpm=120,qlen=2;
    public PianoRoll(Context c,AttributeSet a)
    {
        super(c);
        填充画笔=new Paint(Paint.ANTI_ALIAS_FLAG);
        填充画笔.setStyle(Paint.Style.FILL);
        填充画笔.setTypeface(Typeface.MONOSPACE);
        填充画笔.setTextSize(util.px(15));
        描线画笔=new Paint();
        描线画笔.setStrokeWidth(util.px(1));
        描线画笔.setStyle(Paint.Style.STROKE);
        NoteWidth=util.px(NoteWidth);
        NoteHeight=util.px(NoteHeight);
        yOff=4*NoteHeight*baseFreq.length;
    }
    public void setBeat(int beats,int beatsUnit,int bpm)
	{
        this.beats=beats;
        this.beatsUnit=beatsUnit;
        this.bpm=bpm;
        postInvalidate();
    }
    @Override
    protected void onDraw(Canvas canvas)
    {
        // TODO: Implement this method
        //super.onDraw(canvas);
        if(xOff>0)xOff=0;
        if(yOff<0)yOff=0;
        if(yOff>11*NoteHeight*baseFreq.length)yOff=10*NoteHeight*baseFreq.length;

        int vw=getWidth(),vh=getHeight();
        canvas.drawColor(背景色);
		for(int i=0;i<baseFreq.length;i++)
        {
			填充画笔.setColor(uidata.TEXTMAIN);
			float dy=(i-baseFreq.length)*NoteHeight*12+yOff;
			canvas.drawText(baseFreq[baseFreq.length-1-i],0,dy+25*NoteHeight,填充画笔);
            for(int u=0;u<cdefgab.length;u++)
            {
				if(black[u])
				{
					填充画笔.setColor(黑键);
					canvas.drawRect(0,dy+(u+25)*NoteHeight,vw,dy+(u+26)*NoteHeight,填充画笔);
				}
				填充画笔.setColor(uidata.TEXTMAIN);
                canvas.drawText(cdefgab[cdefgab.length-1-u],0,dy+(u+26)*NoteHeight,填充画笔);
			}
        }
		描线画笔.setColor(灰横线);
		for(float i=yOff%NoteHeight;i<vh;i+=NoteHeight)
			canvas.drawLine(0,i,vw,i,描线画笔);

		float lineGap=NoteWidth/beatsUnit/qlen;
        for(float i=xOff%(NoteWidth);i<vw;i+=lineGap/qlen)
            canvas.drawLine(i,0,i,vh,描线画笔);
        描线画笔.setColor(绿纵线);
        for(float i=xOff%(NoteWidth);i<vw;i+=lineGap)
            canvas.drawLine(i,0,i,vh,描线画笔);
        描线画笔.setColor(红纵线);
		for(float i=xOff%(NoteWidth);i<vw;i+=lineGap*beats)
			canvas.drawLine(i,0,i,vh,描线画笔);
		填充画笔.setColor(0xff002222);
		canvas.drawRect(0,0,vw,描线画笔.getTextSize()*4,填充画笔);
		填充画笔.setColor(uidata.TEXTMAIN);
        for(float i=xOff%(NoteWidth);i<vw;i+=lineGap*beats)
		canvas.drawText(Integer.toString((int)((-xOff+i)/lineGap/beats+1)),i,描线画笔.getTextSize()*4,填充画笔);
		float vx=-xOff,vy=-yOff;
        shownNotes.clear();
        cursorPointedNote=null;
		RectF r=new RectF();
	 for(Note n:Notes)
        {
            int sx=n.off*NoteWidth;
            int ex=sx+n.len*NoteWidth;
            int sy=n.hei*NoteHeight;
            int ey=sy+NoteHeight;
            maxMapSize=Math.max(maxMapSize,n.off+n.len);
            if(sx<cursorX&&ex>cursorX)cursorPointedNote=n;
            // canvas.drawText(sx+","+ex,50,100,NotePaint);
            if(vx<ex&&vx+vw>sx&&vy<ey&&vy+vh>sy)
            {
                if(n.len<=0)continue;
				n.getRect(r);
				填充画笔.setColor(n==currentNote?选定色:音符色);
				canvas.drawRoundRect(r,NoteHeight/2,NoteHeight/2,填充画笔);
				//float stroke =填充画笔.getStrokeWidth() ;
				//描线画笔.setColor(0x00ff9900);
				//canvas.drawRect(r,描线画笔);
                shownNotes.add(n);
            }
        }

        //if(cursorPointedNote!=null)info=cursorPointedNote.lrc+"["+cursorPointedNote.phnms+"]";
        //else info="";
        填充画笔.setColor(uidata.TEXTMAIN);
        if(deltaTime!=0)fps=((1000/deltaTime)+fps)/2;
        canvas.drawText("bpm:"+bpm+","+beats+"/"+beatsUnit+","+(int)xOff+","+(int)yOff+",fps:"+fps+","+info,50,150,填充画笔);
        描线画笔.setColor(指针色);
        canvas.drawLine(cursorX+xOff,0,cursorX+xOff,vh,描线画笔);
        描线画笔.setColor(0xff2299ff);
		填充画笔.setColor(0xff2299ff);
        canvas.drawLine(cur2X+xOff,0,cur2X+xOff,vh,描线画笔);
		canvas.drawCircle(cur2X+xOff,描线画笔.getTextSize()*2,描线画笔.getTextSize()*2,描线画笔);
        deltaTime=(int)(System.currentTimeMillis() -curTime);
        curTime=System.currentTimeMillis();
        if(!isTouch)
		{
            if(xVel>2)
            {xVel--;xOff+=xVel;invalidate();}
            if(xVel<-2)
            {xVel++;xOff+=xVel;invalidate();}
            if(yVel>2)
            {yVel--;yOff+=yVel;invalidate();}
            if(yVel<-2)
            {yVel++;yOff+=yVel;invalidate();}
        }
    }
    public void setData(ArrayList<Note> list)
    {
        Notes=list;
        postInvalidate();
    }
    public void setTouchable(boolean b)
    {
        touchable=b;
    }
    public ArrayList<Note> getData()
    {
        return Notes;
    }
    public ArrayList<Note> getShownData()
    {
        return shownNotes;
    }

    public void setPosition(float x,float y)
    {
        xOff=-x;
        yOff=y;
        postInvalidate();
    }
    public float getX()
    {
        return -xOff;
    }
    public float getY()
    {
        return yOff; 
    }
    public int getBpm()
	{
        return bpm;
    }
    public int getBeats()
	{
        return beats;
    }
    public int getBeatsUnit()
	{
        return beatsUnit;
    }
    public void setDeltaPosition(float dx,float dy)
    {
        xOff-=dx;
        yOff+=dy;
        postInvalidate();
    }
    public void setCursor(int x)
    {
        cursorX=x;
        postInvalidate();
    }
    public float getCursor()
    {
        return cursorX;
    }
    public void sortNote()
    {
        boolean b=true;
        while(b)
        {
            b=false;
            for(int i=0;i<Notes.size()-1;i++)
            {
                if(Notes.get(i).off>Notes.get(i+1).off)
                {
                    Note tmp1=Notes.get(i),tmp2=Notes.get(i+1);
                    Notes.set(i,tmp2);
                    Notes.set(i+1,tmp1);
                    b=true;
                }
            }
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
		getParent().requestDisallowInterceptTouchEvent(true);
        // TODO: Implement this method
        float x=event.getX();
        float y=event.getY();
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                isTouch=true;
                path.reset();
                dxOff=x;
                dyOff=y;
                currentNote=null;
                secondTouch=false;
                //getCurrentNote(x,y);
				if(y<描线画笔.getTextSize()*4)onCur2=true;
				else onCur2=false;
				break;
            case MotionEvent.ACTION_UP:
                isTouch=false;
				if(onCur2){
					cur2X=x-xOff;
					return true;
				}
                getCurrentNote(x, y);
                if(currentNote!=null&&!secondTouch)
                {
                    /*Toast.makeText(getContext(),currentNote.lrc,0).show();
					 setFocusable(true);
					 setFocusableInTouchMode(true);
					 requestFocus();

					 InputMethodManager imm = (InputMethodManager)getContext()
					 .getSystemService(Context.INPUT_METHOD_SERVICE);
					 imm.showSoftInput(MainView.this, InputMethodManager.SHOW_IMPLICIT);
					 */
                    return false;
                }
                else
                {
                    ((InputMethodManager)getContext(). getSystemService(Context.INPUT_METHOD_SERVICE)). 
					hideSoftInputFromWindow(getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS); 

                }
                break;
            case MotionEvent.ACTION_MOVE:
				if(onCur2){
					cur2X=x-xOff;
					return true;
				}
				if(touchable)
                {
                    xVel=(x-dxOff);
                    xOff+=xVel;
                    dxOff=x;

                }
                yVel=(y-dyOff);
                yOff+=yVel;
                dyOff=y;

                if(event.getPointerCount()>1)
                {
                    float x2=event.getX(1);
                    float y2=event.getY(1);
                    if(!secondTouch)path.moveTo(x2,y2);
                    secondTouch=true;

                    path.lineTo(x2,y2);
                    getCurrentNote(x2,y2);
                    if(currentNote!=null)
                    {
                        float a=currentNote.len*NoteWidth/2;
                        float c=currentNote.off*NoteWidth;
                        int b=Math.round((x2-xOff)/NoteWidth);
                        if(x2<xOff+c+a)currentNote.off=b;
                        else if(x2>xOff+c+a)currentNote.len=b-currentNote.off;
						currentNote.hei=Math.round((y2-yOff)/NoteHeight);
                        if(currentNote.len<=0){
							Notes.remove(currentNote);
							currentNote=null;
						}
                        
                    }
                    else
                    {
                        int b=(int)((x2-xOff)/NoteWidth);
                        if(newNote==null)
                        {
                            newNote=new Note();
                            newNote.off=b;
                            //newNote.lrc="a";
                            newNote.len=1;
                            Notes.add(newNote);
                            currentNote=newNote;
                            newNote=null;
                        }
                    }
                }
                break;
        }
        invalidate();
        return true;
    }
	public class Note
	{
		public int hei,len,off;
		public RectF getRect(RectF r){
			r.set(off*NoteWidth+xOff,hei*NoteHeight+yOff,(off+len)*NoteWidth+xOff,(hei+1)*NoteHeight+yOff);
			return r;
		}
	}
    private void getCurrentNote(float x, float y)
    {
		RectF r=new RectF();
        for(Note n:shownNotes)
        {
            if(n.getRect(r).contains(x,y))
            {
                currentNote=n;
                break;
            }
        }
    }
}
