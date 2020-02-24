import cartype.Economy;
import cartype.Luxury;
import edu.cu.ooad.Car;
import edu.cu.ooad.Store;

public class CarRental extends Store {

    /**
     * @param carType : Type of the car to be created
     * @return Concrete object of carType
     *
     * This method demonstrates the 'Factory method pattern'
     */
    @Override
    protected Car getNewCar(Car.Type carType) {
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
