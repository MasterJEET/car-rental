package customer;

import edu.cu.ooad.Customer;
import edu.cu.ooad.util.UniqueIDGenerator;

public class Casual extends Customer {
    public Casual(){
        type = Type.CASUAL;
        customerID = UniqueIDGenerator.getInstance().generateUniqueID("CSL");
    }
}
