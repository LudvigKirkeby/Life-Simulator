import itumulator.world.World;

import java.util.*;

 class TunnelNetwork {
    List<Hole> holes;

    TunnelNetwork() {
        holes = new ArrayList<>();
    }

    public void addHole(Hole hole) {
    holes.add(hole);
    }

    public void removeHole(Hole hole) {
        holes.remove(hole);
    }

    public int getSize() {
        return holes.size();
    }

    public Hole getHole(int pos) {
        return holes.get(pos);
    }

    public boolean contains(Hole hole) {
        return holes.contains(hole);
    }

    //Used to make sure deleted holes aren't kept
    public void clean(World world) {
        for(int i = 0; i<holes.size(); i++) {
            if(holes.get(i) == null || !world.contains(holes.get(i))) {
                holes.remove(i);
                i--;
            }
        }
    }
}
