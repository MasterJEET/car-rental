package customer;

import edu.cu.ooad.Customer;
import edu.cu.ooad.util.UniqueIDGenerator;

public class Business extends Customer {
    public Business() {
        type = Type.BUSINESS;
        customerID = UniqueIDGenerator.getInstance().generateUniqueID("BZN");
    }
}
