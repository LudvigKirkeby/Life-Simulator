import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.awt.Color;

public class Burrow implements NonBlocking, DynamicDisplayInformationProvider, Actor {
    Rabbit rabbit;
    private int age;
    TunnelNetwork network;

    Burrow() {
    this.network = new TunnelNetwork();
    network.addBurrow(this);
    age = 0;
    }

    Burrow(TunnelNetwork network) {
        this.network = network;
        network.addBurrow(this);
        age = 0;
    }

    @Override
    public void act(World world) {
        age++;
        if (age >= 100) {
            world.delete(this);
            network.removeHole(this);
        }
    }

    @Override
    public DisplayInformation getInformation() {
        if (age < 60)
            return new DisplayInformation(Color.BLACK, "hole");
        return new DisplayInformation(Color.BLACK,"hole-small");
    }
}
