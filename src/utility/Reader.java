package utility;

import itumulator.world.World;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import animals.*;
import misc.*;

public class Reader {
    private File file;
    private int worldSize;
    private HashMap<String, Integer> entities;

    public Reader(File file) { //constructor that has file from start
        this();
        this.file = file;
    }
    public Reader() { //empty generic instantiation constructor
        entities = new HashMap<String, Integer>();
        //instantiation
    }

    public void loadFile(String file_string) {this.file = new File(file_string);}

    public void readFile() {
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
                    if (line.isEmpty()) {
                        continue; // Skip empty lines
                    }

                    // Split the line into the entity name and its value(s)
                    String[] parts = line.split(" ");
                    if (parts.length < 2) {
                        throw new IllegalArgumentException("Invalid line format: " + line); //in the case the actor has no values
                    }

                    String entityName = parts[0];
                    String valuePart = parts[1];

                    if (valuePart.contains("-")) {
                        // Handle range case (min-max)
                        String[] rangeParts = valuePart.split("-");
                        if (rangeParts.length != 2) {
                            throw new IllegalArgumentException("Invalid range format: " + valuePart); //for weird formats
                        }

                        int min = Integer.parseInt(rangeParts[0]);
                        int max = Integer.parseInt(rangeParts[1]);
                        if (min > max) {
                            throw new IllegalArgumentException("Invalid range: min > max in " + valuePart);
                        }

                        // Choose a random value within the range
                        int value = random.nextInt(max - min + 1) + min;
                        entities.put(entityName, value);
                    } else {
                        // Handle single integer case
                        int value = Integer.parseInt(valuePart);
                        entities.put(entityName, value);
                    }
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
        for (String class_name : entities.keySet()) {
            int amount = entities.get(class_name);
            switch (class_name) {
                case "rabbit":
                    for(int i = 0; i<amount; i++) {
                        placement.placeRandomly(w, new Rabbit());
                    }
                break;

                case "bear":
                    for(int i = 0; i<amount; i++) {
                       // placement.placeRandomly(w, new Bear());
                    }
                break;

                case "wolf":
                    for(int i = 0; i<amount; i++) {
                        placement.placeRandomly(w, new Wolf());
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
