package foo.factory;

import foo.models.ComplexModel;

/**
 * Represents a person factory
 */
public class ComplexModelFactory {
    /**
     * Contains the type information within the result type of the
     * method signature.
     * @return A person instance
     */
    public ComplexModel createComplexModel() {
        return new ComplexModel();
    }
}
