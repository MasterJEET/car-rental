package main.java;

import main.java.cartype.*;
import main.java.edu.cu.ooad.Car;
import main.java.edu.cu.ooad.CarType;
import main.java.edu.cu.ooad.Recorder;
import main.java.edu.cu.ooad.Store;
import main.java.edu.cu.ooad.util.UniqueIDGenerator;

public class CarRental extends Store {

    /**
     * @param carType: Type of the car to be created
     * @return Concrete object of carType
     *
     * This method demonstrates the 'Factory method pattern'
     */
    @Override
    protected Car getNewCar(CarType carType) {
        // TODO: Complete all cases switch statement
        Car car = null;
        switch (carType) {
            case ECONOMY: {
                car = new Economy();
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
