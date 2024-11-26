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

        Reader reader = new Reader();
        reader.loadFile("src/inputs_week-1/tf1-1.txt");
        reader.readFile();

        Program p = new Program(reader.getWorldSize(), 800, 10);
        World w = p.getWorld();
        reader.Execute(w);

        p.show();


        Placement placement = new Placement();
        int amount = 10;
        for (int i = 0; i < amount; i++) {
            placement.placeRandomly(w, new Grass());
        }

    }
}