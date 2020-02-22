package cartype;

import edu.cu.ooad.Car;
import edu.cu.ooad.CarType;
import edu.cu.ooad.util.UniqueIDGenerator;

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
