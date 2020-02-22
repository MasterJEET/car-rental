package customer;

import edu.cu.ooad.Customer;
import edu.cu.ooad.util.UniqueIDGenerator;

public class Business extends Customer {
    public Business(Integer numOfCars, Integer numOfDays) {
        super(7, 7, 3, 3, numOfCars, numOfDays);
        customerID = UniqueIDGenerator.getInstance().generateUniqueID("BZN");
    }
}
