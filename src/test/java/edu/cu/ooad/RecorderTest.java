package edu.cu.ooad;

import cartype.Economy;
import customer.Regular;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

class RecorderTest {
    private Recorder recorder = new Recorder();
    private Customer regular1 = new Regular(1,4);

    @BeforeEach
    void setUp() {
        StringBuffer errMsg = new StringBuffer();
        Car economy1 = new Economy();
        Car economy2 = new Economy();
        Car economy3 = new Economy();
        Car economy4 = new Economy();

        recorder.addCar(economy1);
        recorder.addCar(economy2);
        recorder.addCar(economy3);
        recorder.addCar(economy4);

        recorder.addNewRental(CarType.ECONOMY, regular1, errMsg);
        recorder.addNewRental(CarType.ECONOMY, regular1, errMsg);
    }

    @Test
    void getNCarsOfType() {
        Assertions.assertEquals(null, recorder.getNCarsOfType(CarType.LUXURY, 1));
        Assertions.assertEquals(null, recorder.getNCarsOfType(CarType.ECONOMY, 3));
        List<Car> list = recorder.getNCarsOfType(CarType.ECONOMY, 2);
        Assertions.assertTrue(list != null);
        Assertions.assertEquals(2, list.size());
        Assertions.assertEquals(CarType.ECONOMY, list.get(0).getType());
    }

    @Test
    void getCarOfType() {
        Assertions.assertEquals(null, recorder.getCarOfType(CarType.MINIVAN));
        Assertions.assertEquals(
                CarType.ECONOMY.toString(),
                recorder.getCarOfType(CarType.ECONOMY).getType().toString()
        );
    }

    @Test
    void getNumOfCarsRentedByCustomer() {
        Assertions.assertEquals(2, recorder.getNumOfCarsRentedByCustomer(regular1));
    }

    @Test
    void addNewRental() {
        StringBuffer errMsg = new StringBuffer();

        //max car limit check
        regular1.setNumOfCars(2);
        Assertions.assertTrue(null == recorder.addNewRental(CarType.ECONOMY, regular1, errMsg));

        //min car limit check
        Customer regular2 = new Regular(0, 4);
        Assertions.assertTrue(null == recorder.addNewRental(CarType.ECONOMY, regular2, errMsg));

        //max day limit check
        regular2.setNumOfDays(6);
        Assertions.assertTrue(null == recorder.addNewRental(CarType.ECONOMY, regular2, errMsg));

        //min day limit check
        regular1.setNumOfDays(-1);
        Assertions.assertTrue(null == recorder.addNewRental(CarType.ECONOMY, regular1, errMsg));
    }

    @Test
    @Disabled
    void completeRental() {
        //TODO: Add test here
    }
}