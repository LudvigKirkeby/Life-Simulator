import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.world.Location;
import itumulator.world.World;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Rabbit extends Herbivore implements DynamicDisplayInformationProvider {
    boolean grownup;
    TunnelNetwork network;
    private int cooldown;
    private int view_distance;

    Rabbit() {
        grownup = false;
    }

    Rabbit(boolean grownup) {
        this.grownup = grownup;
        view_distance = 3;
        network = new TunnelNetwork();
        hunger = 10;
    }

    @Override
    public void act(World world) {
        if (cooldown > 0) {
            cooldown--;
            return;
        }
        Location target = world.getCurrentLocation();
        //Rabbit daytime behaviour
        if (world.isDay()) {
            /*
                If no special conditions are met the rabbit just wanders.
                I'm thinking it should decide what to do based on hunger.
             */

            if (!world.isOnTile(this)) {
                unburrow(world);
                return;
            }
            if(world.getNonBlocking(world.getCurrentLocation()) instanceof Grass) {
                eat(world, ((Grass)world.getNonBlocking(world.getCurrentLocation())));
                return;
            }

            if (hunger > 5/*Temporary*/) {// Seek out grass
                Grass closest_grass = (Grass)closest_object(Grass.class, world.getCurrentLocation(), world);
                if(closest_grass != null) {
                    List<Location> path = path(world, world.getLocation(closest_grass));
                    if(path.size()<2) return;
                    target = path.get(1);
                }else {
                    target = random_move(world);
                }

            } else {
                //Just wander
                target = random_move(world);
            }
            world.move(this, target); // Moves the rabbit to target tile
            if(1 == (new Random().nextInt(5) + 1))
                digHole(world, world.getLocation(this));
        } else {
            if (world.isOnTile(this)) {
                burrow(world); // nighttime behaviour
            }
        }
    }

    @Override
    public DisplayInformation getInformation () {
        if (grownup)
            return new DisplayInformation(Color.GRAY, "rabbit-large");
        return new DisplayInformation(Color.BLACK, "rabbit-small");
    }

    public void digHole (World world, Location location) {
        if (!world.containsNonBlocking(location)) {
        }else if(world.getNonBlocking(location) instanceof Grass) {
            world.delete(world.getNonBlocking(location));
        }else return;

        Hole hole = new Hole(network);
        world.setTile(location, hole);
        network.addHole(hole);
        cooldown = 3;
    }

    public void burrow (World world) {
        Hole closest_hole = getClosestHole(world.getLocation(this), world);
        if(closest_hole==null) return;
        Location target = world.getLocation(closest_hole);
        if(target.equals(world.getLocation(this))) {
            // Burrow successful!
            world.remove(this);
            return;
        }
        List<Location> path = path(world, world.getLocation(closest_hole));
        if(path.size()<2) return;
        world.move(this, path.get(1));
    }

    public void unburrow(World world) {
        //!!!!!!TEMPORARY SOLUTION!!!!!!
        network.clean(world);
        if (network.getSize() > 0) { // If the network has any holes, then unburrow
            Hole hole = network.getHole(new Random().nextInt(network.getSize()));
            Location l = world.getLocation(hole);
            world.setCurrentLocation(l);
            world.setTile(l, this);
        } else {// Else create a new hole and add it to the network
            Placement placement = new Placement();
            placement.placeRandomly(world, new Hole(network));
            unburrow(world);
        }
    }

    /*
     Finds closest Hole in holes to the location
     If no hole found, raises exception
     */

    public Hole getClosestHole(Location location, World world) {
        //!!!!!!TEMPORARY SOLUTION!!!!!!
        network.clean(world);

        if(network.getSize()==0){
            //throw new RuntimeException("Network is empty!");
            digHole(world, location);
            return null;
        }
        Hole closest_hole = null;
        double closest_distance = Double.MAX_VALUE;
        for (int i = 0; i < network.getSize(); i++) {
            //if(world.contains(network.getHole(i))) {
                Location l = world.getLocation(network.getHole(i));
                double distance = (l.getX() - location.getX()) * (l.getX() - location.getX()) + (l.getY() - location.getY()) * (l.getY() - location.getY());
                if(distance < closest_distance) {
                    closest_distance = distance;
                    closest_hole = network.getHole(i);
                }
            //}

        }
        return closest_hole;
    }

    public Object closest_object(Class c, Location location, World world) {
        Set<Location> tiles = world.getSurroundingTiles(location, view_distance);
        tiles.add(location);
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

    public Location random_move(World world) {
        Set<Location> available_tiles = world.getEmptySurroundingTiles();
        if (available_tiles.isEmpty()) return null;
        return (Location) available_tiles.toArray()[new Random().nextInt(available_tiles.size())];
    }
}
