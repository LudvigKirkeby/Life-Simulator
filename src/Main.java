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

        Placement placement = new Placement();
        int amount = 1;
        for (int i = 0; i < amount; i++) {
            placement.placeRandomly(w, new Rabbit(true));
            placement.placeRandomly(w, new Rabbit(true));
            placement.placeRandomly(w, new Rabbit(true));
            placement.placeRandomly(w, new Rabbit(false));
            placement.placeRandomly(w, new Grass());
        }
        p.show();
    }
}