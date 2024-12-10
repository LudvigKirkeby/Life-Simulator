package animals;

import itumulator.executable.DisplayInformation;
import itumulator.world.Location;
import itumulator.world.World;
import misc.*;
import plants.Bush;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Bear extends Animal {
    private List<Location> territorylist;
    private Set<Animal> children;
    private Location center;
    private boolean sleeping;
    private boolean mating;

    public Bear() {
        age = 0;
        cooldown = 0;
        hunger = 0;
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

        if (hunger > 15) {health_points -= 0.5;} // starving

        if (age > new Random().nextDouble(25, 900) || health_points <= 0) { // En bjørn dør tidligst ved alderen 25
            die(world);
            return;
        }

        setTerritoryIfNull(world); // For children

        if (world.isDay()) {
            sleeping = false;

            mateIfReady(world);

            children.removeIf(Animal::getGrownup); // Makes sure that bears can fight their parents when grownup.

            if (territorylist == null) {return;}

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

                findAndEatBerries(world, x);

                attack(world, x);

            }
        } else if (world.isNight()) {
            sleeping = true;
        }
    }

    /**
     * Seeks another bear and produces a child, iff the Bear is in range of the other bear. When a child is produced, the returned set of animals is added to a local list of children.
     * @param world World to mate in.
     */
    private void mateIfReady(World world) {
        if (ReadyToMate() && canFind(Bear.class, world)) {
            seek(Bear.class, world, world.getLocation(this), world.getSize()); // Seeks a bear anywhere in the world
            Set<Animal> newchildren = reproduce(Bear.class, world);
            if (newchildren != null && !newchildren.isEmpty()) {
                children.addAll(newchildren);
                mating = false;
                energy -= 6;
            }
        }
    }

    /**
     * Seeks an object and attacks a Location with this object, iff the Bear is in range of that Location.
     * @param x Location to check for berries.
     * @param world World to eat berries in.
     */

    private void findAndEatBerries(World world, Location x) {
        if (world.getTile(x) instanceof Bush b) {
            Set<Location> surrounding = world.getSurroundingTiles(world.getLocation(this));
            if (surrounding.contains(world.getLocation(b))) {
                if (hunger >= b.getFoodValue()) {
                    double foodvalue = b.getEaten(world);
                    hunger -= foodvalue;
                    energy += foodvalue;
                }
            }
        }
    }


    /**
     * Seeks an object and attacks a Location with this object, iff the Bear is in a tile adjacent to that Location.
     * @param x Location to check for object to attack.
     * @param world World to attack in.
     */

    private void attack(World world, Location x) {
        if (getGrownup() && !world.isTileEmpty(x) || world.getTile(x) instanceof Carcass) {
            Object o = world.getTile(x);

            if (o.equals(this)) {return;}

            if (children == null || children.contains(o)) {return;}

            if (o instanceof Bear && ReadyToMate()) {return;}

            seek(o.getClass(), world, world.getLocation(this), territorylist.size());
            attackIfInRange(world, 3, false);
        }
    }

    /**
     * Sets a territory in case the bear has none. If the bear already has a center, it sets a new territory based on that. Otherwise, it denotes a random center to make a territory from.
     * @param world World to set the territory in.
     */
    private void setTerritoryIfNull(World world) {
        if (center == null) {
            center = new Location(new Random().nextInt(world.getSize()), new Random().nextInt(world.getSize()));
        }

        if (territorylist == null) {
            Set<Location> territory = world.getSurroundingTiles(center, 2);
            territorylist = new ArrayList<Location>(territory);
            territorylist.add(center);
        }
    }

    /**
     * @return True if grownup and age is a multiple of 4
     */

    private boolean ReadyToMate() {
        if (mating) {return true;}
        mating = getGrownup() && isMatingSeason();
        return mating;
    }

    /**
     * @return true everytime the bears age hits a multiple of 4.
     */
    private boolean isMatingSeason() {
        return (age % 4) < 0.05; // Checking if the age is 4, 8, etc. because bears mate once every 2 years. Failure check cus double
    }

    /**
     * @return Whether this bear is grown up or not, defined by if age is larger than 3.
     */
    @Override
    public boolean getGrownup() {
        return age > 3;
    }

    /**
     * @return If grown up returns 10, else returns 3.
     */
    @Override
    public double getFoodValue() {
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

    /**
     * @return List of classes containing the Carcass class.
     */
    public List<Class<?>> getEdibleClasses() {
        List<Class<?>> classes = new ArrayList<>();
        classes.add(Carcass.class);
        return classes;
    }

    /**
     * @return List of Locations representing the bears territory.
     */
    public List<Location> getTerritoryList() {
        return territorylist;
    }

    /**
     * Used for testing.
     * @param matingvalue Set to true to force breeding.
     */
    public void setMating(boolean matingvalue) { // for testing
        mating = matingvalue;
    }

    /**
     * @return the set of Children that the bear has.
     */
    public Set<Animal> getChildren() {
        return children;
    }
}
