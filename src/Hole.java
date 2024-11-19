import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.world.NonBlocking;

import java.awt.*;

public class Hole implements NonBlocking, DynamicDisplayInformationProvider {
    DisplayInformation displayInformation;
    TunnelNetwork network;

    @Override
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.BLACK, "hole");
    }
}
