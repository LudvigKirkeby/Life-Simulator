package misc;

import itumulator.world.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

 public class TunnelNetwork implements Iterable<Burrow> {
    private List<Burrow> burrows;

    public TunnelNetwork() {
        burrows = new ArrayList<>();
    }

     /**
      * Adds a burrow to the tunnelnetwork.
      * @param burrow the Burrow to add to the network
      */
    public void addBurrow(Burrow burrow) {
    burrows.add(burrow);
    }

     /**
      * Removes a burrow from the tunnelnetwork.
      * @param burrow the Burrow to remove from the network
      */
    public void removeBurrow(Burrow burrow) {
        burrows.remove(burrow);
    }

     /**
      * @return A specific burrow from the tunnelnetwork
      * @param index the index of the burrow to get.
      */
    public Burrow getBurrow(int index) {
        return burrows.get(index);
    }

     /**
      * @return The size of the tunnelnetwork.
      */
    public int getSize() {
        return burrows.size();
    }

     /**
      * @return True or false based on whether the tunnelnetwork contains the Burrow.
      * @param burrow The burrow to check for.
      */
    public boolean contains(Burrow burrow) {
        return burrows.contains(burrow);
    }

     /**
      * Removes burrows that are no longer on the map, but still in the world.
      * @param world The world to clean the network in.
      */
    public void clean(World world) {
        for(int i = 0; i < burrows.size(); i++) {
            if(!world.contains(burrows.get(i))) {
                burrows.remove(i);
                i--;
            }
        }
    }

     @Override
     public Iterator<Burrow> iterator() {
         return burrows.iterator();
     }
 }
