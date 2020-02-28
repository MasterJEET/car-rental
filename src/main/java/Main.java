import customer.Business;
import customer.Casual;
import customer.Regular;
import edu.cu.ooad.*;
import edu.cu.ooad.util.Transaction;
import store.CarRental;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

class Simulator {
    private CarRental carRental = new CarRental();
    private Summarizer summarizer = new Summarizer(carRental);
    private Map<Customer.Type, Recorder.CusTypeLimit> customerTypeLimitMap = carRental.getCustomerTypeLimitMap();
    private Map<CarOption.OptionType, Integer> optionTypeMaxLimitMap = carRental.getOptionTypeMaxLimitMap();

    /**
     * Key: The day number on which the specified rental needs to be completed i.e. return the cars
     * Value: The transaction ID of the rental to be completed
     */
    private Map<Integer, List<String>> dayNumTIDListMap = new HashMap<>();
    private List<Car.Type> carTypeList = new ArrayList<>(Arrays.asList(
            Car.Type.ECONOMY,
            Car.Type.STANDARD,
            Car.Type.MINIVAN,
            Car.Type.SUV,
            Car.Type.LUXURY
    ));
    private List<Customer.Type> customerTypeList = new ArrayList<>(Arrays.asList(
            Customer.Type.CASUAL,
            Customer.Type.REGULAR,
            Customer.Type.BUSINESS
    ));

    public Simulator() {
        //Try to initialize with a few rental requests, zeroth day transaction

        //6 Casual customers, 1 car per customer
        makeMinimalTransaction(Customer.Type.CASUAL, 6);

        //4 Regular customers, 1 car per customer
        makeMinimalTransaction(Customer.Type.REGULAR, 4);

        //2 Business customers, 3 cars per customer
        makeMinimalTransaction(Customer.Type.BUSINESS, 2);

        //end the zeroth day
        carRental.endDay();
    }

    /**
     * @param cusType Customer type
     * @param numOfRentals Number of rentals to be added
     *
     * The function adds specified number of rental requests with minimum number of cars(per rental) possible
     */
    private void makeMinimalTransaction(Customer.Type cusType, Integer numOfRentals) {
        for (int i = 0; i < numOfRentals; i++) {
            Transaction transaction = new Transaction();
            transaction.customer = getCustomerOfType(cusType);
            transaction.numOfCars = customerTypeLimitMap.get(cusType).minNumOfCars;
            transaction.numOfDays = getNumOfDays(cusType);

            for (int j = 0; j < transaction.numOfCars; j++) {
                Car car = carRental.getCar();
                transaction.carTypeList.add(car.getType());
                transaction.numOfChildSeatsList.add(getNumOfOptions(CarOption.OptionType.CHILD_SEAT));
                transaction.numOfGPSModulesList.add(getNumOfOptions(CarOption.OptionType.GPS_MODULE));
                transaction.numOfRadioPackagesList.add(getNumOfOptions(CarOption.OptionType.RADIO_PACKAGE));
            }

            String transactionID = carRental.addNewRental(transaction);
            if (null != transactionID) {
                addToDayNumTIDListMap(carRental.getDayNumber()+transaction.numOfDays, transactionID);
            }
        }
    }

    /**
     * This will attempt to make a random rental request with random cars, customers, options etc.
     * Not all requests will be accepted by the system
     */
    private void makeRandomTransaction() {
        //With 5% probability select a customer with active rental, else select a new customer
        Customer customer = null;
        if (Math.random() <= 0.05) {
            customer = carRental.getActiveCustomer();
        }
        else {
            customer = getCustomerOfType(getCustomerType());
        }

        Transaction transaction = new Transaction();
        transaction.customer = customer;
        transaction.numOfCars = getNumOfCars(customer.getType());
        transaction.numOfDays = getNumOfDays(customer.getType());

        for (int j = 0; j < transaction.numOfCars; j++) {
            Car car = carRental.getCar();
            if (car == null) {
                return;
            }
            transaction.carTypeList.add(car.getType());
            transaction.numOfChildSeatsList.add(getNumOfOptions(CarOption.OptionType.CHILD_SEAT));
            transaction.numOfGPSModulesList.add(getNumOfOptions(CarOption.OptionType.GPS_MODULE));
            transaction.numOfRadioPackagesList.add(getNumOfOptions(CarOption.OptionType.RADIO_PACKAGE));
        }

        String transactionID = carRental.addNewRental(transaction);
        if (null != transactionID) {
            addToDayNumTIDListMap(carRental.getDayNumber()+transaction.numOfDays, transactionID);
        }
    }

