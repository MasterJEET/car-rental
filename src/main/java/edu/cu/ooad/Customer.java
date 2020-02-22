package edu.cu.ooad;

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

    //TODO: Check for allowed number of Cars and Days should be in BusinessRule
    protected Customer(Integer minNumDays,
                       Integer maxNumDays,
                       Integer minNumCars,
                       Integer maxNumCars,
                       Integer numCarsRequested,
                       Integer numDaysRequested) {
        this.minNumDays = minNumDays;
        this.maxNumDays = maxNumDays;
        this.minNumCars = minNumCars;
        this.maxNumCars = maxNumCars;

        if(numCarsRequested < minNumCars) {
            this.numCarsRequested = minNumCars;
        }
        else if (numCarsRequested > maxNumCars) {
            this.numCarsRequested = maxNumCars;
        }
        else {
            this.numCarsRequested = numCarsRequested;
        }

        if(numDaysRequested < minNumDays) {
            this.numDaysRequested = minNumDays;
        }
        else if (numDaysRequested > maxNumDays) {
            this.numDaysRequested = maxNumDays;
        }
        else {
            this.numDaysRequested = numDaysRequested;
        }
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
