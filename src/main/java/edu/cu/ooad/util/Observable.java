package edu.cu.ooad.util;

import edu.cu.ooad.util.Observer;

public interface Observable {
    void registerObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers(Object object);
}
