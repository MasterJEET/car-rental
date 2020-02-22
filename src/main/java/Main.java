import edu.cu.ooad.CarType;
import edu.cu.ooad.Customer;
import edu.cu.ooad.Summarizer;
import customer.Business;

public class Main {
    public static void main(String[] args) {
        CarRental carRental = new CarRental();
        Summarizer summarizer = new Summarizer(carRental);
        Customer business = new Business(3,7);

        carRental.startNewDay();
        carRental.startRent(CarType.LUXURY, business);
        carRental.startRent(CarType.SUV, business);
    }
}
