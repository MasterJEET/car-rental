import edu.cu.ooad.CarType;
import edu.cu.ooad.Customer;
import edu.cu.ooad.Record;
import edu.cu.ooad.Summarizer;
import customer.Business;

public class Main {
    public static void main(String[] args) {
        CarRental carRental = new CarRental();
        Summarizer summarizer = new Summarizer(carRental);
        Customer business = new Business();
        Record recordBus = new Record();

        carRental.startNewDay();
        carRental.addNewRental(CarType.LUXURY, business, 3, 7);
        carRental.addNewRental(CarType.SUV, business, 3, 7);
    }
}
