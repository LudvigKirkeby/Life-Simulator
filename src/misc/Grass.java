package misc;

import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Grass extends Plant implements DynamicDisplayInformationProvider, NonBlocking, Actor, Edible {

    @Override
    public void act(World world) {
        if (0 == new Random().nextInt(10)) {
            try {
            grow(Grass.class, world);
        } catch (Exception e) {
            System.out.println(e.getMessage());}
        }
    }

    @Override
    public double getFoodValue() {
        return 1;
    }

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
