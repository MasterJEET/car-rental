package main.java.edu.cu.ooad;

public abstract class CarOption extends Car {

    /**
     * This reference is updated dynamically to decorate concrete Cars
     */
    protected Car car;

    /**
     * The cost of this option (per rent NOT per day)
     */
    protected Double optionPrice;

    protected CarOption(Car car, Double optionPrice) {
        super(CarType.INVALID, "NO LICENSE PLATE", 0.0);
        this.car = car;
        this.optionPrice = optionPrice;
    }

    public Double getOptionPrice() {
        return optionPrice;
    }

    public void setOptionPrice(Double optionPrice) {
        this.optionPrice = optionPrice;
    }
}
