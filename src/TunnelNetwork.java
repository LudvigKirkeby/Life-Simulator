import itumulator.world.World;

import java.util.*;

 class TunnelNetwork {
    List<Burrow> burrows;

    TunnelNetwork() {
        burrows = new ArrayList<>();
    }

    public void addHole(Burrow burrow) {
    burrows.add(burrow);
    }

    public void removeHole(Burrow burrow) {
        burrows.remove(burrow);
    }

    public int getSize() {
        return burrows.size();
    }

    public Burrow getHole(int pos) {
        return burrows.get(pos);
    }

    public boolean contains(Burrow burrow) {
        return burrows.contains(burrow);
    }

    //Used to make sure deleted holes aren't kept
    public void clean(World world) {
        for(int i = 0; i< burrows.size(); i++) {
            if(burrows.get(i) == null || !world.contains(burrows.get(i))) {
                burrows.remove(i);
                i--;
            }
        }
    }
}
