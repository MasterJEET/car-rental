package main.java.edu.cu.ooad;

import main.java.BusinessRule;
import main.java.edu.cu.ooad.util.UniqueIDGenerator;

import java.util.*;

public class Recorder {
    /**
     * Enumerates actions that can taken on this Recorder
     */
    public enum Action {
        /**
         * Default action means do nothing
         */
        DEFAULT,
        /**
         * This Action specifies that daily status report needs to be generated
         */
        GENERATE_DAILY_REPORT,
        /**
         * This Action specifies that a final overall report needs to be generated
         */
        GENERATE_FINAL_REPORT
    }

    /**
     * Enumerates all possible state of a car rental
     */
    public enum RentalStatus {
        /**
         * Unknown status
         */
        DEFAULT,
        /**
         * Customer has the Cars, rent period is active
         */
        ACTIVE,
        /**
         * Customer has returned the Cars after completion of rental duration
         */
        COMPLETE
    }

    /**
     * Used for passing information to BusinessRule
     */
    public class Data {
        public CarType carType;
        public Customer customer;
        public StringBuffer msg;
        public Data(CarType carType, Customer customer, StringBuffer msg) {
            this.carType = carType;
            this.customer = customer;
            this.msg = msg;
        }
    }

    public class Transaction {
        public String transactionID;
        public Car car;
        public Customer customer;
        public RentalStatus rentalStatus;
        public Integer numOfCars;
        public Integer numOfDays;
        Transaction(String transactionID,
                    Car car,
                    Customer customer,
                    RentalStatus rentalStatus,
                    Integer numOfCars,
                    Integer numOfDays) {
            this.transactionID = transactionID;
            this.car = car;
            this.customer = customer;
            this.rentalStatus = rentalStatus;
            this.numOfCars = numOfCars;
            this.numOfDays = numOfDays;
        }
    }

    private Data data;

    /**
     * All the validation is done by business rule
     */
    private Rule rule = new BusinessRule(this);

    private Integer dayNumber = 0;

    /**
     * Key: License plate number of the Car
     * Value: The Car
     *
     * This container stores all the Cars owned by the store
     */
    private Map<String, Car> lplCarMap = new HashMap<>();

    /**
     * Key: Transaction ID
     * Value: Transaction
     */
    private Map<String, Transaction> tidTransactionMap = new HashMap<>();

    /**
     * Key: rental status
     * Value; List of transaction ID of rental with associated status
     */
    private Map<RentalStatus, List<String>> rentalStatusTIDListMap = new HashMap<>();

    /**
     * Key: customer ID
     * Value: List of active rentals associated with the customer
     */
    private Map<String, List<String>> cidActiveTIDListMap = new HashMap<>();

    /**
     * Key: Day number
     * Value: List of transactions (ID) happened on the particular day
     */
    private Map<Integer, List<String>> dayNumTIDListMap = new HashMap<>();

    /**
     * Key: CarType
     * Value: List of license plate numbers of CarType available for rent
     */
    private Map<CarType, List<String>> carTypeAvailableLPLListMap = new HashMap<>();

    private Integer maxAllowedRental = 2;

    /**
     * This specifies what to be done with this object (Recorder) when passed to an Summarizer
     */
    private Action action = Action.DEFAULT;

    private boolean addToRentalStatusTIDListMap(RentalStatus rentalStatus, String transactionID, StringBuffer errMsg) {
        List<String> tidList = rentalStatusTIDListMap.get(rentalStatus);
        if(tidList == null) {
            rentalStatusTIDListMap.put(
                    RentalStatus.ACTIVE,
                    new LinkedList<>(Collections.singletonList(transactionID))
            );
        }
        else {
            tidList.add(transactionID);
        }
        return true;
    }

    private boolean addToCIDActiveTIDListMap(String customerID, String transactionID, StringBuffer errMsg) {
        List<String> activeTIDList = cidActiveTIDListMap.get(customerID);
        if(activeTIDList == null) {
            cidActiveTIDListMap.put(
                    customerID,
                    new LinkedList<>(Collections.singletonList(transactionID))
            );
        }
        else {
            activeTIDList.add(transactionID);
        }
        return true;
    }

    private boolean addToDayNumTIDListMap(Integer dayNumber, String transactionID, StringBuffer errMsg) {
        List<String> tidInDayList = dayNumTIDListMap.get(dayNumber);
        if(tidInDayList == null) {
            dayNumTIDListMap.put(
                    dayNumber,
                    new LinkedList<>(Collections.singletonList(transactionID))
            );
        }
        else {
            tidInDayList.add(transactionID);
        }
        return true;
    }

