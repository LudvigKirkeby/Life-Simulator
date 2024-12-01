package animals;

import itumulator.executable.DisplayInformation;
import itumulator.world.Location;
import itumulator.world.World;
import misc.Cave;
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
        hunger = 10;
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

        if(sleeping) {
            hunger += 0.05;
            if(world.isDay())
                sleeping = false;
            else
                return;
        }
        hunger += 0.05;

        if(pack.getCenter() == null)
            createHome(world);

        if(world.isDay()) {// Daytime behaviour
            if(hunger >= 3) {
                if(!tryAttack(world)) {
                    wander(world);
                }
            }else {
                goToPack(world);
                if(inSafety(world) && canFindPackMember(world)) {
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
                goToPack(world);
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

    public void goToPack(World world) {
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

    public boolean canFindPackMember(World world) {
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
            if(o instanceof Wolf wolf && pack.contains(wolf)) {
                attack_list.remove(i);
                i--;
            }
        }
        attackTiles(world,attack_list,3);
    }

    public void setPack(AnimalPack pack) {
        this.pack = pack;
    }

    @Override
    public boolean getGrownup() {
        return age > 3; // Just set it to a temporary value
    }

    @Override
    public int getFoodValue() {
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
}
