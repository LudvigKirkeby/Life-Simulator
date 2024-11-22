import java.awt.Color;

import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;

import java.util.HashMap;

public class Main {

    public static void main(String[] args) {

        /*
        int size = 10;
        Program p = new Program(size, 800, 10);

        World w = p.getWorld();

        Placement placement = new Placement();
        int amount = 2;
        for (int i = 0; i < amount; i++) {
            placement.placeRandomly(w, new Rabbit(true));
            placement.placeRandomly(w, new Rabbit(false));
            placement.placeRandomly(w, new Grass());
            placement.placeRandomly(w, new Grass());
            placement.placeRandomly(w, new Grass());
        }
         */

        Reader reader = new Reader();
        /*
        reader.loadFile("src/inputs_week-1/t1-1a.txt");
        reader.loadFile("src/inputs_week-1/t1-1b.txt");
        reader.loadFile("src/inputs_week-1/t1-1c.txt");
        reader.loadFile("src/inputs_week-1/t1-2a.txt");
        reader.loadFile("src/inputs_week-1/t1-2b.txt");
        reader.loadFile("src/inputs_week-1/t1-2cde.txt");
        reader.loadFile("src/inputs_week-1/t1-2fg.txt");
        reader.loadFile("src/inputs_week-1/t1-3a.txt");
        reader.loadFile("src/inputs_week-1/t1-3b.txt");
        reader.loadFile("src/inputs_week-1/tf1-1.txt");
        */
         
        reader.readFile();

        Program p = new Program(reader.getWorldSize(), 800, 10);
        World w = p.getWorld();
        reader.Execute(w);

        p.show();
    }
}