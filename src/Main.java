import java.awt.Color;

import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;
import misc.*;
import animals.*;
import utility.Placement;
import utility.Reader;

import java.util.HashMap;

public class Main {

    public static void main(String[] args) {

        /*
        Reader reader = new Reader();
        reader.loadFile("src/inputs_week-3/t3-2ab.txt");
        reader.readFile();

         */

        Placement placement = new Placement();

        //reader.getWorldSize()
        Program p = new Program(2, 800, 10);
        World w = p.getWorld();
        placement.placeRandomly(w, new Carcass(true));
        placement.placeRandomly(w, new Carcass());
        placement.placeRandomly(w, new Carcass());
        placement.placeRandomly(w, new Carcass());
        //reader.Execute(w);
        p.show();

    }
}