package store;

import cartype.*;
import edu.cu.ooad.Car;
import edu.cu.ooad.Store;

public class CarRental extends Store {
    public CarRental() {
        super();
    }

    public CarRental(Integer maxNumOfCar) {
        super(maxNumOfCar);
    }
    /**
     * @param carType : Type of the car to be created
     * @return Concrete object of carType
     *
     * This method demonstrates the 'Factory method pattern'
     */
    @Override
    protected Car getNewCar(Car.Type carType) {
        Car car = null;
        switch (carType) {
            case ECONOMY: {
                car = new Economy();
                break;
            }
            case STANDARD: {
                car = new Standard();
                break;
            }
            case MINIVAN: {
                car = new MiniVan();
                break;
            }
            case SUV: {
                car = new SUV();
                break;
            }
            case LUXURY: {
                car = new Luxury();
                break;
            }
            default: {
                System.err.println("Unknown car type requested: " + carType.toString());
                break;
            }
        }
        return car;
    }
}
