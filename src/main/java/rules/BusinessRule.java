package rules;

import edu.cu.ooad.Rule;
import edu.cu.ooad.Recorder;
import edu.cu.ooad.Customer;
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
        Recorder.Data data = recorder.getData();
        Integer numAvailable = recorder.getNumOfCarOfType(data.carType);
        Integer numRequest = data.customer.getNumOfCars();
        if (numRequest > numAvailable) {
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
        Customer customer = recorder.getData().customer;
        Integer numOfCars = customer.getNumOfCars() + recorder.getNumOfCarsRentedByCustomer(customer);
        Integer minLimit = recorder.getMinCarLimitForCustomerType(customer.getType());
        Integer maxLimit = recorder.getMaxCarLimitForCustomerType(customer.getType());
        if(numOfCars < minLimit || numOfCars > maxLimit) {
            recorder.getData().msg
                    .append("Approval of requested number of cars will breach the limit for customer type: ")
                    .append(customer.getType().toString())
                    .append("; max: ")
                    .append(maxLimit)
                    .append(" , min: ")
                    .append(minLimit)
                    .append(", already rented: ")
                    .append(recorder.getNumOfCarsRentedByCustomer(customer))
                    .append(", newly requested : ")
                    .append(customer.getNumOfCars());
            return false;
        }
        return true;
    }

    /**
     * @return True if requested numOfDays for rent falls within the allowed limit customer for customer type
     */
    private boolean isNumOfDayWithinLimit() {
        Customer customer = recorder.getData().customer;
        Integer numOfDays = customer.getNumOfDays();
        Integer minLimit = recorder.getMinDayLimitForCustomerType(customer.getType());
        Integer maxLimit = recorder.getMaxDayLimitForCustomerType(customer.getType());
        if (numOfDays < minLimit || numOfDays > maxLimit) {
            recorder.getData().msg
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

    private boolean doesTransactionExist() {
        Recorder.RentalStatus oldStatus =
                recorder.getTransactionFromTID(recorder.getData().transactionID).rentalStatus;
        if(oldStatus == null) {
            recorder.getData().msg
                    .append("Transaction not found in the system, tid: ")
                    .append(recorder.getData().transactionID);
            return false;
        }
        return true;
    }

    /**
     * @return True if today is the day rental should be complete and car should be returned by the customer
     */
    private boolean shouldCompleteToday() {
        Recorder.Transaction transaction = recorder.getTransactionFromTID( recorder.getData().transactionID );
        Integer transactionDay = transaction.dayNumber;
        Integer today = recorder.getDayNumber();
        Integer numOfDaysRented = transaction.numOfDays;
        if(today < transactionDay + numOfDaysRented) {
            recorder.getData().msg
                    .append("Cannot accept car return earlier than originally agreed, deal start: ")
                    .append(transactionDay)
                    .append(" , deal end: ")
                    .append(transactionDay+numOfDaysRented)
                    .append(", today: ")
                    .append(today);
            return false;
        }
        else if (today > transactionDay + numOfDaysRented) {
            recorder.getData().msg
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
                recorder.getTransactionFromTID(recorder.getData().transactionID).rentalStatus;
        if (oldStatus == Recorder.RentalStatus.COMPLETE) {
            recorder.getData().msg
                    .append("Transaction status is complete, tid: ")
                    .append(recorder.getData().transactionID);
            return false;
        }
        return true;
    }

    public Recorder getRecorder() {
        return recorder;
    }
}
