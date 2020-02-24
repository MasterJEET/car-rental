package edu.cu.ooad;

import edu.cu.ooad.util.Observable;
import edu.cu.ooad.util.Observer;

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
    protected abstract Car getNewCar(Car.Type carType);

    /**
     * Creates required number of Cars and add to recorder, any other initializations
     */
    private void initialize() {
        //TODO: Complete the initialization
        recorder.addCar(getNewCar(Car.Type.ECONOMY));
        recorder.addCar(getNewCar(Car.Type.LUXURY));
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

    public String addNewRental(Car.Type carType,
                               Customer customer,
                               Integer numOfCars,
                               Integer numOfDays,
                               Integer numOfChildSeats,
                               Integer numOfGPSModules,
                               Integer numOfRadioPackages) {
        Record record = new Record();
        record.carType = carType;
        record.customer = customer;
        record.numOfCars = numOfCars;
        record.numOfDays = numOfDays;
        record.numOfChildSeats = numOfChildSeats;
        record.numOfGPSModules = numOfGPSModules;
        record.numOfRadioPackages = numOfRadioPackages;

        String transactionID = recorder.addNewRental(record);
        if( transactionID == null ) {
            System.err.println(record.msg);
            return null;
        }
        return transactionID;
    }

    //TODO: Simulator should call finish and system should verify
    /*
    public void completeRental(String transactionID) {
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
