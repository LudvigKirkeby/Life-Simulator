package animals;

import itumulator.executable.DisplayInformation;
import itumulator.world.Location;
import itumulator.world.World;
import misc.Cave;
import misc.Carcass;
import misc.Edible;
import misc.Plant;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Wolf extends Animal {
    AnimalPack pack;
    boolean sleeping;

    public Wolf() {
        this(new AnimalPack(Wolf.class));
    }

    public Wolf(AnimalPack pack) {
        if(pack == null)
            throw new IllegalArgumentException("pack can't be null!");
        if(pack.getType() != null && !pack.getType().isInstance(this))
            throw new IllegalArgumentException("pack type can't be " + pack.getType()+" for wolf!");
        this.pack = pack;
        pack.add(this);
        view_distance = 8;
        hunger = 0;
        energy = 10;
        age = 0;
        health_points = 12;
    }

    @Override
    public void act(World world) {
        age += 0.05;

        if(hunger >= 15) {
            reduceHP(0.25);
            hunger = 15;
        }

        if (age > new Random().nextDouble(13,800) || health_points <= 0) {// A Wolf can die of age at 13 years
            pack.remove(this);
            die(world);
            return;
        }

        hunger += 0.05;
        if(sleeping) {
            health_points += 1;
            if(world.isDay() && health_points >= 10)
                sleeping = false;
            else
                return;
        }
        hunger += 0.05;

        if(pack.getCenter() == null)
            createHome(world);

        // Called just in case a wolf in pack somehow got removed from the world
        // without being removed from the pack
        pack.clean(world);

        if(world.isDay()) {// Daytime behaviour
            if(health_points < 4) {
                goToCave(world);
                if(inSafety(world)) {
                    sleeping = true;
                }
            }else if(hunger >= 3) {
                if(!tryAttack(world)) {
                    Location nearest_member = nearestPackMemberLocation(world);
                    if(nearest_member != null && distToSquared(world, nearest_member)>=2) {
                        takeStepToward(world, nearest_member);
                    }else {
                        wander(world);
                    }
                }
            }else {
                goToCave(world);
                if(inSafety(world) && nextToPackMember(world)) {
                    Set<Animal> babies = reproduce(Wolf.class, world);
                    for(Animal a : babies) {
                        if(a instanceof Wolf wolf) {
                            wolf.setPack(pack);
                        }
                    }
                }
            }
        }else {
            if(inSafety(world)) {
                sleeping = true;
            }else {
                goToCave(world);
            }
        }
    }

    protected boolean inSafety(World world) {
        Set<Location> surrounding_tiles = world.getSurroundingTiles(world.getLocation(this));

        if(surrounding_tiles.contains(pack.getCenter()))
            return true;

        int members = 0;
        for(Location tile : surrounding_tiles) {
            if(world.getTile(tile) instanceof Wolf wolf) {
                if(pack.contains(wolf) && wolf.isSleeping()) {
                    members++;
                    if(members >= 3) return true;
                }
            }
        }
        return false;
    }

    public Location nearestPackMemberLocation(World world) {
        Set<Location> tiles = world.getSurroundingTiles(world.getLocation(this), view_distance);
        Location location = null;
        double nearest_dist = Double.MAX_VALUE;
        for(Location l : tiles) {
            if(world.getTile(l) instanceof Wolf wolf) {
                double dist = distToSquared(world, l);
                if(pack.contains(wolf) && dist < nearest_dist) {
                    nearest_dist = dist;
                    location = l;
                }
            }
        }
        return location;
    }

    public void goToCave(World world) {
        if(world.getSurroundingTiles().contains(pack.getCenter()))
            return;
        takeStepToward(world, pack.getCenter());
    }

    public void createHome(World world) {
        Location self = world.getLocation(this);
        for(Location tile : world.getEmptySurroundingTiles(self)) {
            if(world.isTileEmpty(tile)) {
                world.setTile(tile, new Cave());
                pack.setCenter(tile);
                break;
            }
        }
    }

    public boolean nextToPackMember(World world) {
        Object o = closestObject(Wolf.class, world.getLocation(this),world,1,false);
        return o instanceof Animal a && pack.contains(a);
    }

    /**
     *
     * @param world The world to find an Object to attack in
     * @return whether the wolf attacked or went toward something to attack.
     */
    public boolean tryAttack(World world) {
        if (!getGrownup()){return false;} // no child attacks

        Location own_location = world.getLocation(this);
        Set<Location> surrounding = world.getSurroundingTiles(own_location,view_distance);
        Set<Location> search_locations = new HashSet<>();
        for(Animal animal : pack.getAnimals()) {
            surrounding.remove(world.getLocation(animal));
        }
        for(Location tile : surrounding) {
            if(!(world.getTile(tile) instanceof Plant))
                search_locations.add(tile);
        }
        if (!search_locations.isEmpty()) {
            Object target = closestObject(Edible.class, own_location, world, search_locations);
            if (target != null && world.contains(target)) {
                Location t_loc = world.getLocation(target);
                if(world.getSurroundingTiles(world.getLocation(this)).contains(t_loc)) {
                    attack(world);
                }else takeStepToward(world, world.getLocation(target));
                return true;
            }
        }
        return false;
    }

    public void attack(World world) {
        Set<Location> surrounding = world.getSurroundingTiles(world.getLocation(this));
        List<Location> attack_list = new ArrayList<>(surrounding);
        for(int i = 0; i < attack_list.size(); i++) {
            Object o = world.getTile(attack_list.get(i));
            if(o instanceof Wolf wolf) {
                if(pack.contains(wolf)) {
                    attack_list.remove(i);
                    i--;
                }else if(wolf.getHP() <= 4) {
                    attack_list.remove(i);
                    wolf.setPack(pack);
                }
            }
        }
        attackTiles(world,attack_list,3);
    }

    public void setPack(AnimalPack pack) {
        this.pack = pack;
    }

    public AnimalPack getPack() {
        return pack;
    }

    @Override
    public boolean getGrownup() {
        return age > 3; // Just set it to a temporary value
    }

    @Override
    public double getFoodValue() {
        if(getGrownup())
            return 4;
        return 2;
    }

    @Override
    public DisplayInformation getInformation() {
        if (getGrownup()) {
            if(sleeping)
                return new DisplayInformation(Color.GRAY, "wolf-sleeping");
            return new DisplayInformation(Color.GRAY, "wolf");
        }
        if(sleeping)
            return new DisplayInformation(Color.GRAY, "wolf-small-sleeping");
        return new DisplayInformation(Color.GRAY, "wolf-small");
    }

    public boolean isSleeping() {
        return sleeping;
    }

    public List<Class<?>> getEdibleClasses() {
        List<Class<?>> classes = new ArrayList<>();
        classes.add(Carcass.class);
        return classes;
    }
}
