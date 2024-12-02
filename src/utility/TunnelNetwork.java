package utility;

import animals.Animal;
import itumulator.world.World;
import misc.Burrow;

import java.util.HashSet;
import java.util.Set;

 public class TunnelNetwork {
    private Set<Burrow> burrows;

    public TunnelNetwork() {
        burrows = new HashSet<>();
    }

    public void addBurrow(Burrow burrow) {
    burrows.add(burrow);
    }

    public void removeBurrow(Burrow burrow) {
        burrows.remove(burrow);
    }

    public int getSize() {
        return burrows.size();
    }

    public boolean contains(Burrow burrow) {
        return burrows.contains(burrow);
    }

    //Used to make sure deleted holes aren't kept
    public void clean(World world) {
        Set<Burrow> new_burrows = new HashSet<>();
        for(Burrow burrow : burrows) {
            if(world.contains(burrow) && world.isOnTile(burrow)) new_burrows.add(burrow);
        }
        burrows = new_burrows;
    }
}
