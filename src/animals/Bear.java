package animals;

import itumulator.executable.DisplayInformation;
import itumulator.world.Location;
import itumulator.world.World;
import misc.Bush;
import misc.Edible;
import misc.Grass;
import misc.Plant;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Bear extends Animal {
    private List<Location> territorylist;
    private Set<Animal> children;
    private Location center;
    private boolean sleeping;
    private boolean mating;
    private int cooldown;

    public Bear() {
        age = 0;
        cooldown = 0;
        hunger = 10;
        energy = 10;
        health_points = 20;
        view_distance = 10;
        mating = false;
        children = new HashSet<>();
    }

    public Bear(int x, int y) {
        this();
        this.center = new Location(x, y);
    }

    @Override
    public void act(World world) {

        hunger += 0.05;
        age += 0.05;

        if (hunger > 15) {health_points -= 0.5;}

        if (getGrownup() && children != null) {children.remove(this);} // Bears can be attacked by their parents when grown up

        if (center == null) {
            center = new Location(new Random().nextInt(world.getSize()), new Random().nextInt(world.getSize()));
        }

        if (territorylist == null) {
            Set<Location> territory = world.getSurroundingTiles(center, 1);
            territorylist = new ArrayList<Location>(territory);
        }

        if (age > new Random().nextDouble(25, 900) || health_points <= 0) { // En bjørn dør tidligst ved alderen 25
            die(world);
            return;
        }

        if (world.isDay()) {
            sleeping = false;

            if (ReadyToMate() && canFind(Bear.class, world)) {
                seek(Bear.class, world, world.getLocation(this), world.getSize()); // Seeks a bear anywhere in the world
                Set<Animal> newchildren = reproduce(Bear.class, world);
                if (newchildren != null && !newchildren.isEmpty()) {
                    children.addAll(newchildren);
                    mating = false;
                }
            }

            if (territorylist == null) {
                return;
            }

            if (!territorylist.contains(world.getLocation(this)) && !mating) {
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

            // Behaviour regarding the territory
            for (Location x : territorylist) {

                if (world.getTile(x) instanceof Bush) {
                    Object o = world.getTile(x);
                    Bush b = (Bush) o;
                    pathTo(world, world.getLocation(this), world.getLocation(b));
                    Set<Location> surrounding = world.getSurroundingTiles(world.getLocation(this));
                    if (surrounding.contains(world.getLocation(b)) && b.getRipe()) {
                        if (hunger >= b.getFoodValue()) {
                            b.eatBerries(); // sets ripe to false
                            hunger -= b.getFoodValue();
                        }
                    }
                }


                if (getGrownup() && !world.isTileEmpty(x)) {
                    Object o = world.getTile(x);

                    if (o.equals(this)) {return;}

                    if (children == null || children.contains(o)) {return;}

                    if (o instanceof Bear && ReadyToMate()) {return;}

                    seek(o.getClass(), world, world.getLocation(this), territorylist.size());
                    attackIfInRange(world, 3, false);
                    System.out.println(health_points);
                }
            }
        } else if (world.isNight()) {
            sleeping = true;
        }
    }

    private boolean ReadyToMate() {
        if (mating) {return true;}
        mating = getGrownup() && isMatingSeason();
        return mating;
    }

    private boolean isMatingSeason() {
        return (age % 4) < 0.05; // Checking if the age is 4, 8, etc. because bears mate once every 2 years. Failure check cus double
    }

    @Override
    public boolean getGrownup() {
        return age > 3;
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
