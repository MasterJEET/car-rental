package caroption;

import edu.cu.ooad.Car;
import edu.cu.ooad.CarOption;

public class GPSModule extends CarOption {

    public GPSModule(Car car) {
        //TODO: Decide on option price
        super(car, 15.50);
    }

    @Override
    public Double getRentalCost(Integer numOfDays) {
        return optionPrice + car.getRentalCost(numOfDays);
    }
}