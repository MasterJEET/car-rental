package main.java.edu.cu.ooad.util;

import main.java.edu.cu.ooad.util.Observer;

public interface Observable {
    void registerObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers(Object object);
}
