package com.solo.starlightdrift;
import java.util.*;
public final class SpiderGame {
 public final List<List<Integer>> columns=new ArrayList<>();
 public final List<Integer> stock=new ArrayList<>();
 private final Deque<String> history=new ArrayDeque<>();
 public int score=500,completed,moves;
 public SpiderGame(){for(int i=0;i<10;i++)columns.add(new ArrayList<>());}
 public void newGame(Random random){stock.clear();history.clear();columns.forEach(List::clear);score=500;completed=0;moves=0;for(int i=0;i<8;i++)for(int r=1;r<=13;r++)stock.add(r);Collections.shuffle(stock,random);for(int c=0;c<10;c++){int n=c<4?6:5;for(int i=0;i<n;i++)columns.get(c).add((i==n-1?1:-1)*stock.remove(stock.size()-1));}}
 public boolean movable(int c,int i){if(c<0||c>=10||i<0||i>=columns.get(c).size())return false;List<Integer>a=columns.get(c);for(int j=i;j<a.size();j++)if(a.get(j)<1||(j>i&&a.get(j-1)!=a.get(j)+1))return false;return true;}
 public boolean canMove(int c,int i,int d){if(d<0||d>=10||c==d||!movable(c,i))return false;List<Integer>b=columns.get(d);return b.isEmpty()||b.get(b.size()-1)==columns.get(c).get(i)+1;}
 private void checkpoint(){history.push(encode());if(history.size()>200)history.removeLast();}
 public boolean move(int c,int i,int d){if(!canMove(c,i,d))return false;checkpoint();List<Integer>a=columns.get(c);columns.get(d).addAll(new ArrayList<>(a.subList(i,a.size())));a.subList(i,a.size()).clear();score--;moves++;reveal(a);finish();return true;}
 public boolean canDeal(){return stock.size()>=10&&columns.stream().noneMatch(List::isEmpty);}
 public boolean deal(){if(!canDeal())return false;checkpoint();for(List<Integer>a:columns)a.add(stock.remove(stock.size()-1));score--;moves++;finish();return true;}
 private void reveal(List<Integer>a){if(!a.isEmpty())a.set(a.size()-1,Math.abs(a.get(a.size()-1)));}
 private void finish(){for(List<Integer>a:columns)while(a.size()>=13){int start=a.size()-13;boolean ok=true;for(int i=0;i<13;i++)if(a.get(start+i)!=13-i)ok=false;if(!ok)break;a.subList(start,a.size()).clear();completed++;score+=100;reveal(a);}}
 public boolean canUndo(){return !history.isEmpty();}
 public boolean undo(){if(history.isEmpty())return false;decode(history.pop());return true;}
 public int[] hint(){for(int c=0;c<10;c++)for(int i=0;i<columns.get(c).size();i++)for(int d=0;d<10;d++)if(canMove(c,i,d)&&!(i==0&&columns.get(d).isEmpty()))return new int[]{c,i,d};return null;}
 public String encode(){StringBuilder s=new StringBuilder(score+","+completed+","+moves);for(List<Integer>a:columns){s.append(';');for(int r:a)s.append(r).append(',');}s.append(';');for(int r:stock)s.append(r).append(',');return s.toString();}
 public void decode(String text){String[]p=text.split(";",-1);if(p.length!=12)throw new IllegalArgumentException();String[]head=p[0].split(",");List<List<Integer>>all=new ArrayList<>();int count=0;for(int i=1;i<12;i++){List<Integer>a=new ArrayList<>();for(String token:p[i].split(","))if(!token.isEmpty()){int r=Integer.parseInt(token);if(r==0||Math.abs(r)>13)throw new IllegalArgumentException();a.add(r);count++;}all.add(a);}int done=Integer.parseInt(head[1]);if(done<0||done>8||count+done*13!=104)throw new IllegalArgumentException();score=Integer.parseInt(head[0]);completed=done;moves=Integer.parseInt(head[2]);for(int i=0;i<10;i++){columns.get(i).clear();columns.get(i).addAll(all.get(i));}stock.clear();stock.addAll(all.get(10));}
}
