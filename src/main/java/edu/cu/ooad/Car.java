package edu.cu.ooad;

public abstract class Car {
    public enum Type {
        DEFAULT,
        ECONOMY,
        STANDARD,
        MINIVAN,
        SUV,
        LUXURY,
        INVALID
    }

    private Type type;
    private String licensePlateNumber;
    protected Double pricePerDay;

    protected Car(Type type, String licensePlateNumber, Double pricePerDay) {
        this.type = type;
        this.licensePlateNumber = licensePlateNumber;
        this.pricePerDay = pricePerDay;
    }

    /**
     * @param numOfDays : Number of days the car is being rented
     * @return Total cost to rent the car
     */
    public abstract Double getRentalCost(Integer numOfDays);

    public String getLicensePlateNumber() {
        return licensePlateNumber;
    }

    public Double getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(Double pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public Type getType() {
        return type;
    }
}
