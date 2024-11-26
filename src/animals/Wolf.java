package animals;

import itumulator.world.World;
import misc.Edible;

public class Wolf extends Animal {
    AnimalPack pack;

    public Wolf() {
        pack = new AnimalPack(this.getClass());
    }

    public Wolf(AnimalPack pack) {
        if(pack == null)
            throw new IllegalArgumentException("pack can't be null!");
        if(pack.getType() != null && !pack.getType().isInstance(this))
            throw new IllegalArgumentException("pack type can't be " + pack.getType()+" for wolf!");
        this.pack = pack;
    }

    @Override
    public void act(World world) {

    }

    @Override
    public boolean getGrownup() {
        return age > 3; // Just set it to a temporary value
    }

    @Override
    public int getFoodValue() {
        if(getGrownup())
            return 4;
        return 2;
    }
}
