package animals;

import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;
import misc.Carcass;
import misc.Edible;

import java.util.*;

public abstract class Animal implements DynamicDisplayInformationProvider, Actor, Edible {
    protected int view_distance, cooldown;
    protected double age, energy, hunger, health_points;

    /**
     * Removes this from the World provided and places a Carcass with value from getFoodValue()-method if this is on a tile in world.
     * @param world World that this is in
     */
    public void die(World world) {
        if (!world.contains(this)) {return;}

        if (world.isOnTile(this)) {
            Location l = world.getLocation(this);
            world.delete(this);
            world.setTile(l, new Carcass(getFoodValue(), false));
        } else {
            world.delete(this);
        }
    }

    /**
     * Short form of distSquared(World, Location, Location). Assumes parameter from is Location of this in world.
     * @param world World that this and to are in
     * @param to Location two for distance calculation
     * @return The squared distance between Location of this in world and to.
     */
    public double distToSquared(World world, Location to) {
        return distToSquared(world, world.getLocation(this), to);
    }

    /**
     * Uses the pythagorean theorem without square root to get the squared distance between Location parameters from and to.
     * @param world World that from and to are in
     * @param from Location one for the calculation
     * @param to Location two for the calculation
     * @return The squared distance between from and to.
     */
    public double distToSquared(World world, Location from, Location to) {
        if (to == null) throw new IllegalArgumentException("to is null!");
        if (from == null) throw new IllegalArgumentException("from is null!");
        if (to.getX() >= world.getSize() || to.getX() < 0 || to.getY() >= world.getSize() || to.getY() < 0)
            throw new IllegalArgumentException("to location is not in the world!");
        if (from.getX() >= world.getSize() || from.getX() < 0 || from.getY() >= world.getSize() || from.getY() < 0)
            throw new IllegalArgumentException("from location is not in the world!");
        return Math.pow(from.getX() - to.getX(), 2) + Math.pow(from.getY() - to.getY(), 2);
    }

    /**
     * @param world World that this and target are in
     * @param target Location to take a step toward
     * @return The closest Location from getEmptySurroundingTiles(...) to target in world.
     */
    public Location getStepToward(World world, Location target) {
        if (target == null) throw new IllegalArgumentException("target is null!");
        Location step = null, self = world.getLocation(this);
        if (self.equals(target)) return target;
        Set<Location> surrounding_tiles = world.getEmptySurroundingTiles(self);
        double least_dist = Double.MAX_VALUE;
        for (Location l : surrounding_tiles) {
            double dist = distToSquared(world, l, target);
            if (dist < least_dist) {
                least_dist = dist;
                step = l;
            }
        }
        return step;
    }

    /**
     * Checks if any of the tiles within view_distance of this is the given class c.
     * @param c Class to check for
     * @param world World to check in
     * @param exclude Objects that shouldn't be included in the search
     * @return True or false depending on whether an instance of class c is found or not.
     */
    protected boolean canFind(Class<?> c, World world, Set<Object> exclude) {
        Set<Location> visible_locations = world.getSurroundingTiles(world.getLocation(this), view_distance);
        for (Location loc : visible_locations) {
            Object o = world.getTile(loc);
            if (!exclude.contains(o) && c.isInstance(o)) {
                return true;
            }
        }
        return false;
    }

    protected boolean canFind(Class<?> c, World world) {
        return canFind(c, world, new HashSet<>());
    }

    /**
     * Reproduces with the closest animal of the same class, given that they are within adjacent tiles. Reproduces non-stop if the implementation does not have an energy requirement.
     *
     * @param c     The class of the animal to breed with and the baby to be born.
     * @param world The world in which it reproduces.
     * @return Set of Animals that this reproduce call created.
     */

