package tests;

import static org.junit.jupiter.api.Assertions.*;
//import org.junit.jupiter.api.AfterEach;
import misc.Cave;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import animals.Wolf;
import itumulator.world.Location;
import itumulator.world.World;

import java.util.Set;

/*
    Should test:
    - Wolf hunts down prey
    - Wolf attacks prey when near
    - Wolf seeks den when sleepy
    - Wolf eats Carcass and decreases hunger
    - Wolf reproduces under correct circumstances
 */

public class WolfTest {
    World world;
    Wolf wolf;
    Location init_location;

    @BeforeEach
    void setUp() {
        world = new World(2);
        wolf = new Wolf();
        init_location = new Location(0, 0);
        world.setTile(init_location, wolf);
        world.setCurrentLocation(init_location);
    }

    @Test
    public void testPlaceDen() {
        wolf.act(world);
        Cave den = null;
        Set<Location> tiles = world.getSurroundingTiles(world.getLocation(wolf));
        for(Location tile : tiles) {
            if(world.getTile(tile) instanceof Cave cave) {
                den = cave;
                break;
            }
        }
        assertNotNull(den);
    }
}
