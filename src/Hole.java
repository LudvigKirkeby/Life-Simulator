import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.awt.Color;
import java.util.*;

public class Hole implements NonBlocking, DynamicDisplayInformationProvider {
    Rabbit rabbit;
    DisplayInformation displayInformation;
    TunnelNetwork network;

    Hole(Rabbit r, World w) {
        this.rabbit = r;
        getInformation();
        w.setTile(w.getLocation(r), this);
        }

    @Override
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.BLACK, "hole");
    }
}
