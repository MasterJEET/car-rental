package main.java.edu.cu.ooad;

import main.java.edu.cu.ooad.util.*;

import java.util.LinkedList;
import java.util.List;

public abstract class Store implements Observable {
    private List<Observer> observers = new LinkedList<>();
    private Recorder recorder = new Recorder();

    protected Store() {
        initialize();
    }

    /**
     * @param carType : Concrete Car 'type' of object that needs to be created
     * @return Concrete Car object of type 'carType'
     *
     * This method is used to get new Cars that need to be added to the inventory
     */
    protected abstract Car getNewCar(CarType carType);

    /**
     * Creates required number of Cars and add to recorder, any other initializations
     */
    private void initialize() {
        //TODO: Complete the initialization
        recorder.addCar(getNewCar(CarType.ECONOMY));
        recorder.addCar(getNewCar(CarType.LUXURY));
    }

    @Override
    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(Object object) {
        if (observers == null) {
            return;
        }
        for (Observer observer: observers) {
            observer.update(object);
        }
    }

    public boolean startRent(CarType carType, Customer customer) {
        StringBuffer errMsg = new StringBuffer();
        if( !recorder.addRecord(carType, customer, errMsg) ) {
            System.err.println(errMsg);
            return false;
        }
        return true;
    }

    //TODO: Finish rent should be done automatically either by Store or the Recorder
    /*
    public void finishRent(String transactionID) {
        StringBuffer errMsg = new StringBuffer();
        if( !recorder.updateRecord(transactionID, Recorder.RentalStatus.COMPLETE, errMsg) ) {
            System.err.println(errMsg);
        }
    }*/

    public void startNewDay() {
        recorder.increaseDayNumber();
        recorder.setAction(Recorder.Action.GENERATE_DAILY_REPORT);
        notifyObservers(recorder);
    }

    public Integer getDayNumber() {
        return recorder.getDayNumber();
    }
}
