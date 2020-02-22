package customer;

import edu.cu.ooad.Customer;
import edu.cu.ooad.util.UniqueIDGenerator;

public class Regular extends Customer {
    public Regular(Integer numOfCars, Integer numOfDays) {
        super(3, 5, 1, 3, numOfCars, numOfDays);
        customerID = UniqueIDGenerator.getInstance().generateUniqueID("REG");
    }
}
