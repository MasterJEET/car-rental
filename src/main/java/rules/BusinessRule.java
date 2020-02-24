package rules;

import edu.cu.ooad.*;

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
                if (!isCarTypeAvailable()) {
                    return false;
                }
                if (!isNumOfCarWithinLimit()) {
                    return false;
                }
                if (!isNumOfDayWithinLimit()) {
                    return false;
                }
                if (!isNumOfChildSeatsWithinLimit()) {
                    return false;
                }
                if (!isNumOfGPSModulesWithinLimit()) {
                    return false;
                }
                if (!isNumOfRadioPackagesWithinLimit()) {
                    return false;
                }
                break;
            }
            case COMPLETE_RENTAL:
            {
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

    private boolean isCarTypeAvailable() {
        Record data = recorder.getRecord();
        Integer numAvailable = recorder.getAvailableNumOfCarOfType(data.carType);
        Integer numRequest = data.numOfCars;
        if (numRequest > numAvailable) {
            data.msg.delete(0, data.msg.length());
            data.msg.append("Requested number of cars of required type not available; type: ")
                    .append(data.carType.toString())
                    .append(" , requested: ")
                    .append(numRequest)
                    .append(", available: ")
                    .append(numAvailable);
            return false;
        }
        return true;
    }

    /**
     * @return False if total number of Cars already rented plus newly requested breaches the allowed limit
     */
    private boolean isNumOfCarWithinLimit() {
        Customer customer = recorder.getRecord().customer;
        Integer numOfCars = recorder.getRecord().numOfCars + recorder.getNumOfCarsRentedByCustomer(customer);
        Integer minLimit = recorder.getMinCarLimitForCustomerType(customer.getType());
        Integer maxLimit = recorder.getMaxCarLimitForCustomerType(customer.getType());
        if(numOfCars < minLimit || numOfCars > maxLimit) {
            recorder.getRecord().msg.delete(0, recorder.getRecord().msg.length());
            recorder.getRecord().msg
                    .append("Approval of requested number of cars will breach the limit for customer type: ")
                    .append(customer.getType().toString())
                    .append("; max: ")
                    .append(maxLimit)
                    .append(" , min: ")
                    .append(minLimit)
                    .append(", already rented: ")
                    .append(recorder.getNumOfCarsRentedByCustomer(customer))
                    .append(", newly requested : ")
                    .append(recorder.getRecord().numOfCars);
            return false;
        }
        return true;
    }

    /**
     * @return True if requested numOfDays for rent falls within the allowed limit customer for customer type
     */
    private boolean isNumOfDayWithinLimit() {
        Customer customer = recorder.getRecord().customer;
        Integer numOfDays = recorder.getRecord().numOfDays;
        Integer minLimit = recorder.getMinDayLimitForCustomerType(customer.getType());
        Integer maxLimit = recorder.getMaxDayLimitForCustomerType(customer.getType());
        if (numOfDays < minLimit || numOfDays > maxLimit) {
            recorder.getRecord().msg.delete(0, recorder.getRecord().msg.length());
            recorder.getRecord().msg
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

    private boolean isNumOfChildSeatsWithinLimit() {
        Integer requested = recorder.getRecord().numOfChildSeats;
        Integer maxAllowed = recorder.getMaxLimitForOptionType(CarOption.OptionType.CHILD_SEAT);
        Integer minAllowed = 0;
        if (requested < minAllowed || requested > maxAllowed) {
            recorder.getRecord().msg.delete(0, recorder.getRecord().msg.length());
            recorder.getRecord().msg
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

    private boolean isNumOfGPSModulesWithinLimit() {
        Integer requested = recorder.getRecord().numOfGPSModules;
        Integer maxAllowed = recorder.getMaxLimitForOptionType(CarOption.OptionType.GPS_MODULE);
        Integer minAllowed = 0;
        if (requested < minAllowed || requested > maxAllowed) {
            recorder.getRecord().msg.delete(0, recorder.getRecord().msg.length());
            recorder.getRecord().msg
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

    private boolean isNumOfRadioPackagesWithinLimit() {
        Integer requested = recorder.getRecord().numOfRadioPackages;
        Integer maxAllowed = recorder.getMaxLimitForOptionType(CarOption.OptionType.RADIO_PACKAGE);
        Integer minAllowed = 0;
        if (requested < minAllowed || requested > maxAllowed) {
            recorder.getRecord().msg.delete(0, recorder.getRecord().msg.length());
            recorder.getRecord().msg
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

    private boolean doesTransactionExist() {
        Recorder.Transaction transaction =
                recorder.getTransactionFromTID(recorder.getRecord().transactionID);
        if(transaction == null) {
            recorder.getRecord().msg.delete(0, recorder.getRecord().msg.length());
            recorder.getRecord().msg
                    .append("Transaction not found in the system, tid: ")
                    .append(recorder.getRecord().transactionID);
            return false;
        }
        return true;
    }

    /**
     * @return True if today is the day rental should be complete and car should be returned by the customer
     */
    private boolean shouldCompleteToday() {
        Recorder.Transaction transaction = recorder.getTransactionFromTID( recorder.getRecord().transactionID );
        Integer transactionDay = transaction.dayNumber;
        Integer today = recorder.getDayNumber();
        Integer numOfDaysRented = transaction.numOfDays;
        if(today < transactionDay + numOfDaysRented) {
            recorder.getRecord().msg.delete(0, recorder.getRecord().msg.length());
            recorder.getRecord().msg
                    .append("Cannot accept car return earlier than originally agreed, deal start: ")
                    .append(transactionDay)
                    .append(" , deal end: ")
                    .append(transactionDay+numOfDaysRented)
                    .append(", today: ")
                    .append(today);
            return false;
        }
        else if (today > transactionDay + numOfDaysRented) {
            recorder.getRecord().msg.delete(0, recorder.getRecord().msg.length());
            recorder.getRecord().msg
                    .append("Car return overdue, need to go through special process; expected car on day: ")
                    .append(transactionDay+numOfDaysRented)
                    .append(", today: ")
                    .append(today);
            return false;
        }
        return true;
    }

    /**
     * @return True if status of transaction (rental) in consideration is NOT complete
     */
    private boolean isNotComplete() {
        Recorder.RentalStatus oldStatus =
                recorder.getTransactionFromTID(recorder.getRecord().transactionID).rentalStatus;
        if (oldStatus == Recorder.RentalStatus.COMPLETE) {
            recorder.getRecord().msg.delete(0, recorder.getRecord().msg.length());
            recorder.getRecord().msg
                    .append("Transaction status is complete, tid: ")
                    .append(recorder.getRecord().transactionID);
            return false;
        }
        return true;
    }

    public Recorder getRecorder() {
        return recorder;
    }
}
