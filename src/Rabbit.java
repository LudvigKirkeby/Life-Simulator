import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.world.Location;
import itumulator.world.World;

import java.awt.Color;
import java.util.Random;
import java.util.Set;

public class Rabbit extends Herbivore implements DynamicDisplayInformationProvider {
    private Random rand = new Random();
    private Hole hole;
    private boolean grownup;
    private int cooldown;

    Rabbit() {
        grownup = false;
    }

    Rabbit(boolean grownup) {
        this.grownup = grownup;
    }

    @Override
    public void act(World world) {
        if(cooldown>0) {
            cooldown--;
            return;
        }

        Location target = world.getCurrentLocation();
        //Rabbit daytime behaviour
        if(world.isDay()) {
            /*
                If no special conditions are met the rabbit just wanders.
                I'm thinking it should decide what to do based on hunger.
             */
            if(hunger < 5 && false/*Temporary*/) {
                //Seek out grass
            }else {
                //Just wander
                Set<Location> available_tiles =  world.getEmptySurroundingTiles();
                if(available_tiles.isEmpty()) return;
                target = (Location)available_tiles.toArray()[new Random().nextInt(available_tiles.size())];
            }
        }else {// Nighttime behaviour

        }
        // Moves the rabbit to target tile
        world.move(this, target);

        digHole(world, world.getLocation(this));
    }

    @Override
    public DisplayInformation getInformation() {
        if(grownup)
            return new DisplayInformation(Color.GRAY,"rabbit-large");
        return new DisplayInformation(Color.LIGHT_GRAY,"rabbit-small");
    }

    public void digHole(World world, Location location) {
        if (1 == (rand.nextInt(10) + 1) && !world.containsNonBlocking(location)) {
            world.setTile(location, new Hole(this));
            cooldown = 3;
        }
    }
}
