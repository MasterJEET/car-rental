package edu.cu.ooad;

import edu.cu.ooad.util.UniqueIDGenerator;
import rules.BusinessRule;

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
        public String transactionID;
        public RentalStatus rentalStatus;
        public Data(CarType carType, Customer customer, StringBuffer msg, String transactionID, RentalStatus rentalStatus) {
            this.carType = carType;
            this.customer = customer;
            this.msg = msg;
            this.transactionID = transactionID;
            this.rentalStatus = rentalStatus;
        }
        Data() {
            this(null, null, null, null, null);
        }
    }

    public class Transaction {
        public String transactionID;
        public Car car;
        public Customer customer;
        public RentalStatus rentalStatus;
        public Integer numOfCars;

        /**
         * The number of days the Car has been rented
         */
        public Integer numOfDays;

        /**
         * The day on which the current transaction took place
         */
        public Integer dayNumber;
        Transaction(String transactionID,
                    Car car,
                    Customer customer,
                    RentalStatus rentalStatus,
                    Integer numOfCars,
                    Integer numOfDays,
                    Integer dayNumber) {
            this.transactionID = transactionID;
            this.car = car;
            this.customer = customer;
            this.rentalStatus = rentalStatus;
            this.numOfCars = numOfCars;
            this.numOfDays = numOfDays;
            this.dayNumber = dayNumber;
        }
    }

    public class Limit {
        public Integer minNumOfCars;
        public Integer maxNumOfCars;
        public Integer minNumOfDays;
        public Integer maxNumOfDays;
        Limit(Integer minNumOfCars, Integer maxNumOfCars, Integer minNumOfDays, Integer maxNumOfDays) {
            this.maxNumOfCars = maxNumOfCars;
            this.minNumOfCars = minNumOfCars;
            this.minNumOfDays = minNumOfDays;
            this.maxNumOfDays = maxNumOfDays;
        }
        Limit() {
            this(0,0,0,0);
        }
    }

    private Data data;

    /**
     * All the validation is done by business rule
     */
    private Rule rule = new BusinessRule(this);

    /**
     * Key: Customer type
     * Value: Limit object storing maximum and minimum limits for a customer type
     */
    private Map<Customer.Type, Limit> customerTypeLimitMap = new HashMap<>();

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

    public Recorder() {
        Limit casual = new Limit(1,1,1,3);
        Limit regular = new Limit(1,3,3,5);
        Limit business = new Limit(3,3,7,7);

        customerTypeLimitMap.put(Customer.Type.CASUAL, casual);
        customerTypeLimitMap.put(Customer.Type.REGULAR, regular);
        customerTypeLimitMap.put(Customer.Type.BUSINESS, business);
    }

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

    public String addNewRental(CarType carType,
                                Customer customer,
                                StringBuffer errMsg) {
        //TODO: Move the validation to somewhere else, may be new class BusinessRule
        data = new Data(carType, customer, errMsg, null, null);
        if(!rule.validate(BusinessRule.Validation.ADD_NEW_RENTAL))
        {
            return null;
        }

        String transactionID = UniqueIDGenerator.getInstance().generateUniqueID("TRN");
        Car car = getCarOfType(carType);

        Transaction transaction = new Transaction(
                transactionID,
                car,
                customer,
                RentalStatus.ACTIVE,
                customer.getNumOfCars(),
                customer.getNumOfDays(),
                dayNumber
        );
        tidTransactionMap.put(transactionID, transaction);

        addToCIDActiveTIDListMap(customer.getCustomerID(), transactionID, errMsg);
        addToRentalStatusTIDListMap(RentalStatus.ACTIVE, transactionID, errMsg);
        addToDayNumTIDListMap(dayNumber, transactionID, errMsg);
        removeFromCarTypeAvailableLPLListMap(car.getType(), car.getLicensePlateNumber());
        return transactionID;
    }

    public boolean completeRental(String transactionID,
                                  RentalStatus newStatus,
                                  StringBuffer errMsg) {
        data = new Data(null, null, errMsg, transactionID, newStatus);
        if (!rule.validate(BusinessRule.Validation.COMPLETE_RENTAL)) {
            return false;
        }

        if (newStatus != RentalStatus.ACTIVE) {
            Customer customer = tidTransactionMap.get(transactionID).customer;
            cidActiveTIDListMap.get( customer.getCustomerID() ).remove(transactionID);
        }
        if(newStatus == RentalStatus.COMPLETE) {
            Car car = tidTransactionMap.get(transactionID).car;
            addToCarTypeAvailableLPLListMap(car.getType(), car.getLicensePlateNumber());
        }
        tidTransactionMap.get(transactionID).rentalStatus = newStatus;
        rentalStatusTIDListMap.get( tidTransactionMap.get(transactionID).rentalStatus ).remove(transactionID);
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
        addToCarTypeAvailableLPLListMap(car.getType(), car.getLicensePlateNumber());
    }

    public void removeCar(Car car) {
        //TODO: Check if you need it
    }

    public void removeCar(String lpl) {
        //TODO: Check if you need it
    }

    //TODO: Add code to update inventory as the Customers return the Cars
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
                    .forEach(lpl -> {
                        list.add(lplCarMap.get(lpl));
                    });
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

    /**
     * @param carType Car Type
     * @return Number of Cars of type 'carType' available for rent
     */
    public Integer getNumOfCarOfType(CarType carType) {
        List<String> availableLPLList = carTypeAvailableLPLListMap.get(carType);
        if (availableLPLList == null) {
            return 0;
        }
        return availableLPLList.size();
    }

    public Data getData() {
        return data;
    }

    public Integer getNumOfCarsRentedByCustomer(Customer customer) {
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

    public void setMinCarLimitForCustomerType(Customer.Type customerType, Integer minNumOfCars) {
        customerTypeLimitMap.get(customerType).minNumOfCars = minNumOfCars;
    }
    public void setMaxCarLimitForCustomerType(Customer.Type customerType, Integer maxNumOfCars) {
        customerTypeLimitMap.get(customerType).maxNumOfCars = maxNumOfCars;
    }
    public void setMinDayLimitForCustomerType(Customer.Type customerType, Integer minNumOfDays) {
        customerTypeLimitMap.get(customerType).minNumOfDays = minNumOfDays;
    }
    public void setMaxDayLimitForCustomerType(Customer.Type customerType, Integer maxNumOfDays) {
        customerTypeLimitMap.get(customerType).maxNumOfDays = maxNumOfDays;
    }
    public Integer getMinCarLimitForCustomerType(Customer.Type customerType) {
        return customerTypeLimitMap.get(customerType).minNumOfCars;
    }
    public Integer getMaxCarLimitForCustomerType(Customer.Type customerType) {
        return customerTypeLimitMap.get(customerType).maxNumOfCars;
    }
    public Integer getMinDayLimitForCustomerType(Customer.Type customerType) {
        return customerTypeLimitMap.get(customerType).minNumOfDays;
    }
    public Integer getMaxDayLimitForCustomerType(Customer.Type customerType) {
        return customerTypeLimitMap.get(customerType).maxNumOfDays;
    }

    public Transaction getTransactionFromTID(String tid) {
        return tidTransactionMap.get(tid);
    }
}
