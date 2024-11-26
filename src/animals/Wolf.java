package animals;

import itumulator.executable.DisplayInformation;
import itumulator.world.Location;
import itumulator.world.World;
import misc.Burrow;
import misc.Cave;
import misc.Edible;
import misc.Plant;

import java.awt.*;
import java.util.List;
import java.util.Set;

public class Wolf extends Animal {
    AnimalPack pack;
    boolean sleeping;

    public Wolf() {
        pack = new AnimalPack(this.getClass());
        pack.add(this);
    }

    public Wolf(AnimalPack pack) {
        if(pack == null)
            throw new IllegalArgumentException("pack can't be null!");
        if(pack.getType() != null && !pack.getType().isInstance(this))
            throw new IllegalArgumentException("pack type can't be " + pack.getType()+" for wolf!");
        this.pack = pack;
        pack.add(this);
    }

    @Override
    public void act(World world) {
        age += 0.05;
        if(sleeping) {
            if(world.isDay())
                sleeping = false;
            else
                return;
        }

        if(pack.getCenter() == null)
            createHome(world);

        if(world.isDay()) {// Daytime behaviour
            wander(world);
            //if(energy<3 && pack.getCenter() != null)
            //    goToPack(world);
        }else {
            if(canSleep(world)) {
                sleeping = true;
            }else {
                goToPack(world);
            }
        }
    }

    protected boolean canSleep(World world) {
        System.out.println(pack.size());
        Set<Location> surrounding_tiles = world.getSurroundingTiles(world.getLocation(this));

        if(surrounding_tiles.contains(pack.getCenter()))
            return true;

        int members = 0;
        for(Location tile : surrounding_tiles) {
            if(world.getTile(tile) instanceof Wolf wolf) {
                System.out.println("is sleeping: "+wolf.isSleeping());
                System.out.println("in pack: "+pack.contains(wolf));
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
