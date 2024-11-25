import itumulator.world.World;

public class Wolf extends Animal {
    AnimalPack pack;

    Wolf() {
        pack = new AnimalPack();
    }

    Wolf(AnimalPack pack) {
        this.pack = pack;
    }

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
            return 4;
        return 2;
    }
}
