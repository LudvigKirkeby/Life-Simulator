import itumulator.world.NonBlocking;
import itumulator.world.World;

public interface Plant extends NonBlocking {

    public void grow(World world);

}
