package tests;

import static org.junit.jupiter.api.Assertions.*;
//import org.junit.jupiter.api.AfterEach;
import animals.Rabbit;
import itumulator.world.Location;
import misc.Burrow;
import misc.Grass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import itumulator.world.World;

public class RabbitTest {
    World world;
    Rabbit rabbit;
    Location init_location;

    @BeforeEach
    void setUp() {
        world = new World(2);
        rabbit = new Rabbit();
        init_location = new Location(0, 0);
        world.setTile(init_location, rabbit);
        world.setCurrentLocation(init_location);
    }

    @Test
    public void testBurrowSelf() {
        Burrow b = new Burrow();
        world.setTile(init_location, b);
        assertEquals(world.getLocation(rabbit), world.getLocation(b)); // Checking that the rabbit is on the hole before burrowing
        rabbit.digHole(world, init_location); // Adding the hole to its own network
        rabbit.burrowSelf(world);
        assertTrue(world.isTileEmpty(init_location)); // Rabbit should no longer be on the tile
    }

    @Test
    public void testUnburrowSelf() {
        assertTrue(world.isOnTile(rabbit)); // Checking that the rabbit is on a tile
        world.remove(rabbit); // forcefully removing it from the world
        rabbit.unburrow(world); // it unborrows
        world.getLocation(rabbit);
        assertTrue(world.isOnTile(rabbit)); // rabbit should be on a tile again
    }

    @Test
    public void testRabbitDigEmpty() {
        rabbit.digHole(world,init_location);
        assertInstanceOf(Burrow.class, world.getNonBlocking(init_location));
    }

    @Test
    public void testRabbitDigOnGrass() {
        world.setTile(init_location, new Grass());
        rabbit.digHole(world,init_location);
        assertInstanceOf(Burrow.class, world.getNonBlocking(init_location));
    }

    @Test
    public void testRabbitDigOnBurrow() {
        Burrow burrow = new Burrow();
        world.setTile(init_location, burrow);
        rabbit.digHole(world,init_location);
        assertInstanceOf(Burrow.class, world.getNonBlocking(init_location));
        assertTrue(rabbit.getNetwork().contains(burrow));
    }

    @Test
    public void testEat() {
        rabbit.setHunger(10);
        Grass grass = new Grass();
        world.setTile(init_location, grass);
        assertTrue(world.contains(grass)); // Checking if the location contains grass
        assertEquals(10, rabbit.getHunger()); // Checking that hunger is at max (10)
        rabbit.eat(world, init_location);
        assertEquals(10 - grass.getFoodValue(), rabbit.getHunger()); // Hunger has been reduced by grass.getFoodValue()
    }
}
