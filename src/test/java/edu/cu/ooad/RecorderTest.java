package edu.cu.ooad;

import cartype.Economy;
import cartype.Luxury;
import customer.Business;
import customer.Casual;
import customer.Regular;
import edu.cu.ooad.util.IntWithSum;
import edu.cu.ooad.util.Report;
import edu.cu.ooad.util.Transaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import store.CarRental;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

//TODO: Implement as per project description
class RecorderTest {
    private CarRental store = new CarRental();
    private Recorder recorder = new Recorder(store);
    private Customer regular1 = new Regular();
    private Transaction transaction1 = new Transaction();
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

        transaction1.numOfCars=1;
        transaction1.numOfDays=4;

        transaction1.carTypeList.add(Car.Type.ECONOMY);
        transaction1.numOfChildSeatsList.add(0);
        transaction1.numOfGPSModulesList.add(0);
        transaction1.numOfRadioPackagesList.add(0);
        transaction1.customer = regular1;

        //1
        recorder.increaseDayNumber();
        tid11 = recorder.addNewRental(transaction1);
        if (tid11 == null) {
            System.err.println("ERROR MSG: " + transaction1.msg);
        }

        //2
        recorder.increaseDayNumber();
        tid12 = recorder.addNewRental(transaction1);
        if (tid12 == null) {
            System.err.println(transaction1.msg);
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
        Transaction data1 = new Transaction();
        data1.carTypeList.add(Car.Type.ECONOMY);
        data1.customer = regular1;
        data1.numOfCars=2;
        data1.numOfDays=3;
        data1.numOfChildSeatsList.add(0);
        data1.numOfGPSModulesList.add(0);
        data1.numOfRadioPackagesList.add(0);
        Assertions.assertTrue(null == recorder.addNewRental(data1));

        //min car limit check
        Customer regular2 = new Regular();
        Transaction transaction2 = new Transaction();
        transaction2.numOfCars = 0;
        transaction2.numOfDays = 4;

        transaction2.carTypeList.add(Car.Type.ECONOMY);
        transaction2.customer = regular2;
        transaction2.numOfChildSeatsList.add(0);
        transaction2.numOfGPSModulesList.add(0);
        transaction2.numOfRadioPackagesList.add(0);
        Assertions.assertTrue(null == recorder.addNewRental(transaction2));

        //max day limit check
        transaction2.numOfDays = 6;
        Assertions.assertTrue(null == recorder.addNewRental(transaction2));

        //min day limit check
        data1.numOfDays = -1;
        Assertions.assertTrue(null == recorder.addNewRental(data1));
    }

    @Test
    void completeRental() {
        //3
        recorder.increaseDayNumber();
        Transaction transaction = new Transaction();
        transaction.transactionID = tid11;

        //Return before due date
        Assertions.assertFalse(recorder.completeRental(transaction), transaction.msg.toString());

        //4
        recorder.increaseDayNumber();
        //5
        recorder.increaseDayNumber();

        //Return on due date
        Assertions.assertTrue(recorder.completeRental(transaction), transaction.msg.toString());

        //No active transaction/rental found (already returned)
        Assertions.assertFalse(recorder.completeRental(transaction));

        //6
        recorder.increaseDayNumber();
        //7
        recorder.increaseDayNumber();

        //Return after due date
        transaction.transactionID = tid12;
        Assertions.assertFalse(recorder.completeRental(transaction), transaction.msg.toString());

        //Transaction not found (Invalid transaction ID)
        transaction.transactionID = "INVALID_TRANSACTION_ID";
        Assertions.assertFalse(recorder.completeRental(transaction), transaction.msg.toString());
    }

