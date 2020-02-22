package main.java;

import main.java.customer.Business;
import main.java.edu.cu.ooad.CarType;
import main.java.edu.cu.ooad.Customer;
import main.java.edu.cu.ooad.Summarizer;

public class Main {
    public static void main(String[] args) {
        CarRental carRental = new CarRental();
        Summarizer summarizer = new Summarizer(carRental);
        Customer business = new Business();

        carRental.startNewDay();
        carRental.startRent(CarType.LUXURY, business);
        carRental.startRent(CarType.SUV, business);
    }
}
