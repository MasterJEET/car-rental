package edu.cu.ooad.util;

import edu.cu.ooad.Car;
import edu.cu.ooad.Customer;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Used for sharing information across the system
 */
public class Transaction {
    public String transactionID = null;
    public Integer dayNumber = 0;
    public RentalStatus rentalStatus = RentalStatus.DEFAULT;
    public Customer customer = null;
    public Integer numOfDays = 0;
    public Integer numOfCars = 0;
    /**
     * List of 'Car.Type's of size 'numOfCars'
     */
    public List<Car.Type> carTypeList = new LinkedList<>();
    /**
     * List of 'Car's of size 'numOfCars'
     */
    public List<Car> carList = new LinkedList<>();
    /**
     * List of Integers of size 'numOfCars' containing number of child seats in each car
     */
    public List<Integer> numOfChildSeatsList = new LinkedList<>();
    /**
     * List of Integers of size 'numOfCars' containing number of gps modules for each car
     */
    public List<Integer> numOfGPSModulesList = new LinkedList<>();
    /**
     * List of Integers of size 'numOfCars' containing number of radio packages for each car
     */
    public List<Integer> numOfRadioPackagesList = new LinkedList<>();

    public StringBuffer msg = new StringBuffer();

    public Transaction(){}

    /**
     * @param trn Transaction object to be copied
     *            This is Copy Constructor
     */
    public Transaction(Transaction trn) {
        this.transactionID = trn.transactionID;
        this.dayNumber = Integer.valueOf(trn.dayNumber);
        this.rentalStatus = trn.rentalStatus;
        this.customer = trn.customer;
        this.numOfDays = Integer.valueOf(trn.numOfDays);
        this.numOfCars = Integer.valueOf(trn.numOfCars);
        this.carTypeList = new LinkedList<>(trn.carTypeList);
        this.carList = new LinkedList<>(trn.carList);
        this.numOfChildSeatsList = new LinkedList<>(trn.numOfChildSeatsList);
        this.numOfGPSModulesList = new LinkedList<>(trn.numOfGPSModulesList);
        this.numOfRadioPackagesList = new LinkedList<>(trn.numOfRadioPackagesList);
        this.msg = new StringBuffer(trn.msg);
    }

    public Double getCost() {
        Double cost = 0.0;
        if (carList != null) {
            for (Car car : carList) {
                cost += car.getRentalCost(numOfDays);
            }
        }
        return cost;
    }

    @Override
    public String toString() {
        StringBuffer display = new StringBuffer();
        display
                .append("Transaction { ")
                .append("tID='").append(transactionID).append("'")
                .append(", dayNumber=").append(String.format("%03d",dayNumber))
                .append(", rentalStatus=").append(rentalStatus)
                .append(", ").append(customer)
                .append(", numOfDays=").append(String.format("%03d",numOfDays))
                .append(", numOfCars=").append(String.format("%03d",numOfCars))
                .append(System.lineSeparator());

        Iterator<Car> itrCar = carList.iterator();
        Iterator<Integer> itrNumSeat = numOfChildSeatsList.iterator();
        Iterator<Integer> itrNumGPS = numOfGPSModulesList.iterator();
        Iterator<Integer> itrNumRadio = numOfRadioPackagesList.iterator();

        while (
                        itrCar.hasNext()
                        && itrNumSeat.hasNext()
                        && itrNumGPS.hasNext()
                        && itrNumRadio.hasNext()
        ) {
            display
                    .append("{")
                    .append(itrCar.next())
                    .append(", numOfChildSeats=").append(String.format("%03d",itrNumSeat.next()))
                    .append(", numOfGPSModules=").append(String.format("%03d",itrNumGPS.next()))
                    .append(", numOfRadioPackages=").append(String.format("%03d",itrNumRadio.next()))
                    .append("} ");
        }

        Double transactionCost = 0.0;
        transactionCost += carList.stream()
                .map(car -> car.getRentalCost(numOfDays))
                .mapToDouble(p->p)
                .sum();

        display
                .append(", rentalCost=").append(String.format("%06.2f",transactionCost))
                .append("}");

        return display.toString();
    }
}
