import itumulator.world.World;

public abstract class Herbivore extends Animal {

    public void eat(World world, Plant plant) {
        hunger--;
        world.delete(plant);
    }

}
