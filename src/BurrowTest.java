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
    public void TestNetworkAddRemove() {
        TunnelNetwork network = new TunnelNetwork();
        Burrow b = new Burrow();
        assertTrue(network.contains(b));
        network.removeBurrow(b);
        assertFalse(network.contains(b));
    }

    @Test
    public void TestBurrowDecay() {
        TunnelNetwork network = new TunnelNetwork();
        Location l = new Location(0,0);
        Burrow b = new Burrow();
        w.setTile(l,b);
        w.setCurrentLocation(l);
        // Hole should automatically disappear after 100 steps(100 calls of act-method)
        for(int i = 0; i<100; i++) {
            b.act(w);
        }
        assertFalse(w.contains(b));
        assertFalse(network.contains(b));
    }
}
