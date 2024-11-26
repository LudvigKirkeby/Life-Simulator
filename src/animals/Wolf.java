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

public class Wolf extends Animal {
    AnimalPack pack;
    boolean sleeping;

    public Wolf() {
        pack = new AnimalPack(this.getClass());
    }

    public Wolf(AnimalPack pack) {
        if(pack == null)
            throw new IllegalArgumentException("pack can't be null!");
        if(pack.getType() != null && !pack.getType().isInstance(this))
            throw new IllegalArgumentException("pack type can't be " + pack.getType()+" for wolf!");
        this.pack = pack;
    }

    @Override
    public void act(World world) {
        if(sleeping) {
            if(world.isDay())
                sleeping = false;
            else
                return;
        }

        if(energy>5 || world.isNight()) {
            if(pack.getCenter() != null)
                goToPack(world);
            else
                createHome(world);
        }
    }

    public void goToPack(World world) {
        takeStepToward(world, pack.getCenter());
    }

    protected boolean canCreateHome(World world, Location location) {
        return !world.containsNonBlocking(location) || world.getNonBlocking(location) instanceof Plant;
    }

    public void createHome(World world) {
        Location self = world.getLocation(this);
        if(canCreateHome(world, self)) {
            world.setTile(self, new Cave());
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
}
