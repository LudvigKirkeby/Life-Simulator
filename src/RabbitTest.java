import static org.junit.jupiter.api.Assertions.*;
//import org.junit.jupiter.api.AfterEach;
import itumulator.world.Location;
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
}
