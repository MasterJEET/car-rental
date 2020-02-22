package main.java.edu.cu.ooad;

import java.util.concurrent.ThreadLocalRandom;

public abstract class Customer {
    /**
     * Unique ID associated with each customer
     */
    protected String customerID;

    /**
     * Minimum number of days the customer can rent the Car for
     */
    protected Integer minNumDays;

    /**
     * Maximum number of days the customer can rent the Car for
     */
    protected Integer maxNumDays;

    /**
     * Minimum number of Cars the customer can rent
     */
    protected Integer minNumCars;

    /**
     * Maximum number of Cars the customer can rent
     */
    protected Integer maxNumCars;

    /**
     * The number of Cars requested  for rent by the customer
     */
    protected Integer numCarsRequested;

    /**
     * The number of Days the customer requested for the rent
     */
    protected Integer numDaysRequested;

    protected Customer(Integer minNumDays,
                       Integer maxNumDays,
                       Integer minNumCars,
                       Integer maxNumCars) {
        this.minNumDays = minNumDays;
        this.maxNumDays = maxNumDays;
        this.minNumCars = minNumCars;
        this.maxNumCars = maxNumCars;

        numDaysRequested = ThreadLocalRandom.current().nextInt(minNumDays, maxNumDays + 1);
        numCarsRequested = ThreadLocalRandom.current().nextInt(minNumCars, maxNumCars + 1);
    }

    public Integer getNumDaysRequested() {
        return numDaysRequested;
    }

    public Integer getNumCarsRequested() {
        return numCarsRequested;
    }

    public String getCustomerID() {
        return customerID;
    }
}
