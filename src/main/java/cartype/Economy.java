package cartype;

import edu.cu.ooad.Car;
import edu.cu.ooad.util.UniqueIDGenerator;

public class Economy extends Car {

    public Economy() {
        //TODO: decide on price
        super(Car.Type.ECONOMY, UniqueIDGenerator.getInstance().generateUniqueID("ECO"), 11.11);
    }

    @Override
    public Double getRentalCost(Integer numOfDays) {
        return pricePerDay*numOfDays;
    }
}
