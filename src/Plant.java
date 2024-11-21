import itumulator.world.NonBlocking;
import itumulator.world.World;

public interface Plant extends NonBlocking, Edible {

    public void grow(World world);
}
