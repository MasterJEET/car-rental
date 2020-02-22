package main.java.cartype;

import main.java.edu.cu.ooad.Car;
import main.java.edu.cu.ooad.CarType;
import main.java.edu.cu.ooad.util.UniqueIDGenerator;

public class Economy extends Car {

    public Economy() {
        //TODO: decide on price
        super(CarType.ECONOMY, UniqueIDGenerator.getInstance().generateUniqueID("ECO"), 11.11);
    }

    @Override
    public Double getRentalCost(Integer numOfDays) {
        return pricePerDay*numOfDays;
    }
}
