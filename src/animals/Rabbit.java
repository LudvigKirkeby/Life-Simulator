package animals;

import itumulator.executable.DisplayInformation;
import itumulator.world.Location;
import itumulator.world.World;
import misc.*;
import misc.TunnelNetwork;

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

    /**
     * Initializes variables.
     * - network = a new TunnelNetwork instance.
     * - view_distance = 8
     * - hunger = 0
     * - energy = 10
     * - age = 0
     * - cooldown = 0
     * - health_points = 6
     */
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
            try {
                burrowSelf(world);
            }catch(RuntimeException e) {// Should make a custom exception instead
                if(canDig(world, world.getCurrentLocation()))
                    digHole(world, world.getCurrentLocation());
                else wander(world, world.getCurrentLocation());
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

    /**
     * Checks if the tile at the given Location in world either has no nonblocking, contains an instance of a Plant or contains an instance of a Burrow.
     * @param world World that the tile is in.
     * @param location Location to check for 'digability'.
     * @return If it is possible to dig at Location parameter in the World parameter.
     */
    protected boolean canDig(World world, Location location) {
        return !world.containsNonBlocking(location) || world.getNonBlocking(location) instanceof Plant || world.getNonBlocking(location) instanceof Burrow;
    }

    /**
     * Instantiates a Burrow and places it in world at location if possible. If there is already a Burrow at the location in the world, then the Burrow is added to this Rabbits TunnelNetwork.
     * @param world World to dig in
     * @param location Location in world to dig at
     */
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
        world.setTile(location, burrow);
        network.addBurrow(burrow);
        cooldown = 3;
    }

    /**
     * If this is on a Burrow, then enter(remove from world parameters tiles). Else take a step toward the nearest Burrow in the TunnelNetwork burrows on this.
     * @param world World that this is in
     */
    public void burrowSelf(World world) {
        if (world.isOnTile(this)) {
            Burrow closest_burrow = getClosestHole(world.getLocation(this), world);
            if (closest_burrow == null) throw new RuntimeException("Burrow could not be found!");
            Location target = world.getLocation(closest_burrow);
            if (target.equals(world.getLocation(this))) {
                // Burrow successful!
                world.remove(this);
                return;
            }
            takeStepToward(world, target);
        }
    }

    /**
     * Places this back in world on one of the Burrows in the TunnelNetwork this Rabbit has. If no Burrow is available to leave from then a new hole is dug and unburrow is called again.
     * @param world World this will be placed in
     */
    public void unburrow(World world) {
        if (!world.isOnTile(this)) {
            network.clean(world);
            if (network.getSize() > 0) { // If the network has any holes, then unburrow
                Burrow burrow = network.getBurrow(new Random().nextInt(network.getSize()));
                Location l = world.getLocation(burrow);
                world.setCurrentLocation(l);
                if (world.isTileEmpty(l)) {
                    world.setTile(l, this);
                }
            } else {// Else create a new hole and add it to the network
                Set<Location> all_tiles = world.getSurroundingTiles(new Location(0,0), world.getSize());
                all_tiles.add(new Location(0,0));
                for (Location l : all_tiles) {
                    if (canDig(world,l)) {
                        digHole(world,l);
                        break;
                    }
                }
                unburrow(world);
            }
        }
    }

    /**
     * If this Rabbits TunnelNetwork is empty then digHole(...) is called and the method stops.
     * @param location Location to search from
     * @param world World to search in
     * @return The closest Burrow in this Rabbit's TunnelNetwork to location in world.
     */
    protected Burrow getClosestHole(Location location, World world) {
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
            double distance = distToSquared(world, location, l);
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

    /**
     * @return Whether this is grown up, defined by if age is larger than or equal to 3.
     */
    @Override
    public boolean getGrownup() {
        return age >= 3;
    }

    /**
     * @return Value of age field inherited from Animal.
     */
    public double getAge() {
        return age;
    }

    /**
     * @return Reference to this Rabbit's TunnelNetwork, stored in the network field.
     */
    public TunnelNetwork getNetwork() {
        return network;
    }

    /**
     * @return Food value of Rabbit, which is 5.
     */
    @Override
    public double getFoodValue() { return 5; }

    /**
     * @return A List of edible classes. For Rabbits, it only contains Grass.
     */
    @Override
    public List<Class<?>> getEdibleClasses() {
        List<Class<?>> classes = new ArrayList<>();
        classes.add(Grass.class);
        return classes;
    }
}
