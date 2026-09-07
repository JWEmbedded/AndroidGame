package com.solo.starlightdrift;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.*;
import android.view.*;
import java.util.*;

public final class SpiderView extends View {
 private final SpiderGame game=new SpiderGame();
 private CardSounds sounds;
 private final Paint p=new Paint(3);
 private final SharedPreferences prefs;
 private float w,h,cw,ch,scale=1,ox,oy;
 private int selected=-1,index=-1,hintDest=-1;
 private float downX,downY,dragX,dragY;
 private boolean dragging,paused,background;
 private long last,elapsed,messageUntil;
 private String message="카드를 끌거나 선택한 뒤 목적지를 누르세요";
 public SpiderView(Context context){super(context);setFocusable(true);prefs=context.getSharedPreferences("spider_solitaire",0);try{game.decode(prefs.getString("board",""));elapsed=prefs.getLong("elapsed",0);}catch(Exception e){game.newGame(new Random());}messageUntil=android.os.SystemClock.uptimeMillis()+6500;setContentDescription("스파이더 카드놀이. 스페이드 한 무늬.");}
 @Override protected void onSizeChanged(int width,int height,int a,int b){scale=Math.min(width/1200f,height/560f);w=width/scale-72;h=height/scale-12;ox=36*scale;oy=6*scale;cw=(w-16-108)/10;ch=cw*1.38f;}
 private float step(int col){return Math.min(28,Math.max(3,(h-110-57-ch)/Math.max(1,game.columns.get(col).size()-1)));}
 private float x(int col){return 8+col*(cw+12);}
 private void text(Canvas c,String s,float x,float y,float size,int color,Paint.Align align){p.setShader(null);p.setStyle(Paint.Style.FILL);p.setColor(color);p.setTextSize(size);p.setTypeface(Typeface.create("sans-serif",Typeface.BOLD));p.setTextAlign(align);c.drawText(s,x,y,p);}
 private void box(Canvas c,float x,float y,float width,float height,int color,float radius){p.setShader(null);p.setStyle(Paint.Style.FILL);p.setColor(color);c.drawRoundRect(x,y,x+width,y+height,radius,radius,p);}
 @Override protected void onDraw(Canvas c){
  long now=android.os.SystemClock.uptimeMillis();if(last!=0&&!paused&&!background&&game.completed<8)elapsed+=Math.min(now-last,1000);last=now;
  c.drawColor(0xff214c80);c.save();c.translate(ox,oy);c.scale(scale,scale);
  p.setShader(new LinearGradient(0,0,w,h,new int[]{0xff204b82,0xff3979bb,0xff204e86},null,Shader.TileMode.CLAMP));c.drawRect(-36,-6,w+36,h+6,p);p.setShader(null);
  p.setColor(0x13ffffff);for(int i=0;i<2200;i++)c.drawPoint((i*73.17f)%w,(i*39.71f)%h,p);
  box(c,0,0,w,42,0x50203960,8);long sec=elapsed/1000;
  text(c,String.format(Locale.US,"TIME  %02d:%02d:%02d",sec/3600,sec/60%60,sec%60),14,28,20,0xffaaceff,Paint.Align.LEFT);
  text(c,"SPIDER SOLITAIRE",w/2,30,28,Color.WHITE,Paint.Align.CENTER);
  text(c,String.format(Locale.US,"SCORE  %04d",game.score),w-14,28,20,0xffaaceff,Paint.Align.RIGHT);
  for(int col=0;col<10;col++){box(c,x(col),57,cw,ch,0x30203c65,6);java.util.List<Integer>a=game.columns.get(col);for(int i=0;i<a.size();i++){if(dragging&&col==selected&&i>=index)continue;boolean blocked=a.get(i)>0&&!game.movable(col,i);card(c,x(col),57+i*step(col),a.get(i),col==selected&&i>=index,ch,blocked);}if(col==hintDest){p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(3);p.setColor(0xffffda78);c.drawRoundRect(x(col)-3,54,x(col)+cw+3,57+Math.max(ch,(a.size()-1)*step(col)+ch)+3,7,7,p);p.setStyle(Paint.Style.FILL);}}
  button(c,10,h-65,66,"Ⅱ",false);button(c,w/2-185,h-65,150,"↶  되돌리기",!game.canUndo());button(c,w/2-20,h-65,105,"힌트",false);button(c,w/2+100,h-65,105,"새 게임",false);
  text(c,"완성  "+game.completed+" / 8",100,h-28,19,0xffb7d6ff,Paint.Align.LEFT);
  int packs=game.stock.size()/10;for(int i=0;i<packs;i++)card(c,w-cw-10-(packs-1-i)*16,h-85,-1,false,78);
  if(packs==0)text(c,"배분 완료",w-12,h-28,18,0xffb7d6ff,Paint.Align.RIGHT);
  if(dragging&&selected>=0){java.util.List<Integer>a=game.columns.get(selected);for(int i=index;i<a.size();i++)card(c,dragX-cw/2,dragY-22+(i-index)*step(selected),a.get(i),true,ch);}
  if(now<messageUntil&&!paused){box(c,w/2-300,h-112,600,32,0xdb173757,10);text(c,message,w/2,h-89,17,Color.WHITE,Paint.Align.CENTER);}
  if(paused||game.completed==8){box(c,-36,-6,w+72,h+12,0xd010243b,0);text(c,game.completed==8?"모든 카드를 완성했어요!":"잠시 쉬어가기",w/2,h/2-55,34,Color.WHITE,Paint.Align.CENTER);text(c,game.completed==8?"점수 "+game.score+" · 이동 "+game.moves:"K부터 A까지 차례대로 모으면 한 묶음이 완성됩니다",w/2,h/2-12,20,0xffc1dbf8,Paint.Align.CENTER);button(c,w/2-110,h/2+20,220,game.completed==8?"새 게임":"계속하기",false);}
  c.restore();if(!background)postInvalidateDelayed(33);
 }
 private void button(Canvas c,float x,float y,float width,String label,boolean disabled){box(c,x,y+3,width,52,0xff183d69,9);box(c,x,y,width,52,disabled?0xff3b6088:0xff4a7ebd,9);text(c,label,x+width/2,y+34,label.equals("Ⅱ")?30:20,disabled?0xff8eaccd:Color.WHITE,Paint.Align.CENTER);}
 private void card(Canvas c,float xx,float yy,int rank,boolean highlight,float height){
  card(c,xx,yy,rank,highlight,height,false);
 }
 private void card(Canvas c,float xx,float yy,int rank,boolean highlight,float height,boolean blocked){
  // Use the same rule as touch handling: every card below this card must descend by one.
  box(c,xx+1,yy+2,cw,height,0x550c2446,5);box(c,xx,yy,cw,height,blocked?0xff9299a2:highlight?0xffffdd81:Color.WHITE,5);
  if(rank<0){box(c,xx+3,yy+3,cw-6,height-6,0xff71a4ec,3);p.setColor(0x30ffffff);p.setStrokeWidth(1);for(float k=10;k<cw;k+=9)c.drawLine(xx+k,yy+5,xx+k,yy+height-5,p);text(c,"♠",xx+cw/2,yy+height*.75f,Math.min(cw*.65f,height*.7f),0xffe1edff,Paint.Align.CENTER);return;}
  String label=rank==1?"A":rank==11?"J":rank==12?"Q":rank==13?"K":""+rank;
  text(c,label,xx+6,yy+25,25,0xff1e2024,Paint.Align.LEFT);text(c,"♠",xx+cw-6,yy+25,23,0xff1e2024,Paint.Align.RIGHT);
  text(c,"♠",xx+cw/2,yy+height*.85f,cw*.83f,0xff202224,Paint.Align.CENTER);
  if(rank>10)text(c,"♛",xx+cw/2,yy+height*.44f,cw*.32f,blocked?0xff343b44:0xffb48724,Paint.Align.CENTER);
 }
 private void tell(String msg){message=msg;messageUntil=android.os.SystemClock.uptimeMillis()+4000;invalidate();}
 private void save(){prefs.edit().putString("board",game.encode()).putLong("elapsed",elapsed).apply();}
 private boolean moveWithSound(int col,int card,int dest){int before=game.completed;if(!game.move(col,card,dest))return false;if(sounds!=null){sounds.place();if(game.completed>before)sounds.complete();}return true;}
 private boolean dealWithSound(){int before=game.completed;if(!game.deal())return false;if(sounds!=null){sounds.place();if(game.completed>before)sounds.complete();}return true;}
 private void clearSelection(){selected=-1;index=-1;dragging=false;hintDest=-1;}
 private int columnAt(float xx){int col=(int)((xx-8)/(cw+12));return xx<8||col>=10?-1:col;}
 private int cardAt(int col,float yy){if(col<0||yy<57)return -1;int n=game.columns.get(col).size();if(n==0||yy>57+(n-1)*step(col)+ch)return -1;return Math.min(n-1,(int)((yy-57)/step(col)));}
 @Override public boolean onTouchEvent(MotionEvent e){
  float xx=(e.getX()-ox)/scale,yy=(e.getY()-oy)/scale;
  if(e.getActionMasked()==MotionEvent.ACTION_DOWN){downX=xx;downY=yy;return true;}
  if(e.getActionMasked()==MotionEvent.ACTION_MOVE&&!paused&&game.completed<8){if(!dragging&&Math.hypot(xx-downX,yy-downY)>10&&downY<h-100){int col=columnAt(downX),i=cardAt(col,downY);if(game.movable(col,i)){selected=col;index=i;dragging=true;hintDest=-1;if(sounds!=null)sounds.pickUp();}}dragX=xx;dragY=yy;invalidate();return true;}
  if(e.getActionMasked()==MotionEvent.ACTION_CANCEL){dragging=false;invalidate();return true;}
  if(e.getActionMasked()!=MotionEvent.ACTION_UP)return true;performClick();
  if(paused||game.completed==8){if(xx>w/2-110&&xx<w/2+110&&yy>h/2+20&&yy<h/2+72){if(game.completed==8)newGame();else{paused=false;last=0;}}invalidate();return true;}
  if(dragging){if(yy>=57&&yy<h-90&&moveWithSound(selected,index,columnAt(xx)))save();else tell("한 단계 큰 숫자 위 또는 빈 열에 놓으세요");clearSelection();invalidate();return true;}
  if(yy>=h-85&&xx>=w-cw-90){clearSelection();if(dealWithSound())save();else tell(game.stock.isEmpty()?"남은 배분 카드가 없습니다":"빈 열을 채워야 새 카드를 배분할 수 있어요");}
  else if(yy>=h-65){
   if(xx<80){paused=true;clearSelection();save();}
   else if(xx>w/2-185&&xx<w/2-35){if(game.undo()){clearSelection();save();}else tell("되돌릴 이동이 없습니다");}
   else if(xx>w/2-20&&xx<w/2+85){int[] hint=game.hint();if(hint!=null){selected=hint[0];index=hint[1];hintDest=hint[2];tell((hint[0]+1)+"열의 선택 카드를 "+(hint[2]+1)+"열로 옮기세요");}else tell(game.canDeal()?"추가 카드를 배분해 보세요":"가능한 이동이 없습니다. 되돌리기를 사용하세요");}
   else if(xx>w/2+100&&xx<w/2+205){paused=true;new AlertDialog.Builder(getContext()).setTitle("새 게임을 시작할까요?").setMessage("현재 판은 새 판으로 바뀝니다.").setPositiveButton("새 게임",(d,v)->newGame()).setNegativeButton("계속하기",(d,v)->{paused=false;last=0;invalidate();}).setOnCancelListener(d->{paused=false;last=0;invalidate();}).show();}
  }else{int col=columnAt(xx),i=cardAt(col,yy);if(selected>=0&&col!=selected&&yy>=57&&moveWithSound(selected,index,col)){clearSelection();save();}else if(game.movable(col,i)){selected=col;index=i;hintDest=-1;if(sounds!=null)sounds.pickUp();}else{clearSelection();tell("앞면의 연속된 내림차순 카드만 옮길 수 있어요");}}
  invalidate();return true;
 }
 private void newGame(){game.newGame(new Random());elapsed=0;last=0;paused=false;clearSelection();save();invalidate();}
 @Override public boolean performClick(){super.performClick();return true;}
 @Override protected void onAttachedToWindow(){super.onAttachedToWindow();sounds=new CardSounds();}
 @Override protected void onDetachedFromWindow(){if(sounds!=null){sounds.release();sounds=null;}super.onDetachedFromWindow();}
 public void pauseForLifecycle(){background=true;paused=true;if(sounds!=null)sounds.stop();clearSelection();save();}
 public void resumeFromLifecycle(){background=false;last=0;invalidate();}
}
