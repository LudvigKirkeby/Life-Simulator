import static org.junit.jupiter.api.Assertions.*;
//import org.junit.jupiter.api.AfterEach;
import itumulator.world.Location;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import itumulator.world.World;

import java.util.List;
import java.util.Random;

public class AnimalTest {
    // Makes use of the Rabbit class to test the abstract class Animal
    World world;
    Rabbit rabbit;
    Location rabbit_location;

    @BeforeEach
    public void setUp() {
        world = new World(7);
        rabbit = new Rabbit();
        rabbit_location = new Location(3, 3);
        world.setTile(rabbit_location, rabbit);
        world.setCurrentLocation(rabbit_location);
    }


    @Test
    public void testPathTo() {
        Location target_location;
        Random rand = new Random();
        for(int i = 0; i<100; i++) {
            target_location = new Location(rand.nextInt(world.getSize()), rand.nextInt(world.getSize()));
            List<Location> path = rabbit.pathTo(world, rabbit_location, target_location);
            if(path.isEmpty()) {
                assertEquals(rabbit_location, target_location);
            }else {
                assertEquals(path.getLast(), target_location);
            }
        }
    }

    @Test
    public void testPathToWithBlocking() {
        Location target_location;
        Random rand = new Random();
        Placement p = new Placement();
        Rabbit block = new Rabbit();
        p.placeRandomly(world, block);
        for(int i = 0; i<100; i++) {
            target_location = new Location(rand.nextInt(world.getSize()), rand.nextInt(world.getSize()));
            while(world.getLocation(block).equals(target_location)) {
                world.delete(block);
                p.placeRandomly(world, block);
            }
            List<Location> path = rabbit.pathTo(world, rabbit_location, target_location);
            if(path.isEmpty()) {
                assertEquals(rabbit_location, target_location);
            }else {
                assertEquals(path.getLast(), target_location);
            }
        }
    }
}
