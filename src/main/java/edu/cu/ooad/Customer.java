package edu.cu.ooad;

public abstract class Customer {
    public enum Type {
        DEFAULT,
        CASUAL,
        REGULAR,
        BUSINESS
    }

    protected Type type = Type.DEFAULT;

    /**
     * Unique ID associated with each customer
     */
    protected String customerID;

    /**
     * The number of Cars  the customer will rent
     */
    protected Integer numOfCars = 0;

    /**
     * The number of Days the customer will rent the car for
     */
    protected Integer numOfDays = 0;

    //TODO: Check for allowed number of Cars and Days should be in BusinessRule
    protected Customer(Integer numOfCars, Integer numOfDays) {
        this.numOfCars = numOfCars;
        this.numOfDays = numOfDays;
    }

    public Integer getNumOfDays() {
        return numOfDays;
    }

    public void setNumOfDays(Integer numOfDays) {
        this.numOfDays = numOfDays;
    }

    public Integer getNumOfCars() {
        return numOfCars;
    }

    public void setNumOfCars(Integer numOfCars) {
        this.numOfCars = numOfCars;
    }

    public String getCustomerID() {
        return customerID;
    }

    public Type getType() {
        return type;
    }
}
