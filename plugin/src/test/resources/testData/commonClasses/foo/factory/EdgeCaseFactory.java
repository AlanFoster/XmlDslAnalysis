package foo.factory;

import foo.models.EdgeCaseModel;

/**
 * Represents a EdgeCaseFactory
 */
public class EdgeCaseFactory {
    /**
     * Creates an instance of an object with associated edge case attributes
     * @return An instance of an object with difficult attributes to handle
     */
    public EdgeCaseModel createEdgeCaseModel() {
        return new EdgeCaseModel();
    }
}