    protected Set<Animal> reproduce(Class<?> c, World world) {
        Set<Animal> babies = new HashSet<>();
        if (!this.getGrownup()) return babies;
        Animal closest_animal = (Animal) closestObject(c, world.getLocation(this), world, view_distance, false);
        if (closest_animal == null) {
            //throw new RuntimeException("Closest animal of type " + c.getName() + " null");
            return babies;
        }

        Location location = world.getLocation(this);
        Set<Location> surrounding_tiles = world.getSurroundingTiles(location);
        List<Location> tile_list = new ArrayList<>(surrounding_tiles);
        if (tile_list.contains(world.getLocation(closest_animal)) && closest_animal.getGrownup()) {
            Set<Location> neighbours = world.getEmptySurroundingTiles(location);
            List<Location> neightbor_list = new ArrayList<>(neighbours);
            if (!neightbor_list.isEmpty()) {
                Location l = neightbor_list.get(new Random().nextInt(neightbor_list.size()));
                try {
                    Animal baby = (Animal) c.getDeclaredConstructor().newInstance();
                    world.setTile(l, baby);
                    babies.add(baby);
                } catch (Exception e) {
                    System.out.println("Baby instantiation failed || Could not place baby");
                    e.printStackTrace();
                }
            } else {
                return babies;
            }
        }
        return babies;
    }

    /**
     * Eats supplied edible object by calling getEaten(World) on that object. Removes returned value from hunger and adds it to energy.
     * @param world World that the edible object is in
     * @param edible The object to eat
     */
    public void eat(World world, Edible edible) {
        double food = edible.getEaten(world);
        hunger -= food;
        energy += food;
    }

    /**
     * If the tile in world contains an object that is an instance of one of the edible classes from the getEdibleClasses()-method, then it is eaten with eat(World, Edible)-method.
     * @param world world tile is in
     * @param tile  tile to eat from
     */
    public void eat(World world, Location tile) {
        for(Class<?> c : getEdibleClasses()) {
            if(c.isInstance(world.getNonBlocking(tile))) {
                eat(world, (Edible)world.getNonBlocking(tile));
                return;
            } else if (c.isInstance(world.getTile(tile))) {
                eat(world, (Edible)world.getTile(tile));
                return;
            }
        }
    }

    /**
     * @param c             The type of object that is being searched for
     * @param from          Where to search from
     * @param world         World to search in
     * @param view_distance How far away the object may be
     * @param include_mid   Whether to include from when searching
     * @return The closest object of type c from location within view_distance
     */
    protected Object closestObject(Class<?> c, Location from, World world, int view_distance, boolean include_mid) {
        Set<Location> tiles = world.getSurroundingTiles(from, view_distance);
        if (include_mid) {
            tiles.add(from);
        }
        return closestObject(c, from, world, tiles);
    }

    /**
     * @param c     The type of object that is being searched for
     * @param from  Where to search from
     * @param world World to search in
     * @param tiles How far away the object may be
     * @return The closest object of type c from location within the set tiles.
     */
    protected Object closestObject(Class<?> c, Location from, World world, Set<Location> tiles) {
        Object closest_object = null;
        double closest_distance = Double.MAX_VALUE;
        for (Location tile : tiles) {
            Object current = world.getTile(tile);
            if (c.isInstance(current)) {
                double distance = distToSquared(world, from, tile);
                if (distance < closest_distance) {
                    closest_distance = distance;
                    closest_object = world.getTile(tile);
                }
            }
        }
        return closest_object;
    }

    /**
     * Makes use of random_move to move the Animal in a random direction.
     *
     * @param world World to wander in
     * @param from  Location to wander from
     */
    protected void wander(World world, Location from) {
        Location target = randomMove(world, from);
        if (target == null) return;
        world.move(this, target); // Moves the rabbit to target tile
    }

    /**
     * Short form of wander(World, Location)
     * @param world World to wander in
     */
    protected void wander(World world) {
        wander(world, world.getLocation(this));
    }

    /**
     * @param world The world to wander in
     * @param start The location to wander from
     * @return A random location exactly 1 tile from start
     */
    protected Location randomMove(World world, Location start) {
        Set<Location> available_tiles = world.getEmptySurroundingTiles(start);
        if (available_tiles.isEmpty()) return null;
        return (Location) available_tiles.toArray()[new Random().nextInt(available_tiles.size())];
    }

