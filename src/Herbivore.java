import itumulator.world.World;

public abstract class Herbivore extends Animal {

    /**
     * Eats a plant and reduces hunger by plants food value (See plant.getFoodValue());
     * @param world
     * @param plant
     */

    public void eat(World world, Plant plant) {
        if (hunger > 0) {
            hunger -= plant.getFoodValue();
        }
        world.delete(plant);
    }

}
