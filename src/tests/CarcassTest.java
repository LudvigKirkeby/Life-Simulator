package tests;

import static org.junit.jupiter.api.Assertions.*;
//import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import itumulator.world.World;
import itumulator.world.Location;
import misc.Carcass;
import misc.Fungi;

public class CarcassTest {
    World world;
    Carcass carcass;
    Location init_location;

    @BeforeEach
    public void setUp() {
        world = new World(2);
        carcass = new Carcass(1,false);
        init_location = new Location(0,0);
        world.setTile(init_location,carcass);
        world.setCurrentLocation(init_location);
    }

    @Test
    public void testCarcassDecay() {
        // Carcass should decay at a rate of 0.2 per act call, unless it has fungi.
        for(double expected = 1; expected>0; expected-=0.2) {
            assertEquals(expected,carcass.getFoodValue());
            carcass.act(world);
            carcass.setHasFungi(false);
        }
        // Carcass should disappear when food_value is at 0
        assertFalse(world.contains(carcass));
    }

    @Test
    public void testCarcassDecayFungi() {
        // Carcass should decay at a rate of 0.5 per act call when it has fungi
        carcass.setHasFungi(true);
        for(double expected = 1; expected>0; expected-=0.5) {
            // Accounts for floating-point error
            assertEquals(Math.round(expected*1000)/1000,Math.round(carcass.getFoodValue()*1000)/1000);
            carcass.act(world);
        }
        // Carcass should disappear when food_value is at 0 and place a fungus instead if has_fungi is true
        assertInstanceOf(Fungi.class, world.getTile(init_location));
    }
}
