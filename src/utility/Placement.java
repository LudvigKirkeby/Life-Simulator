package utility;

import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Placement {
    Random rand = new Random();

    /**
     * Places a given object on a random Location in the world.
     * @param object The object to be placed.
     * @param world the world to place it in.
     */
    public void placeRandomly(World world, Object object) {
            int x = rand.nextInt(world.getSize());
            int y = rand.nextInt(world.getSize());
            Location l = new Location(x, y);

            while(true) {
                x = rand.nextInt(world.getSize());
                y = rand.nextInt(world.getSize());
                l = new Location(x, y);

                if(object instanceof NonBlocking) {
                    if (!world.containsNonBlocking(l))
                        break;
                } else if (world.isTileEmpty(l))
                        break;
            }
            // Still sometimes places a NonBlocking object on another NonBlocking object
            world.setTile(l, object);
    }
}
