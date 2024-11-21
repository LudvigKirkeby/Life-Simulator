import java.io.File;
import java.util.HashMap;
import java.util.Map;
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
    public void loadFile(File file) {this.file = file;}
    public void readFile() {
        entities = new HashMap<String, Integer>();
        //Read line one
    }
    public int getWorldSize() {
        return worldSize;
    }

    public HashMap getEntities() {
        return entities;
    }
}
