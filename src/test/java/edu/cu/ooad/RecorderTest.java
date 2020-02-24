package edu.cu.ooad;

import cartype.Economy;
import customer.Regular;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class RecorderTest {
    private Recorder recorder = new Recorder();
    private Customer regular1 = new Regular();
    private Record record1 = new Record();
    private String tid11 = null;
    private String tid12 = null;

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

        record1.numOfCars=1;
        record1.numOfDays=4;

        record1.carType = CarType.ECONOMY; record1.customer = regular1;

        //1
        recorder.increaseDayNumber();
        tid11 = recorder.addNewRental(record1);

        //2
        recorder.increaseDayNumber();
        tid12 = recorder.addNewRental(record1);
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

        //max car limit check
        Record data1 = new Record();
        data1.carType = CarType.ECONOMY; data1.customer = regular1; data1.numOfCars=2;
        Assertions.assertTrue(null == recorder.addNewRental(data1));

        //min car limit check
        Customer regular2 = new Regular();
        Record record2 = new Record();
        record2.numOfCars = 0;
        record2.numOfDays = 4;

        Record data2 = new Record();
        data2.carType = CarType.ECONOMY; data2.customer = regular2;
        Assertions.assertTrue(null == recorder.addNewRental(data2));

        //max day limit check
        data2.numOfDays = 6;
        Assertions.assertTrue(null == recorder.addNewRental(data2));

        //min day limit check
        data1.numOfDays = -1;
        Assertions.assertTrue(null == recorder.addNewRental(data1));
    }

    @Test
    void completeRental() {
        //3
        recorder.increaseDayNumber();
        Record record = new Record();
        record.transactionID = tid11;

        //Return before due date
        Assertions.assertFalse(recorder.completeRental(record));

        //4
        recorder.increaseDayNumber();
        //5
        recorder.increaseDayNumber();

        //Return on due date
        Assertions.assertTrue(recorder.completeRental(record));

        //No active transaction/rental found (already returned)
        Assertions.assertFalse(recorder.completeRental(record));

        //6
        recorder.increaseDayNumber();
        //7
        recorder.increaseDayNumber();

        //Return after due date
        record.transactionID = tid12;
        Assertions.assertFalse(recorder.completeRental(record));

        //Transaction not found (Invalid transaction ID)
        record.transactionID = "INVALID_TRANSACTION_ID";
        Assertions.assertFalse(recorder.completeRental(record));
    }
}