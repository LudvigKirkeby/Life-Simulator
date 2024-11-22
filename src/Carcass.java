import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.World;

import java.awt.*;

public class Carcass implements Edible, Actor, DynamicDisplayInformationProvider {
    private int age;

    Carcass() {
        age = 0;
    }

    public void act(World world) {
        age++;
        if (age >= 50) {
            world.delete(this);
        }
    }

    @Override
    public DisplayInformation getInformation() {
        if (age < 30)
            return new DisplayInformation(Color.BLACK, "carcass");
        return new DisplayInformation(Color.BLACK,"carcass-small");
    }

    public int getFoodValue() { return 1; }

}
