package misc;

import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public abstract class Plant implements NonBlocking {

    /**
     * Places a plant of class c on an adjacent empty tile.
     * @param c The class to place another instance of. Unless the plant turns into another species, this class will always be an instance of itself.
     * @param world The world to grow in.
     */
    public void grow(Class<?> c, World world) throws Exception {

        Set<Location> set = world.getSurroundingTiles(world.getLocation(this));
        List<Location> list = new ArrayList<>(set);

        // The following code makes sure the plant is not placed on an occupied tile
        Location target = null;
        while(!list.isEmpty() && target == null) {
            Location current = list.get(new Random().nextInt(list.size()));
            if(world.containsNonBlocking(current)) {
                list.remove(current);
            } else {
                target = current;
            }
            // If no available tile was found then don't grow
            if(target == null) return;
            // Otherwise grass is placed
            try {
                Plant newplant = (Plant) c.getDeclaredConstructor().newInstance();
                world.setTile(target, newplant);
            } catch(Exception e) {
                System.out.println("Could not grow");
            }
        }
    }
}
