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
        CAR_TYPE_AVAILABILITY,
        ADD_NEW_RENTAL
    }

    private Recorder recorder;
    /**
     * Maximum number of Cars that a Customer can rent
     */
    private Integer maxNumOfCarAllowed = 3;

    public BusinessRule(Recorder recorder) {
        this.recorder = recorder;
    }

    private boolean isCarTypeAvailable() {
        Recorder.Data data = recorder.getData();
        if (recorder.getCarOfType(data.carType) == null) {
            data.msg.append(data.carType.toString()).append(": CarType not available");
            return false;
        }
        return true;
    }

    /**
     * @return False if total number of Cars already rented plus newly requested for rent by a
     * Customer crosses the maximum allowable number of Cars, else return true
     */
    private boolean isNumOfCarBelowLimit() {
        Customer customer = recorder.getData().customer;
        if(customer.getNumCarsRequested() + recorder.getNumberOfCarsRentedByCustomer(customer) > maxNumOfCarAllowed) {
            recorder.getData().msg.append(maxNumOfCarAllowed).append(" cars per customer allowed for active rental");
            return false;
        }
        return true;
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
            case CAR_TYPE_AVAILABILITY:
            {
                //TODO: add validation checks
                System.out.println(validation.toString() + " validation criteria to be added (car type)");
                break;
            }
            case ADD_NEW_RENTAL:
            {
                if (!isCarTypeAvailable()) {
                    return false;
                }
                if (!isNumOfCarBelowLimit()) {
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

    public Recorder getRecorder() {
        return recorder;
    }

    public Integer getMaxNumOfCarAllowed() {
        return maxNumOfCarAllowed;
    }

    public void setMaxNumOfCarAllowed(Integer maxNumOfCarAllowed) {
        this.maxNumOfCarAllowed = maxNumOfCarAllowed;
    }
}
