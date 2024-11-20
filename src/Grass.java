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

            // The following code makes sure grass is not placed on an occupied tile
            Location target = null;
            while(!list.isEmpty() && target == null) {
                Location current = list.get(rand.nextInt(list.size()));
                if(world.containsNonBlocking(current)) {
                    list.remove(current);
                }else {
                    target = current;
                }
            }
            // If no available tile was found then don't grow
            if(target == null) return;
            // Otherwise grass is placed
            world.setTile(target, new Grass());
        }
    }

    @Override
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.GREEN, "grass");
    }
}
