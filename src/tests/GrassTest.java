package tests;

import static org.junit.jupiter.api.Assertions.*;
//import org.junit.jupiter.api.AfterEach;
import plants.Grass;
import org.junit.jupiter.api.Test;
import itumulator.world.Location;
import itumulator.world.World;

public class GrassTest {
    @Test
    public void testNoAvailableTiles() throws Exception {
        World w = new World(2);
        Grass g = new Grass();
        Location l = new Location(0, 0);
        w.setTile(l, g);
        w.setTile(new Location(1,0), new Grass());
        w.setTile(new Location(0,1), new Grass());
        w.setTile(new Location(1,1), new Grass());
        w.setCurrentLocation(l);
        g.grow(Grass.class, w);
    }

    @Test
    public void testGrassChance() { // The chance of grass growth should be 10%. Accepts 7.5% - 12.5%
        int attempts = 1000,successfulAttempts = 0;
        double variation = 0.025;
        for(int i = 0; i<attempts; i++) {
            World w = new World(2);
            Grass g = new Grass();
            Location l = new Location(0, 0);
            w.setTile(l, g);
            w.setCurrentLocation(l);
            g.act(w);
            for(Location tile : w.getSurroundingTiles()) {
                if(w.containsNonBlocking(tile)) {
                    successfulAttempts++;
                    break;
                }
            }
        }
        double grow_chance = (1.0 * successfulAttempts)/attempts;
        assertTrue(grow_chance>0.1-variation && grow_chance<0.1+variation);
    }
}