    public void nextDay() {
        //simply increases the day count
        carRental.startNewDay();

        //complete rental if any
        List<String> tidList = dayNumTIDListMap.get(carRental.getDayNumber());
        if (tidList != null) {
            for (String tid: tidList) {
                carRental.completeRental(tid);
            }
            dayNumTIDListMap.remove(carRental.getDayNumber());
        }

        //start new day, create new rental requests
        Integer maxNumRequests = 5;
        Integer actualNumOfRequests = ThreadLocalRandom.current().nextInt(1,maxNumRequests+1);
        for (int i = 0; i < actualNumOfRequests; i++) {
            makeRandomTransaction();
        }

        //end day
        carRental.endDay();
    }

    /**
     * Retrieve daily status reports generated throughout the simulation
     */
    public void getReportForDay(Integer dayNumber) {
        System.out.println(carRental.getReportForDay(dayNumber));
    }

    public void getOverallStatusReport() {
        carRental.generateOverallStatus();
        System.out.println(carRental.getOverallStatusReport());
    }

    private void addToDayNumTIDListMap(Integer dayNumber, String transactionID) {
        List<String> tidList = dayNumTIDListMap.get(dayNumber);
        if (tidList == null) {
            dayNumTIDListMap.put(dayNumber, new LinkedList<>());
        }
        dayNumTIDListMap.get(dayNumber).add(transactionID);
    }

    private boolean removeFromDayNumTIDListMap(Integer dayNumber, String transactionID) {
        List<String> tidList = dayNumTIDListMap.get(dayNumber);
        if (tidList == null) {
            return false;
        }
        return dayNumTIDListMap.get(dayNumber).remove(transactionID);
    }

    public Customer getCustomerOfType(Customer.Type cusType) {
        switch (cusType) {
            case CASUAL:
                return new Casual();
            case REGULAR:
                return new Regular();
            case BUSINESS:
                return new Business();
            default:
                return null;
        }
    }

    /**
     * @return The customer type, this will be used to make a new rental request
     */
    public Customer.Type getCustomerType() {
        Integer numOfAvailableCars = carRental.getTotalNumOfCars();

        Integer customerTypeListSize;
        // if less than 3 cars available don't allow business customers
        if (numOfAvailableCars < 3) {
            customerTypeListSize = 2;
        }
        else {
            customerTypeListSize = 3;
        }
        return customerTypeList.get(new Random().nextInt(customerTypeListSize));
    }

    /**
     * @return The number of cars to be requested for rental
     */
    public Integer getNumOfCars(Customer.Type customerType) {
        Integer numOfAvailableCars = carRental.getTotalNumOfCars();
        if (numOfAvailableCars == 0) {
            return 0;
        }
        Integer minLimit = customerTypeLimitMap.get(customerType).minNumOfCars;
        Integer maxLimit = Math.min(customerTypeLimitMap.get(customerType).maxNumOfCars, numOfAvailableCars);

        return ThreadLocalRandom.current().nextInt(minLimit, maxLimit+1);
    }

    public Integer getNumOfDays(Customer.Type cusType) {
        Integer minLimit = customerTypeLimitMap.get(cusType).minNumOfDays;
        Integer maxLimit = customerTypeLimitMap.get(cusType).maxNumOfDays;

        return ThreadLocalRandom.current().nextInt(minLimit, maxLimit+1);
    }

    public Integer getNumOfOptions(CarOption.OptionType optionType) {
        return new Random().nextInt(optionTypeMaxLimitMap.get(optionType));
    }

}

public class Main {
    public static void main(String[] args) {
        String outFile = "car-rental-output.txt";
        String errFile = "car-rental-error.txt";

        try {
            System.setOut(new PrintStream(outFile));
            System.setErr(new PrintStream(errFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Simulator simulator = new Simulator();

        int numOfDays = 35;
        for (int i = 0; i < numOfDays; i++) {
            simulator.nextDay();
        }

        for (int day = 0; day <= numOfDays; day++) {
            simulator.getReportForDay(day);
        }
        simulator.getOverallStatusReport();
    }
}
