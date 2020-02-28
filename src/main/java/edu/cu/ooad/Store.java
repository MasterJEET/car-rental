package edu.cu.ooad;

import edu.cu.ooad.util.*;
import edu.cu.ooad.util.Observable;
import edu.cu.ooad.util.Observer;

import java.util.*;

public abstract class Store implements Observable {
    private List<Observer> observers = new LinkedList<>();
    private Recorder recorder = new Recorder(this);
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
     * @param car Car to be decorated
     * @param numOfChildSeats required number of child seats
     * @param numOfGPSModules required number of gps modules
     * @param numOfRadioPackages required number of radio packages
     * @return A Car decorated with specified number of options using 'Decorator pattern'
     */
    protected abstract Car decorateCar(Car car,
                                       Integer numOfChildSeats,
                                       Integer numOfGPSModules,
                                       Integer numOfRadioPackages);

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
        for (Integer numOfCar:  integers) {
            List<Car> cars = getNewCars(carTypes.get(i), numOfCar);
            for (Car car:
                 cars) {
                recorder.addCar(car);
            }
            i++;
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

    /**
     * @param customer Customer who wants to rent a car
     * @param numOfDays number of days for which the customer wants to rent a car
     * @param numOfCars number of cars the customer wants
     * @param carTypeList types of cars the customer wants
     * @return transaction ID if customers request is approved
     */
    public String addNewRental(Customer customer,
                           Integer numOfDays,
                           Integer numOfCars,
                           List<Car.Type> carTypeList) {
        List<Integer> list = new LinkedList<>();
        for (int i =0; i<carTypeList.size(); i++) {
            list.add(0);
        }
        return addNewRental(customer, numOfDays, numOfCars, carTypeList, list, list, list);
    }

    public String addNewRental(Customer customer,
                               Integer numOfDays,
                               Integer numOfCars,
                               List<Car.Type> carTypeList,
                               List<Integer> numOfChildSeatsList,
                               List<Integer> numOfGPSModulesList,
                               List<Integer> numOfRadioPackagesList) {
        Transaction transaction = new Transaction();
        transaction.customer = customer;
        transaction.numOfDays = numOfDays;
        transaction.numOfCars = numOfCars;
        transaction.carTypeList = carTypeList;
        transaction.numOfChildSeatsList = numOfChildSeatsList;
        transaction.numOfGPSModulesList = numOfGPSModulesList;
        transaction.numOfRadioPackagesList = numOfRadioPackagesList;

        return addNewRental(transaction);
    }

    public String addNewRental(Transaction transaction) {
        String transactionID = recorder.addNewRental(transaction);
        if( transactionID == null ) {
            System.err.println(transaction.msg);
            return null;
        }
        return transactionID;
    }

    public boolean completeRental(String transactionID) {
        Transaction transaction = new Transaction();
        transaction.transactionID = transactionID;
        if( !recorder.completeRental(transaction) ) {
            System.err.println(transaction.msg);
            return false;
        }
        return true;
    }

    public void startNewDay() {
        recorder.increaseDayNumber();
    }

    public void endDay() {
        recorder.setAction(Recorder.Action.GENERATE_DAILY_REPORT);
        notifyObservers(recorder);
    }

    public void generateOverallStatus() {
        recorder.setAction(Recorder.Action.GENERATE_OVERALL_STATUS);
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

    public void addCarOfType(Car.Type carType) {
        Car car = getNewCar(carType);
        recorder.addCar(car);
        maxNumOfCars++;
    }

    public Report getReportForDay(Integer dayNumber) {
        return recorder.getReportForDay(dayNumber);
    }

    public Report getOverallStatusReport() {
        return recorder.getOverallStatusReport();
    }

    public Map<Customer.Type, Recorder.CusTypeLimit> getCustomerTypeLimitMap() {
        return recorder.getCustomerTypeLimitMap();
    }

    public Map<CarOption.OptionType, Integer> getOptionTypeMaxLimitMap() {
        return recorder.getOptionTypeMaxLimitMap();
    }

    public Car getCar() {
        return recorder.getCar();
    }

    /**
     * @return A customer with active rental
     */
    public Customer getActiveCustomer() {
        return recorder.getActiveCustomer();
    }
}
