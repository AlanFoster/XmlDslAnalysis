package foo.processors;

import foo.models.OrderModel;

/**
 * Represents a simple processor which takes an Order model in someway, and
 * mutates the camel exchange's body type
 */
public class OrderProcessor {
    /**
     * Represents a simple method which returns the String type, effectively replacing the camel
     * exchange with a new string body.
     *
     * @param orderModel The order model
     * @return The new String exchange body
     */
    public String extractOrderType(OrderModel orderModel) {
        return orderModel.getOrderType();
    }
}
