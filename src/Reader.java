import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

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

    public HashMap<String, Integer> getEntities() {
        return entities;
    }
}
