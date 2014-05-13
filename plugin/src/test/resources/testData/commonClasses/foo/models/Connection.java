package foo.models;

/**
 * Represents a Connection created by an instance of IConnectionFactory
 */
public class Connection {
    private int id;
    private int statusCode;

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getResponse() {
        return "Hello World";
    }

    public void voidReturn() { }

    @Override
    public String toString() {
        return "Connection{" +
                "id=" + id +
                ", statusCode=" + statusCode +
                '}';
    }
}
