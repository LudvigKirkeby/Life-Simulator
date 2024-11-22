import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.world.Location;
import itumulator.world.World;

import java.awt.Color;
import java.util.List;
import java.util.Random;

public class Rabbit extends Animal implements DynamicDisplayInformationProvider, Edible {
    TunnelNetwork network;
    private int cooldown;

    Rabbit(boolean grownup) {
        this();
        if(grownup) {
            age = 3;
        }
    }

    Rabbit() {
        network = new TunnelNetwork();
        view_distance = 8;
        hunger = 10;
        energy = 10;
        age = 0;
        cooldown = 0;
    }

    @Override
    public void act(World world) {
        age += 0.05; // 1 år per 20 steps. En rabbit er gammel efter 3 år, aka 60 steps.
        if (age > new Random().nextDouble(9,300)) { // En rabbit dør ved tidligst ved age 9
           die(world);
           return;
        }

        if (cooldown > 0) {
            cooldown--;
            return;
        }
        Location target = world.getCurrentLocation();

        if (world.isDay()) { // Rabbit daytime behaviour

            unburrow(world);

            if(!world.isOnTile(this)) {
                return;
            }

            if(!world.isOnTile(this)) return;
            wander(world, world.getLocation(this));

            if(world.getNonBlocking(world.getCurrentLocation()) instanceof Grass) {
                eat(world, ((Grass)world.getNonBlocking(world.getCurrentLocation())));
            }

            if (hunger > 6)
                seek(Grass.class, world, world.getLocation(this), view_distance);

            if (hunger <= 10)
                seek(Rabbit.class, world, world.getLocation(this), view_distance);

            try {
                if (energy > 0) {
                    energy--;
                    reproduce(Rabbit.class, this, world);
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
        if (getGrownup())
            return new DisplayInformation(Color.GRAY, "rabbit-large");
        return new DisplayInformation(Color.BLACK, "rabbit-small");
    }

    protected boolean canDig(World world, Location location) {
        return !world.containsNonBlocking(location) || world.getNonBlocking(location) instanceof Grass;
    }

    protected void digHole (World world, Location location) {
        if (!world.containsNonBlocking(location)) {
        } else if (world.getNonBlocking(location) instanceof Grass) {
            world.delete(world.getNonBlocking(location));
        } else return;
        energy -= 3;
        Burrow burrow = new Burrow(network);
        world.setTile(location, burrow);
        network.addBurrow(burrow);
        cooldown = 3;
    }

    protected void burrow (World world) {
        if (world.isOnTile(this)) {
            Burrow closest_burrow = getClosestHole(world.getLocation(this), world);
            if (closest_burrow == null) return;
            Location target = world.getLocation(closest_burrow);
            if (target.equals(world.getLocation(this))) {
                // Burrow successful!
                world.remove(this);
                return;
            }
            List<Location> path = path_to(world, world.getCurrentLocation(), world.getLocation(closest_burrow));
            if (path.isEmpty()) throw new RuntimeException("Can't find any hole!");
            world.move(this, path.getFirst());
        }
    }

    protected void unburrow(World world) {
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
                placement.placeRandomly(world, new Burrow(network));
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
        for (int i = 0; i < network.getSize(); i++) {
            Location l = world.getLocation(network.getBurrow(i));
            double distance = (l.getX() - location.getX()) * (l.getX() - location.getX()) + (l.getY() - location.getY()) * (l.getY() - location.getY());
            if(distance < closest_distance) {
                closest_distance = distance;
                closest_burrow = network.getBurrow(i);
            }

        }
        return closest_burrow;
    }

    protected void restoreEnergy() {
        while (hunger < 10 && energy < 10) {
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

    public int getFoodValue() { return 5; }

    public void eat(World world, Edible edible) {
        if (hunger > 0) {
            hunger -= edible.getFoodValue();
        }
        world.delete(edible);
    }

}
