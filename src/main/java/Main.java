import customer.Business;
import edu.cu.ooad.*;
import store.CarRental;

public class Main {
    public static void main(String[] args) {
        CarRental carRental = new CarRental();
        Summarizer summarizer = new Summarizer(carRental);
        Customer business = new Business();
        Record recordBus = new Record();

        carRental.startNewDay();
        carRental.addNewRental(Car.Type.LUXURY, business, 3, 7, 0, 0, 0);
        carRental.addNewRental(Car.Type.SUV, business, 3, 7, 0, 0, 0);
    }
}
