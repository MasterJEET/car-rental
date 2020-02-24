package cartype;

import edu.cu.ooad.Car;
import edu.cu.ooad.util.UniqueIDGenerator;

public class Standard extends Car {
    public Standard() {
        super(Type.STANDARD, UniqueIDGenerator.getInstance().generateUniqueID("STD"), 25.00);
    }

    @Override
    public Double getRentalCost(Integer numOfDays) {
        return pricePerDay*numOfDays;
    }
}
