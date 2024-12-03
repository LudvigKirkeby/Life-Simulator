package misc;

import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

import java.awt.*;
import java.util.Random;

public class Carcass implements Edible, Actor, DynamicDisplayInformationProvider {
    private double food_value;
    private double fungiFoodvalue;
    private boolean hasFungi;

    public Carcass(double food_value, boolean hasFungi) {
        this.food_value = food_value;
        this.hasFungi = hasFungi;
        this.fungiFoodvalue = hasFungi ? food_value : 4;
    }

    public Carcass(boolean hasFungi) {
        this(4, hasFungi);
    }

    public Carcass() {
        this(4, false);
    }

    public void act(World world) {
        if (hasFungi) {
            food_value -= 0.3;
        }
        food_value -= 0.2;
        if (food_value <= 0 && hasFungi) {
            Location l = world.getLocation(this);
            world.delete(this);
            world.setTile(l, new Fungi(fungiFoodvalue));
        } else if (food_value <= 0) {
            world.delete(this);
        }

        // WE CAN POTENTIALLY REMOVE BELOW. It just means a natural way for Fungi to appear in the world
        if (!hasFungi && new Random().nextInt(100) == 0) {
            hasFungi = true;
            fungiFoodvalue = food_value; // takes the food_value at that time, to create a Fungi with
        }
    }

    public boolean getFungi() {
        return hasFungi;
    }

    public void spawnFungi() {
        hasFungi = true;
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
