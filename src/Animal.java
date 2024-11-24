import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

import java.util.*;

public abstract class Animal implements Actor, Edible {
    protected int hunger, view_distance;
    protected double age, energy;

    /**
     * @param world Removes this Animal from world
     */
    protected void die(World world) {
        if(world.contains(this)) {
            if (world.isOnTile(this)) {
                Location l = world.getLocation(this);
                world.delete(this);
                world.setTile(l, new Carcass());
            }else {
                world.delete(this);
            }
        }
    }

    /**
     * @param world  World to path find in
     * @param start  The start of the path
     * @param target Where to path find to
     * @return A path from start to target when successful. Null if start equals target.
     */
    protected List<Location> pathTo(World world, Location start, Location target) {
        if (target == null) throw new IllegalArgumentException("Target is null!");
        if (world == null) throw new IllegalArgumentException("World is null!");
        if (!world.contains(this)) throw new RuntimeException("This is not in the world!");
        if(start.getX()>=world.getSize() || start.getX()<0 || start.getY()>=world.getSize() || start.getY()<0)
            throw new IllegalArgumentException("Start location is not in the world!");
        if(target.getX()>=world.getSize() || target.getX()<0 || target.getY()>=world.getSize() || target.getY()<0)
            throw new IllegalArgumentException("Target location is not in the world!");

        if (start.equals(target)) {
            return new ArrayList<>();
        }
        List<Location> final_path = new ArrayList<>();
        List<Location> previous_locations = new ArrayList<>();
        final_path.add(start);
        while (true) {
            Location next = final_path.getLast();
            double least_dist = Double.MAX_VALUE;
            for (Location l : world.getEmptySurroundingTiles(next)) {
                double dist = Math.pow(l.getX() - target.getX(), 2) + Math.pow(l.getY() - target.getY(), 2);
                if (!previous_locations.contains(l) && dist < least_dist) {
                    least_dist = dist;
                    next = l;
                }
            }
            if (next.equals(final_path.getLast())) {
                final_path.removeFirst();
                return final_path;
            }
            previous_locations.add(next);
            final_path.add(next);
            if (next.equals(target)) {
                final_path.removeFirst();
                return final_path;
            }
        }
    }

    /**
     * Finds a path from Location start to an Object in world using path(World world, Location start, Location target)
     *
     * @param world  World where the Animal(this) and the target(object) are placed.
     * @param start  Location to start from
     * @param object Object to path find to
     * @return returns a path from start to object found by path(World world, Location start, Location target)
     */
    protected List<Location> pathTo(World world, Location start, Object object) {
        return pathTo(world, start, world.getLocation(object));
    }

    protected boolean canFindMate(Class<?> c, World world) {
        Set<Location> visible_locations = world.getSurroundingTiles(world.getLocation(this), view_distance);
        for(Location loc : visible_locations) {
            if(c.isInstance(world.getTile(loc))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Reproduces with the closest animal of the same class, given that they are within adjacent tiles. Reproduces non-stop if the implementation does not have an energy requirement.
     *
     * @param c      The class of the animal to breed with and the baby to be born.
     * @param world  The world in which it reproduces.
     */

    protected void reproduce(Class c, World world) throws Exception {
        if(!this.getGrownup()) return;
        Animal closest_animal = (Animal) closestObject(c, world.getLocation(this), world, view_distance, false);
        if (closest_animal == null) { throw new Exception("Closest animal of type "+c.getName()+" null"); }

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
                } catch (Exception e) {
                    System.out.println("Baby instantiation failed || Could not place baby");
                    e.printStackTrace();
                }
            } else {
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
     * @return returns the closest object of type c from location within view_distance
     */
    protected Object closestObject(Class<?> c, Location from, World world, int view_distance, boolean include_mid) {
        Set<Location> tiles = world.getSurroundingTiles(from, view_distance);
        if (include_mid) {
            tiles.add(from);
        }
        Object closest_object = null;
        double closest_distance = Double.MAX_VALUE;
        for (Location tile : tiles) {
            Object current = world.getTile(tile);
            if (c.isInstance(current)) {
                double distance = (tile.getX() - from.getX()) * (tile.getX() - from.getX()) + (tile.getY() - from.getY()) * (tile.getY() - from.getY());
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
        Object closest = closestObject(c, world.getCurrentLocation(), world, view_distance, false);
        if (closest != null) {
            List<Location> path = pathTo(world, start, world.getLocation(closest));
            if (path.isEmpty()) return;
            Location target = path.getFirst();
            world.move(this, target); // Moves the rabbit to target tile
        } else {
            wander(world, start);
        }
    }

    public abstract boolean getGrownup();

    public int getHunger() {
        return hunger;
    }
}