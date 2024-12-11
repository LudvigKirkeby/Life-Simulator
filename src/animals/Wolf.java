package animals;

import itumulator.executable.DisplayInformation;
import itumulator.world.Location;
import itumulator.world.World;
import misc.*;
import plants.Plant;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Wolf extends Animal {
    AnimalPack pack;
    boolean sleeping;

    /**
     * Calls other constructor with a new AnimalPack with type Wolf.
     */
    public Wolf() {
        this(new AnimalPack(Wolf.class));
    }

    /**
     * Pack is set to the parameter pack. this is added to the pack.
     * Initialises the wolf's variables.
     * - view_distance = 8
     * - hunger = 0
     * - energy = 10
     * - age = 0
     * - health_points = 12
     * @param pack AnimalPack that this wolf will be part of.
     */
    public Wolf(AnimalPack pack) {
        if(pack == null)
            throw new IllegalArgumentException("pack can't be null!");
        if(pack.getType() != null && !pack.getType().isInstance(this))
            throw new IllegalArgumentException("pack type can't be " + pack.getType()+" for wolf!");
        this.pack = pack;
        pack.add(this);
        view_distance = 100;
        hunger = 0;
        energy = 10;
        age = 0;
        health_points = 12;
    }

    @Override
    public void act(World world) {
        age += 0.05;
        hunger += 0.10;

        if(hunger >= 10) {
            reduceHP(0.4);
            hunger = 10;
        }

        if (age > new Random().nextDouble(13,800) || health_points <= 0) {// A Wolf can die of age at 13 years
            pack.remove(this);
            die(world);
            return;
        }

        if(sleeping) {
            if (hunger < 10 && health_points < 12)
                health_points += 0.5;
            if(world.isDay() && health_points >= 10 || hunger >= 10)
                sleeping = false;
            else
                return;
        }

        if(pack.getCenter() == null)
            createHome(world);

        // Called just in case a wolf in pack got removed from the world
        // without being removed from the pack
        pack.clean(world);

        if(world.isDay()) {// Daytime behaviour
            if(health_points < 3) {
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
                        wander(world, world.getLocation(this));
                    }
                }
            }else {
                goToCave(world);
                if(inSafety(world) && nextToPackMember(world) && energy > 6) {
                    Set<Animal> babies = reproduce(Wolf.class, world);
                    energy -= 4;
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

    /**
     * Safety is defined as either being next to the center of the pack or being surrounded by 3 or more pack members.
     * @param world World that this is in
     * @return Whether either safety condition is met.
     */
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

    /**
     * Checks tiles within view_distance to try and find the nearest member of the pack to this.
     * @param world World that this is in
     * @return Location of the nearest pack member.
     */
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

    /**
     * Makes this head towards the center of the pack.
     * @param world World that this and the pack center is in.
     */
    public void goToCave(World world) {
        if(world.getSurroundingTiles().contains(pack.getCenter()))
            return;
        takeStepToward(world, pack.getCenter());
    }

    /**
     * Sets center of pack to a location next to this in world and places a Cave-object there.
     * @param world World this is in and where the home will be created.
     */
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

    /**
     * Checks if the surrounding tiles in world from the location of this contain a wolf
     * @param world World this is in
     * @return Whether there is a pack member on one of the eight tiles around this in world.
     */
    public boolean nextToPackMember(World world) {
        for(Location tile : world.getSurroundingTiles(world.getLocation(this))) {
            if(world.getTile(tile) instanceof Wolf wolf && pack.contains(wolf)) return true;
        }
        return false;
    }

    /**
     * Tries to find something to attack within view_distance in the supplied World. If it is close enough to attack then it calls attack(World)-method. Else takes a step toward the found prey.
     * @param world The world to find an Object to attack in
     * @return Whether the wolf attacked or went toward something to attack.
     */
    public boolean tryAttack(World world) {
        if (!getGrownup()){return false;} // no child attacks

        Location own_location = world.getLocation(this);
        Set<Location> surrounding = world.getSurroundingTiles(own_location,view_distance);
        Set<Location> search_locations = new HashSet<>();
        for(Animal animal : pack) {
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

    /**
     * Attacks surrounding tiles with attackTiles(...)-method.
     * If one of the tiles contains a wolf from this wolf's pack, then that tile is removed from the attack list.
     * If a tile contains a wolf from a different pack with less than or exactly 4 health, then that wolf becomes a part of this wolf's pack and is removed from the attack list.
     * @param world World this is in and where the attack will take place
     */
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

    /**
     * Sets this wolf's pack to pack parameter.
     * @param pack new pack
     */
    public void setPack(AnimalPack pack) {
        this.pack = pack;
    }

    /**
     * @return This wolf's pack.
     */
    public AnimalPack getPack() {
        return pack;
    }

    /**
     * @return Whether this wolf is grown up or not, defined by if age is larger than 3.
     */
    @Override
    public boolean getGrownup() {
        return age > 3; // Just set it to a temporary value
    }

    /**
     * @return If grown up returns 4, else returns 2.
     */
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

    /**
     * @return Value of sleeping field.
     */
    public boolean isSleeping() {
        return sleeping;
    }

    /**
     * @return List of classes containing the Carcass class.
     */
    public List<Class<?>> getEdibleClasses() {
        List<Class<?>> classes = new ArrayList<>();
        classes.add(Carcass.class);
        return classes;
    }
}
