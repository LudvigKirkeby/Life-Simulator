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

}
