package cartype;

import edu.cu.ooad.Car;
import edu.cu.ooad.util.UniqueIDGenerator;

public class MiniVan extends Car {
    public MiniVan() {
        super(Type.MINIVAN, UniqueIDGenerator.getInstance().generateUniqueID("MVN"), 28.0);
    }

    @Override
    public Double getRentalCost(Integer numOfDays) {
        return pricePerDay*numOfDays;
    }
}
