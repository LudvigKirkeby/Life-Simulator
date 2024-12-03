package tests;

import static org.junit.jupiter.api.Assertions.*;
//import org.junit.jupiter.api.AfterEach;
import animals.AnimalPack;
import animals.Rabbit;
import misc.Carcass;
import misc.Cave;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import animals.Wolf;
import itumulator.world.Location;
import itumulator.world.World;

import java.util.Set;

/*
    Should test:
    ✓ Wolf places den if pack doesn't have one
    ✓ Wolf hunts down prey
    ✓ Wolf attacks prey when near
    ✓ Wolf seeks den during nighttime
    ✓ Wolf sleeps when near den during nighttime
    ✓ Wolf eats Carcass and decreases hunger
    - Wolf reproduces under correct circumstances
 */

public class WolfTest {
    World world;
    Wolf wolf;
    Location init_location;

    @BeforeEach
    void setUp() {
        world = new World(3);
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

    @Test
    public void testEatsCarcass() {
        Carcass carcass = new Carcass(5, false);
        world.setTile(new Location(1, 0), carcass);
        wolf.setAge(5); // Makes sure getGrownUp() is true
        wolf.setHunger(4); // If the wolf is not hungry then it won't hunt
        wolf.act(world);
        assertFalse(world.contains(carcass));
    }

    @Test
    public void testSeeksDenAtNight() {
        // There is a chance that wolf randomly wanders toward the den
        // but, having run this test multiple times, it always seems to succeed.
        world.setNight();
        AnimalPack pack = new AnimalPack();
        pack.setCenter(new Location(2, 0));
        wolf.setPack(pack);
        wolf.act(world);
        assertEquals(world.getTile(new Location(1, 0)), wolf);
    }

    @Test
    public void testSleepsNearDenAtNight() {
        world.setNight();
        AnimalPack pack = new AnimalPack();
        pack.setCenter(new Location(1, 0));
        wolf.setPack(pack);
        wolf.act(world);
        assertTrue(wolf.isSleeping());
    }

    @Test
    public void testAttacksPrey() {
        AnimalPack pack = new AnimalPack();
        pack.setCenter(new Location(1, 0));
        Rabbit rabbit = new Rabbit();
        world.setTile(new Location(0, 1), rabbit);
        wolf.setPack(pack);
        wolf.setAge(5);
        wolf.setHunger(4);
        wolf.act(world);
        assertTrue(rabbit.getHP() < 6);
    }

    @Test
    public void testHuntsPrey() {
        AnimalPack pack = new AnimalPack();
        pack.setCenter(new Location(1, 0));
        Rabbit rabbit = new Rabbit();
        world.setTile(new Location(2, 2), rabbit);
        wolf.setPack(pack);
        wolf.setAge(5);
        wolf.setHunger(4);
        wolf.act(world);
        assertEquals(new Location(1, 1), world.getLocation(wolf));
    }
}
