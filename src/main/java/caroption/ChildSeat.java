package caroption;

import edu.cu.ooad.Car;
import edu.cu.ooad.CarOption;

public class ChildSeat extends CarOption {
    public ChildSeat(Car car) {
        super(car, 15.0);
    }

    @Override
    public Double getRentalCost(Integer numOfDays) {
        return optionPrice + car.getRentalCost(numOfDays);
    }
}