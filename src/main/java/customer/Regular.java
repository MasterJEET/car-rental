package customer;

import edu.cu.ooad.Customer;
import edu.cu.ooad.util.UniqueIDGenerator;

public class Regular extends Customer {
    public Regular() {
        type = Type.REGULAR;
        customerID = UniqueIDGenerator.getInstance().generateUniqueID("REG");
    }
}
