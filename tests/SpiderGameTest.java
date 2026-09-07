import com.solo.starlightdrift.SpiderGame;
import java.util.*;
public class SpiderGameTest {
 static void check(boolean ok,String m){if(!ok)throw new AssertionError(m);}
 public static void main(String[]args){
  SpiderGame g=new SpiderGame();g.newGame(new Random(7));
  check(g.stock.size()==50,"stock");int[] counts=new int[14];
  for(int c=0;c<10;c++){List<Integer>a=g.columns.get(c);check(a.size()==(c<4?6:5),"deal size");for(int i=0;i<a.size();i++){check((a.get(i)>0)==(i==a.size()-1),"face up");counts[Math.abs(a.get(i))]++;}}
  for(int r:g.stock)counts[r]++;for(int r=1;r<14;r++)check(counts[r]==8,"eight copies");
  String initial=g.encode();check(g.deal(),"deal");check(g.stock.size()==40,"stock reduced");check(g.undo()&&g.encode().equals(initial),"deal undo");
  SpiderGame restored=new SpiderGame();restored.decode(initial);check(restored.encode().equals(initial),"save roundtrip");
  int[]hint=g.hint();check(hint!=null,"hint exists");check(g.move(hint[0],hint[1],hint[2]),"hint legal");check(g.undo()&&g.encode().equals(initial),"move undo");
  g.columns.forEach(List::clear);g.stock.clear();g.completed=0;g.score=500;
  g.columns.get(0).add(-8);for(int r=13;r>=2;r--)g.columns.get(0).add(r);g.columns.get(1).add(1);
  for(int i=0;i<90;i++)g.stock.add(i%13+1);
  String before=g.encode();check(g.move(1,0,0),"complete run");check(g.completed==1&&g.score==599&&g.columns.get(0).equals(Arrays.asList(8)),"remove run and flip");
  check(g.undo()&&g.encode().equals(before),"completion undo");check(!g.canDeal(),"empty column blocks deal");
  g.columns.get(2).addAll(Arrays.asList(7,5));check(!g.movable(2,0),"broken run invalid");check(!g.move(2,0,3),"reject broken run");
  System.out.println("PASS: deal, rank counts, moves, undo, completion, reveal, save, invalid moves");
 }
}
