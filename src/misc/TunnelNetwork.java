package misc;

import itumulator.world.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

 public class TunnelNetwork implements Iterable<Burrow> {
    private List<Burrow> burrows;

    public TunnelNetwork() {
        burrows = new ArrayList<>();
    }

    public void addBurrow(Burrow burrow) {
    burrows.add(burrow);
    }

    public void removeBurrow(Burrow burrow) {
        burrows.remove(burrow);
    }

    public Burrow getBurrow(int index) {
        return burrows.get(index);
    }

    public int getSize() {
        return burrows.size();
    }

    public boolean contains(Burrow burrow) {
        return burrows.contains(burrow);
    }

    //Used to make sure deleted holes aren't kept
    public void clean(World world) {
        for(int i = 0; i < burrows.size(); i++) {
            if(!world.contains(burrows.get(i))) {
                burrows.remove(i);
                i--;
            }
        }
    }

     @Override
     public Iterator<Burrow> iterator() {
         return burrows.iterator();
     }
 }