    @Test
    void optionTypeLimitCheck() {
        Transaction r1 = new Transaction();
        r1.customer = new Business();
        r1.numOfCars = 3;
        r1.numOfDays = 7;

        r1.carTypeList.add(Car.Type.LUXURY);
        r1.numOfChildSeatsList.add(0);
        r1.numOfGPSModulesList.add(0);
        r1.numOfRadioPackagesList.add(0);

        r1.carTypeList.add(Car.Type.LUXURY);
        r1.numOfChildSeatsList.add(0);
        r1.numOfGPSModulesList.add(0);
        r1.numOfRadioPackagesList.add(0);
        r1.carTypeList.add(Car.Type.LUXURY);

        r1.numOfChildSeatsList.add(0);
        r1.numOfGPSModulesList.add(0);
        r1.numOfRadioPackagesList.add(0);

        recorder.addCar(new Luxury());
        recorder.addCar(new Luxury());
        recorder.addCar(new Luxury());
        recorder.addCar(new Luxury());

        //TODO: add test for carTypeList.size may be?

        // min child seat: 0
        r1.numOfChildSeatsList.remove(2);
        r1.numOfChildSeatsList.add(-3);
        Assertions.assertFalse(null != recorder.addNewRental(r1));

        // max child seat: 4
        r1.numOfChildSeatsList.remove(2);
        r1.numOfChildSeatsList.add(5);
        Assertions.assertFalse(null != recorder.addNewRental(r1));

        // min gps module: 0
        r1.numOfChildSeatsList.remove(2);
        r1.numOfChildSeatsList.add(2);
        r1.numOfGPSModulesList.remove(2);
        r1.numOfGPSModulesList.add(-1);
        Assertions.assertFalse(null != recorder.addNewRental(r1));

        // max gps module: 1
        r1.numOfGPSModulesList.remove(2);
        r1.numOfGPSModulesList.add(2);
        Assertions.assertFalse(null != recorder.addNewRental(r1));

        // min radio packages: 0
        r1.numOfGPSModulesList.remove(2);
        r1.numOfGPSModulesList.add(0);
        r1.numOfRadioPackagesList.remove(2);
        r1.numOfRadioPackagesList.add(-3242);
        Assertions.assertFalse(null != recorder.addNewRental(r1));

        // max radio packages: 1
        r1.numOfRadioPackagesList.remove(2);
        r1.numOfRadioPackagesList.add(2);
        Assertions.assertFalse(null != recorder.addNewRental(r1));

        // all options within limit
        r1.numOfRadioPackagesList.remove(2);
        r1.numOfRadioPackagesList.add(1);
        Assertions.assertTrue(null != recorder.addNewRental(r1), r1.msg.toString());
    }

    //IntWithSum class testing
    @Test
    void intWithSum() {
        Integer numOfInt = 5;
        Integer sumOfInt = 24;
        List<Integer> integers = IntWithSum.getIntegersWithSum(numOfInt, sumOfInt);

        //verify num
        Assertions.assertEquals(numOfInt, integers.size());

        //verify sum
        Assertions.assertEquals(sumOfInt, integers.stream().mapToInt(i->i).sum());

        numOfInt = 20;
        sumOfInt = 19;

        //all integers must be non-negative
        integers = IntWithSum.getIntegersWithSum(numOfInt, sumOfInt);
        Assertions.assertTrue(integers.stream().allMatch(i -> i>=0));
    }

    //Check store is being initialized as specified
    @Test
    void storeInitialization() {
        Integer numOfCar = new Random().nextInt(31);
        Store store = new CarRental(numOfCar);

        // total num of car
        if (numOfCar > 10) {
            Assertions.assertEquals(numOfCar, store.getTotalNumOfCars());
        }
        else {
            Assertions.assertEquals(10, store.getTotalNumOfCars());
        }

        // num of Economy cars
        Assertions.assertTrue(store.getTotalNumOfCarsOfType(Car.Type.ECONOMY) >= 2);

        // num of Standard cars
        Assertions.assertTrue(store.getTotalNumOfCarsOfType(Car.Type.STANDARD) >= 2);

        // num of MiniVans
        Assertions.assertTrue(store.getTotalNumOfCarsOfType(Car.Type.MINIVAN) >= 2);

        // num of SUV
        Assertions.assertTrue(store.getTotalNumOfCarsOfType(Car.Type.SUV) >= 2);

        // num of Luxury cars
        Assertions.assertTrue(store.getTotalNumOfCarsOfType(Car.Type.LUXURY) >= 2);
    }

