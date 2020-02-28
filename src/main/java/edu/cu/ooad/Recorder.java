package edu.cu.ooad;

import edu.cu.ooad.util.RentalStatus;
import edu.cu.ooad.util.Report;
import edu.cu.ooad.util.Transaction;
import edu.cu.ooad.util.UniqueIDGenerator;
import rules.BusinessRule;

import java.util.*;
import java.util.stream.Collectors;

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
        GENERATE_OVERALL_STATUS
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

    private Transaction transaction;

    /**
     * All the validation is done by business rule
     */
    private Rule rule = new BusinessRule(this);

    private Store store = null;

    private Integer dayNumber = 0;

    /**
     * The maximum number of Cars that a customer can rent, this is different from per transaction limit.
     *
     * Per transaction limit specifies what's the maximum (or minimum) number of cars that a given
     * customer can rent in one rental request (transaction). But customer can make multiple rental
     * request, the limit below specifies the maximum number of Cars that certain customer can rent
     * considering all rental requests.
     */
    private Integer maxCarLimit = 3;

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
     * Key: The day number
     * Value: The report for the day
     */
    private Map<Integer, Report> dayNumReportMap = new HashMap<>();

    /**
     * To hold the overall status of the system/car rental after the completion of simulation
     */
    private Report overallStatusReport = null;

    /**
     * This specifies what to be done with this object (Recorder) when passed to an Summarizer
     */
    private Action action = Action.DEFAULT;

    private Recorder() {
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

    public Recorder(Store store) {
        this();
        this.store = store;
    }

    public String addNewRental(Transaction transaction) {
        this.transaction = new Transaction(transaction);
        this.transaction.dayNumber = dayNumber;
        this.transaction.transactionID = UniqueIDGenerator.getInstance().generateUniqueID("TRN");
        if(!rule.validate(BusinessRule.Validation.ADD_NEW_RENTAL))
        {
            transaction.msg = this.transaction.msg;
            return null;
        }

        String transactionID = this.transaction.transactionID;
        addToCIDActiveTIDListMap(this.transaction.customer.getCustomerID(), transactionID);
        tidTransactionMap.put(transactionID, this.transaction);
        addToRentalStatusTIDListMap(RentalStatus.ACTIVE, transactionID);
        addToDayNumTIDListMap(dayNumber, transactionID);

        Iterator<Car.Type> itrCarType = this.transaction.carTypeList.iterator();
        Iterator<Integer> itrNumSeats = this.transaction.numOfChildSeatsList.iterator();
        Iterator<Integer> itrNumGPS = this.transaction.numOfGPSModulesList.iterator();
        Iterator<Integer> itrNumRadio = this.transaction.numOfRadioPackagesList.iterator();
        while (itrCarType.hasNext() && itrCarType.hasNext() && itrNumGPS.hasNext() && itrNumRadio.hasNext()) {
            Car car = store.decorateCar(
                    getCarOfType( itrCarType.next() ),
                    itrNumSeats.next(),
                    itrNumGPS.next(),
                    itrNumRadio.next());
            this.transaction.carList.add(car);
            removeFromCarTypeAvailableLPLListMap(car.getType(), car.getLicensePlateNumber());
        }
        this.transaction.rentalStatus = RentalStatus.ACTIVE;
        return transactionID;
    }

    public boolean completeRental(Transaction transaction) {
        //NOTE: Transaction object received will only have the transactionID, nothing else
        this.transaction = new Transaction();
        this.transaction.transactionID = transaction.transactionID;
        if (!rule.validate(BusinessRule.Validation.COMPLETE_RENTAL)) {
            return false;
        }

        String transactionID = this.transaction.transactionID;

        //This seems trivial but it's required to get the relevant details from system
        this.transaction = tidTransactionMap.get(transactionID);

        Customer customer = this.transaction.customer;
        removeFromCIDActiveTIDListMap(customer.getCustomerID(), transactionID);

        for (Car car:
             this.transaction.carList) {
            addToCarTypeAvailableLPLListMap(car.getType(), car.getLicensePlateNumber());
        }
        removeFromRentalStatusTIDListMap(this.transaction.rentalStatus, transactionID);
        addToRentalStatusTIDListMap(RentalStatus.COMPLETE, transactionID);
        tidTransactionMap.get(transactionID).rentalStatus = RentalStatus.COMPLETE;
        return true;
    }

    private boolean addToRentalStatusTIDListMap(RentalStatus rentalStatus, String transactionID) {
        List<String> tidList = rentalStatusTIDListMap.get(rentalStatus);
        if(tidList == null) {
            rentalStatusTIDListMap.put(
                    rentalStatus,
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

    public Car getCar() {
        List<Car.Type> carTypeList = new LinkedList<>(Arrays.asList(
                Car.Type.ECONOMY,
                Car.Type.STANDARD,
                Car.Type.MINIVAN,
                Car.Type.SUV,
                Car.Type.LUXURY
        ));
        List<Car> list = new LinkedList<>();

        for (Car.Type carType: carTypeList) {
            List<String> availableLPLList = carTypeAvailableLPLListMap.get(carType);
            if (availableLPLList != null) {
                for (String lpn: availableLPLList) {
                    list.add(lplCarMap.get(lpn));
                }
            }
        }
        if (list.size() != 0) {
            return list.get(new Random().nextInt(list.size()));
        }
        return null;
    }

    /**
     * @param carType Car Type
     * @return Number of Cars of type 'carType' available for rent
     */
    public Integer getAvailableNumOfCarOfType(Car.Type carType) {
        List<String> availableLPLList = carTypeAvailableLPLListMap.get(carType);
        if (availableLPLList == null) {
            return 0;
        }
        return availableLPLList.size();
    }

    public Transaction getTransaction() {
        return transaction;
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

    /**
     * @return Total number of cars owned by store), counts all cars irrespective of whether it's rented or not
     */
    public Integer getTotalNumOfCars() {
        return lplCarMap.size();
    }

    /**
     * @param carType Car type
     * @return number of Cars of type 'carType' owned by the store
     */
    public Integer getTotalNumOfCarsOfType(Car.Type carType) {
        return Math.toIntExact(lplCarMap.entrySet().stream()
                .map(Map.Entry::getValue)
                .filter(car -> car.getType() == carType)
                .count());
    }

    public List<Transaction> getTransactionsOfStatus(RentalStatus rentalStatus) {
        List<String> tidList = rentalStatusTIDListMap.get(rentalStatus);
        if (tidList == null) {
            return null;
        }
        List<Transaction> transactions = tidList.stream()
                .map(tid -> tidTransactionMap.get(tid))
                .collect(Collectors.toList());
        return transactions;
    }

    /**
     * @return List of cars available for rent
     */
    public List<Car> getAvailableCars() {
        List<Car> cars = new LinkedList<>();
        carTypeAvailableLPLListMap.entrySet().stream()
                .map(Map.Entry::getValue)
                .forEach(lplList -> {
                    for (String lpl: lplList) {
                        cars.add(lplCarMap.get(lpl));
                    }
                });
        return cars;
    }

    public List<Transaction> getTransactionsOfDay() {
        return getTransactionsOfDay(dayNumber);
    }

    public List<Transaction> getTransactionsOfDay(Integer dayNumber) {
        if (dayNumber > this.dayNumber) {
            return null;
        }
        List<String> tidList = dayNumTIDListMap.get(dayNumber);
        if (tidList == null) {
            return null;
        }

        List<Transaction> transactions = tidList.stream()
                .map(tid -> tidTransactionMap.get(tid))
                .collect(Collectors.toList());
        return transactions;
    }

    public Integer getMaxCarLimit() {
        return maxCarLimit;
    }

    public void setMaxCarLimit(Integer maxCarLimit) {
        this.maxCarLimit = maxCarLimit;
    }

    public void addReportForDay(Integer dayNumber, Report report) {
        dayNumReportMap.put(dayNumber, report);
    }

    public Report getReportForDay(Integer dayNumber) {
        return dayNumReportMap.get(dayNumber);
    }

    public void setOverallStatusReport(Report overallStatusReport) {
        this.overallStatusReport = overallStatusReport;
    }

    public Report getOverallStatusReport() {
        return overallStatusReport;
    }

    public Map<Customer.Type, CusTypeLimit> getCustomerTypeLimitMap() {
        return customerTypeLimitMap;
    }

    public Map<CarOption.OptionType, Integer> getOptionTypeMaxLimitMap() {
        return optionTypeMaxLimitMap;
    }

    /**
     * @return Customer with at least one active rental
     */
    public Customer getActiveCustomer() {
        List<String> activeTIDList = new LinkedList<>();
        cidActiveTIDListMap.entrySet().stream()
                .map(entry -> entry.getValue())
                .forEach(tidList -> activeTIDList.addAll(tidList));

        String tid = activeTIDList.get(new Random().nextInt(activeTIDList.size()));
        return tidTransactionMap.get(tid).customer;
    }
}
