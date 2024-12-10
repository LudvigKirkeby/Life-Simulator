import java.awt.Color;

import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;
import misc.*;
import animals.*;
import plants.Bush;
import plants.Grass;
import utility.Placement;
import utility.Reader;

import java.util.HashMap;

public class Main {

    public static void main(String[] args) {

        Reader reader = new Reader();
        //reader.loadFile("src/inputs_week-1/tf1-1.txt");
        reader.loadFile("src/inputs_week-2/t2-4b.txt");
        //reader.loadFile("src/inputs_week-3/t3-2ab.txt");

        reader.readFile();

        Program p = new Program(reader.getWorldSize(), 800, 100);
        World w = p.getWorld();

/*
Program p = new Program(20, 800, 100);
        World w = p.getWorld();


        for (int i = 0; i < 30; i++) {
        placement.placeRandomly(w, new Grass());
        }

        for (int i = 0; i < 8; i++) {
        placement.placeRandomly(w, new Bush());
        placement.placeRandomly(w, new Wolf());
        placement.placeRandomly(w, new Rabbit());
        placement.placeRandomly(w, new Rabbit());
        }

Placement placement = new Placement();
        for (int i = 0; i < 4; i++) {
            placement.placeRandomly(w, new Bear());
        }
 */

        reader.Execute(w);
        p.show();

    }
}