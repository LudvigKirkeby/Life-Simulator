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
    private boolean sleeping;
    private int cooldown;

    public Bear(World w, int x, int y) {
        Location center = new Location(x, y);
        Set<Location> territory = w.getSurroundingTiles(center, 3);
        territorylist = new ArrayList<Location>(territory);
        age = 0;
        cooldown = 0;
        health_points = 80;
        view_distance = territory.size() * 2;
    }

    @Override
    public void act(World world) {
        age += 0.05;

        if (age > new Random().nextDouble(25,900) || health_points <= 0) { // En bjørn dør tidligst ved alderen 25
            die(world);
            return;
        }

        if (world.isDay()) {
            sleeping = false;

            try {
                if (readyToMate()) {
                    seek(Bear.class, world, world.getLocation(this), view_distance);

                    if (canFindMate(Bear.class, world)) {
                        energy -= 4;
                        reproduce(Bear.class, world);
                    }
                }
            } catch(Exception e) {
                System.out.println("Bear breeding issue");
            }

            if (territorylist == null) {return;}
            if (!territorylist.contains(world.getLocation(this))) {
                Location locationinterritory = territorylist.get(new Random().nextInt(territorylist.size()));
                takeStepToward(world, locationinterritory);
            } else {

                if (cooldown > 0) {
                    cooldown--;
                    return;
                }

                wander(world, world.getLocation(this));
                cooldown = 3;
            }

            for (Location x : territorylist) {
                if (world.contains(x)) {
                    Object o = world.getTile(x);
                    if (!(o instanceof Bear)) {
                    seek(o.getClass(), world, world.getLocation(this), view_distance);
                    }
                }
            }




        } else if (world.isNight()) {
         sleeping = true;
        }
        // If outside of territory, move to a random tile in territory

    }

    public boolean readyToMate() {
        if (age > 4 && energy > 2) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean getGrownup() {
        return age > 3; // Just set it to a temporary value
    }

    @Override
    public int getFoodValue() {
        if(getGrownup())
            return 5;
        return 2;
    }

    @Override
    public DisplayInformation getInformation () {
        if (getGrownup()) {
            if(sleeping)
                return new DisplayInformation(Color.GRAY, "bear-sleeping");
            return new DisplayInformation(Color.GRAY, "bear");
        }
        if(sleeping)
            return new DisplayInformation(Color.GRAY, "bear-small-sleeping");
        return new DisplayInformation(Color.GRAY, "bear-small");
    }

}
