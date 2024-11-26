package misc;

import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.awt.Color;

public class Burrow implements NonBlocking, DynamicDisplayInformationProvider, Actor {
    private int age;

    public Burrow() {
    age = 0;
    }

    @Override
    public void act(World world) {
        if(!world.contains(this)) {
            return;
        }
        age++;
        if (age >= 100) {
            world.delete(this);
        }
    }

    @Override
    public DisplayInformation getInformation() {
        if (age < 60)
            return new DisplayInformation(Color.BLACK, "hole");
        return new DisplayInformation(Color.BLACK,"hole-small");
    }
}
