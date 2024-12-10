package tests;

import static org.junit.jupiter.api.Assertions.*;
//import org.junit.jupiter.api.AfterEach;
import animals.Animal;
import animals.Rabbit;
import itumulator.world.Location;
import plants.Grass;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import itumulator.world.World;

import java.util.Random;

public class AnimalTest {
    // Makes use of the Rabbit class to test the abstract class Animal
    World world;
    Animal animal;
    Location rabbit_location;

    @BeforeEach
    public void setUp() {
        world = new World(15);
        animal = new Rabbit(true);
        rabbit_location = new Location(7,7);
        world.setTile(rabbit_location, animal);
        world.setCurrentLocation(rabbit_location);
    }

    /*
        die tests
    */
    @Test
    public void testDieInWorld() {
        animal.die(world);
        assertFalse(world.contains(animal));
    }

    @Test
    public void testDieNotInWorld1() {
        Rabbit r = new Rabbit();
        world.add(r);
        r.die(world);
        assertFalse(world.contains(r));
    }

    @Test
    public void testDieNotInWorld2() {
        Rabbit r = new Rabbit();
        r.die(world);
        assertFalse(world.contains(r));
    }

    @Test
    public void testTakeStepToward() {
        Random rand = new Random();
        for(int i = 0; i<100; i++) {
            setUp();
            Rabbit r = new Rabbit();
            Location r_location = new Location(rand.nextInt(15), rand.nextInt(15));
            while(r_location.equals(rabbit_location)) {
                r_location = new Location(rand.nextInt(15), rand.nextInt(15));
            }
            world.setTile(r_location, r);
            double start_dist = animal.distToSquared(world, r_location);
            if(start_dist>1) { // If animal is already at r then it is impossible for it to get closer
                animal.takeStepToward(world, r_location);
                assertTrue(start_dist > animal.distToSquared(world, r_location));
            }
        }
    }

    /*
       attackTile tests
     */

    // If the tile is not in the world then an IllegalArgumentException should be thrown
    @Test
    public void testAttack1() {
        assertThrows(IllegalArgumentException.class, () -> {
            animal.attackTile(world, new Location(-1,-1),1);
        });
    }

    // If the tile contains an Animal to attack, then attack it
    @Test
    public void testAttack2() {
        Rabbit r = new Rabbit();
        Location l = new Location(rabbit_location.getX()+1, rabbit_location.getY());
        world.setTile(l, r);
        double start_hp = r.getHP();
        animal.attackTile(world, l, 2);
        assertEquals(start_hp-2, r.getHP());
    }

    // If attacking a tile that the Animal can eat, then it will
    @Test
    public void testAttack3() {
        Grass grass = new Grass();
        Location l = new Location(rabbit_location.getX()+1, rabbit_location.getY());
        world.setTile(l, grass);
        animal.attackTile(world, l, 2);
        assertFalse(world.contains(grass));
    }
}