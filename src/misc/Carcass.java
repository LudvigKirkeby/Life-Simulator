package misc;

import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.World;

import java.awt.*;

public class Carcass implements Edible, Actor, DynamicDisplayInformationProvider {
    private double food_value;

    public Carcass(double food_value) {
        this.food_value = food_value;
    }
    public Carcass() {
        food_value = 4;
    }

    public void act(World world) {
        food_value -= 0.2;
        if (food_value <= 0) {
            world.delete(this);
        }
    }

    @Override
    public double getEaten(World world) {
        world.delete(this);
        return getFoodValue();
    }

    @Override
    public DisplayInformation getInformation() {
        if (food_value > 4)
            return new DisplayInformation(Color.BLACK, "carcass");
        return new DisplayInformation(Color.BLACK,"carcass-small");
    }

    public double getFoodValue() { return food_value; }

}
