import itumulator.world.World;

public abstract class Herbivore extends Animal {

    public void eat(World world, Plant plant) {
        if (hunger > 0) {
            hunger -= plant.getFoodValue();
        }
        world.delete(plant);
    }

}
