package edu.cu.ooad;

import cartype.Economy;
import cartype.Luxury;
import customer.Business;
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

        record1.carType = Car.Type.ECONOMY; record1.customer = regular1;

        //1
        recorder.increaseDayNumber();
        tid11 = recorder.addNewRental(record1);
        if (tid11 == null) {
            System.err.println(record1.msg);
        }

        //2
        recorder.increaseDayNumber();
        tid12 = recorder.addNewRental(record1);
        if (tid12 == null) {
            System.err.println(record1.msg);
        }
    }

    @Test
    void getFromMap() {
        Assertions.assertEquals(1, recorder.getMinCarLimitForCustomerType(Customer.Type.CASUAL));
        Assertions.assertEquals(1, recorder.getMaxCarLimitForCustomerType(Customer.Type.CASUAL));
        Assertions.assertEquals(1, recorder.getMinCarLimitForCustomerType(Customer.Type.REGULAR));
        Assertions.assertEquals(3, recorder.getMaxCarLimitForCustomerType(Customer.Type.REGULAR));
        Assertions.assertEquals(3, recorder.getMinCarLimitForCustomerType(Customer.Type.BUSINESS));
        Assertions.assertEquals(3, recorder.getMaxCarLimitForCustomerType(Customer.Type.BUSINESS));
    }

    @Test
    void basicAddRental() {
        Assertions.assertFalse(null == tid11);
        Assertions.assertFalse(null == tid12);
    }

    @Test
    void getNCarsOfType() {
        Assertions.assertEquals(null, recorder.getNCarsOfType(Car.Type.LUXURY, 1));
        Assertions.assertEquals(null, recorder.getNCarsOfType(Car.Type.ECONOMY, 3));
        List<Car> list = recorder.getNCarsOfType(Car.Type.ECONOMY, 2);
        Assertions.assertTrue(list != null);
        Assertions.assertEquals(2, list.size());
        Assertions.assertEquals(Car.Type.ECONOMY, list.get(0).getType());
    }

    @Test
    void getCarOfType() {
        Assertions.assertEquals(null, recorder.getCarOfType(Car.Type.MINIVAN));
        Assertions.assertEquals(
                Car.Type.ECONOMY.toString(),
                recorder.getCarOfType(Car.Type.ECONOMY).getType().toString()
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
        data1.carType = Car.Type.ECONOMY; data1.customer = regular1; data1.numOfCars=2;
        Assertions.assertTrue(null == recorder.addNewRental(data1));

        //min car limit check
        Customer regular2 = new Regular();
        Record record2 = new Record();
        record2.numOfCars = 0;
        record2.numOfDays = 4;

        Record data2 = new Record();
        data2.carType = Car.Type.ECONOMY; data2.customer = regular2;
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

    @Test
    void optionTypeLimitCheck() {
        Record r1 = new Record();
        r1.carType = Car.Type.LUXURY;
        r1.customer = new Business();
        r1.numOfCars = 3;
        r1.numOfDays = 7;

        recorder.addCar(new Luxury());
        recorder.addCar(new Luxury());
        recorder.addCar(new Luxury());
        recorder.addCar(new Luxury());

        // min child seat: 0
        r1.numOfChildSeats = -3;
        Assertions.assertFalse(null != recorder.addNewRental(r1));

        // max child seat: 4
        r1.numOfChildSeats = 5;
        Assertions.assertFalse(null != recorder.addNewRental(r1));

        // min gps module: 0
        r1.numOfChildSeats = 2;
        r1.numOfGPSModules = -1;
        Assertions.assertFalse(null != recorder.addNewRental(r1));

        // max gps module: 1
        r1.numOfGPSModules = 2;
        Assertions.assertFalse(null != recorder.addNewRental(r1));

        // min radio packages: 0
        r1.numOfGPSModules = 0;
        r1.numOfRadioPackages = -3242;
        Assertions.assertFalse(null != recorder.addNewRental(r1));

        // max radio packages: 1
        r1.numOfRadioPackages = 2;
        Assertions.assertFalse(null != recorder.addNewRental(r1));

        // all options within limit
        r1.numOfRadioPackages = 1;
        Assertions.assertTrue(null != recorder.addNewRental(r1), r1.msg.toString());
    }
}