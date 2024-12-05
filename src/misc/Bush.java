package misc;

import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.World;

import java.awt.*;
import java.util.Random;

public class Bush extends Plant implements Actor, Edible, DynamicDisplayInformationProvider {
    private boolean hasBerries;
    private double timeToRipen;

    @Override
    public void act(World world) {
        timeToRipen += 0.03;
        if (0 == new Random().nextInt(70)) {
            try {
                grow(Bush.class, world);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        if (timeToRipen > 2) {
            hasBerries = true;
            timeToRipen = 0;
        }

    }

    public double getFoodValue() {
        return 4;
    }

    @Override
    public double getEaten(World world) {
        if (hasBerries) {
            hasBerries = false;
            return getFoodValue();
        }
        return 0;
    }

    @Override
    public DisplayInformation getInformation() {
        if (hasBerries)
            return new DisplayInformation(Color.GREEN, "bush-berries", false);
        return new DisplayInformation(Color.RED, "bush", false);
    }

    public void setRipe() { // for testing
    hasBerries = true;
    }
}


