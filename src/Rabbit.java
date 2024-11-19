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
    Random rand = new Random();
    boolean grownup;
    TunnelNetwork network;
    private int cooldown;

    Rabbit() {
        grownup = false;
    }

    Rabbit(boolean grownup) {
        this.grownup = grownup;
        network = new TunnelNetwork();
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

            if (hunger < 5 && false/*Temporary*/) {
                //Seek out grass
            } else {
                //Just wander
                Set<Location> available_tiles = world.getEmptySurroundingTiles();
                if (available_tiles.isEmpty()) return;
                target = (Location) available_tiles.toArray()[new Random().nextInt(available_tiles.size())];
            }
            world.move(this, target); // Moves the rabbit to target tile
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
        if (1 == (rand.nextInt(5) + 1) && !world.containsNonBlocking(location)) {
            Hole hole = new Hole(network);
            world.setTile(location, hole);
            network.addHole(hole);
            cooldown = 3;
        }
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
     If no hole found, raises exception, even so, returns null
     */

    public Hole getClosestHole(Location location, World world) {
        if(network.getSize()==0) throw new RuntimeException("Network is empty!");
        Hole closest_hole = null;
        double closest_distance = Double.MAX_VALUE;
        for (int i = 0; i < network.getSize(); i++) {
            Location l = world.getLocation(network.getHole(i));
            double distance = (l.getX() - location.getX()) * (l.getX() - location.getX()) + (l.getY() - location.getY()) * (l.getY() - location.getY());
            if(distance < closest_distance) {
                closest_distance = distance;
                closest_hole = network.getHole(i);
            }
        }
        return closest_hole;
    }
}
