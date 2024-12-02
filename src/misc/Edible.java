package misc;

import itumulator.world.World;

public interface Edible {
    public int getFoodValue();
    public int getEaten(World world);
}
