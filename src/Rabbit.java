import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.world.Location;
import itumulator.world.World;

import java.awt.Color;
import java.util.Random;
import java.util.Set;

public class Rabbit extends Herbivore implements DynamicDisplayInformationProvider {
    Random rand = new Random();
    private Hole h;
    boolean grownup;

    Rabbit() {
        grownup = false;
    }

    Rabbit(boolean grownup) {
        this.grownup = grownup;
    }

    @Override
    public void act(World world) {
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
        }
        // Moves the rabbit to target tile
        world.move(this, target);

        digHole(world);
    }

    @Override
    public DisplayInformation getInformation() {
        if(grownup)
            return new DisplayInformation(Color.GRAY,"rabbit-large");
        return new DisplayInformation(Color.BLACK,"rabbit-small");
    }


    public void digHole(World world) {
        if (1 == (rand.nextInt(10) + 1)) {
            h = new Hole(this, world);
        }
    }

}
