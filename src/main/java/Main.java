import customer.Casual;
import edu.cu.ooad.Car;
import edu.cu.ooad.Customer;
import edu.cu.ooad.Summarizer;
import edu.cu.ooad.util.Transaction;
import store.CarRental;

import java.util.LinkedList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        CarRental carRental = new CarRental();
        Summarizer summarizer = new Summarizer(carRental);
        Customer casual = new Casual();
        Transaction transactionBus = new Transaction();

        List<Car.Type> carTypeList = new LinkedList<Car.Type>(); carTypeList.add(Car.Type.LUXURY);
        List<Integer> numSeatList = new LinkedList<Integer>(); numSeatList.add(0);
        List<Integer> numGPSList = new LinkedList<Integer>(); numGPSList.add(0);
        List<Integer> numRadioList = new LinkedList<Integer>(); numRadioList.add(0);
        carRental.startNewDay();
        String tid1 = carRental.addNewRental(
                casual,
                1,
                1,
                carTypeList,
                numSeatList,
                numGPSList,
                numRadioList);

        carTypeList.remove(0); carTypeList.add(Car.Type.SUV);

        carRental.startNewDay();
        carRental.completeRental(tid1);

        String tid2 = carRental.addNewRental(
                casual,
                1,
                1,
                carTypeList,
                numSeatList,
                numGPSList,
                numRadioList);
        String tid3 = carRental.addNewRental(
                casual,
                1,
                1,
                carTypeList,
                numSeatList,
                numGPSList,
                numRadioList);
        carRental.endDay();

        carRental.generateOverallStatus();
    }
}
