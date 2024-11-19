import itumulator.world.World;

public abstract class Carnivore extends Animal {

    // Nedenstående er eks kode og ikke reelt kode
    public void eat(World world, HerbivoreAnimal target) {
            hunger--;
            world.delete(target);
    }
}
