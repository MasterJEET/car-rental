package rules;

import edu.cu.ooad.*;
import edu.cu.ooad.util.RentalStatus;
import edu.cu.ooad.util.Transaction;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BusinessRule implements Rule {
    /**
     * All available validation that this BusinessRule can do
     */
    public enum Validation {
        ADD_NEW_RENTAL,
        COMPLETE_RENTAL
    }

    private Recorder recorder;

    public BusinessRule(Recorder recorder) {
        this.recorder = recorder;
    }

    @Override
    public boolean validate(Object validationCriteria) {
        if (!Objects.equals(
                validationCriteria.getClass().getSimpleName(),
                Validation.class.getSimpleName()))
        {
            System.err.println("Expected Validation object, got: " + validationCriteria.getClass().getSimpleName());
            return false;
        }

        Validation validation = (Validation)validationCriteria;
        switch (validation) {
            case ADD_NEW_RENTAL:
            {
                //NOTE: The sequence of checks is important, DO NOT change it
                if (!isValidTransaction()) {
                    return false;
                }
                if (!isNumOfDayWithinLimit()) {
                    return false;
                }
                if (!isNumOfCarWithinLimit()) {
                    return false;
                }
                if (!areCarTypesAvailable()) {
                    return false;
                }
                if (!areNumOfChildSeatsWithinLimit()) {
                    return false;
                }
                if (!areNumOfGPSModulesWithinLimit()) {
                    return false;
                }
                if (!areNumOfRadioPackagesWithinLimit()) {
                    return false;
                }
                break;
            }
            case COMPLETE_RENTAL:
            {
                //NOTE: The sequence of checks is important, DO NOT change it
                if (!doesTransactionExist()) {
                    return false;
                }
                if (!shouldCompleteToday()) {
                    return false;
                }
                if (!isNotComplete()) {
                    return false;
                }
                break;
            }
            default:
            {
                System.err.println("No validation available for: " + validation.toString());
                return false;
            }
        }
        return true;
    }

    /**
     * @return True if data passed in Transaction object is intrinsically consistent.
     * Does minimal check to ensure data is valid and be processes further
     */
    private boolean isValidTransaction() {
        // numOfCars must match the size of lists (except carList) in the Transaction as elements in a list
        // specify particular property of all cars
        Transaction trn = recorder.getTransaction();
        if (
                   trn.numOfCars != trn.carTypeList.size()
                || trn.numOfCars != trn.numOfChildSeatsList.size()
                || trn.numOfCars != trn.numOfGPSModulesList.size()
                || trn.numOfCars != trn.numOfRadioPackagesList.size()
        ) {
            trn.msg.delete(0, trn.msg.length());
            trn.msg
                    .append("'numOfCars' must match size of each list in Transaction")
                    .append("; numOfCars: ").append(trn.numOfCars)
                    .append(", carTypeList.size: ").append(trn.carTypeList.size())
                    .append(", numOfChildSeatsList.size: ").append(trn.numOfChildSeatsList.size())
                    .append(", numOfGPSModulesList.size: ").append(trn.numOfGPSModulesList.size())
                    .append(", numOfRadioPackagesList.size: ").append(trn.numOfRadioPackagesList.size());
            return false;
        }

        // Cannot have a transaction with zero cars or days
        if (trn.numOfCars == 0 || trn.numOfDays == 0) {
            trn.msg.delete(0, trn.msg.length());
            trn.msg
                    .append("Must specify positive number of Cars and Days")
                    .append("; numOfCars: ").append(trn.numOfCars)
                    .append(", numOfDays: ").append(trn.numOfDays);
            return false;
        }
        return true;
    }

    /**
     * @param carType Car types requested
     * @param numRequested Requested number of specified car types
     * @return True if required number of cars of specified type available, false otherwise
     */
    private boolean isCarTypeAvailable(Car.Type carType, Integer numRequested) {
        Transaction data = recorder.getTransaction();
        Integer numAvailable = recorder.getAvailableNumOfCarOfType(carType);
        if (numAvailable < numRequested) {
            data.msg.delete(0, data.msg.length());
            data.msg.append("Requested number of Cars of required type not available; type: ")
                    .append(carType)
                    .append(" , requested: ")
                    .append(numRequested)
                    .append(", available: ")
                    .append(numAvailable);
            return false;
        }
        return true;
    }
    private boolean areCarTypesAvailable() {
        Transaction transaction = recorder.getTransaction();

        //Build a map with key as Car.Type and value as number of cars of that type requested
        Map<Car.Type, Integer> carTypeNumRequestedMap = new HashMap<>();
        for (Car.Type carType: transaction.carTypeList) {
            Integer count = carTypeNumRequestedMap.get(carType);
            if (count == null) {
                count = 0;
                carTypeNumRequestedMap.put(carType,0);
            }
            carTypeNumRequestedMap.put(carType, count+1);
        }

        //check if requested number of cars of required type available
        Boolean allAvailable = carTypeNumRequestedMap.entrySet().stream()
                .allMatch(entry -> isCarTypeAvailable(entry.getKey(), entry.getValue()));
        if (!allAvailable) {
            return false;
        }

        return true;
    }

    /**
     * @return False if total number of Cars already rented plus newly requested breaches the allowed limit
     */
    private boolean isNumOfCarWithinLimit() {
        Customer customer = recorder.getTransaction().customer;
        Integer numOfCarsOverall = recorder.getTransaction().numOfCars
                + recorder.getNumOfCarsRentedByCustomer(customer);
        Integer minLimitPerTransaction = recorder.getMinCarLimitForCustomerType(customer.getType());
        Integer maxLimitPerTransaction = recorder.getMaxCarLimitForCustomerType(customer.getType());
        if(recorder.getTransaction().numOfCars < minLimitPerTransaction
                || recorder.getTransaction().numOfCars > maxLimitPerTransaction
                || numOfCarsOverall > recorder.getMaxCarLimit()
        ) {
            recorder.getTransaction().msg.delete(0, recorder.getTransaction().msg.length());
            recorder.getTransaction().msg
                    .append("Car number limit breach")
                    .append("; customer: ").append(customer)
                    .append("; maxPerTransaction: ").append(maxLimitPerTransaction)
                    .append(" , minPerTransaction: ").append(minLimitPerTransaction)
                    .append(", rented: ").append(recorder.getNumOfCarsRentedByCustomer(customer))
                    .append(", more requested : ").append(recorder.getTransaction().numOfCars)
                    .append(", maxOverall: ").append(recorder.getMaxCarLimit());
            return false;
        }
        return true;
    }

    /**
     * @return True if requested numOfDays for rent falls within the allowed limit customer for customer type
     */
    private boolean isNumOfDayWithinLimit() {
        Customer customer = recorder.getTransaction().customer;
        Integer numOfDays = recorder.getTransaction().numOfDays;
        Integer minLimit = recorder.getMinDayLimitForCustomerType(customer.getType());
        Integer maxLimit = recorder.getMaxDayLimitForCustomerType(customer.getType());
        if (numOfDays < minLimit || numOfDays > maxLimit) {
            recorder.getTransaction().msg.delete(0, recorder.getTransaction().msg.length());
            recorder.getTransaction().msg
                    .append("Number of days requested for rent violates the limit for customer type: ")
                    .append(customer.getType().toString())
                    .append("; max: ")
                    .append(maxLimit)
                    .append(" , min: ")
                    .append(minLimit)
                    .append(", requested: ")
                    .append(numOfDays);
            return false;
        }
        return true;
    }

    private boolean isNumOfChildSeatsWithinLimit(Integer requested) {
        Integer maxAllowed = recorder.getMaxLimitForOptionType(CarOption.OptionType.CHILD_SEAT);
        Integer minAllowed = 0;
        if (requested < minAllowed || requested > maxAllowed) {
            recorder.getTransaction().msg.delete(0, recorder.getTransaction().msg.length());
            recorder.getTransaction().msg
                    .append("Requested number of child car seats violates the limit; requested: ")
                    .append(requested)
                    .append(", min allowed: ")
                    .append(minAllowed)
                    .append(", max allowed: ")
                    .append(maxAllowed);
            return false;
        }
        return true;
    }
    private boolean areNumOfChildSeatsWithinLimit() {
        Transaction transaction = recorder.getTransaction();
        for (Integer numOfChildSeats:
             transaction.numOfChildSeatsList) {
            if (!isNumOfChildSeatsWithinLimit(numOfChildSeats)) {
                return false;
            }
        }
        return true;
    }

    private boolean isNumOfGPSModulesWithinLimit(Integer requested) {
        Integer maxAllowed = recorder.getMaxLimitForOptionType(CarOption.OptionType.GPS_MODULE);
        Integer minAllowed = 0;
        if (requested < minAllowed || requested > maxAllowed) {
            recorder.getTransaction().msg.delete(0, recorder.getTransaction().msg.length());
            recorder.getTransaction().msg
                    .append("Requested number of GPS modules violates the limit; requested: ")
                    .append(requested)
                    .append(", min allowed: ")
                    .append(minAllowed)
                    .append(", max allowed: ")
                    .append(maxAllowed);
            return false;
        }
        return true;
    }
    private boolean areNumOfGPSModulesWithinLimit() {
        Transaction transaction = recorder.getTransaction();
        for (Integer numOfGPSModules:
             transaction.numOfGPSModulesList) {
            if (!isNumOfGPSModulesWithinLimit(numOfGPSModules)) {
                return false;
            }
        }
        return true;
    }

    private boolean isNumOfRadioPackagesWithinLimit(Integer requested) {
        Integer maxAllowed = recorder.getMaxLimitForOptionType(CarOption.OptionType.RADIO_PACKAGE);
        Integer minAllowed = 0;
        if (requested < minAllowed || requested > maxAllowed) {
            recorder.getTransaction().msg.delete(0, recorder.getTransaction().msg.length());
            recorder.getTransaction().msg
                    .append("Requested number of satellite radio packages violates the limit; requested: ")
                    .append(requested)
                    .append(", min allowed: ")
                    .append(minAllowed)
                    .append(", max allowed: ")
                    .append(maxAllowed);
            return false;
        }
        return true;
    }
    private boolean areNumOfRadioPackagesWithinLimit() {
        Transaction transaction = recorder.getTransaction();
        for (Integer numOfRadioPackages:
             transaction.numOfRadioPackagesList) {
            if (!isNumOfRadioPackagesWithinLimit(numOfRadioPackages)) {
                return false;
            }
        }
        return true;
    }

    private boolean doesTransactionExist() {
        Transaction transaction = recorder.getTransactionFromTID(recorder.getTransaction().transactionID);
        if(transaction == null) {
            recorder.getTransaction().msg.delete(0, recorder.getTransaction().msg.length());
            recorder.getTransaction().msg
                    .append("Transaction not found in the system, tid: ")
                    .append(recorder.getTransaction().transactionID);
            return false;
        }
        return true;
    }

    /**
     * @return True if today is the day rental should be complete and car should be returned by the customer
     */
    private boolean shouldCompleteToday() {
        Transaction transaction = recorder.getTransactionFromTID( recorder.getTransaction().transactionID );
        Integer transactionDay = transaction.dayNumber;
        Integer today = recorder.getDayNumber();
        Integer numOfDaysRented = transaction.numOfDays;
        if(today < transactionDay + numOfDaysRented) {
            recorder.getTransaction().msg.delete(0, recorder.getTransaction().msg.length());
            recorder.getTransaction().msg
                    .append("Cannot accept car return earlier than originally agreed")
                    .append("; transactionDay: ").append(transactionDay)
                    .append(", numOfDaysRented: ").append(numOfDaysRented)
                    .append(", due day: ").append(transactionDay+numOfDaysRented)
                    .append(", today: ").append(today);
            return false;
        }
        else if (today > transactionDay + numOfDaysRented) {
            recorder.getTransaction().msg.delete(0, recorder.getTransaction().msg.length());
            recorder.getTransaction().msg
                    .append("Car return overdue, need to go through special process")
                    .append("; transactionDay: ").append(transactionDay)
                    .append(", numOfDaysRented: ").append(numOfDaysRented)
                    .append(", due day: ").append(transactionDay+numOfDaysRented)
                    .append(", today: ").append(today);
            return false;
        }
        return true;
    }

    /**
     * @return True if status of transaction (rental) in consideration is NOT complete
     */
    private boolean isNotComplete() {
        RentalStatus oldStatus = recorder.getTransactionFromTID(recorder.getTransaction().transactionID).rentalStatus;
        if (oldStatus == RentalStatus.COMPLETE) {
            recorder.getTransaction().msg.delete(0, recorder.getTransaction().msg.length());
            recorder.getTransaction().msg
                    .append("Transaction status is already complete, tid: ")
                    .append(recorder.getTransaction().transactionID);
            return false;
        }
        return true;
    }

    public Recorder getRecorder() {
        return recorder;
    }
}
