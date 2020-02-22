package edu.cu.ooad;

import cartype.Economy;
import customer.Regular;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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

        recorder.addRecord(CarType.ECONOMY, regular1, errMsg);
        recorder.addRecord(CarType.ECONOMY, regular1, errMsg);
    }

    @Test
    void getNCarsOfType() {
        Assertions.assertEquals(null, recorder.getNCarsOfType(CarType.LUXURY, 1));
        Assertions.assertEquals(null, recorder.getNCarsOfType(CarType.ECONOMY, 3));
        List<Car> list = recorder.getNCarsOfType(CarType.ECONOMY, 2);
        Assertions.assertTrue(list != null);
        Assertions.assertEquals(2, list.size());
        Assertions.assertEquals(CarType.ECONOMY, list.get(0).getCarType());
    }

    @Test
    void getCarOfType() {
        Assertions.assertEquals(null, recorder.getCarOfType(CarType.MINIVAN));
        Assertions.assertEquals(
                CarType.ECONOMY.toString(),
                recorder.getCarOfType(CarType.ECONOMY).getCarType().toString()
        );
    }

    @Test
    void getNumberOfCarsRentedByCustomer() {
        Assertions.assertEquals(2, recorder.getNumberOfCarsRentedByCustomer(regular1));
    }
}