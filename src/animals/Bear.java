package animals;

import itumulator.executable.DisplayInformation;
import itumulator.world.Location;
import itumulator.world.World;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

public class Bear extends Animal {
    private ArrayList<Location> territorylist;
    private Location center;
    private boolean sleeping;
    private int cooldown;

    public Bear() {
        age = 0;
        cooldown = 0;
        health_points = 20;
        view_distance = 10;
    }

    public Bear(int x, int y) {
        this();
        this.center = new Location(x, y);
    }

    @Override
    public void act(World world) {

        if (center == null) {
            center = new Location(new Random().nextInt(world.getSize()), new Random().nextInt(world.getSize()));
        }

        if (territorylist == null) {
            Set<Location> territory = world.getSurroundingTiles(center, 3);
            territorylist = new ArrayList<Location>(territory);
        }

        age += 0.05;

        if (age > new Random().nextDouble(25, 900) || health_points <= 0) { // En bjørn dør tidligst ved alderen 25
            die(world);
            return;
        }

        if (world.isDay()) {
            sleeping = false;

            try {
                if (ReadyToMate() && canFind(Bear.class, world)) {
                    seek(Bear.class, world, world.getLocation(this), view_distance);
                    reproduce(Bear.class, world);
                    energy -= 6;
                }
            } catch (Exception e) {
                System.out.println("Bear breeding issue");
            }

            if (territorylist == null) {
                return;
            }

            if (!territorylist.contains(world.getLocation(this))) {
                Location location_in_territory = territorylist.get(new Random().nextInt(territorylist.size()));
                takeStepToward(world, location_in_territory);
            } else {

                // Makes it so the bear moves slowly
                if (cooldown > 0) {
                    cooldown--;
                    return;
                }
                wander(world, world.getLocation(this));
                cooldown = 3;
            }

            // Seek and attack intruder in the territory unless child
            if (!getGrownup()) {return;}
            for (Location x : territorylist) {
                if (!world.isTileEmpty(x)) {
                    Object o = world.getTile(x);
                    seek(o.getClass(), world, world.getLocation(this), view_distance);
                    if (!(o instanceof Bear && ReadyToMate())) { // If its not a bear and this is not ready to mate, then attack
                        attackIfInRange(world, 3, false);
                    }
                }
            }
        } else if (world.isNight()) {
            sleeping = true;
        }
    }

    public boolean ReadyToMate() {
        return getGrownup() && Math.abs(age % 4) < 0.05; // Checking if the age is 2, 4, 6, etc. because bears mate once every 2 years. Failure check cus double
    }

    @Override
    public boolean getGrownup() {
        return age > 3; // Just set it to a temporary value
    }

    @Override
    public int getFoodValue() {
        if (getGrownup())
            return 10;
        return 3;
    }

    @Override
    public DisplayInformation getInformation() {
        if (getGrownup()) {
            if (sleeping)
                return new DisplayInformation(Color.GRAY, "bear-sleeping");
            return new DisplayInformation(Color.GRAY, "bear");
        }
        if (sleeping)
            return new DisplayInformation(Color.GRAY, "bear-small-sleeping");
        return new DisplayInformation(Color.GRAY, "bear-small");
    }

}
