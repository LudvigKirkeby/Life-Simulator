package misc;

import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.world.Location;
import itumulator.world.World;
import itumulator.simulator.Actor;

import java.awt.*;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Fungi implements Actor, DynamicDisplayInformationProvider {
    private double timetodeath;

    Fungi(double foodvalue) {
        this.timetodeath = foodvalue;
    }

    @Override
    public void act(World world) {

        timetodeath -= 0.07;

        if (timetodeath <= 0) {
            world.delete(this);
        }

        if (!world.contains(this)) {return;}
        Set<Location> tiles = world.getSurroundingTiles(world.getLocation(this), 2);
        for (Location loc : tiles) {
            if (world.getTile(loc) instanceof Carcass c && !c.getFungi()) {
                if (new Random().nextInt(9) == 0) {
                    c.spawnFungi();
                }
            }
        }
    }

    @Override
    public DisplayInformation getInformation() {
        if (timetodeath > 2)
            return new DisplayInformation(Color.BLACK, "fungi");
        return new DisplayInformation(Color.WHITE, "fungi-small");
    }


}



