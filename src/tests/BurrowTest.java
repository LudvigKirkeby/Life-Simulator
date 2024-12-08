package tests;

import static org.junit.jupiter.api.Assertions.*;
//import org.junit.jupiter.api.AfterEach;
import misc.Burrow;
import org.junit.Before;
import org.junit.Test;
import itumulator.world.Location;
import itumulator.world.World;
import misc.TunnelNetwork;

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
        network.addBurrow(b);
        assertTrue(network.contains(b));
        network.removeBurrow(b);
        assertFalse(network.contains(b));
    }

    @Test
    public void TestNetworkClean() {// Clean removes Burrows that are not in the provided world from the network
        TunnelNetwork network = new TunnelNetwork();
        Burrow b = new Burrow();
        w.setTile(new Location(0, 0), b);
        network.addBurrow(b);
        assertTrue(network.contains(b));
        w.delete(b);
        network.clean(w);
        assertFalse(network.contains(b));
    }

    @Test
    public void TestBurrowDecay() {
        Location l = new Location(0,0);
        Burrow b = new Burrow();
        w.setTile(l,b);
        w.setCurrentLocation(l);
        // Hole should automatically disappear after 100 steps(100 calls of act-method)
        for(int i = 0; i<100; i++) {
            b.act(w);
        }
        assertFalse(w.contains(b));
    }
}
