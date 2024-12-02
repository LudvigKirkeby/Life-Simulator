package misc;

import itumulator.world.World;

public interface Edible {
    public double getFoodValue();
    public double getEaten(World world);
}