    /**
     * Makes use of closest_object to find an object of type c, then uses the path-method to find
     * a path from start to that object. It then moves to the first Location in the List path returned.
     *
     * @param c             Type to seek
     * @param world         World to seek in
     * @param start         Where to seek from
     * @param view_distance How far away the target can be
     */
    protected void seek(Class<?> c, World world, Location start, int view_distance) {
        Object closest = closestObject(c, world.getLocation(this), world, view_distance, false);
        if (closest != null) {
            takeStepToward(world, world.getLocation(closest));
        } else {
            wander(world, start);
        }
    }

    /**
     * Find step toward target in world with getStepToward and makes the move.
     * @param world World this and target is in
     * @param target Location to move to in world
     */
    public void takeStepToward(World world, Location target) {
        Location step = getStepToward(world, target);
        if (step == null) return;
        world.move(this, step);
    }

    /**
     * @return Whether this Animal is grown up.
     */
    public abstract boolean getGrownup();

    /**
     * @return Hunger field from Animal.
     */
    public double getHunger() {
        return hunger;
    }

    /**
     * Attacks the specified tile in world. If the tile is not an instance of Animal, tries to eat what is on the tile.
     * @param world World the tile is in
     * @param tile Location to attack
     * @param damage Damage to deal to Animal on tile
     */
    public void attackTile(World world, Location tile, int damage) {
        if (tile.getX() >= world.getSize() || tile.getX() < 0 || tile.getY() >= world.getSize() || tile.getY() < 0)
            throw new IllegalArgumentException("tile is not in the world!");

        if (world.getTile(tile) instanceof Animal animal) {
            animal.reduceHP(damage);
        }else {
            eat(world, tile);
        }
    }

    /**
     * Attacks all Locations in the List parameter tiles in the supplied world, dealing the specified amount of damage.
     * @param world World tiles are in
     * @param tiles Locations to attack in world
     * @param damage How much damage to deal to Animals on tiles
     */
    protected void attackTiles(World world, List<Location> tiles, int damage) {
        if (tiles == null)
            throw new IllegalArgumentException("tiles is null!");
        if (world == null) throw new IllegalArgumentException("world is null!");
        for (Location tile : tiles) {
            attackTile(world, tile, damage);
        }
    }

    /**
     * Attacks an enemy in range, reducing their health_points.
     *
     * @param world      World to attack in
     * @param damage     Amount of damage to deal
     * @param attack_own Whether the animal should be able to attack own species or not
     */
    protected void attackIfInRange(World world, int damage, boolean attack_own) {
        Set<Location> surrounding = world.getSurroundingTiles(world.getLocation(this));
        List<Location> surroundinglist = new ArrayList<>(surrounding);
        if (attack_own) {
            for (int i = 0; i < surroundinglist.size(); i++) {
                Object current = world.getTile(surroundinglist.get(i));
                if (this.getClass().isInstance(current)) {
                    surroundinglist.remove(i);
                    i--;
                }
            }
        }
        attackTiles(world, surroundinglist, damage);
    }

    /**
     * Kills animal with die(...)-method.
     * @param world World that this object is in.
     * @return Food value of this through getFoodValue()-method.
     */
    @Override
    public double getEaten(World world) {
        die(world);
        return getFoodValue();
    }

    /**
     * Reduces health_points field by amount through setHealth(...)-method.
     * @param amount Amount to reduce health by
     */
    public void reduceHP(double amount) {
        setHealth(health_points-amount);
    }

    /**
     * Sets Animals hunger field to hunger parameter
     * @param hunger Value to set hunger to
     */
    public void setHunger(double hunger) {
        this.hunger = hunger;
    }

    /**
     * Sets Animals age field to age parameter
     * @param age Value to set age to
     */
    public void setAge(double age) {
        this.age = age;
    }

    /**
     * Sets health_points field on Animal to health. If health is less than 0, then health_points is set to 0.
     * @param health Value to set health_points to
     */
    public void setHealth(double health) {
        if(health<0) {
            health_points = 0;
            return;
        }
        health_points = health;
    }

    /**
     *
     * @return Value of health_points field
     */
    public double getHP() {
        return health_points;
    }

    /**
     * Used by eat to make sure instances of Animal-subclasses only eat what they should.
     * @return List of edible classes
     */
    public List<Class<?>> getEdibleClasses() {
        List<Class<?>> classes = new ArrayList<>();
        classes.add(Edible.class);
        return classes;
    }
}