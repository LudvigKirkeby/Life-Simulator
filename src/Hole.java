import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.awt.Color;
import java.util.*;

public class Hole implements NonBlocking, DynamicDisplayInformationProvider {
    Rabbit rabbit;
    TunnelNetwork network;

    Hole(Rabbit r) {
        this.rabbit = r;
    }

    @Override
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.BLACK, "hole");
    }
}
