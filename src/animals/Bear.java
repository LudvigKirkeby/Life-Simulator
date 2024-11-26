package animals;

import itumulator.executable.DisplayInformation;
import itumulator.world.World;

public class Bear extends Animal {

    @Override
    public void act(World world) {

    }

    @Override
    public boolean getGrownup() {
        return age > 3; // Just set it to a temporary value
    }

    @Override
    public int getFoodValue() {
        if(getGrownup())
            return 5;
        return 2;
    }

    @Override
    public DisplayInformation getInformation() {
        return null;
    }
}
