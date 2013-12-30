package foo.models;

/**
 * Represents a very basic 'order' model POJO which could pass through an Apache Camel EIP route.
 */
public class OrderModel {
    private int orderId;
    private int customerId;
    private String orderType;

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }
}

