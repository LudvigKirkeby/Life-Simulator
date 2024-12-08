package tests;

import static org.junit.jupiter.api.Assertions.*;
//import org.junit.jupiter.api.AfterEach;
import animals.Bear;
import animals.Rabbit;
import misc.Bush;
import misc.Carcass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import animals.Wolf;
import itumulator.world.Location;
import itumulator.world.World;

/*
    Should test:
    ✓ Bear creates territory if it doesn't have one
    ✓ Bear seeks and attacks Animals in its territory
    ✓ Bear seeks and eats from berry bushes
    ✓ Bear eats Carcass and decreases hunger
    ✓ Bear reproduces under correct circumstances
 */

public class BearTest {
    World world;
    Bear bear;
    Location init_location;

    @BeforeEach
    void setUp() {
        world = new World(15);
        bear = new Bear(0,0);
        init_location = new Location(0, 0);
        world.setTile(init_location, bear);
        world.setCurrentLocation(init_location);
    }

    @Test
    public void createTerritoryIfNone() {
    assertNull(bear.getTerritoryList()); // Checks that the bears has no territory
    bear.act(world);
    assertNotNull(bear.getTerritoryList()); // Should now have a territory

    Bear bear1 = new Bear(); // Check without parameter
    assertNull(bear1.getTerritoryList()); // Territorylist should not exist
    world.setTile(new Location(0,2), bear1);
    bear1.act(world);
    assertNotNull(bear1.getTerritoryList()); // Territorylist should exist
    }

    @Test
    public void AttacksIfInTerritory() {
    bear.setAge(4);
    Wolf wolf = new Wolf();
    wolf.setAge(5);
    world.setTile(new Location(0,1), wolf);
    bear.act(world);
    assertTrue(wolf.getHP() < 12);
    }

    @Test
    public void SeeksIfInTerritory() {
        bear.setAge(4);
        Rabbit rabbit = new Rabbit();
        world.setTile(new Location(1, 2), rabbit);
        bear.act(world);
        assertEquals(new Location(1, 1), world.getLocation(bear));
    }

    @Test
    public void eatsBerries() {
    Bush bush = new Bush();
    world.setTile(new Location(0,1), bush);
    bush.setRipe();
    bear.setHunger(4);
    bear.act(world);
    assertTrue(bear.getHunger() < 4);
    }

    @Test
    public void testEatsCarcass() {
        Carcass carcass = new Carcass(4, false);
        world.setTile(new Location(1, 0), carcass);
        bear.act(world);
        assertFalse(world.contains(carcass));
    }

    @Test
    public void matingTest() {
        assertTrue(bear.getChildren().isEmpty());
        bear.setAge(4);
        bear.setMating(true);

        Bear bear2 = new Bear();
        assertTrue(bear2.getChildren().isEmpty());
        bear2.setAge(4);
        bear2.setMating(true);
        world.setTile(new Location(0,1), bear2);
        bear.act(world);
        assertFalse(bear.getChildren().isEmpty());
    }
}