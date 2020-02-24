package edu.cu.ooad;

import edu.cu.ooad.util.IntWithSum;
import edu.cu.ooad.util.Observable;
import edu.cu.ooad.util.Observer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public abstract class Store implements Observable {
    private List<Observer> observers = new LinkedList<>();
    private Recorder recorder = new Recorder();
    /**
     * The numbers of Cars in inventory, when no car is rented (=maximum number of cars the store has at
     * any given time)
     */
    private Integer maxNumOfCars;

    protected Store() {
        this(24);
    }

    /**
     * @param maxNumOfCars The number of Cars the store should add to its inventory, should be > 10.
     *                     In any case at least 10 cars, 2 of each category are always created
     */
    protected Store(Integer maxNumOfCars) {
        this.maxNumOfCars = maxNumOfCars;
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
        // Add two cars of each category, irrespective of maxNumOfCars specified
        List<Car.Type> carTypes = new ArrayList<>(Arrays.asList(
                Car.Type.ECONOMY,
                Car.Type.STANDARD,
                Car.Type.MINIVAN,
                Car.Type.SUV,
                Car.Type.LUXURY));
        for (Car.Type type:
             carTypes) {
            for (Car car:
                 getNewCars(type,2)) {
                recorder.addCar(car);
            }
        }
        // if maxNumOfCars <= 10, stop creating more create cars instead of raising exceptions
        if (maxNumOfCars <= 10) {
            return;
        }

        // get a list of 5 (number of car types) Integer who sum to 14 (maxNumOfCars - num already added)
        Integer numOfInt = carTypes.size();   //number of car types
        Integer sumOfInt = maxNumOfCars - 10;
        List<Integer> integers = IntWithSum.getIntegersWithSum(numOfInt, sumOfInt);
        int i = 0;
        for (Integer numOfCar:
             integers) {
            List<Car> cars = getNewCars(carTypes.get(i), numOfCar);
            for (Car car:
                 cars) {
                recorder.addCar(car);
            }
        }
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

    public List<Car> getNewCars(Car.Type type, Integer numOfCars) {
        List<Car> cars = new LinkedList<>();
        for(int i=0; i<numOfCars; i++) {
            cars.add(getNewCar(type));
        }
        return cars;
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
    public boolean completeRental(String transactionID) {
        Record record = new Record();
        record.transactionID = transactionID;
        if( !recorder.completeRental(record) ) {
            System.err.println(record.msg);
            return false;
        }
        return true;
    }

    public void startNewDay() {
        recorder.increaseDayNumber();
        recorder.setAction(Recorder.Action.GENERATE_DAILY_REPORT);
        notifyObservers(recorder);
    }

    public Integer getDayNumber() {
        return recorder.getDayNumber();
    }

    /**
     * @return Total number of cars added to the inventory, it's basically the number of cars owned by the store
     */
    public Integer getTotalNumOfCars() {
        return recorder.getTotalNumOfCars();
    }

    public Integer getTotalNumOfCarsOfType(Car.Type carType) {
        return recorder.getTotalNumOfCarsOfType(carType);
    }
}
