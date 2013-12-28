package features;

/**
 * Represents a concrete implementation of an expected Token tuple used
 * within the CucumberJVM behaviour tests
 */
public class SplitPackageTuple {
    /**
     * The token to match
     */
    private String token;
    /**
     * The start location
     */
    private int start;
    /**
     * The end locating
     */
    private int end;

    /**
     * Public empty constructor for CucumberJVM's introspection
     */
    public SplitPackageTuple() {
    }

    public SplitPackageTuple(String token, int start, int end) {
        this.token = token;
        this.start = start;
        this.end = end;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }
}