    private boolean addToCarTypeAvailableLPLListMap(CarType carType, String lpl) {
        List<String> availableLPLList = carTypeAvailableLPLListMap.get(carType);
        if(availableLPLList == null){
            carTypeAvailableLPLListMap.put(
                    carType,
                    new LinkedList<>(Collections.singletonList(lpl))
            );
        }
        else {
            availableLPLList.add(lpl);
        }
        return true;
    }

    private boolean removeFromCarTypeAvailableLPLListMap(CarType carType, String lpl) {
        List<String> availableLPLList = carTypeAvailableLPLListMap.get(carType);
        if (availableLPLList != null) {
            availableLPLList.remove(lpl);
            if (availableLPLList.isEmpty()) {
                carTypeAvailableLPLListMap.remove(carType);
            }
        }
        return true;
    }

    public boolean addRecord(CarType carType,
                             Customer customer,
                             StringBuffer errMsg) {
        //TODO: Move the validation to somewhere else, may be new class BusinessRule
        data = new Data(carType, customer, errMsg);
        if(!rule.validate(BusinessRule.Validation.ADD_NEW_RENTAL))
        {
            return false;
        }

        String transactionID = UniqueIDGenerator.getInstance().generateUniqueID("TRN");
        Car car = getCarOfType(carType);

        Transaction transaction = new Transaction(
                transactionID,
                car,
                customer,
                RentalStatus.ACTIVE,
                customer.getNumCarsRequested(),
                customer.getNumDaysRequested()
        );
        tidTransactionMap.put(transactionID, transaction);

        addToCIDActiveTIDListMap(customer.getCustomerID(), transactionID, errMsg);
        addToRentalStatusTIDListMap(RentalStatus.ACTIVE, transactionID, errMsg);
        addToDayNumTIDListMap(dayNumber, transactionID, errMsg);
        removeFromCarTypeAvailableLPLListMap(car.getCarType(), car.getLicensePlateNumber());
        return true;
    }

    public boolean updateRecord(String transactionID,
                                RentalStatus newStatus,
                                StringBuffer errMsg) {
        RentalStatus oldStatus = tidTransactionMap.get(transactionID).rentalStatus;
        if(oldStatus == null) {
            errMsg.append("Transaction ID not found in the system: ").append(transactionID);
            return false;
        }

        if(newStatus == oldStatus) {
            //Nothing to update
            return true;
        }

        if (newStatus != RentalStatus.ACTIVE) {
            Customer customer = tidTransactionMap.get(transactionID).customer;
            cidActiveTIDListMap.get( customer.getCustomerID() ).remove(transactionID);
        }
        if(newStatus == RentalStatus.COMPLETE) {
            Car car = tidTransactionMap.get(transactionID).car;
            addToCarTypeAvailableLPLListMap(car.getCarType(), car.getLicensePlateNumber());
        }
        tidTransactionMap.get(transactionID).rentalStatus = newStatus;
        rentalStatusTIDListMap.get(oldStatus).remove(transactionID);
        addToRentalStatusTIDListMap(newStatus, transactionID, errMsg);
        return true;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public Action getAction() {
        return action;
    }

    /**
     * @param car : Car to be added.
     *
     * Adds 'car' to the map which keeps record of Car owned by the Store
     */
    public void addCar(Car car) {
        lplCarMap.put(car.getLicensePlateNumber(), car);
        addToCarTypeAvailableLPLListMap(car.getCarType(), car.getLicensePlateNumber());
    }

    public void removeCar(Car car) {
        //TODO: Check if you need it
    }

    public void removeCar(String lpl) {
        //TODO: Check if you need it
    }

    public void increaseDayNumber() {
        dayNumber++;
    }

    public Integer getDayNumber() {
        return dayNumber;
    }

    /**
     * @param carType : CarType requested
     * @param numOfCar : Number of car of type 'carType' requested
     * @return : 'numOfCar' concrete Cars of type 'carType', null if requested number of Car not available
     */
    public List<Car> getNCarsOfType(CarType carType, Integer numOfCar) {
        List<String> availableLPLList = carTypeAvailableLPLListMap.get(carType);
        if(availableLPLList != null && availableLPLList.size() >= numOfCar) {
            List<Car> list = new LinkedList<>();
            availableLPLList.stream()
                    .limit(numOfCar)
                    .forEach(lpl -> list.add(lplCarMap.get(lpl)));
            return list;
        }
        return null;
    }
    public Car getCarOfType(CarType carType) {
        List<Car> list = getNCarsOfType(carType, 1);
        if(list != null) {
            return list.get(0);
        }
        return null;
    }

    public Data getData() {
        return data;
    }

    public Integer getNumberOfCarsRentedByCustomer(Customer customer) {
        Integer count = 0;
        List<String> list = cidActiveTIDListMap.get(customer.getCustomerID());
        if (list == null) {
            return count;
        }

        for( String tid: list ) {
            count += tidTransactionMap.get(tid).numOfCars;
        }
        return count;
    }
}
