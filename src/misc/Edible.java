package misc;

import itumulator.world.World;

public interface Edible {
    /**
     * Method signature intended for implementation by anything Edible. Its purpose is to return that things food value, that is how much you get from eating it.
     */
    public double getFoodValue();

    /**
     * Implemented by objects that need to be eaten, such as Carcass and Grass. Its purpose is to define a method which deletes the eaten object.
     */
    public double getEaten(World world);
}
