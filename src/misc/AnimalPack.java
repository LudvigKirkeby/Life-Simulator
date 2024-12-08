package misc;

import animals.Animal;
import itumulator.world.Location;
import itumulator.world.World;

import java.util.HashSet;
import java.util.Set;

public class AnimalPack {
    Set<Animal> animals;
    Class<?> type;
    Location center;

    /**
     * Creates animals as a HashSet.
     */
    public AnimalPack() {
        animals = new HashSet<>();
    }

    /**
     * Creates animals as a HashSet and sets type of this pack to type-parameter.
     * @param type Type of this pack
     */
    public AnimalPack(Class<?> type) {
        this();
        this.type = type;
    }

    /**
     * Adds Animal-object to animals field if the supplied animal fits this packs type.
     * @param animal Animal-object to add
     */
    public void add(Animal animal) {
        if(type != null && !type.isInstance(animal)) return;
        animals.add(animal);
    }

    /**
     * Removes animal from animals field of this pack.
     * @param animal Animal-object to remove.
     */
    public void remove(Animal animal) {
        animals.remove(animal);
    }

    /**
     * @param animal Animal-object to check for
     * @return whether field animals on this pack contains animal
     */
    public boolean contains(Animal animal) {
        return animals.contains(animal);
    }

    /**
     * Changes type of this pack. If any Animals that are not an instance of the new type are in the array then they are removed.
     * @param type New type that this pack should accept
     */
    public void setType(Class<?> type) {
        this.type = type;
        if(type == null) return;
        Set<Animal> new_animals = new HashSet<>();
        for(Animal animal : animals) {
            if(type.isInstance(animal)) new_animals.add(animal);
        }
        animals = new_animals;
    }

    /**
     * Removes all animals in this pack that aren't in the supplied world.
     * @param world World that this pack is in
     */
    public void clean(World world) {
        Set<Animal> remaining_animals = new HashSet<>();
        for(Animal animal : animals) {
            if(world.contains(animal) && world.isOnTile(animal)) remaining_animals.add(animal);
        }
        animals = remaining_animals;
    }

    /**
     * @return type field of pack
     */
    public Class<?> getType() {
        return type;
    }

    /**
     * Sets center of this pack
     * @param center New center Location for this pack
     */
    public void setCenter(Location center) {
        this.center = center;
    }

    /**
     * @return this packs center
     */
    public Location getCenter() {
        return center;
    }

    /**
     * @return size of set animals
     */
    public int size() {
        return animals.size();
    }

    /**
     * @return animals field
     */
    public Set<Animal> getAnimals() {
        return animals;
    }
}
