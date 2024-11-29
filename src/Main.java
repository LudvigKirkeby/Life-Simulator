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

        /*Reader reader = new Reader();
        reader.loadFile("src/inputs_week-1/tf1-1.txt");
        reader.readFile();

        Program p = new Program(reader.getWorldSize(), 800, 10);
        World w = p.getWorld();
        reader.Execute(w);*/
        Program p = new Program(3, 800, 100);
        World w = p.getWorld();
        p.show();

        Placement placement = new Placement();
        placement.placeRandomly(w, new Bear(1, 2));
        placement.placeRandomly(w, new Bush());
        placement.placeRandomly(w, new Grass());
        //placement.placeRandomly(w, new Bear(1, 1));

        /*
        AnimalPack pack = new AnimalPack(Wolf.class);
        placement.placeRandomly(w, new Wolf(pack));
        placement.placeRandomly(w, new Wolf(pack));
        placement.placeRandomly(w, new Rabbit());
        placement.placeRandomly(w, new Rabbit());
        placement.placeRandomly(w, new Rabbit());
        placement.placeRandomly(w, new Rabbit());
        placement.placeRandomly(w, new Bush());

        int amount = 10;
        for (int i = 0; i < amount; i++) {
            placement.placeRandomly(w, new Grass());
        }

         */

    }
}