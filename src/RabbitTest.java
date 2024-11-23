import static org.junit.jupiter.api.Assertions.*;
import itumulator.world.Location;
import itumulator.world.World;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;


public class RabbitTest {
    World w;
    Rabbit r ;
    Location l;


    @BeforeEach
    public void setUp() {
        w = new World(2);
        r = new Rabbit();
        l = new Location(0, 0);
        w.setTile(l, r);
        w.setCurrentLocation(l);
    }

    @Test
    public void testUnburrowSelf() {
        assertTrue(w.isOnTile(r)); // Checking that the rabbit is on a tile
        w.remove(r); // forcefully removing it from the world
        r.unburrow(w); // it unborrows
        w.getLocation(r);
        assertTrue(w.isOnTile(r)); // rabbit should be on a tile again
    }

    @Test
    public void testBurrowSelf() {
        Burrow b = new Burrow();
        w.setTile(l, b);
        assertEquals(w.getLocation(r), w.getLocation(b)); // Checking that the rabbit is on the hole before burrowing
        r.digHole(w, l); // Adding the hole to its own network
        r.burrowSelf(w);
        assertTrue(w.isTileEmpty(l)); // Rabbit should no longer be on the tile
    }

    @Test
    public void testEat() {
        Grass grass = new Grass();
        w.setTile(l, grass);
        assertTrue(w.contains(grass)); // Checking if the location contains grass
        assertEquals(10, r.getHunger()); // Checking that hunger is at max (10)
        r.eat(w, grass);
        assertEquals(10 - grass.getFoodValue(), r.getHunger()); // Hunger has been reduced by grass.getFoodValue()
    }

    @Test
    public void testDigHole() {
        r.digHole(w, l);
        assertTrue(w.containsNonBlocking(l)); // Checking to see if the hole is on the location
    }

    // Checks if a Rabbit can add the hole its standing on to its Network
    @Test
    public void testDigHoleAddExistingBurrowToNetwork() {
        Burrow b = new Burrow();
        w.setTile(l, b);
        assertFalse(r.getNetwork().contains(b), "The network should not contain the burrow.");
        r.digHole(w, l);
        assertTrue(r.getNetwork().contains(b), "The network should now contain the burrow.");
    }

}
