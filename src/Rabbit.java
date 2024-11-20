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

    Rabbit() {
        grownup = false;
    }

    Rabbit(boolean grownup) {
        this.grownup = grownup;
        view_distance = 3;
        network = new TunnelNetwork();
        hunger = 10;
        energy = 10;
    }

    @Override
    public void act(World world) {
        age += 0.1; // 1 år per 10 steps. En rabbit er gammel efter 8 år, aka 60 steps.
        if (age > 150) { // En rabbit dør ved age 15, aka 150 steps.
           die(world);
        }

        if (age > 5 && !this.grownup) {
            this.grownup = true;
            getInformation();
        }

        if (cooldown > 0) {
            cooldown--;
            return;
        }
        Location target = world.getCurrentLocation();

        if (world.isDay()) { // Rabbit daytime behaviour

            unburrow(world);
            if(!world.isOnTile(this)) return;
            wander(world, target);

            eatGrass(world);

            if (hunger > 6)
                seek(Grass.class, world, target, view_distance);

            reproduce(world);

            if (hunger <= 10)
                seek(Rabbit.class, world, target, view_distance);

            if(1 == (new Random().nextInt(5) + 1) && energy > 2)
                digHole(world, world.getLocation(this));
        } else { // Nighttime behaviour
            /*
                This was an attempt to make Rabbits not sit idly on a tile
                when they cannot find a hole at night. Makes the problem
                better(I think), but doesn't fix it.
            */
            try {
                burrow(world);
            }catch(RuntimeException e) {// Should make a custom exception instead
                if(e.getMessage().equals("Can't find any hole!")) {
                    if(canDig(world, world.getCurrentLocation()))
                        digHole(world, world.getCurrentLocation());
                    else wander(world, world.getCurrentLocation());
                }
            }

            restoreEnergy();
        }
    }

    @Override
    public DisplayInformation getInformation () {
        if (grownup)
            return new DisplayInformation(Color.GRAY, "rabbit-large");
        return new DisplayInformation(Color.BLACK, "rabbit-small");
    }

    protected boolean canDig(World world, Location location) {
        return !world.containsNonBlocking(location) || world.getNonBlocking(location) instanceof Grass;
    }

    protected void digHole (World world, Location location) {
        energy -= 2;
        if (!world.containsNonBlocking(location)) {
        } else if (world.getNonBlocking(location) instanceof Grass) {
            world.delete(world.getNonBlocking(location));
        } else return;

        Hole hole = new Hole(network);
        world.setTile(location, hole);
        network.addHole(hole);
        cooldown = 3;
    }

    protected void burrow (World world) {
        if (world.isOnTile(this)) {
            Hole closest_hole = getClosestHole(world.getLocation(this), world);
            if (closest_hole == null) return;
            Location target = world.getLocation(closest_hole);
            if (target.equals(world.getLocation(this))) {
                // Burrow successful!
                world.remove(this);
                return;
            }
            List<Location> path = path(world, world.getLocation(closest_hole));
            if (path.isEmpty()) throw new RuntimeException("Can't find any hole!");
            world.move(this, path.getFirst());
        }
    }

    protected void unburrow(World world) {
        if (!world.isOnTile(this)) {
            //!!!!!!TEMPORARY SOLUTION!!!!!!
            network.clean(world);
            if (network.getSize() > 0) { // If the network has any holes, then unburrow
                Hole hole = network.getHole(new Random().nextInt(network.getSize()));
                Location l = world.getLocation(hole);
                world.setCurrentLocation(l);
                if (world.isTileEmpty(l)) {
                    world.setTile(l, this);
                }
            } else {// Else create a new hole and add it to the network
                // This method for placing holes randomly stop them from placing on Grass
                Placement placement = new Placement();
                placement.placeRandomly(world, new Hole(network));
                unburrow(world);
            }
        }
    }

    protected Hole getClosestHole(Location location, World world) {
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
            Location l = world.getLocation(network.getHole(i));
            double distance = (l.getX() - location.getX()) * (l.getX() - location.getX()) + (l.getY() - location.getY()) * (l.getY() - location.getY());
            if(distance < closest_distance) {
                closest_distance = distance;
                closest_hole = network.getHole(i);
            }

        }
        return closest_hole;
    }

    protected Location random_move(World world) {
        Set<Location> available_tiles = world.getEmptySurroundingTiles();
        if (available_tiles.isEmpty()) return null;
        return (Location) available_tiles.toArray()[new Random().nextInt(available_tiles.size())];
    }

    protected void wander(World world, Location target) {
        target = random_move(world);
        if (target == null) return;
        world.move(this, target); // Moves the rabbit to target tile
    }

    protected void seek(Class c, World world, Location target, int view_distance) {
        Object closest = closest_object(c, world.getCurrentLocation(), world, view_distance, false);
        if(closest != null) {
            List<Location> path = path(world, world.getLocation(closest));
            if(path.isEmpty()) return;
            target = path.getFirst();
            world.move(this, target); // Moves the rabbit to target tile
        } else {
            wander(world, target);
        }
    }

    protected void eatGrass(World world) {
        if(world.getNonBlocking(world.getCurrentLocation()) instanceof Grass) {
            eat(world, ((Grass)world.getNonBlocking(world.getCurrentLocation())));
        }
    }

    protected void reproduce(World world) {
        Rabbit closest_rabbit = (Rabbit) closest_object(Rabbit.class, world.getLocation(this), world, view_distance, false);
        if (closest_rabbit != null) {
            Set<Location> closest_rabbit_tiles = world.getSurroundingTiles(world.getLocation(closest_rabbit));
            List<Location> list = new ArrayList<>(closest_rabbit_tiles);
            if (list.contains(world.getLocation(this)) && closest_rabbit.grownup) {
                Random rand = new Random();
                    while (energy > 9) {
                        energy--;
                        Location rl = world.getLocation(this);
                        Set<Location> neighboursToRabbit = world.getEmptySurroundingTiles(rl);
                        List<Location> list2 = new ArrayList<>(neighboursToRabbit);
                        if (!list2.isEmpty()) {
                            Location l = list2.get(rand.nextInt(list2.size()));
                            world.setTile(l, new Rabbit(false));
                        } else {
                            return;
                        }
                    }
           }
        }
    }

    protected void restoreEnergy() {
        while (hunger < 10 && energy < 10) {
            hunger++;
            energy++;
        }
    }


}
