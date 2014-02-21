package foo.models;

/**
 * Represents a base simple model, which is extended by a complex model.
 * used to ensure that method contribution looks at super methods also
 */
public class BaseSimpleModel {
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

