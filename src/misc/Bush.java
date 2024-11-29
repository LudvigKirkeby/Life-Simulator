package misc;

import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.awt.*;
import java.util.Random;

public class Bush extends Plant implements Actor, DynamicDisplayInformationProvider {
    private boolean hasBerries;
    private double timeToRipen;

    @Override
    public void act(World world) {
        timeToRipen += 0.03;
        if (0 == new Random().nextInt(70)) {
            try {
                grow(Bush.class, world);
            } catch (Exception e) {
                System.out.println(e.getMessage());}
        }

        if (timeToRipen > 2) {
            hasBerries = true;
            timeToRipen = 0;
        }

    }

    public int getFoodValue() {
        return 4;
    }

    @Override
    public DisplayInformation getInformation() {
        if (hasBerries)
            return new DisplayInformation(Color.GREEN, "bush-berries", false);
        return new DisplayInformation(Color.RED, "bush", false);
    }

    public boolean getRipe() {
        return hasBerries;
    }

    public void eatBerries() {
    hasBerries = false;
    getInformation();
    }

}
