package animals;

import java.util.ArrayList;
import java.util.List;

public class AnimalPack {
    List<Animal> animals = new ArrayList<Animal>();
    Class<?> type;

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
}
