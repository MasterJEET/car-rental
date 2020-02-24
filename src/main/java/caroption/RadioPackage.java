package caroption;

import edu.cu.ooad.Car;
import edu.cu.ooad.CarOption;

public class RadioPackage extends CarOption {
    public RadioPackage(Car car) {
        super(car, 10.0);
    }

    @Override
    public Double getRentalCost(Integer numOfDays) {
        return optionPrice + car.getRentalCost(numOfDays);
    }
}
