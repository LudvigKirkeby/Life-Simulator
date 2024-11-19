import java.awt.Color;

import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;

public class Main {

    public static void main(String[] args) {
        int size = 10;
        Program p = new Program(size, 800, 75);

        World w = p.getWorld();

        // Below code is an example of placeRandomly.

        Placement placement = new Placement();
        int amount = 1;
        for (int i = 0; i < amount; i++) {
            placement.placeRandomly(w, new Rabbit(true));
            placement.placeRandomly(w, new Rabbit(false));
        }

        //w.setTile(new Location(0,0), new Rabbit(true));
        //w.setTile(new Location(2,2), new Rabbit(false));
        // p.setDisplayInformation(<MyClass>.class, new DisplayInformation(<Color>, "<ImageName>"));

        p.show();
    }
}