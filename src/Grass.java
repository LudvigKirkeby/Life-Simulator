import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Grass implements Plant, DynamicDisplayInformationProvider, NonBlocking, Actor {

    Grass() {
    }

    @Override
    public void act(World world) {
        grow(world);
    }

    @Override
    public void grow(World world) {
        Random rand = new Random();
        if (1 == (rand.nextInt(10) + 1)) {
            Set<Location> set = world.getSurroundingTiles(world.getLocation(this));
            List<Location> list = new ArrayList<>(set);
            world.setTile(list.get(rand.nextInt(list.size())), new Grass());
        }
    }

    @Override
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.BLACK, "grass");
    }
}
