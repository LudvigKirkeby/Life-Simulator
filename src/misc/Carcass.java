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
    private double fungi_food_value;
    private boolean has_fungi;

    public Carcass(double food_value, boolean hasFungi) {
        this.food_value = food_value;
        this.has_fungi = hasFungi;
        this.fungi_food_value = hasFungi ? food_value : 4;
    }

    public Carcass(boolean hasFungi) {
        this(4, hasFungi);
    }

    public Carcass() {
        this(4, false);
    }

    public void act(World world) {
        if (has_fungi) {
            food_value -= 0.3;
        }
        food_value -= 0.2;
        if (food_value <= 0 && has_fungi) {
            Location l = world.getLocation(this);
            world.delete(this);
            world.setTile(l, new Fungi(fungi_food_value));
        } else if (food_value <= 0) {
            world.delete(this);
        }

        // WE CAN POTENTIALLY REMOVE BELOW. It just means a natural way for Fungi to appear in the world
        if (!has_fungi && new Random().nextInt(100) == 0) {
            has_fungi = true;
            fungi_food_value = food_value; // takes the food_value at that time, to create a Fungi with
        }
    }

    public boolean getFungi() {
        return has_fungi;
    }

    public boolean setHasFungi(boolean has_fungi) {
        return this.has_fungi = has_fungi;
    }

    public void spawnFungi() {
        has_fungi = true;
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
