package animals;

import itumulator.world.Location;

import java.util.ArrayList;
import java.util.List;

public class AnimalPack {
    List<Animal> animals;
    Class<?> type;
    Location center;

    public AnimalPack() {
        animals = new ArrayList<>();
    }

    public AnimalPack(Class<?> type) {
        animals = new ArrayList<>();
        this.type = type;
    }

    public void addAnimal(Animal animal) {
        if(type != null && !type.isInstance(animal)) return;
        animals.add(animal);
    }

    public void removeAnimal(Animal animal) {
        animals.remove(animal);
    }

    public boolean containsAnimal(Animal animal) {
        return animals.contains(animal);
    }

    public void setType(Class<?> type) {
        this.type = type;
        if(type == null) return;
        for(int i = 0; i<animals.size(); i++) {
            if(!type.isInstance(animals.get(i))) {
                animals.remove(i);
                i--;
            }
        }
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
}
