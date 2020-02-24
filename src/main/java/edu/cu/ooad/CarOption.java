package edu.cu.ooad;

public abstract class CarOption extends Car {

    public enum OptionType {
        DEFAULT,
        CHILD_SEAT,
        GPS_MODULE,
        RADIO_PACKAGE
    }

    protected OptionType optionType = OptionType.DEFAULT;

    /**
     * This reference is updated dynamically to decorate concrete Cars
     */
    protected Car car;

    /**
     * The cost of this option (per rent NOT per day)
     */
    protected Double optionPrice;

    protected CarOption(Car car, Double optionPrice) {
        super(Car.Type.INVALID, "NO LICENSE PLATE", 0.0);
        this.car = car;
        this.optionPrice = optionPrice;
    }

    public Double getOptionPrice() {
        return optionPrice;
    }

    public void setOptionPrice(Double optionPrice) {
        this.optionPrice = optionPrice;
    }

    public OptionType getOptionType() {
        return optionType;
    }
}
