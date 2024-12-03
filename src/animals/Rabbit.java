package animals;

import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.world.Location;
import itumulator.world.World;
import misc.*;
import utility.Placement;
import utility.TunnelNetwork;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Rabbit extends Animal {
    TunnelNetwork network;
    private int starving = 0;

    public Rabbit(boolean grownup) {
        this();
        if(grownup) {
            age = 3;
        }
    }

    public Rabbit() {
        network = new TunnelNetwork();
        view_distance = 8;
        hunger = 0;
        energy = 10;
        age = 0;
        cooldown = 0;
        health_points = 6;
    }

    @Override
    public void act(World world) {
        age += 0.05; // 1 år per 20 steps. En rabbit er gammel efter 3 år, aka 60 steps.
        if (age > new Random().nextDouble(9,300) || health_points <= 0) { // En rabbit dør ved tidligst ved age 9 eller ved 0 HP
           die(world);
           return;
        }


        if (hunger >= 10) {
            starving++;
            if (starving > 20) {
                die(world);
                return;
            }
        }

        if(hunger < 5) {
            starving = 0;
        }

        if (cooldown > 0) {
            cooldown--;
            return;
        }

        if (world.isDay()) { // Rabbit daytime behaviour

            unburrow(world);

            hunger += 0.05;
            if(!world.isOnTile(this)) return; // Checks if Rabbit is in Burrow(sleeping)
            hunger += 0.05;

            if(world.getNonBlocking(world.getCurrentLocation()) instanceof Grass) {
                eat(world, world.getCurrentLocation());
            }

            if (hunger > 3)
                seek(Grass.class, world, world.getLocation(this), view_distance);
            else
                seek(Rabbit.class, world, world.getLocation(this), view_distance);

            try {
                if (energy > 0 && canFind(Rabbit.class, world)) {
                    energy--;
                    reproduce(Rabbit.class, world);
                }
            } catch (Exception e) {
                System.out.println("Reproduce failure");
                e.printStackTrace();
            }
            if(new Random().nextInt(5) == 0 && energy > 2)
                digHole(world, world.getLocation(this));
        } else { // Nighttime behaviour
            /*
                This was an attempt to make Rabbits not sit idly on a tile
                when they cannot find a hole at night. Makes the problem
                better(I think), but doesn't fix it.
            */
            try {
                burrowSelf(world);
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
        if (getGrownup())
            return new DisplayInformation(Color.GRAY, "rabbit-large");
        return new DisplayInformation(Color.BLACK, "rabbit-small");
    }

    protected boolean canDig(World world, Location location) {
        return !world.containsNonBlocking(location) || world.getNonBlocking(location) instanceof Plant || world.getNonBlocking(location) instanceof Burrow;
    }

    public void digHole(World world, Location location) {
        if (!world.containsNonBlocking(location)) {
        } else if (world.getNonBlocking(location) instanceof Plant) {
            world.delete(world.getNonBlocking(location));
        } else if(world.getNonBlocking(location) instanceof Burrow) {
            network.addBurrow((Burrow)world.getNonBlocking(location));
            return;
        } else return;
        energy -= 3;
        Burrow burrow = new Burrow();
        //if(world.containsNonBlocking(location))
        //    System.out.println(world.getNonBlocking(location).getClass());
        world.setTile(location, burrow);
        network.addBurrow(burrow);
        cooldown = 3;
    }

    public void burrowSelf(World world) {
        if (world.isOnTile(this)) {
            Burrow closest_burrow = getClosestHole(world.getLocation(this), world);
            if (closest_burrow == null) return;
            Location target = world.getLocation(closest_burrow);
            if (target.equals(world.getLocation(this))) {
                // Burrow successful!
                world.remove(this);
                return;
            }
            takeStepToward(world, target);
        }
    }

    public void unburrow(World world) {
        if (!world.isOnTile(this)) {
            //!!!!!!TEMPORARY SOLUTION!!!!!!
            network.clean(world);
            if (network.getSize() > 0) { // If the network has any holes, then unburrow
                Burrow burrow = network.getBurrow(new Random().nextInt(network.getSize()));
                Location l = world.getLocation(burrow);
                world.setCurrentLocation(l);
                if (world.isTileEmpty(l)) {
                    world.setTile(l, this);
                }
            } else {// Else create a new hole and add it to the network
                // This method for placing holes randomly stop them from placing on Grass
                Placement placement = new Placement();
                Set<Location> all_tiles = world.getSurroundingTiles(new Location(0,0), world.getSize());
                all_tiles.add(new Location(0,0));
                for (Location l : all_tiles) {
                    if (canDig(world,l)) {
                        digHole(world,l);
                        break;
                    }
                }
                //placement.placeRandomly(world, new Burrow());
                unburrow(world);
            }
        }
    }

    protected Burrow getClosestHole(Location location, World world) {
        //!!!!!!TEMPORARY SOLUTION!!!!!!
        network.clean(world);

        if(network.getSize()==0){
            //throw new RuntimeException("Network is empty!");
            digHole(world, location);
            return null;
        }
        Burrow closest_burrow = null;
        double closest_distance = Double.MAX_VALUE;
        for (Burrow b : network) {
            Location l = world.getLocation(b);
            double distance = distTo(world, location, l);
            if(distance < closest_distance) {
                closest_distance = distance;
                closest_burrow = b;
            }

        }
        return closest_burrow;
    }

    protected void restoreEnergy() {
        while (hunger < 5 && energy < 10) {
            hunger++;
            if (age < 7) {
                energy++;
            } else {
                energy += 0.5;
            }
        }
    }

    public boolean getGrownup() {
        return age >= 3;
    }

    public double getAge() {
        return age;
    }

    public double getFoodValue() { return 5; }


    /*public void eat(World world, Edible edible) {
        if (hunger > 0) {
            hunger -= edible.getFoodValue();
            energy += edible.getFoodValue();
        }
        world.delete(edible);
    }*/

    public TunnelNetwork getNetwork() {
        return network;
    }

    public List<Class<?>> getEdibleClasses() {
        List<Class<?>> classes = new ArrayList<>();
        classes.add(Grass.class);
        return classes;
    }
}
