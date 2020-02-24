package cartype;

import edu.cu.ooad.Car;
import edu.cu.ooad.util.UniqueIDGenerator;

public class Luxury extends Car {

    public Luxury() {
        //TODO: decide on price
        super(Car.Type.LUXURY, UniqueIDGenerator.getInstance().generateUniqueID("LUX"), 100.12);
    }

    @Override
    public Double getRentalCost(Integer numOfDays) {
        return pricePerDay*numOfDays;
    }
}
