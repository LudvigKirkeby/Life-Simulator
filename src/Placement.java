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
    // Placerer et Object et antal gange randomly.
    public void placeRandomly(World world, Object object) {
            int x = rand.nextInt(world.getSize());
            int y = rand.nextInt(world.getSize());
            Location l = new Location(x, y);

            while(!world.isTileEmpty(l) && (!(object instanceof NonBlocking) || world.containsNonBlocking(l))){
                x = rand.nextInt(world.getSize());
                y = rand.nextInt(world.getSize());
                l = new Location(x, y);
            }
            // Still sometimes places a NonBlocking object on another NonBlocking object
            world.setTile(l, object);
    }
}
