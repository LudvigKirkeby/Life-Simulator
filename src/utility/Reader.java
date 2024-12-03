package utility;

import itumulator.world.World;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import animals.*;
import misc.*;

class EntityData { //data storage class. For internal Reader use only
    private final String type;
    private final ArrayList<Integer> data; // first value (index 0) is always the amount
    public EntityData(String type, ArrayList<Integer> data) {
        this.type = type;
        this.data = data;
    }
    public String getEntityName() {
        return type;
    }
    public ArrayList<Integer> getEntityData() {
        return data;
    }
    public int getEntityDataValue(int n) {
        return data.get(n);
    }
}

public class Reader {
    private File file;
    private int worldSize;
    private List<EntityData> entities; // List of read entities

    public Reader(File file) { //constructor that has file from start
        this();
        this.file = file;
    }
    public Reader() { //empty generic instantiation constructor
        entities = new ArrayList<EntityData>();
        //instantiation
    }
    public void loadFile(String file_string) {this.file = new File(file_string);}
    public void readFile() { //still a bit weird, I acknowledge that
        if (file == null) {
            throw new IllegalStateException("File not loaded.");
        }

            try (Scanner scanner = new Scanner(file)) {
                // Read the first line to determine the world size
                if (scanner.hasNextLine()) {
                    worldSize = Integer.parseInt(scanner.nextLine().trim());
                }

                // Read subsequent lines for entities
                Random random = new Random(); // for use in min-max cases
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine().trim();

                    if (line.isEmpty()) {continue;} // Skip empty lines

                    String[] parts = line.split(" +"); // Split the line into the entity name and its value(s)
                    if (parts.length < 2) {throw new IllegalArgumentException("Invalid line format: " + line);} //in the case the actor has no values

                    ArrayList<Integer> data = new ArrayList<>();
                    String entityName = parts[0];
                    String amountPart = parts[1];
                    int amount;

                    if (amountPart.contains("-")) {
                        // Handle range case (min-max)
                        String[] rangeParts = amountPart.split("-");
                        if (rangeParts.length != 2) {throw new IllegalArgumentException("Invalid range format: " + amountPart);}

                        int min = Integer.parseInt(rangeParts[0]);
                        int max = Integer.parseInt(rangeParts[1]);
                        if (min > max) {throw new IllegalArgumentException("Invalid range: min > max in " + amountPart);}

                        amount = random.nextInt(max - min + 1) + min; // Choose a random value within the range
                    } else {
                        // Handle single integer case
                        amount = Integer.parseInt(amountPart);
                    }
                    data.add(amount);
                    if(parts.length > 2) { // bad code, TODO refactor
                        for(int i = 2; i < parts.length; i++) {
                            Pattern pattern = Pattern.compile("[0-9]+]");
                            Matcher matcher = pattern.matcher(parts[i]);
                            while (matcher.find()) {
                                data.add(Integer.parseInt(matcher.group()));
                            }
                        }
                    }
                    entities.add(new EntityData(entityName, data));
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException("File not found: " + file.getPath(), e);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number format in file.", e);
            }
    }

    public int getWorldSize() {
        return worldSize;
    }

    public void Execute(World w) {
        Placement placement = new Placement();
        for (EntityData entity : entities) {
            String entityName = entity.getEntityName();
            int amount = entity.getEntityDataValue(0);
            ArrayList<Integer> data = entity.getEntityData();
            switch (entityName) {
                case "rabbit":
                    for(int i = 0; i<amount; i++) {
                        placement.placeRandomly(w, new Rabbit());
                    }
                break;

                case "bear":
                    for(int i = 0; i<amount; i++) {
                        if(data.size() == 1) {
                            placement.placeRandomly(w, new Bear());
                        } else if (data.size() == 3) {
                            placement.placeRandomly(w, new Bear(data.get(1), data.get(2)));
                        }
                    }
                break;

                case "wolf":
                    AnimalPack pack = new AnimalPack(Wolf.class);
                    for(int i = 0; i<amount; i++) {
                        placement.placeRandomly(w, new Wolf(pack));
                    }
                break;

                case "burrow":
                    for(int i = 0; i<amount; i++) {
                        placement.placeRandomly(w, new Burrow());
                    }
                break;

                case "grass":
                    for(int i = 0; i<amount; i++) {
                        placement.placeRandomly(w, new Grass());
                    }
                break;

                case "carcass":
                    for(int i = 0; i<amount; i++) {
                        placement.placeRandomly(w, new Carcass());
                    }
                break;

                case "carcass fungi":
                    for(int i = 0; i<amount; i++) {
                        placement.placeRandomly(w, new Carcass(true));
                    }
                break;

                case "berry":
                    for(int i = 0; i<amount; i++) {
                        placement.placeRandomly(w, new Bush());
                    }
                break;
            }

            /*try {
                Class<?> clazz = Class.forName(x.substring(0, 1).toUpperCase() + x.substring(1));
                for (int i = 0; i < amount; i++) {
                    Object instance = clazz.getDeclaredConstructor().newInstance();
                    placement.placeRandomly(w, instance);
                }
            } catch(Exception e) {
                System.out.println("Error in converting from String to Class");
                e.printStackTrace();
            }*/
        }
    }

}
