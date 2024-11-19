import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

import java.awt.*;
import java.util.List;

public class TesterBester extends Animal implements DynamicDisplayInformationProvider {
    @Override
    public void act(World world) {
        List<Location> follow_path = path(world, new Location(5,5));
        if(follow_path.size()<2) return;
        world.move(this, follow_path.get(1));
    }

    @Override
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.BLUE);
    }
}
