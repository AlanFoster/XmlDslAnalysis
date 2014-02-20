package foo.factory;

import foo.models.OrderModel;

/**
 * Represents a new order factory
 */
public class OrderFactory {
    /**
     * Contains the type information within the result type of the
     * method signature.
     * @return A order instance
     */
    public OrderModel createOrder() {
        return new OrderModel();
    }
}
