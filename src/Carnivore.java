import itumulator.world.World;

public abstract class Carnivore extends Animal {

    /**
     * Eats an animal and reduces hunger by the animals food value (See Animal.getFoodValue())
     * @param world World to find prey in
     * @param target The target to eat. The target gets Deleted from world
     */

    public void eat(World world, Animal target) {
        if (hunger > 0) {
            hunger -= target.getFoodValue();
        }
            world.delete(target);
    }
}
