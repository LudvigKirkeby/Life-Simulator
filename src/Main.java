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

        // w.setTile(new Location(0,0), new Hole());
        // p.setDisplayInformation(<MyClass>.class, new DisplayInformation(<Color>, "<ImageName>"));

        p.show();

        Placement placement = new Placement();

        // eks på brugen af placeRandomly
        int amount = 4;
        for (int i = 0; i < amount; i++) {
            placement.placeRandomly(w, new Hole());
        }

        int steps = 200;
        for (int i = 0; i < steps; i++) {
            p.simulate();
        }

    }
}