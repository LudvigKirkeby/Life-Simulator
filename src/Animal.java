import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public abstract class Animal implements Actor, Edible {
    protected int hunger, view_distance;
    protected double age, energy;

    /**
     *
     * @param world Removes this Animal from world
     */
    protected void die(World world) {
        world.delete(this);
    }

    /**
     *
     * @param world World to path find in
     * @param start The start of the path
     * @param target Where to path find to
     * @return A path from start to target when successful. Null if start equals target.
     */
    protected List<Location> path(World world, Location start, Location target) {
        if(target == null) throw new RuntimeException("Target is null!");
        if(world == null) throw new RuntimeException("World is null!");
        if(!world.contains(this)) throw new RuntimeException("This is not in the world!");

        if(start.equals(target)) {
            return new ArrayList<>();
        }
        List<Location> final_path = new ArrayList<>();
        List<Location> previous_locations = new ArrayList<>();
        final_path.add(start);
        while(true) {
            Location next = final_path.getLast();
            double least_dist = Double.MAX_VALUE;
            for(Location l : world.getEmptySurroundingTiles(next)) {
                if(!previous_locations.contains(l) && Math.pow(l.getX()-target.getX(),2)+Math.pow(l.getY()-target.getY(),2)<least_dist) {
                    least_dist = Math.pow(l.getX()-target.getX(),2)+Math.pow(l.getY()-target.getY(),2);
                    next = l;
                }
            }
            if(next.equals(final_path.getLast())) {
                final_path.removeFirst();
                return final_path;
            }
            previous_locations.add(next);
            final_path.add(next);
            if(next.equals(target)) {
                final_path.removeFirst();
                return final_path;
            }
        }
    }

    /**
     * Finds a path from Location start to an Object in world using path(World world, Location start, Location target)
     * @param world World where the Animal(this) and the target(object) are placed.
     * @param start Location to start from
     * @param object Object to path find to
     * @return returns a path from start to object found by path(World world, Location start, Location target)
     */
    protected List<Location> path(World world, Location start, Object object) {
        return path(world, start, world.getLocation(object));
    }

    /**
     * Currently implemented in Rabbit
     * @param c
     * @param animal
     * @param world
     */
    protected void reproduce(Class c, Animal animal, World world) {
    }

    /**
     * @param c The type of object that is being searched for
     * @param from Where to search from
     * @param world World to search in
     * @param view_distance How far away the object may be
     * @param include_mid Whether to include from when searching
     * @return returns the closest object of type c from location within view_distance
     */
    protected Object closest_object(Class c, Location from, World world, int view_distance, boolean include_mid) {
        Set<Location> tiles = world.getSurroundingTiles(from, view_distance);
        if (include_mid) {
            tiles.add(from);
        }
        Object closest_object = null;
        double closest_distance = Double.MAX_VALUE;
        for(Location tile : tiles) {
            Object current = world.getTile(tile);
            if(c.isInstance(current)) {
                double distance = (tile.getX() - from.getX()) * (tile.getX() - from.getX()) + (tile.getY() - from.getY()) * (tile.getY() - from.getY());
                if(distance < closest_distance) {
                    closest_distance = distance;
                    closest_object = world.getTile(tile);
                }
            }
        }
        return closest_object;
    }

    /**
     * Makes use of random_move to move the Animal in a random direction.
     * @param world World to wander in
     * @param from Location to wander from
     */
    protected void wander(World world, Location from) {
        Location target = random_move(world, from);
        if (target == null) return;
        world.move(this, target); // Moves the rabbit to target tile
    }

    /**
     * @param world The world to wander in
     * @param start The location to wander from
     * @return A random location exactly 1 tile from start
     */
    protected Location random_move(World world, Location start) {
        Set<Location> available_tiles = world.getEmptySurroundingTiles(start);
        if (available_tiles.isEmpty()) return null;
        return (Location) available_tiles.toArray()[new Random().nextInt(available_tiles.size())];
    }

    /**
     * Makes use of closest_object to find an object of type c, then uses the path-method to find
     * a path from start to that object. It then moves to the first Location in the List path returned.
     * @param c Type to seek
     * @param world World to seek in
     * @param start Where to seek from
     * @param view_distance How far away the target can be
     */
    protected void seek(Class c, World world, Location start, int view_distance) {
        Object closest = closest_object(c, world.getCurrentLocation(), world, view_distance, false);
        if(closest != null) {
            List<Location> path = path(world, start, world.getLocation(closest));
            if(path.isEmpty()) return;
            Location target = path.getFirst();
            world.move(this, target); // Moves the rabbit to target tile
        } else {
            wander(world, start);
        }
    }


}