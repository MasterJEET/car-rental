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

    /**
     * Limit placed on particular customer type based on how man cars can they rent and for how many days
     */
    public class CusTypeLimit {
        public Integer minNumOfCars;
        public Integer maxNumOfCars;
        public Integer minNumOfDays;
        public Integer maxNumOfDays;
        public CusTypeLimit(Integer minNumOfCars, Integer maxNumOfCars, Integer minNumOfDays, Integer maxNumOfDays) {
            this.maxNumOfCars = maxNumOfCars;
            this.minNumOfCars = minNumOfCars;
            this.minNumOfDays = minNumOfDays;
            this.maxNumOfDays = maxNumOfDays;
        }
        CusTypeLimit() {
            this(0,0,0,0);
        }
    }

    private Record record;

    /**
     * All the validation is done by business rule
     */
    private Rule rule = new BusinessRule(this);

    private Integer dayNumber = 0;

    /**
     * Key: Customer type
     * Value: Limit object storing maximum and minimum limits (on days and cars) for a customer type
     */
    private Map<Customer.Type, CusTypeLimit> customerTypeLimitMap = new HashMap<>();

    /**
     * Key: Car option type
     * Value: Integer  which is maximum limit on number of additions of given 'option type' (minimum is zero)
     */
    private Map<CarOption.OptionType, Integer> optionTypeMaxLimitMap = new HashMap<>();

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
    private Map<Car.Type, LinkedList<String>> carTypeAvailableLPLListMap = new HashMap<Car.Type, LinkedList<String>>();

    /**
     * This specifies what to be done with this object (Recorder) when passed to an Summarizer
     */
    private Action action = Action.DEFAULT;

    public Recorder() {
        CusTypeLimit casual = new CusTypeLimit(1,1,1,3);
        CusTypeLimit regular = new CusTypeLimit(1,3,3,5);
        CusTypeLimit business = new CusTypeLimit(3,3,7,7);

        customerTypeLimitMap.put(Customer.Type.CASUAL, casual);
        customerTypeLimitMap.put(Customer.Type.REGULAR, regular);
        customerTypeLimitMap.put(Customer.Type.BUSINESS, business);

        optionTypeMaxLimitMap.put(CarOption.OptionType.CHILD_SEAT,4);
        optionTypeMaxLimitMap.put(CarOption.OptionType.GPS_MODULE,1);
        optionTypeMaxLimitMap.put(CarOption.OptionType.RADIO_PACKAGE,1);
    }

    public String addNewRental(Record record) {
        this.record = record;
        if(!rule.validate(BusinessRule.Validation.ADD_NEW_RENTAL))
        {
            return null;
        }

        String transactionID = UniqueIDGenerator.getInstance().generateUniqueID("TRN");
        Car car = getCarOfType(record.carType);

        Transaction transaction = new Transaction(
                transactionID,
                car,
                record.customer,
                RentalStatus.ACTIVE,
                record.numOfCars,
                record.numOfDays,
                dayNumber
        );
        tidTransactionMap.put(transactionID, transaction);

        addToCIDActiveTIDListMap(record.customer.getCustomerID(), transactionID);
        addToRentalStatusTIDListMap(RentalStatus.ACTIVE, transactionID);
        addToDayNumTIDListMap(dayNumber, transactionID);
        removeFromCarTypeAvailableLPLListMap(car.getType(), car.getLicensePlateNumber());
        return transactionID;
    }

    public boolean completeRental(Record record) {
        this.record = record;
        if (!rule.validate(BusinessRule.Validation.COMPLETE_RENTAL)) {
            return false;
        }

        Customer customer = tidTransactionMap.get(record.transactionID).customer;
        removeFromCIDActiveTIDListMap(customer.getCustomerID(), record.transactionID);
        Car car = tidTransactionMap.get(record.transactionID).car;
        addToCarTypeAvailableLPLListMap(car.getType(), car.getLicensePlateNumber());
        removeFromRentalStatusTIDListMap(tidTransactionMap.get(record.transactionID).rentalStatus, record.transactionID);
        addToRentalStatusTIDListMap(RentalStatus.COMPLETE, record.transactionID);
        tidTransactionMap.get(record.transactionID).rentalStatus = RentalStatus.COMPLETE;
        return true;
    }

    private boolean addToRentalStatusTIDListMap(RentalStatus rentalStatus, String transactionID) {
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

    private boolean removeFromRentalStatusTIDListMap(RentalStatus rentalStatus, String transactionID) {
        List<String> tidList = rentalStatusTIDListMap.get(rentalStatus);
        if(tidList != null) {
            tidList.remove(transactionID);
            if(tidList.isEmpty()) {
                rentalStatusTIDListMap.remove(rentalStatus);
            }
        }
        return true;
    }

    private boolean addToCIDActiveTIDListMap(String customerID, String transactionID) {
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

    private boolean removeFromCIDActiveTIDListMap(String customerID, String transactionID) {
        List<String> activeTIDList = cidActiveTIDListMap.get(customerID);
        if(activeTIDList != null) {
            activeTIDList.remove(transactionID);
            if(activeTIDList.isEmpty()) {
                cidActiveTIDListMap.remove(customerID);
            }
        }
        return true;
    }

    private boolean addToDayNumTIDListMap(Integer dayNumber, String transactionID) {
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

    private boolean addToCarTypeAvailableLPLListMap(Car.Type carType, String lpl) {
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

    private boolean removeFromCarTypeAvailableLPLListMap(Car.Type carType, String lpl) {
        List<String> availableLPLList = carTypeAvailableLPLListMap.get(carType);
        if (availableLPLList != null) {
            availableLPLList.remove(lpl);
            if (availableLPLList.isEmpty()) {
                carTypeAvailableLPLListMap.remove(carType);
            }
        }
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
    public List<Car> getNCarsOfType(Car.Type carType, Integer numOfCar) {
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

    public Car getCarOfType(Car.Type carType) {
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
    public Integer getNumOfCarOfType(Car.Type carType) {
        List<String> availableLPLList = carTypeAvailableLPLListMap.get(carType);
        if (availableLPLList == null) {
            return 0;
        }
        return availableLPLList.size();
    }

    public Record getRecord() {
        return record;
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

    /**
     * @param customerType The customer type
     * @return required limit if 'customerType' is present, Integer.MIN_VALUE otherwise
     */
    public Integer getMinCarLimitForCustomerType(Customer.Type customerType) {
        CusTypeLimit limit = customerTypeLimitMap.get(customerType);
        if (limit == null) {
            return Integer.MAX_VALUE;
        }
        return limit.minNumOfCars;
    }

    /**
     * @param customerType The customer type
     * @return required limit if 'customerType' is present, Integer.MAX_VALUE otherwise
     */
    public Integer getMaxCarLimitForCustomerType(Customer.Type customerType) {
        CusTypeLimit limit = customerTypeLimitMap.get(customerType);
        if (limit == null) {
            return Integer.MIN_VALUE;
        }
        return limit.maxNumOfCars;
    }
    public Integer getMinDayLimitForCustomerType(Customer.Type customerType) {
        CusTypeLimit limit = customerTypeLimitMap.get(customerType);
        if (limit == null) {
            return Integer.MAX_VALUE;
        }
        return limit.minNumOfDays;
    }
    public Integer getMaxDayLimitForCustomerType(Customer.Type customerType) {
        CusTypeLimit limit = customerTypeLimitMap.get(customerType);
        if (limit == null) {
            return Integer.MIN_VALUE;
        }
        return limit.maxNumOfDays;
    }

    public void setMaxLimitForOptionType(CarOption.OptionType optionType, Integer limit) {
        optionTypeMaxLimitMap.put(optionType, limit);
    }

    /**
     * @param optionType : Type of option the customer requested
     * @return Maximum limit per car of this option type if 'optionType' is present, Integer.MIN_VALUE otherwise
     */
    public Integer getMaxLimitForOptionType(CarOption.OptionType optionType) {
        Integer limit = optionTypeMaxLimitMap.get(optionType);
        if (limit == null) {
            return Integer.MIN_VALUE;
        }
        return limit;
    }

    public Transaction getTransactionFromTID(String tid) {
        return tidTransactionMap.get(tid);
    }
}
