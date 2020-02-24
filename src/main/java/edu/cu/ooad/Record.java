package edu.cu.ooad;

/**
 * Used for sharing information across the system
 */
public class Record {
    public CarType carType = CarType.DEFAULT;
    public Customer customer = null;
    public StringBuffer msg = new StringBuffer();
    public String transactionID = null;
    public Integer numOfCars = 0;
    public Integer numOfDays = 0;
    public Integer numOfChildSeats = 0;
    public Integer numOfGPSModules = 0;
    public Integer numOfRadioPackages = 0;
}
