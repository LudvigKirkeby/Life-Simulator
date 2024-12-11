package plants;

import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.World;
import misc.Edible;

import java.awt.Color;
import java.util.Random;

public class Grass extends Plant implements DynamicDisplayInformationProvider, Actor, Edible {

    @Override
    public void act(World world) {
        if(!world.contains(this) || !world.isOnTile(this)) return;
        if (0 == new Random().nextInt(5)) {
            try {
            grow(Grass.class, world);
        } catch (Exception e) {
            System.out.println(e.getMessage());}
        }
    }

    /**
     * @return The grass' food value.
     */
    @Override
    public double getFoodValue() {
        return 1;
    }

    /**
     * Deletes the object and returns its food value when it is eaten.
     * @param world The world to delete it in.
     */
    @Override
    public double getEaten(World world) {
        world.delete(this);
        return getFoodValue();
    }

    @Override
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.GREEN, "grass", true);
    }
}
