import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;
import java.util.Random;

public class Placement {

    // Placerer et Object et antal gange randomly.
    public void placeRandomly(World world, Object object, int amount) {
        Random rand = new Random();
        for (int i = 0; i < amount; i++) {
            int x = rand.nextInt(world.size());
            int y = rand.nextInt(world.size());
            Location l = new Location(x, y);

            while(!world.isTileEmpty(l)){
                x = rand.nextInt(world.size());
                y = rand.nextInt(world.size());
                l = new Location(x, y);
            }
            world.setTile(l, new Object);
        }

    }
}
