package main.java.caroption;

import main.java.edu.cu.ooad.Car;
import main.java.edu.cu.ooad.CarOption;

public class ChildSeat extends CarOption {

    public ChildSeat(Car car) {
        //TODO: Decide on option price
        super(car, 10.12);
    }

    @Override
    public Double getRentalCost(Integer numOfDays) {
        return optionPrice + car.getRentalCost(numOfDays);
    }

}