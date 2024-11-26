package misc;

import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.world.NonBlocking;

import java.awt.*;

public class Cave implements NonBlocking, DynamicDisplayInformationProvider {

    @Override
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.BLACK, "cave");
    }
}
