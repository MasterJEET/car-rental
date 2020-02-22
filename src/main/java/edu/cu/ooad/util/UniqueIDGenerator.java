package main.java.edu.cu.ooad.util;

import java.util.HashMap;
import java.util.Map;

/**
 * This class will generate unique IDs to be used in license plate and record (transaction ID)
 */
public class UniqueIDGenerator {
    /**
     * This map stores for each String (representing a type of Object) the next Integer that
     * can be used as unique ID
     */
    private Map<String, Integer> nextID;

    private static UniqueIDGenerator instance = null;

    private UniqueIDGenerator() {
        nextID = new HashMap<>();
    }

    /**
     * @return Singleton object
     *
     * Note that this implementation is not thread safe and is intended to be so as the problem
     * states to design single threaded system
     */
    public static UniqueIDGenerator getInstance() {
        if (instance == null) {
            instance = new UniqueIDGenerator();
        }
        return instance;
    }

    /**
     * @param type It's a String representing a category (or group)
     * @return String, an Integer 'next' appended to 'type'; Integer 'next' is guaranteed to be unique
     * if the function is called with the same 'type'
     *
     * Note that this implementation is not thread safe and is intended to be so as the problem
     * states to design single threaded system
     */
    public String generateUniqueID(String type) {
        return generateUniqueID(type, "%06d");
    }

    /**
     * @return String, an Integer 'next' appended to 'type'; Integer 'next' is guaranteed to be unique
     * if the function is called with the same 'type'
     *
     * Note that this implementation is not thread safe and is intended to be so as the problem
     * states to design single threaded system
     */
    public String generateUniqueID() {
        return generateUniqueID("DEF", "%06d");
    }

    /**
     * @param type It's a String representing a category (or group)
     * @param format Specify formatting to be used while converting Integer to String
     * @return String, an Integer 'next' appended to 'type'; Integer 'next' is guaranteed to be unique
     * if the function is called with the same 'type'
     *
     * Note that this implementation is not thread safe and is intended to be so as the problem
     * states to design single threaded system
     */
    public String generateUniqueID(String type, String format) {
        nextID.putIfAbsent(type, 1);
        Integer next = nextID.get(type);
        nextID.put(type, next+1);
        return type + String.format(format, next);
    }
}
