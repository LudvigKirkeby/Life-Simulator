import static org.junit.jupiter.api.Assertions.*;
//import org.junit.jupiter.api.AfterEach;
import itumulator.world.Location;
import itumulator.world.World;
import org.junit.Before;
import org.junit.Test;

public class BurrowTest {
    World w;

    @Before
    public void setUp() {
        w = new World(2);
    }

    @Test
    public void TestHoleDecay() {
        TunnelNetwork network = new TunnelNetwork();
        Location l = new Location(0,0);
        Burrow h = new Burrow(network);
        w.setTile(l,h);
        w.setCurrentLocation(l);
        // Hole should automatically disappear after 100 steps(100 calls of act-method)
        for(int i = 0; i<100; i++) {
            h.act(w);
        }
        assertFalse(w.contains(h));
        assertFalse(network.contains(h));
    }
}