    //Test validity of report generation
    @Test
    void generateReport() {
        Store carRental = new CarRental();
        Summarizer summarizer = new Summarizer(carRental);
        //ensure we have ample Luxury cars, tot car: 25
        carRental.addCarOfType(Car.Type.LUXURY);

        //day 1
        carRental.startNewDay();

        //Casual customer rents an Economy Car for 2 days
        Customer casual = new Casual();
        Integer nDaysCsl = 2;
        Integer nCarsCsl = 1;
        List<Car.Type> typeListCsl = new LinkedList<>();
        //ppd: 20
        typeListCsl.add(Car.Type.ECONOMY);
        String tidCsl = carRental.addNewRental(casual, nDaysCsl, nCarsCsl, typeListCsl);

        //Business customer rents 3 Luxury Cars for 7 days
        Customer business = new Business();
        Integer nDaysBus = 7;
        Integer nCarsBus = 3;
        List<Car.Type> typeListBus = new LinkedList<>();
        //ppd: 35, 35, 35
        typeListBus.add(Car.Type.LUXURY);typeListBus.add(Car.Type.LUXURY);typeListBus.add(Car.Type.LUXURY);
        //2,0,3 child seats in cars, ppt: 15
        List<Integer> seatListBus = new LinkedList<>();
        seatListBus.add(2);seatListBus.add(0);seatListBus.add(3);
        //1,1,1 gps modules, ppt: 20
        List<Integer> gpsListBus = new LinkedList<>();
        gpsListBus.add(1);gpsListBus.add(1);gpsListBus.add(1);
        //0,1,0 radio packages, ppt: 10
        List<Integer> radioListBus = new LinkedList<>();
        radioListBus.add(0);radioListBus.add(1);radioListBus.add(0);

        String tidBus = carRental.addNewRental(business,
                nDaysBus,
                nCarsBus,
                typeListBus,
                seatListBus,
                gpsListBus,
                radioListBus);

        carRental.endDay();
        Report report = carRental.getReportForDay(1);
        //complete: 0, active: 2, cars left: 21
        //earning: 920
        // (
        // (20*2+15*0+20*0+10*0)+
        // (35*7+15*2+20*1+10*0)+
        // (35*7+15*0+20*1+10*1)+
        // (35*7+15*3+20*1+10*0)
        // )
        Assertions.assertEquals(0, report.getNumOfCompletedRentals());
        Assertions.assertEquals(2, report.getNumOfActiveRentals());
        Assertions.assertEquals(21, report.getNumOfAvailableCars());
        Assertions.assertEquals(920, report.getDayEarning());

        //day 2
        carRental.startNewDay();

        //Regular Customer rents 1 MinVan for 5 days with no additional options
        Customer regular = new Regular();
        Integer nDaysReg = 5;
        Integer nCarsReg = 1;
        List<Car.Type> typeListReg = new LinkedList<>();
        //ppd: 28
        typeListReg.add(Car.Type.MINIVAN);
        String tidReg = carRental.addNewRental(regular, nDaysReg, nCarsReg, typeListReg);

        carRental.endDay();
        report = carRental.getReportForDay(2);
        //complete: 0, active: 3, cars left: 20
        //earning: 140
        // (
        // (28*5+15*0+20*0+10*0)
        // )
        Assertions.assertEquals(0, report.getNumOfCompletedRentals());
        Assertions.assertEquals(3, report.getNumOfActiveRentals());
        Assertions.assertEquals(20, report.getNumOfAvailableCars());
        Assertions.assertEquals(140, report.getDayEarning());

        //day 3
        carRental.startNewDay();
        carRental.completeRental(tidCsl);
        carRental.endDay();
        report = carRental.getReportForDay(3);
        //complete: 1, active: 2, cars left: 21
        //earning: 0
        Assertions.assertEquals(1, report.getNumOfCompletedRentals());
        Assertions.assertEquals(2, report.getNumOfActiveRentals());
        Assertions.assertEquals(21, report.getNumOfAvailableCars());
        Assertions.assertEquals(0, report.getDayEarning());

        //day 4
        carRental.startNewDay();
        carRental.endDay();
        report = carRental.getReportForDay(4);
        //complete: 1, active: 2, cars left: 21
        //earning: 0
        Assertions.assertEquals(1, report.getNumOfCompletedRentals());
        Assertions.assertEquals(2, report.getNumOfActiveRentals());
        Assertions.assertEquals(21, report.getNumOfAvailableCars());
        Assertions.assertEquals(0, report.getDayEarning());

        //day 5
        carRental.startNewDay();
        carRental.endDay();

        //day 6
        carRental.startNewDay();
        carRental.endDay();

        //day 7
        carRental.startNewDay();
        carRental.completeRental(tidReg);
        carRental.endDay();
        report = carRental.getReportForDay(7);
        //complete: 2, active: 1, cars left: 22
        //earning: 0
        Assertions.assertEquals(2, report.getNumOfCompletedRentals());
        Assertions.assertEquals(1, report.getNumOfActiveRentals());
        Assertions.assertEquals(22, report.getNumOfAvailableCars());
        Assertions.assertEquals(0, report.getDayEarning());

        //day 8
        carRental.startNewDay();
        carRental.completeRental(tidBus);
        carRental.endDay();
        report = carRental.getReportForDay(8);
        Transaction trn = report.completedRentals.get(report.completedRentals.size()-1);
        //complete: 3, active: 0, cars left: 25
        //options{seats:2,0,3; gps:1,1,1; radio:0,1,0}
        Assertions.assertEquals(3, report.getNumOfCompletedRentals());
        Assertions.assertEquals(0, report.getNumOfActiveRentals());
        Assertions.assertEquals(25, report.getNumOfAvailableCars());
        Assertions.assertEquals(2, trn.numOfChildSeatsList.get(0));
        Assertions.assertEquals(0, trn.numOfChildSeatsList.get(1));
        Assertions.assertEquals(3, trn.numOfChildSeatsList.get(2));
        Assertions.assertEquals(1, trn.numOfGPSModulesList.get(0));
        Assertions.assertEquals(1, trn.numOfGPSModulesList.get(1));
        Assertions.assertEquals(1, trn.numOfGPSModulesList.get(1));
        Assertions.assertEquals(0, trn.numOfRadioPackagesList.get(0));
        Assertions.assertEquals(1, trn.numOfRadioPackagesList.get(1));
        Assertions.assertEquals(0, trn.numOfRadioPackagesList.get(2));

        //day 9
        carRental.startNewDay();
        carRental.addNewRental(casual, nDaysCsl, nCarsCsl, typeListCsl);
        carRental.endDay();
        carRental.generateOverallStatus();
        Report overall = carRental.getOverallStatusReport();
        //earning(of the day): 40
        // ( 20*2+15*0+20*0+10*0 )

        //complete (inclusive active on last day):: overall: 4, casual: 2, regular: 1, business: 1
        //total earning:: 1100

        Assertions.assertEquals(4, overall.getNumOfRentals());
        Assertions.assertEquals(2, overall.getNumOfRentalsWithCustomerType(Customer.Type.CASUAL));
        Assertions.assertEquals(1, overall.getNumOfRentalsWithCustomerType(Customer.Type.REGULAR));
        Assertions.assertEquals(1, overall.getNumOfRentalsWithCustomerType(Customer.Type.BUSINESS));
        Assertions.assertEquals(1100, overall.getTotalEarning());
    }

    //TODO: Test cases for max car limit per transaction and overall

}