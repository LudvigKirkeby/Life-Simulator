import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

import java.util.List;

public abstract class Animal implements Actor {
    protected int age, hunger;

    protected void die(World world) {
        world.delete(this);
    }

    protected List<Location> path(World world, Location target) {
        throw new RuntimeException("path method not implemented!");
    }

    protected List<Location> path(World world, Object object) {
        return path(world, world.getLocation(object));
    }

    protected void reproduce(Animal animal) {

    }
    
}
