package main.java.customer;

import main.java.edu.cu.ooad.Customer;
import main.java.edu.cu.ooad.util.UniqueIDGenerator;

public class Regular extends Customer {
    public Regular() {
        super(3, 5, 1, 3);
        customerID = UniqueIDGenerator.getInstance().generateUniqueID("REG");
    }
}
