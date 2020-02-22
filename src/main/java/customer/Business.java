package main.java.customer;

import main.java.edu.cu.ooad.Customer;
import main.java.edu.cu.ooad.util.UniqueIDGenerator;

public class Business extends Customer {
    public Business() {
        super(7, 7, 3, 3);
        customerID = UniqueIDGenerator.getInstance().generateUniqueID("BZN");
    }
}
