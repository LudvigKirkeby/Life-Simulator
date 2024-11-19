import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

public abstract class Animal implements Actor {
    protected int age, hunger;

    protected void die(World world) {
        world.delete(this);
    }

    protected void path(World world, Location target) {

    }

    protected void path(World world, Object object) {

    }

    protected void reproduce(Animal animal) {

    }
    
}
