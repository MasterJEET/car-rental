package edu.cu.ooad.util;

import edu.cu.ooad.Car;
import edu.cu.ooad.Customer;

import java.util.LinkedList;
import java.util.List;

public class Report {
    public enum Type {
        DEFAULT,
        DAILY_REPORT,
        OVERALL_STATUS
    }

    public Type type = Type.DEFAULT;
    public String reportID = UniqueIDGenerator.getInstance().generateUniqueID("REP");
    public Integer dayNumber;
    /**
     * List of completed transactions (rentals)
     */
    public List<Transaction> completedRentals= new LinkedList<>();

    /**
     * List of active transaction (rentals)
     */
    public List<Transaction> activeRentals = new LinkedList<>();

    /**
     * List of available cars
     */
    public List<Car> availableCars = new LinkedList<>();

    /**
     * List of Transactions that took plane on the day
     */
    public List<Transaction> transactions = new LinkedList<>();

    public Integer getNumOfCompletedRentals() {
        Integer numCompleted = 0;
        if (completedRentals != null) {
            numCompleted = completedRentals.size();
        }
        return numCompleted;
    }

    public Integer getNumOfActiveRentals() {
        Integer numActive = 0;
        if (activeRentals != null) {
            numActive = activeRentals.size();
        }
        return numActive;
    }

    /**
     * @return total number of rentals (active + complete)
     */
    public Integer getNumOfRentals() {
        return getNumOfActiveRentals() + getNumOfCompletedRentals();
    }

    public Integer getNumOfRentalsWithCustomerType(Customer.Type cusType) {
        Integer count = 0;
        if (completedRentals != null) {
            count += Math.toIntExact(completedRentals.stream()
                    .filter(trn -> trn.customer.getType() == cusType)
                    .count());
        }
        if (activeRentals != null) {
            count += Math.toIntExact(activeRentals.stream()
                    .filter(trn -> trn.customer.getType() == cusType)
                    .count());
        }
        return count;
    }

    public Integer getNumOfAvailableCars() {
        Integer numAvailable = 0;
        if (availableCars != null) {
            numAvailable = availableCars.size();
        }
        return numAvailable;
    }

    public Double getDayEarning() {
        Double dayEarning = 0.0;
        if (transactions != null) {
            for (Transaction trn : transactions) {
                dayEarning += trn.getCost();
            }
        }
        return dayEarning;
    }

    public Double getTotalEarning() {
        Double totalEarning = 0.0;
        if (completedRentals != null) {
            totalEarning += completedRentals.stream()
                    .mapToDouble(trn -> trn.getCost())
                    .sum();
        }
        if (activeRentals != null) {
            totalEarning += activeRentals.stream()
                    .mapToDouble(trn -> trn.getCost())
                    .sum();
        }
        return totalEarning;
    }

    @Override
    public String toString() {
        switch (type) {
            case DAILY_REPORT:
            {
                return toStringOfDailyReportType();
            }
            case OVERALL_STATUS:
            {
                return toStringOfOverallStatusType();
            }
            default:
            {
                return "Unknown Report type specified: " + type;
            }
        }
    }

    public String toStringOfDailyReportType() {
        StringBuffer display = new StringBuffer();
        display
                .append("Report{")
                .append("reportID='").append(reportID).append("'")
                .append(", dayNumber=").append(String.format("%03d",dayNumber))
                .append(System.lineSeparator())
                .append(System.lineSeparator());

        // completedRentals
        display
                .append("::completedRentals")
                .append(": total=").append(String.format("%03d", getNumOfCompletedRentals()))
                .append(System.lineSeparator());
        if (completedRentals != null) {
            for (Transaction trn : completedRentals) {
                display.append(trn).append(System.lineSeparator());
            }
        }
        display
                .append(System.lineSeparator());

        // activeRentals
        display
                .append("::activeRentals")
                .append(": total=").append(String.format("%03d", getNumOfActiveRentals()))
                .append(System.lineSeparator());
        if (activeRentals != null) {
            for (Transaction trn : activeRentals) {
                display.append(trn).append(System.lineSeparator());
            }
        }
        display
                .append(System.lineSeparator());

        //availableCars
        display.append("::availableCars")
                .append(": total=").append(String.format("%03d",getNumOfAvailableCars()))
                .append(System.lineSeparator());
        if (availableCars != null) {
            for (Car car : availableCars) {
                display.append(car).append(System.lineSeparator());
            }
        }

        display
                .append(System.lineSeparator());

        display
                .append("::totalEarningOfTheDay=").append(String.format("%010.2f", getDayEarning()))
                .append(System.lineSeparator())
                .append('}')
                .append(System.lineSeparator())
                .append(System.lineSeparator())
                .append(System.lineSeparator());
        return display.toString();
    }

    public String toStringOfOverallStatusType() {
        StringBuffer display = new StringBuffer();
        display
                .append("Report{")
                .append("reportID='").append(reportID).append("'")
                .append(", dayNumber=").append(String.format("%03d",dayNumber))
                .append(System.lineSeparator());
        display
                .append("Total num of rentals::").append(System.lineSeparator())
                .append("overall: ").append(String.format("%03d",getNumOfRentals()))
                .append(", customer type {")
                .append(Customer.Type.CASUAL).append(": ")
                .append(getNumOfRentalsWithCustomerType(Customer.Type.CASUAL))
                .append(", ").append(Customer.Type.REGULAR).append(": ")
                .append(getNumOfRentalsWithCustomerType(Customer.Type.REGULAR))
                .append(", ").append(Customer.Type.BUSINESS).append(": ")
                .append(getNumOfRentalsWithCustomerType(Customer.Type.BUSINESS))
                .append("},").append(System.lineSeparator());
        display
                .append("total earning:: ").append(String.format("%010.2f",getTotalEarning()))
                .append(System.lineSeparator())
                .append("}")
                .append(System.lineSeparator());
        return display.toString();
    }
}