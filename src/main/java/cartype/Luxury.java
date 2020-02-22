package main.java.cartype;

import main.java.edu.cu.ooad.Car;
import main.java.edu.cu.ooad.CarType;
import main.java.edu.cu.ooad.util.UniqueIDGenerator;

public class Luxury extends Car {

    public Luxury() {
        //TODO: decide on price
        super(CarType.LUXURY, UniqueIDGenerator.getInstance().generateUniqueID("LUX"), 100.12);
    }

    @Override
    public Double getRentalCost(Integer numOfDays) {
        return pricePerDay*numOfDays;
    }
}
