package edu.cu.ooad;

public abstract class Customer {
    public enum Type {
        DEFAULT,
        CASUAL,
        REGULAR,
        BUSINESS
    }

    protected Type type = Type.DEFAULT;

    /**
     * Unique ID associated with each customer
     */
    protected String customerID;

    public String getCustomerID() {
        return customerID;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        StringBuffer display = new StringBuffer();
        display
                .append("Customer{")
                .append("type=").append(String.format("%10s",type))
                .append(", customerID='").append(customerID).append("'")
                .append('}');
        return display.toString();
    }
}
