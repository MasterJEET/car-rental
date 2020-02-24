package cartype;

import edu.cu.ooad.Car;
import edu.cu.ooad.util.UniqueIDGenerator;

public class SUV extends Car {
    public SUV() {
        super(Type.SUV, UniqueIDGenerator.getInstance().generateUniqueID("SUV"), 30.0);
    }

    @Override
    public Double getRentalCost(Integer numOfDays) {
        return pricePerDay*numOfDays;
    }
}
