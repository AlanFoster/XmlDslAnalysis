package foo.models;

/**
 * Test model used to represent a class which extends another, to ensure
 * that method contribution looks at super methods also.
 */
public class ComplexModel extends BaseSimpleModel {
    private String additionalInformation;

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    // Recursive type definition
//    public ComplexModel getSelf() { return this; }
}

