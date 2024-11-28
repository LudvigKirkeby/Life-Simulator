package animals;

import itumulator.world.Location;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AnimalPack {
    Set<Animal> animals;
    Class<?> type;
    Location center;

    public AnimalPack() {
        animals = new HashSet<>();
    }

    public AnimalPack(Class<?> type) {
        animals = new HashSet<>();
        this.type = type;
    }

    public void add(Animal animal) {
        if(type != null && !type.isInstance(animal)) return;
        animals.add(animal);
    }

    public void remove(Animal animal) {
        animals.remove(animal);
    }

    public boolean contains(Animal animal) {
        return animals.contains(animal);
    }

    public void setType(Class<?> type) {
        this.type = type;
        if(type == null) return;
        Set<Animal> new_animals = new HashSet<>();
        for(Animal animal : animals) {
            if(type.isInstance(animal)) new_animals.add(animal);
        }
        animals = new_animals;
    }

    public Class<?> getType() {
        return type;
    }

    public void setCenter(Location center) {
        this.center = center;
    }

    public Location getCenter() {
        return center;
    }

    public int size() {
        return animals.size();
    }

    public Set<Animal> getAnimals() {
        return animals;
    }
}
