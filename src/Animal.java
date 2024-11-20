import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class Animal implements Actor {
    protected int hunger, view_distance;
    protected double age, energy;

    protected void die(World world) {
        world.delete(this);
    }

    protected List<Location> path(World world, Location target) {
        if(target == null) throw new RuntimeException("Target is null!");
        if(world == null) throw new RuntimeException("World is null!");
        if(!world.contains(this)) throw new RuntimeException("This is not in the world!");

        if(world.getCurrentLocation().equals(target)) {
            return new ArrayList<Location>();
        }

        List<Location> final_path = new ArrayList<>();
        List<Location> previous_locations = new ArrayList<>();
        final_path.add(world.getCurrentLocation());
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
                return final_path;
            }
            previous_locations.add(next);
            final_path.add(next);
            if(next.equals(target)) {
                return final_path;
            }
        }
    }

    protected List<Location> path(World world, Object object) {
        return path(world, world.getLocation(object));
    }

    protected void reproduce(Class c, Animal animal, World world) {
    }

    public Object closest_object(Class c, Location location, World world, int view_distance, boolean middle) {
        Set<Location> tiles = world.getSurroundingTiles(location, view_distance);
        if (middle) {
            tiles.add(location);
        }
        Object closest_object = null;
        double closest_distance = Double.MAX_VALUE;
        for(Location tile : tiles) {
            Object current = world.getTile(tile);
            if(c.isInstance(current)) {
                double distance = (tile.getX() - location.getX()) * (tile.getX() - location.getX()) + (tile.getY() - location.getY()) * (tile.getY() - location.getY());
                if(distance < closest_distance) {
                    closest_distance = distance;
                    closest_object = world.getTile(tile);
                }
            }
        }
        return closest_object;
    }


}