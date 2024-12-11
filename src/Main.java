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
        //reader.loadFile("src/inputs_week-2/t2-4b.txt");
        //reader.loadFile("src/inputs_week-3/t3-2ab.txt");

        reader.readFile();

        Program p = new Program(reader.getWorldSize(), 800, 100);
        World w = p.getWorld();

        reader.Execute(w);
        p.show();

    }
}